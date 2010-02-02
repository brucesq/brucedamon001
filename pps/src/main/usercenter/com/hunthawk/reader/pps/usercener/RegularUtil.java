/**
 * 
 */
package com.hunthawk.reader.pps.usercener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuxh
 *
 */
public class RegularUtil {
	/**
	 * @param phoneNum
	 * @return
	 * @throws Exception
	 */
	public static String checkPhoneNum(String phoneNum) throws Exception {

		Pattern p1 = Pattern.compile("^(13[0-9]|15[0|3|6|7|8|9]|18[8|9])\\d{8}");
		Matcher m1 = p1.matcher(phoneNum);
		if (m1.matches()) {
			return phoneNum;
		} else {
			throw new Exception("The format of phoneNum " + phoneNum
					+ "  is not correct!Please correct it");
		}
	}
	
	public static void main(String[] args){
		try {
			System.out.println(checkPhoneNum("13987"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
