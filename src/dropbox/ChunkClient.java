package dropbox;

import java.io.IOException;
import java.net.Socket;

public class ChunkClient extends ChunkMessage {

	public ChunkClient(Server server) {
		super(server);
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String[] splitMsg = msg.split(" ");
		createFile(splitMsg);
		try {
			send(("SYNC " + splitMsg[0] + " " + splitMsg[1] + " " + splitMsg[2]), socket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}