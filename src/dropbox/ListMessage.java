package dropbox;

public class ListMessage implements Message{

	@Override
	public void preform(FileCache cache) {
		String[] fileName = cache.getFileNames();
	}

}
