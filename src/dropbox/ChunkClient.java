package dropbox;

import java.io.IOException;
import java.net.Socket;

public class ChunkClient extends ChunkMessage {

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String [] splitMsg = msg.split(" ");
		try {
			send(("SYNC " + splitMsg[0] + " " + splitMsg[1] + " " + splitMsg[2]), socket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}