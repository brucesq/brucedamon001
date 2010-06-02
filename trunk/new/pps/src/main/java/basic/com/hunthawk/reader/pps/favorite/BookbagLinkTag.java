package com.hunthawk.reader.pps.favorite;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 书包标签(链接) 标签名称：bookbag_link 参数说明： property:1.添加 -1.删除 (去掉) 只有删除操作 title:链接名称
 * templateId:模板ID isDel:是否做确认删除 (去掉)
 * 
 * @author liuxh
 * 
 */
public class BookbagLinkTag extends BaseTag {

	private CustomService customService;
	private BussinessService bussinessService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title = getParameter("title", "我要删除");// 链接文字
		int templateId = getIntParameter("templateId", -1);// 模板ID
		if (templateId < 0) {
			TagLogger.debug(tagName, "模板ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.COMMON_PAGE);
		sb.append("=");
		sb.append(ParameterConstants.COMMON_PAGE_BOOKBAG_DEL);
		sb.append("&");
		sb.append(ParameterConstants.TEMPLATE_ID);
		sb.append("=");
		sb.append(templateId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PAGE, request);
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", URLUtil.trimUrl(sb));
		// 添加手机号
		velocityMap.put("mobile", RequestUtil.getMobile());
		// 添加手机类型
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));

		/*
		 * Map resultMap = new HashMap();
		 * resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
		 * .parseVM(velocityMap, this));
		 */
		return resultMap;
	}

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
