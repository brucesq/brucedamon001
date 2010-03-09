/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author sunquanzhi
 *
 */
public class RedirectTag extends BaseTag {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest,
	 *      java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String title = getParameter("title", "链接");// 链接文字
		String url = getParameter("url","");
		if(StringUtils.isEmpty(url)){
			TagLogger.error(tagName, "URL参数为空", request.getQueryString(), null);
			return new HashMap();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append("/redirect?gourl=");
		try{
			sb.append(URLEncoder.encode(url,"utf-8"));
		}catch(Exception e){
			e.printStackTrace();
		}
		String content = "<a href=\""+sb.toString()+"\">"+title+"</a>";
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);
		return resultMap;
	}

}
