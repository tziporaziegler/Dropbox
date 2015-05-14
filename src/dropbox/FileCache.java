package dropbox;

import java.io.File;
import java.util.List;

public class FileCache {
	private static final String ROOT = "dropbox/";

	public FileCache() {
		// create dropbox directory
		// will only create the directory if it doesn't exist
		new File(ROOT).mkdir();
	}

	public List<File> getFiles(String username) {
		// TODO
		return null;
	}

	public void addChunk(Chunk chunk) {

	}

	public Chunk getChunk(String username, String filename, int start, int length) {
		return new Chunk(username, filename, start, length);
	}
}
