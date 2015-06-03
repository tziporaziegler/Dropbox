package dropbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ChunkMessage extends Message {
	
	// CHUNK [filename] [last modified] [filesize] [offset] [base64 encoded bytes]
	protected final static Pattern PATTERN = Pattern.compile("CHUNK\\s\\w+.\\w+\\s(\\d+\\s){3}\\[B@[A-Za-z0-9]+");
	protected World world;

	public ChunkMessage(World world) {
		this.world = world;
	}

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		return matcher.matches();
	}

	protected void createFile(String[] splitMsg) {
		// 0CHUNK 1[filename] 2[last modified] 3[filesize] 4[offset] 5[base64 encoded bytes]
		long offset = Long.valueOf(splitMsg[4]);
		if (offset == 0) {
			try {
				world.newFile(splitMsg[1], Long.valueOf(splitMsg[2]));
			}
			catch (NumberFormatException | FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		try {
			world.addBytes(offset, Long.valueOf(splitMsg[3]), splitMsg[5]);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}