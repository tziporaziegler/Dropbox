package dropbox;

import java.io.Serializable;
import java.net.Socket;

public interface Message extends Serializable {

	void preform(FileCache cache, Socket socket);
	
	void boolean matched(String msg);
	
}
