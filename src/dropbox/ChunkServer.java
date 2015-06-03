package dropbox;

import java.net.Socket;

public class ChunkServer extends ChunkMessage {

	public ChunkServer(Client client) {
		super(client);
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String[] splitMsg = msg.split(" ");
		createFile(splitMsg);
	}
}