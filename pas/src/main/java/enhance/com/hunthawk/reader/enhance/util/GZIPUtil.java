/**
 * 
 */
package com.hunthawk.reader.enhance.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author BruceSun
 * 
 */
public class GZIPUtil {

	public static byte[] gzip(byte[] bs) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		GZIPOutputStream gout = new GZIPOutputStream(byteOut);
		gout.write(bs);
		gout.close();
		byte[] gbyte = byteOut.toByteArray();
		byteOut.close();
		return gbyte;

	}

	public static byte[] ungzip(byte[] bs) throws IOException {
		ByteArrayInputStream byteArray = new ByteArrayInputStream(bs);
		GZIPInputStream gzin = new GZIPInputStream(byteArray);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] buf=new byte[1024];
		int num;
		while ((num=gzin.read(buf,0,buf.length)) != -1)
		{
			byteOut.write(buf,0,num);
		} 
		byte[] gbyte = byteOut.toByteArray();
		gzin.close();
		byteArray.close();
		byteOut.close();
		return gbyte;
	}

	public static void main(String[] args) throws IOException {
		String str = "asssaasdsadasdasd123123123123211111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
				"1233333333ÖÐÎÄ3333333333333333333333333333333333333333333333333333333333333333333333" +
				"1233333333333333333333333333333333333333333333333333333333333333" +
				"12333333333333333333333333333333333333333333333";
		byte[] b = str.getBytes();
		System.out.println(b.length);
		byte[] gb = gzip(b);
		System.out.println(gb.length);
	
		byte[] gi = ungzip(gb);
		System.out.println(new String(gi)); 
	}
}