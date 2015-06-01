package dropbox;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
	private FileCache cache = new FileCache("/dropbox/");
	private List<Message> validMsgs;

	private String[] filenames;
	private JList<String> list;
	private JButton listButton;
	private JButton uploadButton;
	private int listPlace;

	public Client() throws UnknownHostException, IOException {
		setTitle("Dropbox");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));

		listButton = new JButton("LIST");
		listButton.addActionListener(listClick);
		panel.add(listButton);
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
		validMsgs.add(new ChunkServer());
		validMsgs.add(new FileMessage(this));
		validMsgs.add(new FilesMessage(this));
		validMsgs.add(new SyncMessage());

		setVisible(true);
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

	// add the filenames from the FILE messages until reach expected amount of
	// files according to the FILES message
	public void addFile(String filename) {
		filenames[listPlace] = filename;
		if (listPlace == filenames.length - 1) {
			// once the correct amount of filenames are received, add the list
			// to the jFrame
			list.setListData(filenames);
		} else {
			listPlace++;
		}
	}

	// Whenever a new line is read in, determine what the Message is by
	// comparing it to all the valid Message Patterns
	@Override
	public void onLineRead(String line, Socket socket) {
		for (Message msg : validMsgs) {
			if (msg.matches(line)) {
				msg.perform(cache, socket);
				break;
			}
		}
	}

	@Override
	public void onCloseSocket(Socket socket) {
		IOUtils.closeQuietly(socket);
	}

	// send a LIST message to the Server whenever the listButton is clicked
	ActionListener listClick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				send("LIST");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

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

				int offset = 0;

				// send chunks of the file
				while (offset < file.length()) {

					byte fileContent[] = new byte[512];

					// read file in byte form and encode to base 64
					FileInputStream fin = new FileInputStream(file);
					int counter = 0;

					// reads file into array
					// until offset
					fin.read(fileContent, offset, 512);

					// encode bytes to base 64
					byte[] base64 = Base64.encodeBase64(fileContent);

					// add 512 to offset for next chunk
					offset += 512;

					// CHUnK [filename] [last modified] [filesize] [offset]
					// [base64 encoded bytes]
					// sends chunk message to be handled by server
					send("CHUNK " + file.getName() + " " + file.lastModified()
							+ " " + file.length() + " " + offset + " "
							+ base64.toString());

					// System.out.println("CHUNK " + file.getName() + " "
					// + file.lastModified() + " " + file.length() + " "
					// + offset + " " + base64.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static void main(String[] args) {
		try {
			new Client();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}