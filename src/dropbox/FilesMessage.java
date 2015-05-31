package dropbox;

import java.io.File;

public class FilesMessage implements Message {
	private static final long serialVersionUID = 1L;
	private int numFiles;
	private File[] fileNames;

	public FilesMessage(File[] fileNames) {
		this.fileNames = fileNames;
		numFiles = fileNames.length;
	}

	@Override
	public void preform(FileCache cache) {
		for (File file : fileNames) {
			new FileMessage(file.getName(), file.lastModified(), file.length());
		}
	}
}