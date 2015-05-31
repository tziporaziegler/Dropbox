package dropbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;

public class Server implements ReaderListener {
	private ServerSocket serverSocket;
	private Socket socket;
	private ArrayList<Socket> sockets;
	private LinkedBlockingQueue<Message> msgQueue;
	private FileCache cache;

	public Server() throws IOException {
		serverSocket = new ServerSocket(6003);
		sockets = new ArrayList<Socket>();

		msgQueue = new LinkedBlockingQueue<Message>();
		new WriterThread(msgQueue, sockets).start();

		while (true) {
			socket = serverSocket.accept();
			new ReaderThread(socket, this).start();
			sockets.add(socket);
		}
	}

	@Override
	public void onObjectRead(Message msg) {
		msgQueue.add(msg);
	}

	@Override
	public void onCloseSocket(Socket socket) {
		IOUtils.closeQuietly(socket);
	}
}