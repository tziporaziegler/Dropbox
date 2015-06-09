package dropbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import dropbox.messages.ChunkClient;
import dropbox.messages.DownloadMessage;
import dropbox.messages.ListMessage;

public class Server extends World {
	private ServerSocket serverSocket;
	private ArrayList<Socket> sockets;
	private String syncMsg;

	public Server() throws IOException {
		super("/dropbox_server/");
		serverSocket = new ServerSocket(6003);
		sockets = new ArrayList<Socket>();

		populateValidMsgs(new ChunkClient(this), new DownloadMessage(this), new ListMessage(this));

		while (true) {
			socket = serverSocket.accept();
			new ReaderThread(socket, this).start();
			sockets.add(socket);
		}
	}

	public ArrayList<Socket> getSockets() {
		return sockets;
	}

	@Override
	public void addBytes(long offset, long filesize, String encodedBytes) throws IOException {
		currentRAFile.seek(offset);
		currentRAFile.write(Base64.decodeBase64(encodedBytes));

		currentOffsetTotal += 512;

		if (filesize - currentOffsetTotal < 512) {
			currentOffsetTotal = 0;
			// send SYNC message to all sockets in array except the socket that uploaded the file
			for (Socket currentSock : sockets) {
				//if (currentSock != socket) {
					try {
						System.out.println("Server sending " + syncMsg + " to socket " + currentSock);
						OutputStream out = currentSock.getOutputStream();
						PrintWriter writer = new PrintWriter(out);
						writer.println(syncMsg);
						writer.flush();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				//}
			}
		}
	}

	public void setSyncMsg(String msg) {
		syncMsg = msg;
	}
}