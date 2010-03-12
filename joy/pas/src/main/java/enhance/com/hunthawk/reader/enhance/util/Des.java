package com.hunthawk.reader.enhance.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Des {

	// 解密数据
	protected static String decrypt(String message, String key)
			throws Exception {

		byte[] bytesrc = convertHexString(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	protected static byte[] encrypt(String message, String key)
			throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

		return cipher.doFinal(message.getBytes("UTF-8"));
	}

	protected static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}

	public static void main(String[] args) throws Exception {
		// String key = "yH901zlg";
		// String value = "13341338586";
		// String jiami = java.net.URLEncoder.encode(value,
		// "UTF-8").toLowerCase();
		//
		// System.out.println("加密数据:" + jiami);
		// String a = toHexString(encrypt(jiami, key)).toUpperCase();
		//
		// System.out.println("加密后的数据为:" + a);
		// String b = java.net.URLDecoder.decode(decrypt(a, key), "UTF-8");
		// System.out.println("解密后的数据:" + b);
		Des des = new Des("yH901zlg");
		String t = des.encrypt("13341338586");
		System.out.println(t);
		System.out.println(des.decrypt(t));

	}

	public Des(String key) {
	}

	public Des() {
	}

	// 加密手机号的key
	static String key_mobileno = "yH901zlg";
//	GlobalConfig
//			.getProperty("sms", "key_mobileno");

	public String encrypt(String original) {
		String text;
		try {
			text = java.net.URLEncoder.encode(original, "UTF-8").toLowerCase();
			return toHexString(encrypt(text, key_mobileno)).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String decrypt(String cryptograph) {
		try {
			return java.net.URLDecoder.decode(
					decrypt(cryptograph, key_mobileno), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}

		return hexString.toString();
	}

}
