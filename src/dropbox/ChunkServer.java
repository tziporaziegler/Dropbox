package dropbox;

import java.net.Socket;

public class ChunkServer extends ChunkMessage {

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		// TODO if first Chunk, create new File
		// send data to client
		// use random access to save bytes to the file starting at the chunk offset location
	}
}