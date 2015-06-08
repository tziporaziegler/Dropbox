package dropbox.messages;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dropbox.FileCache;

public class SyncMessage extends Message {
	// SYNC 1[filename] 2[last modified] 3[filesize]
	private final static Pattern PATTERN = Pattern.compile("SYNC\\s\\w+\\.\\w+(\\s\\d+){2}");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String[] splitMsg = msg.split(" ");
		String fileName = splitMsg[1];
		long lastModified = Long.valueOf(splitMsg[2]);

		// get list of all files in client root directory and
		File folder = new File(cache.getRoot());
		File[] listOfFiles = folder.listFiles();

		boolean found = false;

		try {
			for (File clientFile : listOfFiles) {
				// see if this file exists in the clients directory
				if (clientFile.getName().compareTo(fileName) == 0) {
					// file is found so now compare when last modified from
					// files in server's cache
					found = true;
					if (clientFile.lastModified() < lastModified) {
						// now need to send download msg to server
						// DOWNLOAD [filename] [offset] [chunk size]
						send("DOWNLOAD " + fileName, socket);
					}
					break;
				}
			}
			// file is not on clients dir so send download msg
			if (!found) {
				send("DOWNLOAD " + fileName, socket);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}