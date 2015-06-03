package dropbox;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class FileCache {
	// private static final String ROOT = "/dropbox/";
	private String root;

	public FileCache(String root) {
		this.root = root;
		// create dropbox directory
		// will only create the directory if it doesn't exist
		// FIXME won't automatically generate file on mac since need authentication
		new File(root).mkdir();
	}

	public File[] getFiles() {
		File folder = new File(root);
		return folder.listFiles();
	}

	/*public void addChunk(Chunk chunk) {

	}

	public Chunk getChunk(String username, String filename, int start, int length) {
		return new Chunk(username, filename, start, length);
	}*/

	public ArrayList<String> getFileNames() {
		File folder = new File(root);

		// get list of all files in root directory, excluding hidden and system files
		File[] listOfFiles = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !file.isHidden();
			}
		});

		ArrayList<String> listOfFileNames = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				listOfFileNames.add(file.getName());
			}
		}

		return listOfFileNames;
	}

	public String getRoot() {
		return root;
	}

	public File getFile(String fileName) {
		File[] files = getFiles();
		for (File x : files) {
			if (x.getName().equals(fileName)) {
				return x;
			}
		}
		return null;
	}
}
