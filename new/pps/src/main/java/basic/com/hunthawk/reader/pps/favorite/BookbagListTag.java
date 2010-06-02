package com.hunthawk.reader.pps.favorite;

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

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * 书包列表标签 标签名称：bookbag_list 参数说明： pageSize:每页显示的条数 noPageLink:不显示翻页相关的链接
 * templateId:删除确定页模板ID isConfirm:是否做确认删除 1.确认提示 -1.直接删除不确认 showDelLink:是否显示删除链接
// * columnids:栏目ID集合  用于获取不同类型产品下的资源 用不同的模版  先后顺序 图书-报纸-杂志-漫画
 * bColumnid:图书栏目
 * mColumnid:杂志栏目
 * nColumnid：报纸栏目
 * cColumnid：漫画栏目
 * @author liuxh
 * 
 */
public class BookbagListTag extends BaseTag {

	private CustomService customService;
	private BussinessService bussinessService;
	private ResourceService resourceService;

	private static final int DEFAULT_PAGE_SIZE = 10; // 默认显示10条
	/** 不显示翻页相关的链接 */
	private boolean noPageLink;

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub

		/**
		 * modify by liuxh 09-11-12
		 * 增加标签 参数 
		 */
			int bColumnid=getIntParameter("bColumnid",-1);
			int mColumnid=getIntParameter("mColumnid",-1);
			int nColumnid=getIntParameter("nColumnid",-1);
			int cColumnid=getIntParameter("cColumnid",-1);
			Integer[] cids=new Integer[]{bColumnid,nColumnid,mColumnid,cColumnid};
			
		/**
		 * end
		 */
		Map resultMap = new HashMap();
		String result = "";

		boolean isConfirm = getIntParameter("isConfirm", 1) > 0;// 判断是否进行删除确认
		// 默认确认
		boolean showDelLink = getIntParameter("showDelLink", 1) > 0;// 判断是否显示删除链接
		// 默认显示
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页

		// 得到手机号
		String mobile = RequestUtil.getMobile();
		List bookBags = getCustomService(request).getUserBookbagsByPage(mobile,"",
				pageSize, currentPage);

		// 判断是否导航
		if (!isNoPageLink()) {
			List resAll = getCustomService(request).getUserBookbag(mobile);
			Navigator navi = new Navigator(resAll.size(), currentPage,
					pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}

		// 删除模板ID
		String templateId = getParameter("templateId", "");
		String msg = "";
		// 把对象存到list返回
		List<Object> objs = new ArrayList<Object>();
		if (bookBags == null || bookBags.size() < 1) {
			msg = "您目前没有预定图书。";
		} else {
			msg = "";
			for (Iterator it = bookBags.iterator(); it.hasNext();) {
				BookBag bag = (BookBag) it.next();
				String rid = bag.getContentId();// 内容id
				String channelId = bag.getChannelId();// 渠道ID
				String pid = bag.getPid();// 产品ID
				// 封装参数
				Map mapBag = new HashMap();
				mapBag.put(ParameterConstants.PRODUCT_ID, pid);
				mapBag.put(ParameterConstants.RESOURCE_ID, rid);
				Integer columnId=cids[Integer.parseInt(rid.substring(0,1))-1];
				if(columnId<0){
					TagLogger.debug(tagName, "栏目ID集合信息为空或指定的栏目ID集合参数不全(4个)", request.getQueryString(), null);
					columnId=ParamUtil.getIntParameter(request, ParameterConstants.COLUMN_ID, -1);
				}
				mapBag.put(ParameterConstants.COLUMN_ID, columnId);
				String url = getPageUrl(request, mapBag);// 资源URL
				ResourceAll resource = getResourceService(request).getResource(
						rid);

				if (resource == null) {
					continue;
				}
				// 单个obj
				Map<String, Object> obj = new HashMap<String, Object>();
				String tempTitle = resource.getName();// 图书名称
				obj.put("url", url);
				obj.put("value", tempTitle);
				// obj.put("delete", delete.toLowerCase());
				String delurl = delUrl(request, isConfirm, "1", templateId,
						mapBag);
				obj.put("urldel", delurl);
				obj.put("valuedel", "[删]");
				obj.put("resource", resource); // 新添加上了 资源对象
				String imgUrl = CoverPreview.getPreview(
						getResourceService(request), resource, 51);// 把预览图放进去
				obj.put("preview", imgUrl);
				// 加入objs
				objs.add(obj);
			}
			map.put("objs", objs);
			map.put("showdel", showDelLink);
		}
		map.put("msg", msg);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
		// 添加手机号
		map.put("mobile", RequestUtil.getMobile());
		// 添加手机类型
		map.put("mobileType", RequestUtil.getMobileType());
		
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

	/**
	 * 
	 */
	/**
	 * 页面级别参数+产品ID+页面组ID+区域ID+栏目ID+批价包ID+批价包关联ID+资源ID+章节ID+推广渠道ID
	 * +联通PT参数+页码+每页显示字数+计费ID
	 * 
	 * @param request
	 * @param currentPage
	 *            当前页 导航URL
	 * @param templateid
	 *            删除 和资源 URL
	 * @param flag
	 *            删除和资源链接的判断标志
	 * @param contentid
	 *            删除和资源操作需传入的内容ID参数
	 * @return
	 */
	private String getPageUrl(HttpServletRequest request, Map obj) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_RESOURCE);
		sb.append("&");
		sb.append(ParameterConstants.PRODUCT_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.PRODUCT_ID));
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		sb.append(ParameterConstants.COLUMN_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.COLUMN_ID));
		sb.append("&");
//		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		sb.append(ParameterConstants.RESOURCE_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.RESOURCE_ID));
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append(1);

		return sb.toString();
	}

	private String delUrl(HttpServletRequest request, boolean isConfirm,
			String currentPage, String templateid, Map obj) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		if (!isConfirm) {// 不做删除确认
			sb.append(ParameterConstants.COMMON_PAGE);
			sb.append("=");
			sb.append(ParameterConstants.COMMON_PAGE_BOOKMARK_DEL);
			sb.append("&");
		}
		sb.append(ParameterConstants.TEMPLATE_ID);
		sb.append("=");
		sb.append(templateid);
		sb.append("&");
		String url = request.getQueryString();
		sb.append(URLUtil.removeParameter(
				url.substring((url.indexOf("?") + 1)),
				ParameterConstants.TEMPLATE_ID));
		sb.append("&");
		sb.append(ParameterConstants.RESOURCE_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.RESOURCE_ID));

		return sb.toString();
	}
}
