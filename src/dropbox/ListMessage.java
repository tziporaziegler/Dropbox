package dropbox;

public class ListMessage implements Message {
	private static final long serialVersionUID = 1L;

	// FIXME getting not serializable error when add client
	private Client client;

	public ListMessage(Client client) {
		this.client = client;
	}

	@Override
	public void preform(FileCache cache) {
		String[] filenames = cache.getFileNames();
		// new FilesMessage(fileNames);
		//FIXME not using FileMessage and FilesMessage
		client.postFilenames(filenames);
	}
}