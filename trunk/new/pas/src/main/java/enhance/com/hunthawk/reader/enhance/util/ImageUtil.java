/**
 * 
 */
package com.hunthawk.reader.enhance.util;

/**
 * @author BruceSun
 *
 */
public class ImageUtil {

	public static String getSuffix(String file){
		String suffix = file.substring(file.indexOf(".")+1).toLowerCase();
		if(suffix.equals("jpeg")){
			return "jpg";
		}
		return suffix;
	}
	
	public static String getPrefix(String file){
		return file.substring(0, file.indexOf("."));
	}
	
	public static void main(String[] args){
		System.out.println(getPrefix("a.jpg"));
		System.out.println(getSuffix("a.Jpeg"));
	}
}
