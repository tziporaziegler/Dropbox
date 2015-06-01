package dropbox;

import java.io.File;

public class FileCache {
	//FIXME won't automatically generate file on mac since need authentication
	private static final String ROOT = "/rivka/dropbox/";

	public FileCache() {
		// create dropbox directory
		// will only create the directory if it doesn't exist
		new File(ROOT).mkdir();
	}

	public File[] getFiles() {
		File folder = new File(ROOT);
		return folder.listFiles();
	}

	public void addChunk(Chunk chunk) {

	}

	public Chunk getChunk(String username, String filename, int start, int length) {
		return new Chunk(username, filename, start, length);
	}

	public String[] getFileNames() {
		File folder = new File(ROOT);
		File[] listOfFiles = folder.listFiles();
		String[] listOfFileNames = new String[listOfFiles.length];

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				listOfFileNames[i] = file.getName();
			}
		}

		return listOfFileNames;
	}
	
	public String getRoot(){
		return ROOT;
	}
}
