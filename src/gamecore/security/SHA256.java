package gamecore.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

	public static byte[] Encrypt(byte[] bt) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(bt);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}