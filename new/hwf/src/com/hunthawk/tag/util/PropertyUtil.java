/**
 * 
 */
package com.hunthawk.tag.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;

/**
 * @author sunquanzhi
 *
 */
public class PropertyUtil {

	private static Logger logger = Logger.getLogger(PropertyUtil.class);
	public static void setProperty(Object obj,String propertyName,Object value)
	{
		if(BeanUtilsBean.getInstance().getPropertyUtils().isWriteable(obj, propertyName))
		{
			 try{
				 BeanUtilsBean.getInstance().copyProperty(obj,propertyName,value);
			 }catch(Exception e)
			 {
				 logger.error("Conn't set "+obj.getClass().getName()+"' "+propertyName+" value");
				 logger.error(e);
				 e.printStackTrace();
			 }
		}
	}
	public static Object getProperty(Object obj,String propertyName)
	{
		Object value = null;
		try{
			BeanUtilsBean.getInstance().getPropertyUtils().getProperty(obj,propertyName);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return value;
		
	}

}
