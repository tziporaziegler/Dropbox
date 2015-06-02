package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileMessage extends Message {
	private String msg;
	private Client client;

	// FILE [filename] [last modified] [filesize]
	private final static Pattern PATTERN = Pattern.compile("FILE\\s\\w+\\s(\\d+\\s){2}");

	public FileMessage(Client client) {
		this.client = client;
	}

	@Override
	public boolean matches(String msg) {
		this.msg = msg;
		Matcher matcher = PATTERN.matcher(msg);
		boolean matches = matcher.matches();
		return matches;
	}

	@Override
	public void perform(FileCache cache, Socket socket) {
		String[] splitMsg = msg.split(" ");

		// add the filename to Client's list of files
		client.addFile(splitMsg[1]);
		
		//TODO are you supposed to now see if need to download/upload anything??
	}
}