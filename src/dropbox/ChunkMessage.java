package dropbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ChunkMessage extends Message {
	protected String[] splitMsg;

	// CHUCK [filename] [last modified] [filesize] [offset] [base64 encoded bytes]
	protected final static Pattern PATTERN = Pattern.compile("CHUNK\\s\\w+\\s(\\d+\\s){3}[A-Za-z0-9=-]");

	@Override
	public boolean matches(String msg) {
		Matcher matcher = PATTERN.matcher(msg);
		boolean b = matcher.matches();
		//FIXME ?Split here or in perform - is it okay to even store it here??
		if (b) {
			splitMsg = msg.split(" ");
		}
		return b;
	}
}