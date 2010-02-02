package com.hunthawk.reader.pps.usercener;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
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
		//run(args);
		test();
	}

	public static void run(String args[]) throws Exception {
		if (args == null || args.length < 3) {
			System.out.println("请输入正确参数长度  1为密钥、2为加密或解密 、3 为需处理 内容 ");
			System.exit(0);
		}
		// 密钥
		if (args[0].length() != 8) {
			System.out.println("密钥长度需为8位 ");
			System.exit(1);
		}
		Des des=new Des(args[0]);
		// 处理方法
		String mothod = args[1];
		// 原文
		String content = args[2];
		if (mothod.equals("encrypt")) {
			System.out.print(des.encrypt(content));
		} else if (mothod.equals("decrypt")) {
			System.out.print(des.decrypt(content));
		} else {
			System.out.println("请输入正确的处理方式  encrypt 加密，decrypt 解密");
			System.exit(1);
		}

	}

	public static void test() throws Exception {
//		Des des = new Des("4Czt6Dyl");
//		String key=getRandString();
//		System.out.println(key);
//		Des des=new Des(key);
		Des des=new Des();
//		String t = des.encrypt("13341338586");
//		System.out.println(t);
		String t="DD337EBE3368A7A05BB9BCC6A94A9166";
		System.out.println(des.decrypt(t));
		
	}

	public Des(String key) {
		this.key=key;
	}

	public Des() {
	}

	// 加密手机号的key
	//private String key = "yH901zlg";
	private String key="4Czt6Dyl";

	public String encrypt(String original) {
		String text;
		try {
			text = java.net.URLEncoder.encode(original, "UTF-8").toLowerCase();
			return toHexString(encrypt(text, key)).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String decrypt(String cryptograph) {
		try {
			return java.net.URLDecoder.decode(decrypt(cryptograph, key),
					"UTF-8");
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

	//字符串长度 
    private final static int CODE_LENGTH = 8; 
    private static Random random = new Random();  
	   //随机字符范围 
    private final static char[] CHAR_RANGE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
                    'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
                    'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                    'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
                    '8', '9'
    };  

	 private static String getRandString(){ 
	        StringBuilder sb = new StringBuilder(); 
	        for (int i = 0; i < CODE_LENGTH; i++)         
	            sb.append(CHAR_RANGE[random.nextInt(CHAR_RANGE.length)]); 
	        return sb.toString(); 
	} 


}
