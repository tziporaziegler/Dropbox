package dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class DownloadMessage extends Message {

	// DOWNLOAD [filename]
	private final static Pattern PATTERN = Pattern.compile("DOWNLOAD\\s\\w+");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	@Override
	public void perform(FileCache cache, Socket socket, String msg) {
		String[] splitMsg = msg.split(" ");
		File file = cache.getFile(splitMsg[1]);
		try {
			sendChunkMsg(file, socket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendChunkMsg(File file, Socket socket) throws FileNotFoundException, IOException {
		int offset = 0;
		long size = file.length();

		while (offset < size) {
			byte fileContent[] = new byte[512];

			// read file in byte form
			FileInputStream fin = new FileInputStream(file);

			// reads file into array until offset
			fin.read(fileContent, offset, 512);

			// encode bytes to base 64
			byte[] base64 = Base64.encodeBase64(fileContent);

			// CHUNK [filename] [last modified] [filesize] [offset] [base64 encoded bytes]
			// send chunk message to be handled by server
			send("CHUNK " + file.getName() + " " + file.lastModified() + " " + size + " " + offset + " " + base64.toString(), socket);

			// add 512 to offset for next chunk
			offset += 512;

			fin.close();
		}
	}
}