package dropbox;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import dropbox.messages.ChunkServer;
import dropbox.messages.FileMessage;
import dropbox.messages.FilesMessage;
import dropbox.messages.SyncMessage;

public class Client extends World {
	private JFrame frame;
	private String[][] filenames;
	private JList<String> list;
	private int listPlace;

	public Client(String root) throws UnknownHostException, IOException {
		super(root);

		frame = new JFrame();
		frame.setTitle("Dropbox");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 400);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));
		frame.add(panel, BorderLayout.NORTH);

		JButton uploadButton = new JButton("UPLOAD");
		uploadButton.addActionListener(uploadClick);
		panel.add(uploadButton);

		list = new JList<String>();
		frame.add(list, BorderLayout.CENTER);

		socket = new Socket("localhost", 8181);
		new ReaderThread(socket, this).start();

		populateValidMsgs(new ChunkServer(this), new FileMessage(this), new FilesMessage(this), new SyncMessage(this));

		frame.setVisible(true);

		// retrieve all new and updated files from server
		send("LIST", socket);

		// retrieve list of files on server and automatically download any missing files or files that are not up to date
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(uploadExecute, 0, 7, TimeUnit.SECONDS);
	}

	// check every 7 seconds if there are any new files to upload and update JList
	private Runnable uploadExecute = new Runnable() {
		public void run() {
			list.setListData(cache.getFileNamesArray());

			try {
				// check if have any files that server doesn't or any newer file versions than server. If yes, upload files to server.
				checkUpload();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private void checkUpload() throws FileNotFoundException, IOException {
		ArrayList<String> clientFiles = cache.getFileNames();

		if (filenames == null || filenames.length == 0) {
			for (String clientFile : clientFiles) {
				File file = cache.getFile(clientFile);
				sendChunkMsg(file, socket);
			}
		}
		else {
			ArrayList<String> filenamesList = new ArrayList<String>(Arrays.asList(filenames[0]));
			for (String clientFile : clientFiles) {
				if (!filenamesList.contains(clientFile)) {
					// get the actual file that need from the file cache
					File file = cache.getFile(clientFile);
					// upload clientFile
					sendChunkMsg(file, socket);
				}

				else if (filenamesList.contains(clientFile)) {
					long clientLastModified = (cache.getFile(clientFile)).lastModified();
					for (int i = 0; i < filenames.length; i++) {
						// find filename in the server files
						if (filenames[0][i].equals(clientFile)) {
							// get the last modified of the file
							long serverLastModified = Long.valueOf(filenames[1][i]);
							// check if the server file is not up to date
							if (clientLastModified > serverLastModified) {
								File file = cache.getFile(clientFile);
								sendChunkMsg(file, socket);
							}
						}
					}

				}
			}
		}
	}

	// FILES message creates new array to hold all FILE messages that will follow
	public void createArray(int num) {
		filenames = new String[2][num];
		listPlace = 0;
	}

	// add the filenames from the FILE messages until reach expected amount of files according to the FILES message
	public void addFile(String filename, String lastModified) {
		filenames[0][listPlace] = filename;
		filenames[1][listPlace] = lastModified;
		if (listPlace == filenames[0].length - 1) {
			// FIXME not updating the list as soon as new file is detected
			// once the correct amount of filenames are received, add the list to the jFrame
			list.setListData(cache.getFileNamesArray());
		}
		else {
			listPlace++;
		}
	}

	private ActionListener uploadClick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				// choose file
				File source = null;
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(frame.getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					source = chooser.getSelectedFile();
				}

				File dest = new File(root + source.getName());
				FileUtils.copyFile(source, dest);

				list.setListData(cache.getFileNamesArray());

				sendChunkMsg(dest, socket);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static void main(String[] args) {
		try {
			new Client("./dropbox_client/");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}