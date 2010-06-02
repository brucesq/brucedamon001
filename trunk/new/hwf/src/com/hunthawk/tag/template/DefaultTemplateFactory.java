/**
 * 
 */
package com.hunthawk.tag.template;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.tag.TagConstants;
import com.hunthawk.tag.TemplateFactory;


/**
 * <p>Ĭ��ģ�幤����</p>
 * @author sunquanzhi
 *
 */
public class DefaultTemplateFactory extends TemplateFactory {

	private Map map ;
	/**
	 * 
	 */
	public DefaultTemplateFactory() {
		map = new HashMap();
	}
    /**
     * <p>���ģ������</p>
     */
	public String getTemplate(HttpServletRequest request)
	{
		String page_name = request.getParameter(TagConstants.PARA_PAGE_NAME);
		if(page_name == null)
		{
			page_name = TagConstants.INDEX_JSP_PAGE;
		}
		
		
		
		return getFileTemplate(page_name);
		
		
		
	}
	/**
	 * 
	 * @param page_name
	 * @return
	 */
	public String getFileTemplate(String page_name)
	{
		String fileName = page_name +".tmpl";
		if(map.containsKey(fileName))
		{
			return (String)map.get(fileName);
		}else{
			String template = readFile(fileName);
			if(template != null)
			{
				map.put(fileName,template);
			}
			return template;
		}
	}
	/**
	 * <p>��ģ���ļ�</p>
	 * @param fileName
	 * @return
	 */
	protected String readFile(String fileName)
	{
		String result;
		try{
			InputStreamReader bis = new InputStreamReader(getClass().getResourceAsStream("/template/"+fileName),"GBK");
			BufferedReader reader = new BufferedReader(bis);
			String line = "";
			result = "";
			while( (line = reader.readLine()) != null)
			{
				result += line;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return result;
	}
	


}
