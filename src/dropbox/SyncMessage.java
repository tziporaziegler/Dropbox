package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyncMessage extends Message {

	// SYNC [filename] [last modified] [filesize]
	private final static Pattern PATTERN = Pattern.compile("SYNC\\s\\w+\\s(\\d+\\s){2}");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket) {

	}
}