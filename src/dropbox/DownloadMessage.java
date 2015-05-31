package dropbox;

public class DownloadMessage implements Message {
	private static final long serialVersionUID = 1L;
	private String filename;
	private int offset;
	private int size;

	public DownloadMessage() {
	}

	@Override
	public void preform(FileCache cache) {
		// TODO is the message breaking up file or client??
	}
}