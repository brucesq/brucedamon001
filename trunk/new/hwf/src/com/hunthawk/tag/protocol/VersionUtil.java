/**
 * 
 */
package com.hunthawk.tag.protocol;

/**
 * <p>π§æﬂ¿‡</p>
 * @author sunquanzhi
 *
 */
public class VersionUtil {

	public static Version getVersion(String template)
	{
		int iVersion = getWapVersion(template);
		Version version = new Version();
		switch(iVersion)
		{
		case 1:
			version.setContentType(Version.CONTENT_TYPE_1);
			version.setProtocol(Version.WAP_VERSION_1);
			version.setSuffix(Version.SUFFIX_1);
			break;
		case 2:
			version.setContentType(Version.CONTENT_TYPE_2);
			version.setProtocol(Version.WAP_VERSION_2);
			version.setSuffix(Version.SUFFIX_2);
			break;
		case 3:
			version.setContentType(Version.CONTENT_TYPE_NORMAL);
			version.setProtocol(Version.CONTENT_TYPE_NORMAL);
			version.setSuffix(Version.SUFFIX_NORMAL);
			break;
		case 4:
			version.setContentType(Version.CONTENT_TYPE_JSJC);
			version.setProtocol(Version.WAP_VERSION_JSJC);
			version.setSuffix(Version.SUFFIX_JSJC);
			break;
		case 5:
			version.setContentType(Version.CONTENT_TYPE_JCLIENT);
			version.setProtocol(Version.WAP_VERSION_JCLIENT);
			version.setSuffix(Version.SUFFIX_JCLIENT);
			break;
		default:
			version.setContentType(Version.CONTENT_TYPE_1);
			version.setProtocol(Version.WAP_VERSION_1);
			version.setSuffix(Version.SUFFIX_1);
			break;
		}
		return version;
	}
	public static int getWapVersion(String content)
	{
		
		int iVersion = 1;
		if(isWml(content))
		{
			iVersion = 1;
		}else if(isHtml(content))
		{
			iVersion = 2;
		}else if(isNormal(content))
		{
			iVersion = 3;
		}else if(isJsjc(content))
		{
			iVersion = 4;
		}else if(isJclient(content))
		{
			iVersion = 5;
		}
		return iVersion;
	}
	private static boolean isWml(String content)
	{
		int index = content.indexOf("<wml");
		if(index != -1)
			return true;
		else
			return false;
	}
	private static boolean isHtml(String content)
	{
		int index = content.indexOf("<html");
		if(index != -1)
			return true;
		else
			return false;
	}
	private static boolean isNormal(String content)
	{
		int index = content.indexOf("xml");
		if(index != -1)
			return true;
		else
			return false;
	}
	private static boolean isJsjc(String content)
	{
		int index = content.indexOf("<xpc");
		if(index != -1)
			return true;
		else{
			index = content.indexOf("<hfh");
			if(index != -1)
			{
				return true;
			}
		}
		return false;
	}
	private static boolean isJclient(String content)
	{
		int index = content.indexOf("<jclient");
		if(index != -1)
			return true;
		else
			return false;
	}
}
