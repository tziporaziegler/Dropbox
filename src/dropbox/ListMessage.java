package dropbox;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListMessage extends Message {

	// LIST
	private final static Pattern PATTERN = Pattern.compile("LIST");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		try {
			File folder = new File(cache.getRoot());

			// get list of all files in root directory
			File[] listOfFiles = folder.listFiles();

			// check if directory is empty
			if (listOfFiles != null) {
				// send message with the number of files
				send("FILES " + listOfFiles.length, socket);

				// for each file message with the data
				for (File file : listOfFiles) {
					if (file.isFile()) {
						send("FILE " + file.getName() + " " + file.lastModified() + " " + file.length() + " ", socket);
					}
				}
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}