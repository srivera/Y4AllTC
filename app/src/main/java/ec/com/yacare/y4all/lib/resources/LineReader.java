package ec.com.yacare.y4all.lib.resources;

import java.io.IOException;
import java.io.Reader;

public class LineReader {
	private Reader in;
	private int bucket = -1;

	public LineReader(Reader in) {
		this.in = in;
	}

	public boolean hasLine() throws IOException {
		if (bucket != -1)
			return true;
		bucket = in.read();
		return bucket != -1;
	}

	// Read a line, removing any /r and /n. Buffers the string
	public String readLine() throws IOException {
		int tmp;
		StringBuffer out = new StringBuffer();
		// Read in data
		while (true) {
			// Check the bucket first. If empty read from the input stream
			if (bucket != -1) {
				tmp = bucket;
				bucket = -1;
			} else {
				tmp = in.read();
				if (tmp == -1)
					break;
			}
			// If new line, then discard it. If we get a \r, we need to look
			// ahead so can use bucket
			if (tmp == '\r') {
				int nextChar = getNextCharacter();
				if (tmp != '\n')
					bucket = nextChar;// Ignores \r\n, but not \r\r
				break;
			} else if (tmp == '\n') {
				break;
			} else {
				// Otherwise just append the character
				out.append((char) tmp);
			}
		}
		return out.toString();
	}

	public int getNextCharacter() throws IOException {

		int a = in.read();
		int t = a;

		if ((t | 0xC0) == t) {
			int b = in.read();
			if (b == 0xFF) { // Check if legal
				t = -1;
			} else if (b < 0x80) { // Check for UTF8 compliancy
				throw new IOException("Bad UTF-8 Encoding encountered");
			} else if ((t | 0xE0) == t) {
				int c = in.read();
				if (c == 0xFF) { // Check if legal
					t = -1;
				} else if (c < 0x80) { // Check for UTF8 compliancy
					throw new IOException("Bad UTF-8 Encoding encountered");
				} else
					t = ((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F);
			} else
				t = ((a & 0x1F) << 6) | (b & 0x3F);
		}

		return t;
	}
}
