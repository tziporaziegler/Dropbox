package dropbox;

//import java.io.File;

public class FileMessage{

	private String fileName;
	private long lastModified;
	private int fileSize;
	
	public FileMessage (String fileName, long lastModified, int fileSize){
		//super(fileName);
		this.fileName = fileName;
		this.lastModified = lastModified;
		this.fileSize = fileSize;
	}
	
}
