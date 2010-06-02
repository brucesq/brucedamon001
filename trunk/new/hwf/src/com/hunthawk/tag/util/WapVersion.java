/**
 * 
 */
package com.hunthawk.tag.util;

/**
 * @author sunquanzhi
 * @since 1.1
 */
public class WapVersion {

	public static String getWapContentType(String template)
	{
		int iVersion = getWapVersion(template);
		String strContentType = "";
		switch(iVersion)
		{
		case 1:
			strContentType = "text/vnd.wap.wml;charset=utf-8";
			break;
		case 2:
			strContentType = "text/html;charset=utf-8";
			break;
		default:
			strContentType  = "text/vnd.wap.wml;charset=utf-8";
			break;
		}
		return strContentType;
	}
	
	/**
	 * <p>Get content support wap version</p>
	 * @param content
	 * @return
	 * @since 1.1.
	 */
	public static int getWapVersion(String content)
	{
		int index ;
		int iVersion = 1;
		index = content.indexOf("<wml");
		if(index == -1)
		{
			index = content.indexOf("<html");
			if(index != -1)
			{
				iVersion = 2;
			}
		}
		return iVersion;
	}
}
