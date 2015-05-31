package dropbox;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
	private Client client = this;
	private FileCache cache;

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
		setVisible(true);
	}

	private void send(Message msg) throws IOException {
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
				send(new ListMessage(client));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onObjectRead(Message msg) {
		msg.preform(cache);
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
