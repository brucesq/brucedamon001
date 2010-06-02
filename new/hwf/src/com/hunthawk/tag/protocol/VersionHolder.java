/**
 * 
 */
package com.hunthawk.tag.protocol;


/**
 * @author sunquanzhi
 *
 */
public class VersionHolder {
	private static ThreadLocal<Version> contain = new ThreadLocal<Version>();
	
	public static Version setVersion(String template)
	{
		Version version = VersionUtil.getVersion(template);
		setVersion(version);
		return version;
	}
	
	public static void setVersion(Version version)
	{
		contain.set(version);
	}
	public static Version getVersion()
	{
		return contain.get();
	}
	public static void clear()
	{
		contain.remove();
	}

}
