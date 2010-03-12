/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * @author BruceSun
 *
 */
public class HomeTag extends BaseTag {

	/* (non-Javadoc)
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String title = getParameter("title","·µ»ØÊ×Ò³");
		String url = getParameter("url","");
		if(StringUtils.isEmpty(url)){
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_PRODUCT);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			
//			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
//			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
//			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
//			URLUtil.trimURL(sb);
			url = URLUtil.trimUrl(sb).toString();
		}
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", url);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),VmInstance.getInstance().parseVM(velocityMap, this));
		return resultMap;		
	}

}
