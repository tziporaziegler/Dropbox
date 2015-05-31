package dropbox;

import java.io.Serializable;

public interface Message extends Serializable {

	public void preform(FileCache cache);
}
