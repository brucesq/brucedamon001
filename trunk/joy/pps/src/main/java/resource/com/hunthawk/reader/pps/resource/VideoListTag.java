/**
 * 
 */
package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.VideoSuite;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author BruceSun
 *
 */
public class VideoListTag extends BaseTag {
	private ResourceService resourceService;
	private BussinessService bussinessService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		
		String resourceId = getParameter("resourceId","");
		if(StringUtils.isEmpty(resourceId)){
			resourceId = URLUtil.getResourceId(request);
		}
		
		List<VideoSuite> videos = getResourceService(request).getVideoSuiteList(resourceId);
		String dir = getResourceService(request).getVideoResourceDirectory(resourceId);
		
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append("/download?");
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_DOWNLOAD);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		sb.append(ParameterConstants.RESOURCE_ID);
		sb.append("=");
		sb.append(resourceId);
		sb.append("&downurl=");
		sb.append(dir);
		
		TagTemplate tagTem = null;
		int tagTemplateId = this.getIntParameter("tmd", 0);

		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);

		} 
		Map velocityMap = new HashMap();
		velocityMap.put("objs", videos);
		velocityMap.put("dir", sb.toString());
		velocityMap.put("strUtil", new StrUtil());
		Map resultMap = new HashMap();
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
