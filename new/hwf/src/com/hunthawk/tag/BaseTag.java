/**
 * 
 */
package com.hunthawk.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sunquanzhi
 *
 */
public abstract class BaseTag implements Tag {

	private String parameter;
	
	private HashMap<String,String> parameters = new HashMap<String,String>();
	
	
	public String getParameter()
	{
		return parameter;
	}
	
	public void setParameter(String parameter){
		parameters.clear();
		String[] paras = parameter.split(",");
		for(String para:paras){
			int index = para.indexOf("=");
			if(index > 0){
				parameters.put(para.substring(0,index), para.substring(index+1));
			}
		}
	}
	
	public String getParameter(String name){
		return parameters.get(name);
	}
	
	public String getParameter(String name,String defaultValue){
		String value = parameters.get(name);
		if(value == null)
			return defaultValue;
		return value.replaceAll("!0!", "#");
	}
	
	public int getIntParameter(String name,int defaultNum){
		String value = parameters.get(name);
		int num = defaultNum;
		if(value != null){
			try{
				num = Integer.parseInt(value);
			}catch(Exception ignored){}
		}
		return num;
	}
	
	
	public abstract Map parseTag(HttpServletRequest request, String tagName) ;

	public static void main(String[] args){
		String value = "!0!sda";
		System.out.println(value.replaceAll("!0!", "#"));
	}
}
