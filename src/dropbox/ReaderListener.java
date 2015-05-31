package dropbox;

import java.net.Socket;

public interface ReaderListener {

	void onObjectRead(Message msg);

	void onCloseSocket(Socket socket);

}
