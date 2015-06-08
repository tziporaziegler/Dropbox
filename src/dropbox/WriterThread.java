package dropbox;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import dropbox.messages.Message;

public class WriterThread extends Thread {
	/*private LinkedBlockingQueue<Message> msgQueue;
	private ArrayList<Socket> sockets;
	private Message currentMsg;

	public WriterThread(LinkedBlockingQueue<Message> msgQueue, ArrayList<Socket> sockets) {
		this.msgQueue = msgQueue;
		this.sockets = sockets;
	}

	@Override
	public void run() {
		while (true) {
			try {
				currentMsg = msgQueue.take();
				for (Socket socket : sockets) {
					OutputStream out = socket.getOutputStream();
					ObjectOutputStream objOut = new ObjectOutputStream(out);
					objOut.writeObject(currentMsg);
					objOut.flush();
				}
			}
			catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}*/
}