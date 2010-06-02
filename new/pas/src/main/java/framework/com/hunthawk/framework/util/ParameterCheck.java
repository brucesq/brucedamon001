/**
 * 
 */
package com.hunthawk.framework.util;

/**
 * @author sunquanzhi
 *
 */
public final class ParameterCheck {

	public static boolean isNullOrEmpty(String str)
	{
		if(str == null || str.trim().length() == 0)
		{
			return true;
		}
		return false;
	}
}
