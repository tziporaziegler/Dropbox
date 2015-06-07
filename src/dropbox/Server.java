package dropbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends World {
	private ServerSocket serverSocket;
	private ArrayList<Socket> sockets;
	private LinkedBlockingQueue<Message> msgQueue;

	public Server() throws IOException {
		super("/dropbox_server/");
		serverSocket = new ServerSocket(6003);
		sockets = new ArrayList<Socket>();

		msgQueue = new LinkedBlockingQueue<Message>();
		new WriterThread(msgQueue, sockets).start();

		populateValidMsgs(new ChunkClient(this), new DownloadMessage(this), new ListMessage());

		while (true) {
			socket = serverSocket.accept();
			new ReaderThread(socket, this).start();
			sockets.add(socket);
		}
	}
	
	public ArrayList<Socket> getSockets(){
		return sockets;
	}

}