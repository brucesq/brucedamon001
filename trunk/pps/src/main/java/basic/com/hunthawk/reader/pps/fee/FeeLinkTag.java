/**
 * 
 */
package com.hunthawk.reader.pps.fee;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 资费提醒页面计费地址连接标签
 * @author BruceSun
 *
 */
@SuppressWarnings("unchecked")
public class FeeLinkTag extends BaseTag {

	/* (non-Javadoc)
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		//title链接文字
		String title = getParameter("title","");
		String feeId = ParamUtil.getParameter(request, ParameterConstants.FEE_ID );
		Fee fee = getCustomService(request).getFee(feeId);
		StringBuilder builder = new StringBuilder();
		builder.append( request.getContextPath());
		builder.append("/");
		builder.append(fee.getUrl());
		builder.append(ParameterConstants.PORTAL_PATH);
		builder.append("?");
		builder.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", builder.toString());
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance().parseVM(velocityMap, this));
		return resultMap;
	}
	
	private static CustomService customService;

	private CustomService getCustomService(HttpServletRequest request) {
		if (customService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService) wac.getBean("customService");
		}
		return customService;
	}
}
