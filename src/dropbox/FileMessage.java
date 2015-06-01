package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import java.io.File;

public class FileMessage implements Message {
	private static final long serialVersionUID = 1L;
	private String fileName;
	private long lastModified;
	private int fileSize;

	// TODO create correct pattern
	private final static Pattern PATTERN = Pattern.compile("FILE...");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	public FileMessage(String fileName, long lastModified, int fileSize) {
		// super(fileName);
		this.fileName = fileName;
		this.lastModified = lastModified;
		this.fileSize = fileSize;
	}

	@Override
	public void perform(FileCache cache, Socket socket) {
		// TODO Auto-generated method stub

	}
}
