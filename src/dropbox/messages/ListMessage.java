package dropbox.messages;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dropbox.FileCache;
import dropbox.World;

public class ListMessage extends Message {
	// LIST
	private static final Pattern PATTERN = Pattern.compile("LIST");
	private World world;

	public ListMessage(World world) {
		this.world = world;
	}

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
				world.send(filesMsg, socket);

				// for each file message with the data
				for (File file : listOfFiles) {
					if (file.isFile()) {
						String fileMsg = "FILE " + file.getName() + " " + file.lastModified() + " " + file.length();
						world.send(fileMsg, socket);
					}
				}
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}