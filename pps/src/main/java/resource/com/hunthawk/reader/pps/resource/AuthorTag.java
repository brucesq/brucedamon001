package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * �������Ա�ǩ
 * 
 * @author liuxh ����˵����
 * @param property
 *            ��������
 */
public class AuthorTag extends BaseTag {
	private ResourceService resourceService;
	/** ��ʾ���� */
	private static final int DEFAULT_PAGE_SIZE = 20;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String property = getParameter("property", "");
		if (request.getParameter(ParameterConstants.AUTHOR_ID) == null) {
			TagLogger.debug(tagName, "����IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		int authorId = Integer.parseInt(request
				.getParameter(ParameterConstants.AUTHOR_ID));
		ResourceAuthor author = getResourceService(request).getResourceAuthor(
				authorId);
		if (property.equalsIgnoreCase("name")) {
			return authorName(request, tagName, author);
		} else if (property.equalsIgnoreCase("summary")) {
			return authorSummary(request, tagName, author);
		} else if (property.equalsIgnoreCase("preview")) {
			return authorPreview(request, tagName, author);
		} else if (property.equalsIgnoreCase("penname")) {
			return authorPenName(request, tagName, author);
		}
		return new HashMap();
	}

	/** �������� */
	private Map authorName(HttpServletRequest request, String tagName,
			ResourceAuthor author) {
		Map resultMap = new HashMap();
		String title = author.getName() == null ? "" : author.getName();
		resultMap.put(TagUtil.makeTag(tagName), title);
		return resultMap;
	}

	/** ���߼�� */
	private Map authorSummary(HttpServletRequest request, String tagName,
			ResourceAuthor author) {
		Map resultMap = new HashMap();
		String title = author.getIntro() == null ? "����" : author.getIntro();
		resultMap.put(TagUtil.makeTag(tagName), title);
		return resultMap;
	}

	/** ����ͼƬ */
	private Map authorPreview(HttpServletRequest request, String tagName,
			ResourceAuthor author) {
		String title = imagePreview(request, tagName, author);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), title);
		return resultMap;
	}

	/**
	 * Ԥ��ͼ
	 */
	private String imagePreview(HttpServletRequest request, String tagName,
			ResourceAuthor author) {
		StringBuilder sb = new StringBuilder();
		if (author.getAuthorPic() != null) {
			if (author.getAuthorPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getNormalImg(
						author.getAuthorPic());
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + author.getName() + "\"  width=\"112\" />");
			}
		}
		return sb.toString();
	}

	/** ���߱��� */
	public Map authorPenName(HttpServletRequest request, String tagName,
			ResourceAuthor author) {
		Map resultMap = new HashMap();
		String title = author.getPenName() == null ? "" : author.getPenName();
		resultMap.put(TagUtil.makeTag(tagName), title);
		return resultMap;
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
