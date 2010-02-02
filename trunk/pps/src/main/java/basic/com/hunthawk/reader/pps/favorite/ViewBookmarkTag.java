package com.hunthawk.reader.pps.favorite;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.Bookmark;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * 书签功能标签 标签名称：bookmark_view 参数说明： title:返回链接文字 addsuccess:添加 收藏成功显示文字
 * delsuccess:删除 收藏成功显示文字
 * 
 * @author liuxh
 * 
 */
public class ViewBookmarkTag extends BaseTag {

	private CustomService customService;
	private BussinessService bussinessService;
	private ResourceService resourceService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String flag = request.getParameter(ParameterConstants.COMMON_PAGE);
		if (StringUtils.isEmpty(flag)) {
			TagLogger
					.debug(tagName, "fn参数获取失败", request.getQueryString(), null);
		} else {
			if (flag
					.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_BOOKMARK_ADD)) {// 添加操作
				return addBookMarkFunction(request, tagName);
			} else if (flag
					.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_BOOKMARK_DEL)) {// 删除操作
				return deleteBookMarkFunction(request, tagName);
			}
		}
		return new HashMap();
	}

	/**
	 * 书签添加方法
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map addBookMarkFunction(HttpServletRequest request, String tagName) {
		String pid=request.getParameter(ParameterConstants.PRODUCT_ID)==null?"0":request.getParameter(ParameterConstants.PRODUCT_ID);
		
		boolean isAdd = true;
		String title = getParameter("title", "返回");
		String rid = URLUtil.getResourceId(request);
		if (rid == null || "".equals(rid)) {
			TagLogger
					.debug(tagName, "资源ID获取失败", request.getQueryString(), null);
			return new HashMap();
		}
		boolean ERROR_FLAG = false;
		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl
				.append(URLUtil.removeParameter(request.getQueryString(),
						ParameterConstants.TEMPLATE_ID,
						ParameterConstants.COMMON_PAGE));
		Bookmark mark = new Bookmark();
		mark.setCreateTime(new Date());
		mark.setMobile(RequestUtil.getMobile());

		String flag = request.getParameter(ParameterConstants.PAGE);
		String markTitle = "";
		if (flag.equals(ParameterConstants.PAGE_PRODUCT)) {// 产品页
			String productId = request
					.getParameter(ParameterConstants.PRODUCT_ID);
			Product pro = null;
			if (productId != null && !"".equals(productId)) {
				pro = getBussinessService(request).getProduct(productId);
				markTitle = pro.getName();
			} else {
				markTitle = "产品页";
			}
		} else if (flag.equals(ParameterConstants.PAGE_COLUMN)) {// 栏目页
			String columnId = request
					.getParameter(ParameterConstants.COLUMN_ID);
			Columns col = null;
			if (columnId != null && !"".equals(columnId)) {
				col = getBussinessService(request).getColumns(
						Integer.parseInt(columnId));
				markTitle = col.getName();
			} else {
				markTitle = "栏目页";
			}
		} else if (flag.equals(ParameterConstants.PAGE_DETAIL)) {// 详情页
			ResourceAll res = getResourceService(request).getResource(rid);
			// 当前阅读的章节名＋卷名＋书名+页号
			if (res != null
					&& request.getParameter(ParameterConstants.CHAPTER_ID) != null
					&& !"".equals(request
							.getParameter(ParameterConstants.CHAPTER_ID))) {
				EbookChapterDesc echapter = null;
				MagazineChapterDesc mchapter = null;
				ComicsChapter cchapter = null;
				NewsPapersChapterDesc nchapter = null;
				String chapterId = request
						.getParameter(ParameterConstants.CHAPTER_ID);
				String chapterName = "";
				if (chapterId
						.startsWith(String.valueOf(ResourceType.TYPE_BOOK))) {
					echapter = getResourceService(request).getEbookChapterDesc(
							chapterId);
					if (echapter != null)
						chapterName = echapter.getName();
				} else if (chapterId.startsWith(String
						.valueOf(ResourceType.TYPE_MAGAZINE))) {
					mchapter = getResourceService(request)
							.getMagazineChapterDescById(chapterId);
					if (mchapter != null)
						chapterName = mchapter.getName();
				} else if (chapterId.startsWith(String
						.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
					nchapter = getResourceService(request)
							.getNewsPapersChapterDescById(chapterId);
					if (nchapter != null)
						chapterName = nchapter.getName();
				} else if (chapterId.startsWith(String
						.valueOf(ResourceType.TYPE_COMICS))) {
					cchapter = getResourceService(request)
							.getComicsChapterById(chapterId);
					if (cchapter != null)
						chapterName = cchapter.getName();
				}

				markTitle = chapterName
						+ "-"
						+ res.getName()
						+ "-"
						+ "P"
						+ (request.getParameter(ParameterConstants.PAGE_NUMBER) == null ? "1"
								: request
										.getParameter(ParameterConstants.PAGE_NUMBER));
			} else {
				markTitle = "详情页";
			}

		} else if (flag.equals(ParameterConstants.PAGE_RESOURCE)) {// 资源页
			ResourceAll res = getResourceService(request).getResource(rid);
			if (res != null) {
				markTitle = res.getName();
			} else {
				markTitle = "资源页";
			}

		}
		mark.setTitle(markTitle);
		int showType = Integer.parseInt(rid.substring(0, 1));
		mark.setType(showType);
		mark.setUrl(backUrl.toString());
		mark.setProductId(pid);
		String error_info="";
		int sign=1;//默认成功
		try {
			getCustomService(request).addBookmark(mark);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println(e.toString());
			TagLogger.debug(tagName, "书签添加失败", request.getQueryString(), e);
			ERROR_FLAG = true;
			sign=0;
			error_info=e.getMessage();
		}

		String addsuccess_msg = getParameter("addsuccess", "书签添加成功");
		if (ERROR_FLAG) {
			addsuccess_msg = error_info;
		}
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("addsuccess", addsuccess_msg);
		// 添加手机号
		velocityMap.put("mobile", RequestUtil.getMobile());
		// 添加手机类型
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", sign);
		/**
		 * end
		 */

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));

		/*
		 * String content = VmInstance.getInstance().parseVM(velocityMap, this);
		 * Map resultMap = new HashMap();
		 * resultMap.put(TagUtil.makeTag(tagName), content);
		 */

		return resultMap;
	}

	/**
	 * 书签删除方法
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map deleteBookMarkFunction(HttpServletRequest request,
			String tagName) {
		boolean isAdd = false;
		String title = getParameter("title", "返回");
		boolean ERROR_FLAG = false;
		int sign=1;//默认成功
		try {
			// 根据书签id进行删除
			getCustomService(request).deleteBookmark(
					Integer.parseInt(request
							.getParameter(ParameterConstants.BOOK_MARK_ID)));// 书签列表中书签的ID号
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			TagLogger.debug(tagName, "书签删除失败", request.getQueryString(), e);
			ERROR_FLAG = true;
			sign=0;
		}
		String delsuccess_msg = getParameter("delsuccess", "书签删除成功");
		if (ERROR_FLAG) {
			delsuccess_msg = "书签删除失败";
		}
		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(),
				ParameterConstants.TEMPLATE_ID, ParameterConstants.COMMON_PAGE,
				ParameterConstants.BOOK_MARK_ID));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("delsuccess", delsuccess_msg);
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", sign);
		/**
		 * end
		 */
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));

		/*
		 * String content = VmInstance.getInstance().parseVM(velocityMap, this);
		 * Map resultMap = new HashMap();
		 * resultMap.put(TagUtil.makeTag(tagName), content);
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
