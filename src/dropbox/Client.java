package dropbox;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;

public class Client extends JFrame implements ReaderListener {
	private static final long serialVersionUID = 1L;
	private Socket socket;
	private FileCache cache = new FileCache();
	private List<Message> validMsgs;

	private String[] filenames;
	private JList<String> list;
	private JButton listButton;
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

		list = new JList<String>();
		add(list, BorderLayout.CENTER);

		socket = new Socket("localhost", 6003);
		new ReaderThread(socket, this).start();

		// create an ArrayList of all valid messages
		// used when determining what kind of message was read in by checking if matches any of the Patterns
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

	// FILES message creates new array to hold all FILE messages that will follow
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
	public void onLineRead(String line) {
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
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static void main(String[] args) {
		try {
			new Client();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}