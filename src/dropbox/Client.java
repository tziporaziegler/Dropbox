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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class Client extends World {
	private JFrame frame;
	private String[][] filenames;
	private JList<String> list;
	private JButton uploadButton;
	private int listPlace;

	public Client() throws UnknownHostException, IOException {
		super("/dropbox/");

		frame = new JFrame();
		frame.setTitle("Dropbox");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 400);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));
		frame.add(panel, BorderLayout.NORTH);

		uploadButton = new JButton("UPLOAD");
		uploadButton.addActionListener(uploadClick);
		panel.add(uploadButton);

		list = new JList<String>();
		frame.add(list, BorderLayout.CENTER);

		socket = new Socket("localhost", 6003);
		new ReaderThread(socket, this).start();

		populateValidMsgs(new ChunkServer(this), new FileMessage(this), new FilesMessage(this), new SyncMessage());

		frame.setVisible(true);

		send("LIST");
		System.out.println("Client sending LIST");
		
		checkUpload();
	}

	private void checkUpload() throws FileNotFoundException, IOException {
		ArrayList<String> clientFiles = cache.getFileNames();

		if (filenames == null) {
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
				else if (filenamesList.contains(clientFile)){
					long clientLastModified = (cache.getFile(clientFile)).lastModified();
					for(int i = 0; i < filenames.length; i++){
						// find filename in the server files
						if(filenames[0][i].equals(clientFile)){
							//get the last modified of the file
							long serverLastModified = Long.valueOf(filenames[1][i]);
							//check if the server file is not up to date
							if(clientLastModified > serverLastModified){
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
		filenames[0] = new String[num];
		filenames[1] = new String[num];
		listPlace = 0;
	}

	// add the filenames from the FILE messages until reach expected amount of files according to the FILES message
	public void addFile(String filename, String lastModified) {
		filenames[0][listPlace] = filename;
		filenames[1][listPlace] = lastModified;
		if (listPlace == filenames.length - 1) {
			// once the correct amount of filenames are received, add the list to the jFrame
			list.setListData(filenames[0]);
		}
		else {
			listPlace++;
		}
	}

	ActionListener uploadClick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				// choose file
				File file = null;
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(frame.getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
				}
				// FIXME break up file into chunks < 512
				sendChunkMsg(file, socket);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	};
}