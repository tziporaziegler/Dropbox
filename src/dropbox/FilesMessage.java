package dropbox;

import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesMessage implements Message {
	private static final long serialVersionUID = 1L;
	private ArrayList<FileMessage> fileMessages;

	// TODO create correct pattern
	private final static Pattern PATTERN = Pattern.compile("FILES...");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket) {

	}
}
