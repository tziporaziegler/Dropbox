package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadMessage implements Message {
	private static final long serialVersionUID = 1L;
	private String filename;
	private int offset;
	private int size;

	// TODO create correct pattern
	private final static Pattern PATTERN = Pattern.compile("DOWNLOAD...");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	public DownloadMessage() {
	}

	@Override
	public void perform(FileCache cache, Socket socket) {
		// divide up and create the chunks to send out
	}
}