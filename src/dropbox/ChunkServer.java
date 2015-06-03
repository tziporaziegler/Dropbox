package dropbox;

import java.io.FileNotFoundException;
import java.io.IOException;
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
		// 0CHUNK 1[filename] 2[last modified] 3[filesize] 4[offset] 5[base64 encoded bytes]

		long offset = Long.valueOf(splitMsg[4]);
		
		if (offset == 0) {
			try {
				client.newFile(splitMsg[1], Long.valueOf(splitMsg[2]));
			}
			catch (NumberFormatException | FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		try {
			client.addBytes(offset, Long.valueOf(splitMsg[3]), splitMsg[5]);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}