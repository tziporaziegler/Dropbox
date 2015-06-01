package dropbox;

import java.io.Serializable;
import java.net.Socket;

public interface Message extends Serializable {
	
	void perform(FileCache cache, Socket socket);
	
	boolean matches(String msg);
}
