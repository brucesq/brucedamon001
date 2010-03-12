/**
 * 
 */
package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author BruceSun
 * 
 */
public class RemainTextLinkTag extends BaseTag {

	private BussinessService bussinessService;
	private ResourceService resourceService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String name = getParameter("name", "余下全文");
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);

		}

		String resourceId = URLUtil.getResourceId(request);
		ResourceAll resource = getResourceService(request).getResource(
				resourceId);
		String content = resource.getIntroLon();

		String[] pages = content.split(Constants.SPLIT_DOC_TAG);

		int pageNumber = request.getParameter(ParameterConstants.PAGE_NUMBER) == null ? 1
				: Integer.parseInt(request
						.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页
		int word = request.getParameter(ParameterConstants.WORDAGE) == null ? (getIntParameter(
				"wordset", -1) < 0 ? ParameterConstants.CHAPTER_CONTENT_WORD_SET
				: getIntParameter("wordset",
						ParameterConstants.CHAPTER_CONTENT_WORD_SET))
				: Integer.parseInt(request
						.getParameter(ParameterConstants.WORDAGE));
		boolean isRemainText = request
				.getParameter(ParameterConstants.REMAINING_TEXT) == null ? false
				: true;
		int pageSize = word / 500;
		int totalPage = ((Double) Math.ceil((float) pages.length / pageSize))
				.intValue();
		int startPage = (pageNumber - 1) * pageSize;
		int endPage = isRemainText ? pages.length : pageNumber * pageSize;
		
		if(isRemainText || pageNumber>=totalPage){
			return new HashMap();
		}

		String url = URLUtil.urlChange(request, new HashMap(), new ArrayList());
		url += "&" + ParameterConstants.REMAINING_TEXT + "=1";
		Map velocityMap = new HashMap();
		Map resultMap = new HashMap();
		velocityMap.put("title", name);
		velocityMap.put("url", url);
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,
				tagTem);
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
