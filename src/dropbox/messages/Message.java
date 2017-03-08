package dropbox.messages;

import java.net.Socket;

import dropbox.FileCache;

public abstract class Message {
	public static final String LONG_PATTERN = "^-?\\d{1,19}$";

	public abstract void perform(FileCache cache, Socket socket, String msg);

	public abstract boolean matches(String msg);
}