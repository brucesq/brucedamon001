/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author sunquanzhi
 *
 */
public class CountdownTag extends BaseTag {
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String time = getParameter("time","");
		Date date = null;
		try{
			date = ToolDateUtil.stringToDate(time, ToolDateUtil.DATE_FORMAT);
		}catch(Exception e){
			
		}
		if(date == null){
			return new HashMap();
		}
		Date currentDate = new Date();
		currentDate = ToolDateUtil.stringToDate(ToolDateUtil.dateToString(currentDate),ToolDateUtil.DATE_FORMAT);
		long countdown = (date.getTime()-currentDate.getTime())/(24*60*60*1000);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),""+countdown);
		return resultMap;
	}
	
	public static void main(String[] args){
		String time = "2011-04-09";
		Date date = null;
		try{
			date = ToolDateUtil.stringToDate(time, ToolDateUtil.DATE_FORMAT);
		}catch(Exception e){
			
		}
		
		Date currentDate = new Date();
		currentDate = ToolDateUtil.stringToDate(ToolDateUtil.dateToString(currentDate),ToolDateUtil.DATE_FORMAT);
		long countdown = (date.getTime()-currentDate.getTime())/(24*60*60*1000);
		System.out.println(":"+countdown);
	}
}
