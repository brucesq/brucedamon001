package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 作者列表标签 标签名称：author_list 参数说明： columnId:此参数用于获得批价相关信息 pageSize:每页显示多少条
 * noPageLink:不显示翻页相关的链接 templateId:作者相关作品列表显示模板 number:每行显示几个 split:分隔符号
 * (每个链接之间的分隔符号)
 * 
 * @author liuxh
 * 
 */
public class AuthorListTag extends BaseTag {

	private BussinessService bussinessService;
	private ResourceService resourceService;
	private static final int DEFAULT_SIZE = 10;
	/** 不显示翻页相关的链接 */
	private boolean noPageLink;

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}

	private String split;

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub

		this.number = getIntParameter("number", 3);// 默认每行显示3个
		this.split = getParameter("split", ".");// 默认用点分隔

		/**
		 * 添加了一个首字母参数
		 * 
		 * @author penglei
		 */
		String inital = request.getParameter("inital");
		// end

		String columnId = getParameter("columnId", "");
		ResourcePack rp = null;
		if (StringUtils.isNotEmpty(columnId)) {
			try {
				Integer.parseInt(columnId);
			} catch (Exception ex) {
				TagLogger.debug(tagName, "栏目ID不是有效的属性值", request
						.getQueryString(), null);
			}
			Columns column = getBussinessService(request).getColumns(
					Integer.parseInt(columnId));
			if (column != null) {
				if (column.getPricepackId() != null
						&& column.getPricepackId() != 0) {
					rp = getResourceService(request).getResourcePack(
							column.getPricepackId());
					if (rp == null) {
						TagLogger.debug(tagName, "获取批价信息失败", request
								.getQueryString(), null);
						return new HashMap();
					}
				} else {
					TagLogger.debug(tagName, "批价包ID为空", request
							.getQueryString(), null);
					return new HashMap();
				}
			} else {
				TagLogger.debug(tagName, "获取栏目失败,id为" + columnId + "的栏目不存在！",
						request.getQueryString(), null);
				return new HashMap();
			}
		} else {
			TagLogger.debug(tagName, "栏目Id为空", request.getQueryString(), null);
			return new HashMap();
		}
		String templateId = getParameter("templateId", "");
		int currentPage = request.getParameter(ParameterConstants.PAGE_NUMBER) == null ? 1
				: Integer.parseInt(request
						.getParameter(ParameterConstants.PAGE_NUMBER));
		int pageSize = getIntParameter("pageSize", DEFAULT_SIZE);
		String resourceType = URLUtil.getResourceId(request) == null ? "1"
				: URLUtil.getResourceId(request).substring(0, 1);
		// 判断是否导航
		if (!isNoPageLink()) {
			// List resAll=new ArrayList();
			int realtotal = 0;
			int totalCount = getResourceService(request)
					.getResourceAuthorListByPackIdCount(rp,
							Integer.parseInt(resourceType), inital);
			// for(int i=0;i<totalCount;i++){
			// resAll.add(new Object());
			// }
			// Navigator navi=new Navigator(resAll, currentPage, pageSize, 5);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize
					* this.number, 5);
			// System.out.println("总页数="+navi.getPagecount());
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}

		List<String> authors = getResourceService(request)
				.getResourceAuthorListByPackId(currentPage,
						pageSize * this.number, rp,
						Integer.parseInt(resourceType), inital);
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		for (Iterator it = authors.iterator(); it.hasNext();) {
			loop++;
			String id = (String) it.next();
			// ResourceAuthor author=(ResourceAuthor)it.next();
			ResourceAuthor author = (ResourceAuthor) getResourceService(request)
					.getResourceAuthor(Integer.parseInt(id));
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.TEMPLATE_ID);
			sb.append("=");
			sb.append(templateId);
			sb.append("&");
			sb.append(URLUtil.removeParameter(request.getQueryString(),
					ParameterConstants.TEMPLATE_ID,
					ParameterConstants.AUTHOR_ID,
					ParameterConstants.FEE_BAG_ID,
					ParameterConstants.PAGE_NUMBER,
					ParameterConstants.COLUMN_ID));
			sb.append("&");
			sb.append(ParameterConstants.COLUMN_ID);
			sb.append("=");
			sb.append(columnId);
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(rp.getId());
			sb.append("&");
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append(1);
			sb.append("&");
			sb.append(ParameterConstants.AUTHOR_ID);
			sb.append("=");
			sb.append(author.getId());

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			// obj.put("title",author.getName());
			obj.put("title", (author.getPenName() == null
					|| StringUtils.isEmpty(author.getPenName()) ? author
					.getName() : author.getPenName()));
			obj.put("author", author);
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);

		/*
		 * result = VmInstance.getInstance().parseVM(map, this);
		 * resultMap.put(TagUtil.makeTag(tagName), result);
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
