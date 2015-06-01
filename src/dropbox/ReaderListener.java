package dropbox;

import java.net.Socket;

public interface ReaderListener {

	void onLineRead(String line, Socket socket);

	void onCloseSocket(Socket socket);

}
