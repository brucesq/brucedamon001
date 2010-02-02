/**
 * 
 */
package com.hunthawk.reader.pps.basic;

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
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author BruceSun
 *
 */
public class VersionSetLinkTag extends BaseTag {
	
	private BussinessService bussinessService;


	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {

		int templateId = getIntParameter("templateId", -1);
		if (templateId < 0) {
			TagLogger.debug(tagName, "Ä£°åIDÎª¿Õ", request.getQueryString(), null);
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
		List objs = new ArrayList();
		String url = sb.toString();
		Map obj = new HashMap();
		obj.put("url", url+ParameterConstants.VERSION_TYPE+"=1");
		obj.put("title", "WAP");
		obj.put("version", "1");
		objs.add(obj);
		
		obj = new HashMap();
		obj.put("url", url+ParameterConstants.VERSION_TYPE+"=2");
		obj.put("title", "²Ê°æ");
		obj.put("version", "2");
		objs.add(obj);
		
		obj = new HashMap();
		obj.put("url", url+ParameterConstants.VERSION_TYPE+"=3");
		obj.put("title", "ìÅ°æ");
		obj.put("version", "3");
		objs.add(obj);
		
		
		Map velocityMap = new HashMap();
		velocityMap.put("objs", objs);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		/*result =  VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
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
