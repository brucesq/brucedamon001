/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author sunquanzhi
 *
 */
public class TextRollTag extends BaseTag {
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String defaultValue=getParameter("defaultkey","");
		if(StringUtils.isNotEmpty(defaultValue)){
			String[] keys = defaultValue.split("-");
			if(keys.length>1){
				double random = Math.random();
				int temp = (int) (random * (keys.length));
				defaultValue = keys[temp];
			}
		}
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),defaultValue);
		return resultMap;		
	}

}
