package dropbox;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyncMessage extends Message {

	private String[] splitMsg;

	// SYNC [filename] [last modified] [filesize]
	private final static Pattern PATTERN = Pattern
			.compile("SYNC\\s\\w+\\s(\\d+\\s){2}");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		splitMsg = msg.split(" ");
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket) {
		String fileName = splitMsg[0];

		// get list of all files in client root directory and 
		File folder = new File(cache.getRoot());
		File[] listOfFiles = folder.listFiles();

		try {
			for (File x : listOfFiles) {

				// get the file from the server cache
				FileCache serverCache = new FileCache("/dropbox_server/");
				File file = serverCache.getFile(fileName);

				//see if this file
				// exists in the clients directory
				if (x.getName().equals(fileName)) {
					// file is found so now compare when last modified from
					// files in server's cache
					if (x.lastModified() < file.lastModified()) {
						// now need to send download msg to server
						// DOWNLOAD [filename] [offset] [chunk size]

						send("DOWNLOAD " + fileName + " " + 0
								+ (file.length() >= 512 ? 512 : file.length()),
								socket);

					}
				}
				// file is not on clients dir so send download msg
				else {
					send("DOWNLOAD " + fileName + " " + 0
							+ (file.length() >= 512 ? 512 : file.length()),
							socket);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}