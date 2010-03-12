/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 系统时间标签
 * 
 * @author BruceSun
 * 
 */
public class SystemTimeTag extends BaseTag {

	private final static String DEFAULT_PATTERN = "[MM-dd HH:mm]";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest,
	 *      java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {

		String pattern = this.getParameter("pattern", DEFAULT_PATTERN);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		String result = dateFormat.format(new Date());
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),result);
		return resultMap;
	}

}
