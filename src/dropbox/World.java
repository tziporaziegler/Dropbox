package dropbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class World implements ReaderListener {
	protected Socket socket;
	protected String root;
	protected FileCache cache;
	protected List<Message> validMsgs;

	// store file in middle of writing to
	private RandomAccessFile currentRAFile;
	private int currentOffsetTotal;

	public World(String root) {
		this.root = root;
		cache = new FileCache(root);
	}

	protected void populateValidMsgs(Message... msgs) {
		// create an ArrayList of all valid messages
		// used when determining what kind of message was read in by checking if matches any of the Patterns
		validMsgs = new ArrayList<Message>();
		for (Message m : msgs) {
			validMsgs.add(m);
		}
	}

	public void newFile(String filename, long lastModified) throws FileNotFoundException {
		File currentFile = new File(root + filename + ".txt");
		currentFile.setLastModified(lastModified);
		currentRAFile = new RandomAccessFile(currentFile, "rw");
	}

	public void addBytes(long offset, long filesize, String encodedBytes) throws IOException {
		currentRAFile.seek(offset);
		currentRAFile.write(Base64.decodeBase64(encodedBytes));

		currentOffsetTotal += 512;

		if (filesize - currentOffsetTotal < 512) {
			currentRAFile.close();
			currentOffsetTotal = 0;
		}
	}

	// Whenever a new line is read in, determine what the Message is by comparing it to all the valid Message Patterns
	@Override
	public void onLineRead(String line, Socket socket) {
		for (Message msg : validMsgs) {
			if (msg.matches(line)) {
				msg.perform(cache, socket, line);
				break;
			}
		}
	}

	@Override
	public void onCloseSocket(Socket socket) {
		IOUtils.closeQuietly(socket);
	}
}
