package dropbox;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class FileCache {
	// private static final String ROOT = "/dropbox/";
	private String root;

	// when get list of all files in root directory, filter excludes hidden and system files
	private FileFilter filter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			return !file.isHidden();
		}
	};
	
	public FileCache(String root) {
		this.root = root;
		// create dropbox directory
		// will only create the directory if it doesn't exist
		// FIXME won't automatically generate file on mac since need authentication
		new File(root).mkdirs();
	}

	public File[] getFiles() {
		File folder = new File(root);
		return folder.listFiles();
	}

	public ArrayList<String> getFileNames() {
		File[] listOfFiles = new File(root).listFiles(filter);
		ArrayList<String> listOfFileNames = new ArrayList<String>();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				listOfFileNames.add(file.getName());
			}
		}
		return listOfFileNames;
	}

	public String[] getFileNamesArray() {
		File[] listOfFiles = new File(root).listFiles(filter);
		String[] listOfFileNames = new String[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				listOfFileNames[i] = file.getName();
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