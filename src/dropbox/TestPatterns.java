package dropbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class TestPatterns {

	@Test
	public void testList() {
		Assert.assertTrue(matches("LIST", "LIST"));
	}

	@Test
	public void testFile() {
		Assert.assertTrue(matches("FILE\\s\\w+.\\w+(\\s\\d+){2}", "FILE compileData2.txt 1433305548000 46"));
	}

	@Test
	public void testFiles() {
		Assert.assertTrue(matches("FILES\\s\\d+", "FILES 3"));
	}

	@Test
	public void testDownload() {
		String pattern = "DOWNLOAD\\s\\w+.\\w+";
		Assert.assertTrue(matches(pattern, "DOWNLOAD compileData2.txt"));
		Assert.assertTrue(matches(pattern, "DOWNLOAD Hello.txt"));
	}

	@Test
	public void testChunk() {
		Assert.assertTrue(matches("CHUNK\\s\\w+.\\w+\\s(\\d+\\s){3}\\[B@[A-Za-z0-9]+", "CHUNK compileInstructions.txt 1433305591000 79 0 [B@511603ab"));
	}

	@Test
	public void testSync() {
		Assert.assertTrue(matches("SYNC\\s\\w+.\\w+(\\s\\d+){2}", "SYNC compileData.txt 1433305636000 30"));
	}

	public boolean matches(String pat, String msg) {
		Pattern pattern = Pattern.compile(pat);
		Matcher matcher = pattern.matcher(msg);
		return matcher.matches();
	}
}
