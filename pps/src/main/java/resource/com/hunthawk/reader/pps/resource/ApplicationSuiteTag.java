/**
 * 
 */
package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ApplicationSuite;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author BruceSun
 * 
 */
public class ApplicationSuiteTag extends BaseTag {
	private ResourceService resourceService;
	private BussinessService bussinessService;

	public Map parseTag(HttpServletRequest request, String tagName) {
		int type = getIntParameter("type", 1);// 1自动适配 2列表
		List<Object> lsRess = new ArrayList<Object>();
		String resourceId = URLUtil.getResourceId(request);
		List<ApplicationSuite> suites = getResourceService(request)
				.getApplicationSuiteList(resourceId);
		String brand = ParamUtil.getParameter(request,
				ParameterConstants.BRAND_NAME);

		String dir = getResourceService(request).getResourceDirectory(
				resourceId);

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

		if (type == 1) {
			Map<String, Object> obj = new HashMap<String, Object>();
			String ua = RequestUtil.getUa();
			for (ApplicationSuite suite : suites) {
				if (ua.equalsIgnoreCase(suite.getUa())) {
					
					String title = "立即下载";
					obj.put("name", title);
					obj.put("obj", suite);
					lsRess.add(obj);
					break;
				}
			}
		} else {
			for (ApplicationSuite suite : suites) {
				if (brand.equalsIgnoreCase(suite.getBrand())) {
					Map<String, Object> obj = new HashMap<String, Object>();
					
					String title = suite.getUa();
					obj.put("name", title);
					obj.put("obj", suite);
					lsRess.add(obj);
				}
			}
		}
		TagTemplate tagTem = null;
		int tagTemplateId = this.getIntParameter("tmd", 0);

		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);

		} 
		Map velocityMap = new HashMap();
		velocityMap.put("objs", lsRess);
		velocityMap.put("dir", sb.toString());
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
