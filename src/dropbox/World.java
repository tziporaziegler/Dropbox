package dropbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
		File currentFile = new File(root + filename);
		currentFile.setLastModified(lastModified);
		currentRAFile = new RandomAccessFile(currentFile, "rw");
	}

	public void addBytes(long offset, long filesize, String encodedBytes) throws IOException {
		currentRAFile.seek(offset);
		currentRAFile.write(Base64.decodeBase64(encodedBytes));

		currentOffsetTotal += 512;

		if (filesize - currentOffsetTotal < 512) {
			currentOffsetTotal = 0;
		}
	}

	public void sendChunkMsg(File file, Socket socket) throws FileNotFoundException, IOException {
		RandomAccessFile randomFile = new RandomAccessFile(file, "r");

		int offset = 0;
		long size = file.length();
		long leftToRead = size;

		while (offset < size) {
			// reads file into array until offset
			int chunkSize = leftToRead - 512 > 0 ? 512 : (int) leftToRead;

			randomFile.seek(offset);
			byte fileContent[] = new byte[chunkSize];
			randomFile.read(fileContent, 0, chunkSize);

			// encode bytes to base 64
			byte[] base64 = Base64.encodeBase64(fileContent);
			// convert the array of base64 bytes to a String
			String string64 = new String(base64);

			// CHUNK [filename] [last modified] [filesize] [offset] [base64 encoded bytes]
			String msg = "CHUNK " + file.getName() + " " + file.lastModified() + " " + size + " " + offset + " " + string64;
			send(msg);
			System.out.println("sending " + msg);

			// add 512 to offset for next chunk
			offset += 512;
			leftToRead -= 512;
		}

		randomFile.close();
	}

	protected void send(String msg) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(out);
		writer.println(msg);
		writer.flush();
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
