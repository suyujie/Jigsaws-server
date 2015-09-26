package gamecore.security;

import java.security.MessageDigest;

public class MD5 {

	protected static MD5 md5 = new MD5();

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 获取唯一实例.
	 * 
	 * @return
	 */
	public static MD5 getInstance() {
		return md5;
	}

	public static String encode(String msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] results = md.digest(msg.getBytes());
			// 将得到的字节数组变成字符串返回
			String resultString = byteArrayToHexString(results);
			return resultString.toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/** */
	/** 将一个字节转化成十六进制形式的字符串 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

//	public static void main(String agrs[]) {
//		System.out.print(MD5.getInstance().encode("suiyujie"));
//	}
}