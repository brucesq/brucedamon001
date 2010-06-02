/**
 * 
 */
package com.hunthawk.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.hunthawk.tag.template.DefaultTemplateFactory;

/**
 * <p>模板工厂类</p>
 * @author sunquanzhi
 *
 */
public abstract class TemplateFactory {

	private static  TemplateFactory factory = new DefaultTemplateFactory();
	private static  Map map = new HashMap();
	private static  Logger logger = Logger.getLogger(TemplateFactory.class);
	
	/**
	 * <p>获得模板工厂</p>
	 * <p>如果存在className的工厂类，返回该类，否则返回默认的工厂类</p>
	 * @param className
	 * @return
	 */
	public static TemplateFactory getInstance(String className)
	{
		if(map.containsKey(className))
		{
			return (TemplateFactory)map.get(className);
		}else{
			try{
				Class instanceClass = Class.forName(className);
				TemplateFactory instance = (TemplateFactory)instanceClass.newInstance();
				map.put(className,instance);
				return instance;
			}catch(Exception e)
			{
				e.printStackTrace();
				logger.error(className,e);
				return getInstance();
			}
		}
	}
	
	public static TemplateFactory getInstance()
	{
		return factory;
	}
	/**
	 * <p>获得模板内容,如果文件不存在返回null</p>
	 * @param fileName
	 * @return
	 */
	public abstract String getTemplate(HttpServletRequest request);
	


}

