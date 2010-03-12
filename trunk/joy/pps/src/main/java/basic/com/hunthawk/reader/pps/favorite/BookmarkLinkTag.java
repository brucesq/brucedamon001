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
 * ��ǩ���ӱ�ǩ ��ǩ���ƣ�bookmark_link ����˵���� property:1.��� -1.ɾ�� title:��������
 * templateId:ģ��ID isDel:�Ƿ���ȷ��ɾ�� number:�ڵڼ�ҳ֮ǰ��ʾ add by liuxh 2009-9-2
 * split:�ָ����
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
			TagLogger.debug(tagName, "number����ֵ������Ч������ֵ", request
					.getQueryString(), null);
			return new HashMap();
		}
		this.number = Integer.parseInt(n);
		this.currentPage = Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER) == null ? "1"
				: request.getParameter(ParameterConstants.PAGE_NUMBER));
		this.split = getParameter("split", "");

		int isDel = getIntParameter("isDel", -1);// 1.ɾ�� -1.��ɾ�� ģ����ת
		String title = getParameter("title", "�����ǩ");// ��������
		int templateId = getIntParameter("templateId", -1);// ģ��ID
		int property = getIntParameter("property", 1);// Ĭ�����������
		if (templateId < 0) {
			TagLogger.debug(tagName, "ģ��IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		if (property > 0) {// �������
			sb.append(ParameterConstants.COMMON_PAGE);
			sb.append("=");
			sb.append(ParameterConstants.COMMON_PAGE_BOOKMARK_ADD);
			sb.append("&");
		} else {// ɾ������
			title = "��Ҫɾ��";
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
		// ����ֻ���
		velocityMap.put("mobile", RequestUtil.getMobile());
		// ����ֻ�����
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
