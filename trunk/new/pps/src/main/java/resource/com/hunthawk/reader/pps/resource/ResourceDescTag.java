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
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * @author BruceSun
 * 
 */
public class ResourceDescTag extends BaseTag {

	private ResourceService resourceService;

	private BussinessService bussinessService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		int size = getIntParameter("size", 100);
		String resourceId = URLUtil.getResourceId(request);
		int tagTemplateId = this.getIntParameter("tmd", 0);
		int templateId = this.getIntParameter("templateId", -1);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		ResourceAll resource = getResourceService(request).getResource(
				resourceId);
		if (resource == null)
			return new HashMap();
		
		String desc = resource.getCComment() == null ? "" : resource.getCComment();
		boolean isLong = desc.length()>size;
		String title = desc;
		if(isLong){
			title = desc.substring(0,size);
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
		velocityMap.put("isLong", isLong);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));
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

	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac.getBean("resourceService");
		}
		return resourceService;
	}
}
