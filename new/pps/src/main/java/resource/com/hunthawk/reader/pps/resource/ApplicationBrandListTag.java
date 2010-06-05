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
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author BruceSun
 * 
 */
public class ApplicationBrandListTag extends BaseTag {
	private ResourceService resourceService;
	private BussinessService bussinessService;

	public Map parseTag(HttpServletRequest request, String tagName) {
		int templateId = getIntParameter("templateId", -1);// Ä£°åID
		if (templateId < 0) {
			TagLogger.error(tagName, "Ä£°åIDÎª¿Õ", request.getQueryString(), null);
			return new HashMap();
		}
		String resourceId = URLUtil.getResourceId(request);
		List<String> brands = getResourceService(request).getApplicationBrand(
				resourceId);
		Map<String, String> brandNames = getBussinessService(request)
				.getBrandNames();
		
		List<Object> lsRess = new ArrayList<Object>();
		for (String brand : brands) {
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("brand", brand);
			if (brandNames.containsKey(brand)) {
				obj.put("name", brandNames.get(brand));
			} else {
				obj.put("name", brand);
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
			
			sb.append(ParameterConstants.BRAND_NAME);
			sb.append("=");
			sb.append(brand);
			sb.append("&");
			
			
			
			// sb.append(URLUtil.removeParameter(request.getQueryString(),
			// ParameterConstants.PAGE_NUMBER));
			sb.append(URLUtil.removeParameter(request.getQueryString(),
					ParameterConstants.PAGE_NUMBER,
					ParameterConstants.SEARCH_PARAM_VALUE,
					ParameterConstants.SEARCH_TYPE,
					ParameterConstants.QUICK_SEARCH_LINK_NAME,
					ParameterConstants.COMMENT_PARAM_VALUE,
					ParameterConstants.CUSTOM_KEY_VALUE,
					ParameterConstants.COMMENT_PLATE,
					ParameterConstants.COMMENT_TARGET,
					ParameterConstants.COMMENT_TARGET_ID,
					ParameterConstants.VOTE_VOTE_TYPE,
					ParameterConstants.VOTE_ITEM_ID,
					ParameterConstants.VOTE_CONTENT_ID,
					ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			obj.put("url", sb.toString());
			lsRess.add(obj);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objs", lsRess);

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
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
