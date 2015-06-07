package dropbox;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChunkClient extends ChunkMessage {

	private ArrayList<Socket> sockets;

	public ChunkClient(Server server) {
		super(server);
		sockets = server.getSockets();
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String[] splitMsg = msg.split(" ");
		createFile(splitMsg);
		// send SYNC message to all sockets in arrat
		for (Socket currentSock : sockets) {
			if (currentSock != socket) {
				try {
					send(("SYNC " + splitMsg[1] + " " + splitMsg[2] + " " + splitMsg[3]), currentSock);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}