/**
 * 
 */
package com.hunthawk.tag;

/**
 * @author sunquanzhi
 *
 */
public class TagUtil {

	public static String makeTag(String tagName)
	{
		return TagConstants.PRE_TAG_SUFFIX + tagName + TagConstants.END_TAG_SUFFIX;
	}
	
	public static boolean isNonBlank(String str)
	{
		return !isBlank(str);
	}
	public static boolean isBlank(String str)
	{
		if(str == null || "".equals(str))
		{
			return true;
		}
		return false;
	}
}
