package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadMessage extends Message {

	// DOWNLOAD [filename]
	private final static Pattern PATTERN = Pattern.compile("DOWNLOAD\\s\\w+");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		// TODO divide up and create the chunks to send out
	}
}