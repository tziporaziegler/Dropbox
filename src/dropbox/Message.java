package dropbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Message {
	public final static String LONG_PATTERN = "^-?\\d{1,19}$";
	
	abstract void perform(FileCache cache, Socket socket, String msg);

	abstract boolean matches(String msg);

	protected void send(String msg, Socket socket) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(out);
		writer.println(msg);
		writer.flush();
	}
}
