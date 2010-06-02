/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author BruceSun
 *
 */
public class VersionSetTag extends BaseTag {
	
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String versionType = request.getParameter(ParameterConstants.VERSION_TYPE);
		if(versionType != null && StringUtils.isNumeric(versionType)){
			RequestUtil.addCookie(ParameterConstants.VERSION_SET, versionType);
			RequestUtil.addJoyCookie(ParameterConstants.VERSION_SET, versionType);
		}
		String url = getJumpUrl(request);
		Map<String,String> map = new HashMap<String,String>();
		map.put(ParameterConstants.CHANNEL_ID, versionType);
		url = URLUtil.urlChangeParam(url,map,null);
		Redirect.sendRedirect(url);
		return new HashMap();
	}

	private String getJumpUrl(HttpServletRequest request){
		
		int td = ParamUtil.getIntParameter(request,
				ParameterConstants.TEMPLATE_ID, -1);
		String fn = request.getParameter(ParameterConstants.COMMON_PAGE);
		if (td != -1 && !ParameterConstants.COMMON_PAGE_FEE.equals(fn)) {
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(URLUtil.removeParameter(request.getQueryString(),
					ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMON_PAGE,ParameterConstants.AUTHOR_ID,ParameterConstants.VOTE_CONTENT_ID,ParameterConstants.VOTE_ITEM_ID,ParameterConstants.VOTE_VOTE_TYPE,ParameterConstants.VERSION_TYPE));
			return sb.toString();
		}
		String url = "";
		String page = request.getParameter(ParameterConstants.PAGE);
		if (page.equals(ParameterConstants.PAGE_PRODUCT)) {// ≤˙∆∑“≥
			String unicom_pt = request
					.getParameter(ParameterConstants.UNICOM_PT);
			if (unicom_pt != null && !"".equals(unicom_pt)) {
				url = unicom_pt;
			} else {
				return "";
			}
		} else {
			StringBuilder back = new StringBuilder();
			back.append(request.getContextPath());
			back.append(ParameterConstants.PORTAL_PATH);
			back.append("?");
			back.append(ParameterConstants.PAGE);
			back.append("=");
			if (page.equals(ParameterConstants.PAGE_COLUMN)) {// ¿∏ƒø“≥-->≤˙∆∑“≥
				back.append(ParameterConstants.PAGE_PRODUCT);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
			} else if (page.equals(ParameterConstants.PAGE_RESOURCE)) {// ΩÈ…‹“≥-->¿∏ƒø“≥
				back.append(ParameterConstants.PAGE_COLUMN);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				back.append(ParameterConstants.PAGE_NUMBER);
				back.append("=");
				back.append(1);
				back.append("&");
				URLUtil.append(back, ParameterConstants.FEE_ID, request);
			} else if (page.equals(ParameterConstants.PAGE_DETAIL)) {// ƒ⁄»›“≥-->ΩÈ…‹“≥
				back.append(ParameterConstants.PAGE_RESOURCE);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(back, ParameterConstants.FEE_BAG_ID, request);
				URLUtil.append(back, ParameterConstants.FEE_BAG_RELATION_ID,
						request);
				back.append(ParameterConstants.RESOURCE_ID);
				back.append("=");
				back.append(URLUtil.getResourceId(request));
				back.append("&");
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				back.append(ParameterConstants.PAGE_NUMBER);
				back.append("=");
				back.append(1);
				back.append("&");
				URLUtil.append(back, ParameterConstants.FEE_ID, request);
			} 
			url = URLUtil.trimUrl(back).toString();
		}
		return url;
	}

	
}
