package gamecore.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/** HTTP 实用函数库。
 */
public final class HttpUtils {

	private final static Random sRandom = new Random(System.currentTimeMillis());

	// 字母表
	private static final char[] ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '0' };

	/** 读取 Http 请求数据流。
	 */
	public static byte[] readRequestStream(HttpServletRequest request) throws IOException {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			InputStream is = request.getInputStream();
			if (null != is) {
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = is.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (null != out) {
					out.close();
				}
			} catch (Exception e) {
				// Nothing
			}
		}
		return out.toByteArray();
	}

	/** 读取 Http 请求数据流并转为 JSON 对象。
	 * @throws JSONException 
	 */
	public static JSONObject readRequestStreamAsJSON(HttpServletRequest request) throws IOException, JSONException {
		byte[] data = readRequestStream(request);
		return JSONObject.parseObject(new String(data, Charset.forName("UTF-8")));
	}

	/** 生成 Session ID 。
	 */
	public static String generateSessionID() {
		final int length = 32;
		char[] buf = new char[length];
		int max = ALPHABET.length - 1;
		int min = 0;
		int index = 0;
		for (int i = 0; i < length; ++i) {
			index = sRandom.nextInt(max) % (max - min + 1) + min;
			buf[i] = ALPHABET[index];
		}
		return new String(buf);
	}

	/** 解析 URL 参数。
	 */
	public static Map<String, String> parseAsQuery(byte[] data) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;
		String strUrlParam = new String(data, Charset.forName("UTF-8"));

		// 每个键值为一组
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
			} else {
				if (arrSplitEqual[0].length() != 0) {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}

}
