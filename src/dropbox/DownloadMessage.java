package dropbox;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadMessage extends Message {

	// DOWNLOAD [filename]
	private final static Pattern PATTERN = Pattern.compile("DOWNLOAD\\s\\w+\\.\\w+");
	private World world;

	public DownloadMessage(Server server) {
		world = server;
	}

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String[] splitMsg = msg.split(" ");
		File file = cache.getFile(splitMsg[1]);
		try {
			world.sendChunkMsg(file, socket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}