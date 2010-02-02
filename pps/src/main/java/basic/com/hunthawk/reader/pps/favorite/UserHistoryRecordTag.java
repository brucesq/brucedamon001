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
import com.hunthawk.reader.domain.custom.UserFootprint;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 用户历史记录标签 标签名称：uhistory_list pageSize:页/条 noPageLink:不显示翻页相关的链接
 * 
 * @author liuxh
 * 
 */
public class UserHistoryRecordTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	private static final int DEFAULT_SIZE = 5;

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
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID)==null?"0":request.getParameter(ParameterConstants.PRODUCT_ID);
		int currentPage = Integer.parseInt((request
				.getParameter(ParameterConstants.PAGE_NUMBER) == null ? 1
				: request.getParameter(ParameterConstants.PAGE_NUMBER))
				.toString());
		int pageSize = getIntParameter("pageSize", DEFAULT_SIZE);

		List<UserFootprint> historys = getCustomService(request)
				.getUsetFootprints(RequestUtil.getMobile(), currentPage,
						pageSize,productId);
		// 判断是否导航
		if (!isNoPageLink()) {
			int totalCount = getCustomService(request).getUsetFootprints(
					RequestUtil.getMobile(), 1, 1000,productId).size();
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		for (Iterator it = historys.iterator(); it.hasNext();) {
			loop++;
			UserFootprint history = (UserFootprint) it.next();
			String rid = history.getContentId();
			String url = history.getUrl();
			ResourceAll resource = getResourceService(request).getResource(rid);

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", url);
			String historyTitle = "";// 当前阅读的章节名+书名+页号
			if (resource != null) {
				// 拆分参数
				String[] arr = url.split("&");
				String chapterId = "";
				String pageNum = "";
				for (int i = 0; i < arr.length; i++) {
					if (arr[i].contains(ParameterConstants.CHAPTER_ID)) {
						chapterId = arr[i].split("=")[1];
					}
					if (arr[i].contains(ParameterConstants.PAGE_NUMBER)) {
						pageNum = arr[i].split("=")[1];
					}
				}
				EbookChapterDesc echapter = null;
				MagazineChapterDesc mchapter = null;
				ComicsChapter cchapter = null;
				NewsPapersChapterDesc nchapter = null;
				String chapterName = "";
				if (StringUtils.isNotEmpty(chapterId)) {
					if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_BOOK))) {
						echapter = getResourceService(request)
								.getEbookChapterDesc(chapterId);
						if (echapter != null) {
							chapterName = echapter.getName();
							obj.put("chapter", echapter); // 把章节对象放进去
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_MAGAZINE))) {
						mchapter = getResourceService(request)
								.getMagazineChapterDescById(chapterId);
						if (mchapter != null) {
							/**
							 * 放入卷对象
							 * modify by liuxh 09-11-10
							 */
							EbookTome tome=getResourceService(request).getEbookTomeById(mchapter.getTomeId());
							if(tome!=null){
								obj.put("chapter", tome);//放入卷对象 
								chapterName=tome.getName();
							}
							/**
							 * end
							 */
//							chapterName = mchapter.getName();
//							obj.put("chapter", mchapter); // 把章节对象放进去
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
						nchapter = getResourceService(request)
								.getNewsPapersChapterDescById(chapterId);
						if (nchapter != null) {
							chapterName = nchapter.getName();
							obj.put("chapter", nchapter); // 把章节对象放进去
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_COMICS))) {
						cchapter = getResourceService(request)
								.getComicsChapterById(chapterId);
						if (cchapter != null) {
							chapterName = cchapter.getName();
							obj.put("chapter", cchapter); // 把章节对象放进去
						}
					}
					historyTitle = chapterName
							+ "-"
							+ resource.getName()
							+ (StringUtils.isNotEmpty(pageNum) ? ("-P" + pageNum)
									: "");
				} else {
					historyTitle = resource.getName();
				}
				obj.put("resource", resource); // 把资源对象放进去
				obj.put("pageNum", pageNum);// 把页码放进去
				String imgUrl = CoverPreview.getPreview(
						getResourceService(request), resource, 51);// 把预览图放进去
				obj.put("preview", imgUrl);
			} else {
				TagLogger.debug(tagName, "用户历史记录列表id为" + rid + "的资源未找到",
						request.getQueryString(), null);
				continue;
			}
			obj.put("author", getAuthorName(resource, request));
			obj.put("title", historyTitle);

			lsRess.add(obj);
		}

		map.put("objs", lsRess);
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
	
	private String getAuthorName(ResourceAll resource,
			HttpServletRequest request) {
		String name = "";
		Integer[] authorids = resource.getAuthorIds();
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < authorids.length; i++) {
			ResourceAuthor author = getResourceService(request)
					.getResourceAuthor(authorids[i]);
			str.append((author.getPenName() == null || "".equals(author
					.getPenName())) ? author.getName() : author.getPenName());
			str.append(",");
			if (i == authorids.length - 1) {
				// 去掉最后一个,
				str.replace(str.lastIndexOf(","), str.length(), "");
			}
		}
		try {
			name = StrUtil.getLimitStr(str.toString().trim(),
					ParameterConstants.AUTHOR_NAME_BYTES,
					ParameterConstants.REPLACE_SYMBOL);
		} catch (Exception e) {

			e.printStackTrace();
		}// 作者
		return name;
	}
}
