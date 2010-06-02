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
 * <p>ģ�幤����</p>
 * @author sunquanzhi
 *
 */
public abstract class TemplateFactory {

	private static  TemplateFactory factory = new DefaultTemplateFactory();
	private static  Map map = new HashMap();
	private static  Logger logger = Logger.getLogger(TemplateFactory.class);
	
	/**
	 * <p>���ģ�幤��</p>
	 * <p>�������className�Ĺ����࣬���ظ��࣬���򷵻�Ĭ�ϵĹ�����</p>
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
	 * <p>���ģ������,����ļ������ڷ���null</p>
	 * @param fileName
	 * @return
	 */
	public abstract String getTemplate(HttpServletRequest request);
	


}

