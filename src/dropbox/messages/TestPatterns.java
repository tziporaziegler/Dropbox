package dropbox.messages;

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
		String pattern = "FILE\\s\\w+\\.\\w+(\\s\\d+){2}";
		Assert.assertTrue(matches(pattern, "FILE compileData2.txt 1433305548000 46"));
		Assert.assertTrue(matches(pattern, "FILE Hello.txt 1234 11"));
	}

	@Test
	public void testFiles() {
		Assert.assertTrue(matches("FILES\\s\\d+", "FILES 3"));
	}

	@Test
	public void testDownload() {
		String pattern = "DOWNLOAD\\s\\w+\\.\\w+";
		Assert.assertTrue(matches(pattern, "DOWNLOAD compileData2.txt"));
		Assert.assertTrue(matches(pattern, "DOWNLOAD Hello.txt"));
	}

	@Test
	public void testChunk() {
		String pattern = "CHUNK\\s\\w+\\.\\w+\\s(\\d+\\s){3}[A-Za-z0-9+\\/=-]+";
		Assert.assertFalse(matches(pattern, "CHUNK compileInstructions.txt 1433305591000 79 0 [B@511603ab"));
		Assert.assertTrue(matches(pattern, "CHUNK Hello.txt 1234 11 0 SGVsbG8gV29ybGQ="));
		Assert.assertTrue(matches(
				pattern,
				"CHUNK compileInstructions.txt 1433356368000 1191 0 aGVsbG9faG93X2FyZXlvdWFzZGlvbmRmdG9kYXloZWxsb19ob3dfYXJleW91YXNkaW9uZGZ0b2RheWhlbGxvX2hvd19hcmV5b3Vhc2Rpb25kZnRvZGF5aGVsbG9faG93X2FyZXlvdWFzZGlvbmRmdG9kYXloZWxsb19ob3dfYXJleW91YXNkaW9uZGZ0b2RheWhlbGxvX2hvd19hcmV5b3Vhc2Rpb25kZnRvZGF5aGVsbG9faG93X2FyZXlvdWFzZGlvbmRmdG9kYXloZWxsb19ob3dfYXJleW91YXNkaW9uZGZ0b2RheWhlbGxvX2hvd19hcmV5b3Vhc2Rpb25kZnRvZGF5aGVsbG9faG93X2FyZXlvdWFzZGlvbmRmdG9kYXloZWxsb19ob3dfYXJleW91YXNkaW9uZGZ0b2RheWhlbGxvX2hvd19hcmV5b3Vhc2Rpb25kZnRvZGF5aGVsbG9faG93X2FyZXlvdWFzZGlvbmRmdG9kYXloZWxsb19ob3dfYXJleW91YXNkaW9uZGZ0b2RheWhlbGxvX2hvd19hcmV5b3Vhc2Rpb25kZnRvZGF5aGVsbG9faG93X2FyZXlvdWFzZGlvbmRmdG9kYXloZWxsb19ob3dfYXJleW91YXNkaW9uZGZ0b2RheWhlbGxvX2hvd19hcmV5b3Vhc2Q="));
	}

	@Test
	public void testSync() {
		String pattern = "SYNC\\s\\w+\\.\\w+(\\s\\d+){2}";
		Assert.assertTrue(matches(pattern, "SYNC compileData.txt 1433305636000 30"));
		Assert.assertTrue(matches(pattern, "SYNC compileInstructions2.txt 1433734571000 1546"));
		Assert.assertFalse(matches(pattern, "SYNC compileData 1433305636000 30"));
		Assert.assertFalse(matches(pattern, "SYNC compileData.txt 1433305636000 30 "));

	}

	public boolean matches(String pat, String msg) {
		Pattern pattern = Pattern.compile(pat);
		Matcher matcher = pattern.matcher(msg);
		return matcher.matches();
	}
}
