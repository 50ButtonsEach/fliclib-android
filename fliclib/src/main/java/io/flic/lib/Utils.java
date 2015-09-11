package io.flic.lib;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Emil on 2015-09-06.
 */
class Utils {
	public static byte[] hexToBytes(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
				+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static MessageDigest createSHA256() {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md;
	}
}
