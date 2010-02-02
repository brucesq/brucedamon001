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
	 * ����в�������
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
	 * ����޲�������
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
	 * ��ȡ������Ϣ�޸ĵĺ�������
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
	 * Ϊ��ģʽ���ڵ������ṩһ������,��ֹģʽ���ڲ�ˢ��
	 * @return
	 */
	private static String getParamFlag()
	{
		String paramFlag = "?paramFlag=";
		paramFlag = paramFlag + Calendar.getInstance().getTimeInMillis();
		
		return paramFlag;
	}
}
