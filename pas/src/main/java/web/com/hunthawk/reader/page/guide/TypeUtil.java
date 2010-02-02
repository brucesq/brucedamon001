/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.util.Map;
import java.util.TreeMap;

import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;

/**
 * @author sunquanzhi
 *
 */
public class TypeUtil {

	public static IPropertySelectionModel parseSelect(String value)
	{
		Map<String,String> map = new TreeMap<String,String>();
		value = value.substring(1,value.length()-1);
		
		String[] strs = value.split(",");
		for(int i=0;i<strs.length;i++)
		{
			String[] kv = strs[i].split("=");
		
			map.put(kv[0],kv[1]);
			
		} 
		
		return new MapPropertySelectModel(map);
	}

	public static void parseTextInput(String value,TextInput text)
	{
		//System.out.println(value);
		value = value.substring(1,value.length()-1);
		
		String[] strs = value.split(",");
		//System.out.println(value+":"+strs.length);
		if(strs.length > 0)
		{
			if(strs[0].startsWith("validators:"))
			{
				text.setValidators(strs[0]);
			}else{
				text.setValidators("required");
			}
		//	System.out.println(text.getValidators());
		}
		if(strs.length > 1)
		{
			if(strs[1].startsWith("translator:"))
			{
				text.setTranslator(strs[1]);
			}else{
				text.setTranslator("translator:string");
			}
			//System.out.println(text.getTranslator());
		}
	}
}
