package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadMessage extends Message {

	// DOWNLOAD [filename] [offset] [chunk size]
	private final static Pattern PATTERN = Pattern.compile("DOWNLOAD\\s\\w+\\s\\d+" + Message.LONG_PATTERN);

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket) {
		// divide up and create the chunks to send out
	}
}