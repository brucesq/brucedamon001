/**
 * 
 */
package com.hunthawk.reader.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author sunquanzhi
 *
 */
public class TestUcs {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		String file ="D:/1.ucs";// "D:/var/www/html/media/video/0/60000063/e6b2deee9419ac38dc1345e810df44b3_0_hd.ucs";
//		//"D:/var/www/html/media/video/0/60000050/e6b2deee9419ac38dc1345e810df44b3_0_hd.ucs";
//		//"C:/Users/sunquanzhi/Desktop/新建文件夹/新建文件夹/e6b2deee9419ac38dc1345e810df44b3_0.ucs";//
		String cmd = "D:/resource/uc/getplayurl/getplayurl.exe "+file;
		System.out.println(cmd);
		Process process = Runtime.getRuntime().exec(cmd);
		InputStream in = process.getInputStream();  
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
		String line = null;  
		StringBuilder builder = new StringBuilder();
		while (null != (line = reader.readLine())) {  
			builder.append(line);  
		}  
		System.out.println(builder.toString());
		
		
//		cmd = "D:/resource/uc/windows/moducsd.exe "+file+" "+file+ " http://3g.joy.cn/res/video/ " + "http://3g.joy.cn/res/1/video/";
//		System.out.println(cmd);
//		process = Runtime.getRuntime().exec(cmd);
//		in = process.getInputStream();  
//		reader = new BufferedReader(new InputStreamReader(in));  
//		line = null;  
//		builder = new StringBuilder();
//		while (null != (line = reader.readLine())) {  
//			builder.append(line);  
//		}  
//		System.out.println(builder.toString());
		
//		for(int i=0;i<=60;i++){
//			if(i<10){
//				System.out.println("map.put(/"0"+i+"/","+i+");");
//			}else{
//				System.out.println("map.put(/""+i+"/","+i+");");
//			}
//			
//		}
//		
//		String str = ",,,,,,";
//		System.out.println(str.split(",").length);
	}

}
