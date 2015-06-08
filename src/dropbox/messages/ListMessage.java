package dropbox.messages;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dropbox.FileCache;

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

			// get list of all files in root directory, excluding hidden and system files
			File[] listOfFiles = folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return !file.isHidden();
				}
			});

			// check if directory is empty
			if (listOfFiles != null) {
				// send message with the number of files
				String filesMsg = "FILES " + listOfFiles.length;
				send(filesMsg, socket);
				System.out.println("List sending " + filesMsg);

				// for each file message with the data
				for (File file : listOfFiles) {
					if (file.isFile()) {
						String fileMsg = "FILE " + file.getName() + " " + file.lastModified() + " " + file.length();
						send(fileMsg, socket);
						System.out.println("List sending " + fileMsg);
					}
				}
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}