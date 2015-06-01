package dropbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;

public class Server implements ReaderListener {
	private ServerSocket serverSocket;
	private Socket socket;
	private ArrayList<Socket> sockets;
	private LinkedBlockingQueue<Message> msgQueue;
	private FileCache cache = new FileCache();
	private List<Message> validMsgs;

	public Server() throws IOException {
		serverSocket = new ServerSocket(6003);
		sockets = new ArrayList<Socket>();

		msgQueue = new LinkedBlockingQueue<Message>();
		new WriterThread(msgQueue, sockets).start();

		validMsgs = new ArrayList<Message>();
		validMsgs.add(new ChunkClient());
		validMsgs.add(new DownloadMessage());
		validMsgs.add(new ListMessage());

		while (true) {
			socket = serverSocket.accept();
			new ReaderThread(socket, this).start();
			sockets.add(socket);
		}
	}

	@Override
	public void onCloseSocket(Socket socket) {
		IOUtils.closeQuietly(socket);
	}

	@Override
	public void onLineRead(String line) {
		for (Message msg : validMsgs) {
			if (msg.matches(line)) {
				msg.perform(cache, socket);
				break;
			}
		}
	}

	public static void main(String[] args) {
		try {
			new Server();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}