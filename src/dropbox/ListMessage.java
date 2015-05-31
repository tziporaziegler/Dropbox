package dropbox;

import java.net.Socket;

public class ListMessage implements Message {
	private static final long serialVersionUID = 1L;

	@Override
	public void preform(FileCache cache, Socket socket) {
		String[] filenames = cache.getFileNames();
		// new FilesMessage(fileNames);
		//FIXME not using FileMessage and FilesMessage
		postFilenames(filenames);
	}
}