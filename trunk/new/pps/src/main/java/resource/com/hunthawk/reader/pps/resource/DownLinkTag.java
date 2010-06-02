/**
 * 
 */
package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author BruceSun
 * 
 */
public class DownLinkTag extends BaseTag {

	private BussinessService bussinessService;
	
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String name = getParameter("name", "Á¢¼´ÏÂÔØ");
		int tagTemplateId = this.getIntParameter("tmd", 0);

		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);

		} 
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DETAIL);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		String link = sb.toString();
		Map velocityMap = new HashMap();
		Map resultMap = new HashMap();
		velocityMap.put("title", name);
		velocityMap.put("url", link);
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
	}

	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac
					.getBean("bussinessService");
		}
		return bussinessService;
	}
}
