/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
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
		String url = getParameter("url", "");
		int templateId = getIntParameter("templateId", 0);
		// System.out.println("LLL:"+title+":"+url);
		if (StringUtils.isEmpty(url)) {
			TagLogger.error(tagName, "URL参数为空", request.getQueryString(), null);
			return new HashMap();
		}
		StringBuilder sb = new StringBuilder();
		if (templateId != 0) {
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.COMMON_PAGE);
			sb.append("=");
			sb.append(ParameterConstants.COMMON_PAGE_LINK);
			sb.append("&");
			sb.append(ParameterConstants.TEMPLATE_ID);
			sb.append("=");
			sb.append(templateId);
			sb.append("&");
			//sb.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.PAGE_NUMBER));
			sb.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.PAGE_NUMBER,ParameterConstants.SEARCH_PARAM_VALUE,ParameterConstants.SEARCH_TYPE,ParameterConstants.QUICK_SEARCH_LINK_NAME,
					ParameterConstants.COMMENT_PARAM_VALUE,ParameterConstants.CUSTOM_KEY_VALUE,ParameterConstants.COMMENT_PLATE,ParameterConstants.COMMENT_TARGET,ParameterConstants.COMMENT_TARGET_ID,
					ParameterConstants.VOTE_VOTE_TYPE,ParameterConstants.VOTE_ITEM_ID,ParameterConstants.VOTE_CONTENT_ID,ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append("gourl=");
			sb.append(new String(Base64.encodeBase64(url.getBytes())));
		} else {
			sb.append(request.getContextPath());
			sb.append("/redirect?gourl=");
			try {
				sb.append(URLEncoder.encode(url, "utf-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String content = "<a href=\"" + sb.toString() + "\">" + title + "</a>";
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);
		return resultMap;
	}

}
