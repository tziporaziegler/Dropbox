package dropbox;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class Client extends JFrame implements ReaderListener {
	private static final long serialVersionUID = 1L;
	private Socket socket;
	private String root = "/dropbox/";
	private FileCache cache = new FileCache(root);
	private List<Message> validMsgs;

	private String[] filenames;
	private JList<String> list;
	private JButton uploadButton;
	private int listPlace;

	// store file in middle of writing to
	private RandomAccessFile currentRAFile;
	private int currentOffsetTotal;

	public Client() throws UnknownHostException, IOException {
		setTitle("Dropbox");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));
		add(panel, BorderLayout.NORTH);

		uploadButton = new JButton("UPLOAD");
		uploadButton.addActionListener(uploadClick);
		panel.add(uploadButton);

		list = new JList<String>();
		add(list, BorderLayout.CENTER);

		socket = new Socket("localhost", 6003);
		new ReaderThread(socket, this).start();

		// create an ArrayList of all valid messages
		// used when determining what kind of message was read in by checking if
		// matches any of the Patterns
		validMsgs = new ArrayList<Message>();
		validMsgs.add(new ChunkServer(this));
		validMsgs.add(new FileMessage(this));
		validMsgs.add(new FilesMessage(this));
		validMsgs.add(new SyncMessage());

		setVisible(true);

		send("LIST");
		checkUpload();
	}

	private void checkUpload() throws FileNotFoundException, IOException {
		ArrayList<String> clientFiles = cache.getFileNames();

		if (filenames == null) {
			for (String clientFile : clientFiles) {
				File file = cache.getFile(clientFile);
				sendChunkMsg(file);
			}
		}
		else {
			ArrayList<String> filenamesList = new ArrayList<String>(Arrays.asList(filenames));
			for (String clientFile : clientFiles) {
				if (!filenamesList.contains(clientFile)) {
					// get the actual file that need from the file cache
					File file = cache.getFile(clientFile);
					// upload clientFile
					sendChunkMsg(file);
				}
			}
		}
	}

	private void send(String msg) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(out);
		writer.println(msg);
		writer.flush();
	}

	// FILES message creates new array to hold all FILE messages that will
	// follow
	public void createArray(int num) {
		filenames = new String[num];
		listPlace = 0;
	}

	// add the filenames from the FILE messages until reach expected amount of files according to the FILES message
	public void addFile(String filename) {
		filenames[listPlace] = filename;
		if (listPlace == filenames.length - 1) {
			// once the correct amount of filenames are received, add the list to the jFrame
			list.setListData(filenames);
		}
		else {
			listPlace++;
		}
	}

	// Whenever a new line is read in, determine what the Message is by comparing it to all the valid Message Patterns
	@Override
	public void onLineRead(String line, Socket socket) {
		for (Message msg : validMsgs) {
			if (msg.matches(line)) {
				msg.perform(cache, socket, line);
				break;
			}
		}
	}

	@Override
	public void onCloseSocket(Socket socket) {
		IOUtils.closeQuietly(socket);
	}

	ActionListener uploadClick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				// choose file
				File file = null;
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
				}

				sendChunkMsg(file);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	public void sendChunkMsg(File file) throws FileNotFoundException, IOException {
		int offset = 0;
		long size = file.length();
		long leftToRead = size;

		while (offset < size) {
			// read file in byte
			FileInputStream fin = new FileInputStream(file);

			// reads file into array until offset

			int chunkSize = leftToRead - 512 > 0 ? 512 : (int) leftToRead;
			byte fileContent[] = new byte[chunkSize];
			fin.read(fileContent, offset, chunkSize - 1);

			// encode bytes to base 64
			byte[] base64 = Base64.encodeBase64(fileContent);

			// CHUNK [filename] [last modified] [filesize] [offset] [base64 encoded bytes]
			// send chunk message to be handled by server
			send("CHUNK " + file.getName() + " " + file.lastModified() + " " + size + " " + offset + " " + base64.toString());

			// add 512 to offset for next chunk
			offset += 512;

			fin.close();

			// TODO remove println
			System.out.println("CHUNK " + file.getName() + " " + file.lastModified() + " " + size + " " + offset + " " + base64.toString());
		}
	}

	public void newFile(String filename, long lastModified) throws FileNotFoundException {
		File currentFile = new File(root + filename + ".txt");
		currentFile.setLastModified(lastModified);
		currentRAFile = new RandomAccessFile(currentFile, "rw");
	}

	public void addBytes(long offset, long filesize, String encodedBytes) throws IOException {
		currentRAFile.seek(offset);
		currentRAFile.write(Base64.decodeBase64(encodedBytes));

		currentOffsetTotal += 512;

		if (filesize - currentOffsetTotal < 512) {
			currentRAFile.close();
			currentOffsetTotal = 0;
		}
	}

	/* public static void main(String[] args) {
	 * try {
	 * new Client();
	 * }
	 * catch (IOException e) {
	 * e.printStackTrace();
	 * }
	 * } */
}