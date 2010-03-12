/**
 * 
 */
package com.hunthawk.reader.page.util;

import java.util.Calendar;

import org.apache.tapestry.engine.ExternalServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;

/**
 * @author BruceSun
 *
 */
public class PageHelper {

private static String FUNCTIONNAME = "cuspopup_window";
	
	private static String PERSONFUNCNAME = "person_window";
	/**
	 * 获得有参数链接
	 * @param service
	 * @param pageName
	 * @param params
	 * @return
	 */
	public static String getExternalURL(IEngineService service,String pageName,Object[] params)
	{		
		
		ExternalServiceParameter parameter = new ExternalServiceParameter(pageName, params);
		
		ILink link = service.getLink(false,parameter);
		String URL = link.getURL();
		//logger.info("URL:" + URL);
		return URL;
	}
	
	/**
	 * 获得无参数链接
	 * @param service
	 * @param pageName
	 * @param params
	 * @return
	 */
	public static String getPageURL(IEngineService service,String pageName)
	{		
		//PageServiceParameter parameter = new PageServiceParameter(pageName, null);
		ILink link = service.getLink(false,pageName);
		String URL = link.getURL();
		return URL;
	}
	
	/**
	 * 
	 * @param service
	 * @param pageName
	 * @param params
	 * @return
	 */
	public static String getExternalFunction(IEngineService service,String pageName,Object[] params)
	{
		String URL = getExternalURL(service,pageName,params);
		URL = FUNCTIONNAME + "('" + URL+ "');";
		return URL;
	}
	
	public static String getPageFunction(IEngineService service,String pageName)
	{
		String URL = getPageURL(service,pageName);
		URL = FUNCTIONNAME + "('" + URL + "');";
		return URL;
	}
	
	/**
	 * 获取个人信息修改的函数名称
	 * @param service
	 * @param pageName
	 * @return
	 */
	public static String getPersonFunction(IEngineService service,String pageName)
	{
		String URL = getPageURL(service,pageName);
		URL = PERSONFUNCNAME + "('" + URL + "');";
		return URL;
	}
	
	/**
	 * 为打开模式窗口的连接提供一个参数,防止模式窗口不刷新
	 * @return
	 */
	private static String getParamFlag()
	{
		String paramFlag = "?paramFlag=";
		paramFlag = paramFlag + Calendar.getInstance().getTimeInMillis();
		
		return paramFlag;
	}
}
