package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesMessage extends Message {
	private String msg;
	private Client client;

	// FILES [number of files]
	private final static Pattern PATTERN = Pattern.compile("FILES\\s\\d+");

	public FilesMessage(Client client) {
		this.client = client;
	}

	@Override
	public boolean matches(String msg) {
		this.msg = msg;
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket) {
		String[] splitMsg = msg.split(" ");
		client.createArray(Integer.valueOf(splitMsg[1]));
	}
}
