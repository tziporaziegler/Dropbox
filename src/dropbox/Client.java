package dropbox;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
	private JList<String> list;
	private JButton listButton;
	private Socket socket;
	private FileCache cache;
	private List<Message> validMsgs;

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

		validMsgs = new ArrayList<Message>();
		validMsgs.add(new ChunkServer());
		validMsgs.add(new FilesMessage());
		validMsgs.add(new FilesMessage());
		validMsgs.add(new SyncMessage());

		setVisible(true);
	}

	private void send(String msg) throws IOException {
		OutputStream out = socket.getOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(msg);
		objOut.flush();
	}

	public void postFilenames(String[] filenames) {
		System.out.println(filenames.length);
		list.setListData(filenames);
	}

	ActionListener listClick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				send("LIST...");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

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

	public static void main(String[] args) {
		try {
			new Client();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
