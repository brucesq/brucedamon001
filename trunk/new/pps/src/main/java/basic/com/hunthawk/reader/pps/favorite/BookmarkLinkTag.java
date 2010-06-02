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
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 书签链接标签 标签名称：bookmark_link 参数说明： property:1.添加 -1.删除 title:链接名称
 * templateId:模板ID isDel:是否做确认删除 number:在第几页之前显示 add by liuxh 2009-9-2
 * split:分割符号
 * 
 * @author liuxh
 * 
 */
public class BookmarkLinkTag extends BaseTag {

	private BussinessService bussinessService;

	private int number;
	private int currentPage;
	private String split;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub

		String n = getParameter("number", "0");
		try {
			Integer.parseInt(n);
		} catch (Exception ex) {
			TagLogger.debug(tagName, "number属性值不是有效的属性值", request
					.getQueryString(), null);
			return new HashMap();
		}
		this.number = Integer.parseInt(n);
		this.currentPage = Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER) == null ? "1"
				: request.getParameter(ParameterConstants.PAGE_NUMBER));
		this.split = getParameter("split", "");

		int isDel = getIntParameter("isDel", -1);// 1.删除 -1.不删除 模板跳转
		String title = getParameter("title", "添加书签");// 链接文字
		int templateId = getIntParameter("templateId", -1);// 模板ID
		int property = getIntParameter("property", 1);// 默认是添加链接
		if (templateId < 0) {
			TagLogger.debug(tagName, "模板ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		if (property > 0) {// 添加链接
			sb.append(ParameterConstants.COMMON_PAGE);
			sb.append("=");
			sb.append(ParameterConstants.COMMON_PAGE_BOOKMARK_ADD);
			sb.append("&");
		} else {// 删除链接
			title = "我要删除";
			if (isDel > 0) {
				sb.append(ParameterConstants.COMMON_PAGE);
				sb.append("=");
				sb.append(ParameterConstants.COMMON_PAGE_BOOKMARK_DEL);
				sb.append("&");
			}
		}

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
		URLUtil.append(sb, ParameterConstants.CHAPTER_ID, request);
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		URLUtil.append(sb, ParameterConstants.PAGE_NUMBER, request);
		URLUtil.append(sb, ParameterConstants.WORDAGE, request);
		URLUtil.append(sb, ParameterConstants.FEE_ID, request);
		URLUtil.append(sb, ParameterConstants.BOOK_MARK_ID, request);

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
		 * resultMap.put(TagUtil.makeTag(tagName),
		 * VmInstance.getInstance().parseVM(velocityMap, this));
		 */
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
