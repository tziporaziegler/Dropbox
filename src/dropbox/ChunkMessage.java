package dropbox;

public class ChunkMessage implements Message {
	private static final long serialVersionUID = 1L;
	private String filename;
	private long lastModified;
	private long filesize;
	private int offset;
	private String base64EncodedBytes;

	@Override
	public void preform(FileCache cache) {

	}

}