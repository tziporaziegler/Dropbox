package dropbox.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import dropbox.FileCache;

public abstract class Message {
	public final static String LONG_PATTERN = "^-?\\d{1,19}$";

	public abstract void perform(FileCache cache, Socket socket, String msg);

	public abstract boolean matches(String msg);

	protected void send(String msg, Socket socket) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(out);
		writer.println(msg);
		writer.flush();
	}
}