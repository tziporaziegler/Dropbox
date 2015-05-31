package dropbox;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileCache {
	private static final String ROOT = "dropbox/";

	public FileCache() {
		// create dropbox directory
		// will only create the directory if it doesn't exist
		new File(ROOT).mkdir();
	}

	public File[] getFiles() {
		// TODO
		File folder = new File(ROOT);
		return folder.listFiles();
	}

	public void addChunk(Chunk chunk) {

	}

	public Chunk getChunk(String username, String filename, int start, int length) {
		return new Chunk(username, filename, start, length);
	}
	
	public String[] getFileNames(){
		File folder = new File(ROOT);
		File[] listOfFiles = folder.listFiles();
		String[] listOfFileNames = new String[listOfFiles.length];

		for (int i = 0; i< listOfFiles.length; i++) {
			File file = listOfFiles[i];
		    if (file.isFile()) {
		    	listOfFileNames[i] = file.getName();
		    }
		}
	
		return listOfFileNames;
	}
}
