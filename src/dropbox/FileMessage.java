package dropbox;

public class FileMessage implements Message {
	private static final long serialVersionUID = 1L;
	private String name;
	private long lastModified;
	private long size;

	public FileMessage(String name, long lastModified, long size) {
		this.name = name;
		this.lastModified = lastModified;
		this.size = size;
	}

	@Override
	public void preform(FileCache cache) {

	}
}