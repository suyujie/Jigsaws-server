package gamecore.util;

import gamecore.io.FastByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	/**
	 * 将InputStream中的字节全部读出。
	 */
	public static byte[] drain(InputStream in) throws IOException {
		@SuppressWarnings("resource")
		FastByteArrayOutputStream out = new FastByteArrayOutputStream();

		byte[] buf = new byte[512];
		int bytesRead;
		while ((bytesRead = in.read(buf)) != -1) {
			out.write(buf, 0, bytesRead);
		}

		return out.toByteArray();
	}

}
