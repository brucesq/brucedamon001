/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 通用链接
 * 
 * @author BruceSun
 * 
 */
public class CommonLinkTag extends BaseTag {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest,
	 *      java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String title = getParameter("title", "链接");// 链接文字
		int templateId = getIntParameter("templateId", -1);// 模板ID
		if (templateId < 0) {
			TagLogger.debug(tagName, "模板ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		StringBuilder sb = new StringBuilder();
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
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", sb.toString());
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));
		return resultMap;
	}

}
