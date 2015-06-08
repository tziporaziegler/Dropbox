package dropbox.messages;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import dropbox.FileCache;
import dropbox.Server;

public class ChunkClient extends ChunkMessage {
	private Server server;
	private ArrayList<Socket> sockets;

	public ChunkClient(Server server) {
		super(server);
		this.server = server;
		
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		// CHUNK 1[filename] 2[last modified] 3[filesize] [offset] [base64 encoded bytes]
		String[] splitMsg = msg.split(" ");
		server.setSyncMsg("SYNC " + splitMsg[1] + " " + splitMsg[2] + " " + splitMsg[3]);
		createFile(splitMsg);
		
//		sockets = server.getSockets();
//		// send SYNC message to all sockets in arrat
//		for (Socket currentSock : sockets) {
//			//if (currentSock != socket) {
//				try {
//					send(("SYNC " + splitMsg[1] + " " + splitMsg[2] + " " + splitMsg[3]), currentSock);
//				}
//				catch (IOException e) {
//					e.printStackTrace();
//				}
//			//}
	//	}
	
	}
}