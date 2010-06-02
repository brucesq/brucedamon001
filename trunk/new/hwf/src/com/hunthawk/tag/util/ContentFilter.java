/**
 * 
 */
package com.hunthawk.tag.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunquanzhi
 *
 */
public class ContentFilter {
	
	final static char[] prefix = new char[]{'#','n','l','g','a','q'};
	
	static Map<String,String[]> dictionary = new  HashMap<String,String[]>();
	static {
		dictionary.put("#", new String[]{"#"});
		dictionary.put("n", new String[]{"nbsp;"});
		dictionary.put("l", new String[]{"lt;"});
		dictionary.put("g", new String[]{"gt;"});
		dictionary.put("a", new String[]{"amp;","apos;"});
		dictionary.put("q", new String[]{"quot;"});
		
	}
	final static String AMP = "amp;"  ;
	
	private static String parsePreSymbol(char c)
	{
		for(char p : prefix)
		{
			if(p == c)
			{
				return ""+p;
			}
		}
		return null;
	}
	
	private static String parseSymbol(String preSymbol,String content)
	{
		String[] symbols = dictionary.get(preSymbol);
		for(String str : symbols)
		{
			if(content.startsWith(str))
				return str;
		}
		return null;
	}

	public static String filter(String content)
	{
		StringBuilder builder = new StringBuilder();
		int start = 0;
		int index = content.indexOf('&');
		int length = content.length();
		while(index >= start && index < length-1)
		{
			builder.append(content.substring(start, ++index));
			String preSymbol = parsePreSymbol(content.charAt(index));
			String symbol = AMP;
			if(preSymbol != null)
			{
				symbol = parseSymbol(preSymbol,content.substring(index));
				if(symbol != null)
				{
					index += symbol.length();
				}else{
					symbol = AMP;
				}
			}
			builder.append(symbol);
			start = index ;
			index = content.indexOf('&', start);
		}
		
		builder.append(content.substring(start));
		if(index == length -1)
		{
			builder.append(AMP);
		}
		return builder.toString();
	}
	
//	public static String oldfilter(String content)
//	{
//		content = content.replaceAll("&amp;","&");
//		content = content.replaceAll("&","&amp;");
//		content = content.replaceAll ( "&amp;nbsp;" , "&nbsp;" ) ;
//		content = content.replaceAll ( "&amp;lt;" , "&lt;" ) ;
//		content = content.replaceAll ( "&amp;gt;" , "&gt;" ) ;
//		content = content.replaceAll ( "&amp;#" , "&#" ) ;
//		content = content.replaceAll ( "&amp;apos;" , "&apos;" ) ;
//		return content;
//	}
//	public static String makeStr()
//	{
//		String str = "&asdwq&bas&apos;de&lt;asd&112qwe&amp;&gt;&#a;&nbsp;&&";
//		StringBuilder builder = new StringBuilder();
//		int i = 0;
//		while(i++ <1000)
//		{
//			builder.append(str);
//		}
//		return builder.toString();
//	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String str = makeStr();
//		System.out.println(str.length());
//		long start = System.currentTimeMillis();
//		String s = ContentFilter.filter(str);
//		String s2 = ContentFilter.oldfilter(str);
//		System.out.println(s.equals(s2));
//		long end = System.currentTimeMillis();
//		System.out.println(end-start);
//			

	}

}
