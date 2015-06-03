package dropbox;

import java.net.Socket;

public class ChunkServer extends ChunkMessage {
	private Client client;

	public ChunkServer(Client client) {
		this.client = client;
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String[] splitMsg = msg.split(" ");

		// send data to client
		// use random access to save bytes to the file starting at the chunk offset location
		// CHUNK [filename] [last modified] [filesize] [offset] [base64 encoded bytes]

		int offset = Integer.valueOf(splitMsg[4]);
		if (offset == 0) {
			client.newFile(splitMsg[1], splitMsg[2]);
		}

		client.addBytes();
	}
}