/**
 * 
 */
package com.hunthawk.tag.process;

import com.hunthawk.tag.protocol.Version;

/**
 * @author sunquanzhi
 *
 */
public class RefreshUtil {
	private static ThreadLocal<Refresh> contain = new ThreadLocal<Refresh>();
	
	public static void pageRefresh(Refresh refresh)
	{
		contain.set(refresh);
	}

	public static Refresh getRefresh()
	{
		return contain.get();		
	}
	
	public static boolean isRefresh()
	{
		if(contain.get() == null)
		{
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	public static void clear()
	{
		contain.remove();
	}
	
	public static String process(String content,Refresh refresh,Version version)
	{
		if(version.getProtocol().equals(Version.WAP_VERSION_1))
		{
			return processWml(content,refresh);
		}
		if(version.getProtocol().equals(Version.WAP_VERSION_2))
		{
			return processHtml(content,refresh);
		}
		return content;
	}
	
	private static String processWml(String content,Refresh refresh)
	{
		try{
			int startIndex = content.indexOf("<card");
			int endIndex = content.indexOf(">", startIndex);
			StringBuilder builder = new StringBuilder();
			builder.append(content.substring(0, startIndex));
			String cardStr = content.substring(startIndex, endIndex);
			if(cardStr.indexOf("ontimer")>0)
			{
				return content;
			}
			cardStr += " ontimer=\""+refresh.getTargetUrl()+"\" >";
			builder.append(cardStr);
			builder.append("<timer value=\""+refresh.getTimer()*10+"\"/>");
			builder.append(content.substring(endIndex+1));
			
			return builder.toString();
		}catch(Exception e)
		{
			return content;
		}
		
	}
	private static String processHtml(String content,Refresh refresh)
	{
		try{
			StringBuilder builder = new StringBuilder();
			int startIndex = content.indexOf("<head>");
			if(startIndex >= 0)
			{
				int endIndex = content.indexOf("</head>");
				String headStr = content.substring(startIndex, endIndex);
				if(headStr.indexOf("refresh") > 0 || headStr.indexOf("Refresh") > 0)
				{
					return content;
				}
				builder.append(content.substring(0, endIndex));
				builder.append("<meta http-equiv=\"refresh\" content=\""+refresh.getTimer()+"; url="+refresh.getTargetUrl()+"\" />");
				builder.append(content.substring(endIndex));
				
			}else
			{
				startIndex = content.indexOf("<body");
				
				builder.append(content.substring(0, startIndex));
				builder.append("<head><meta http-equiv=\"refresh\" content=\""+refresh.getTimer()+"; url="+refresh.getTargetUrl()+"\" /></head>");
				builder.append(content.substring(startIndex));
			}
			return builder.toString();
		}catch(Exception e)
		{
			return content;
		}
		
	}
	
	public static void main(String[] args)
	{
		Refresh refresh = new Refresh();
		refresh.setTargetUrl("http://assadsd.com?a=ssd");
		refresh.setTimer(20);
		System.out.println(processWml("<card >",refresh));
		System.out.println(processHtml("<body",refresh));
	}
}
