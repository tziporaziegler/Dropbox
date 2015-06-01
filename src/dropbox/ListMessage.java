package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListMessage implements Message {
	private static final long serialVersionUID = 1L;

	// TODO create correct pattern
	private final static Pattern PATTERN = Pattern.compile("LIST...");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket) {
		String[] filenames = cache.getFileNames();
		// send Files
		// send list of File messages
	}
}