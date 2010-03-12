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
import com.hunthawk.reader.domain.custom.Bookmark;
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
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 书签列表标签 标签名称：bookmark_list 参数说明： pageSize:每页显示的条数 noPageLink:不显示翻页相关的链接
 * templateId:删除确定页模板ID isConfirm:是否做确认删除 1.确认提示 -1.直接删除不确认 showDelLink:是否显示删除链接
 * 
 * @author liuxh
 * 
 */
public class BookmarkListTag extends BaseTag {

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
		String productId = request.getParameter(ParameterConstants.PRODUCT_ID);

		Map resultMap = new HashMap();
		String result = "";

		boolean isConfirm = getIntParameter("isConfirm", 1) > 0;// 判断是否进行删除确认
		// 默认确认
		boolean showDelLink = getIntParameter("showDelLink", 1) > 0;// 判断是否显示删除链接
		// 默认显示
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1) < 0 ? DEFAULT_PAGE_SIZE
				: getIntParameter("pageSize", -1);
		int currentPage = request.getParameter(ParameterConstants.PAGE_NUMBER) == null ? 1
				: Integer.parseInt(request
						.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页
		// 得到手机号
		String mobile = RequestUtil.getMobile();
		List bookMarks = getCustomService(request).getUserBookmarksByPage(
				mobile, productId, pageSize, currentPage);

		// 判断是否导航
		if (!isNoPageLink()) {
			int totalCount = getCustomService(request)
					.getUserBookmarkResultCount(mobile, productId);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}

		// 删除模板ID
		String templateId = getParameter("templateId", "");
		String msg = "";
		// 把对象存到list返回
		List<Object> objs = new ArrayList<Object>();
		if (bookMarks == null || bookMarks.size() < 1) {
			msg = "目前没有书签。";
		} else {
			msg = "";
			// 带链接的列表 根据资源 跳到内容详情页
			for (Iterator it = bookMarks.iterator(); it.hasNext();) {
				Bookmark mark = (Bookmark) it.next();
				String tid = String.valueOf(mark.getId());
				String tmark = mark.getTitle();
				String umark = mark.getUrl();
				String url = umark;
				// 单个obj
				Map<String, Object> obj = new HashMap<String, Object>();
				String tempTitle = tmark;
				obj.put("url", url);
				obj.put("value", tempTitle);
				Map mapMark = new HashMap();
				mapMark.put(ParameterConstants.BOOK_MARK_ID, tid);
				mapMark.put(ParameterConstants.PARAM_URL, url);
				String delurl = getPageUrl(request, isConfirm, templateId,
						mapMark);
				obj.put("urldel", delurl);
				obj.put("valuedel", "[删]");
				// 加入objs

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

				String resourceId = URLUtil.getURLvalue(url, "rd");// 资源
				// String chapterId= URLUtil.getURLvalue(url, "zd");//章节
				if (!StringUtils.isNotEmpty(resourceId))
					resourceId = chapterId.substring(0, chapterId.length() - 3); // 截取章节id赋值给资源

				EbookChapterDesc echapter = null;
				MagazineChapterDesc mchapter = null;
				ComicsChapter cchapter = null;
				NewsPapersChapterDesc nchapter = null;

				if (StringUtils.isNotEmpty(chapterId)) {
					if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_BOOK))) {
						echapter = getResourceService(request)
								.getEbookChapterDesc(chapterId);
						if (echapter != null) {
							obj.put("chapter", echapter); // 把章节对象放进去
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_MAGAZINE))) {
						mchapter = getResourceService(request)
								.getMagazineChapterDescById(chapterId);
						if (mchapter != null) {
							/**
							 * 放入卷对象 modify by liuxh 09-11-10
							 */
							EbookTome tome = getResourceService(request)
									.getEbookTomeById(mchapter.getTomeId());
							if (tome != null)
								obj.put("chapter", tome);// 放入卷对象
							/**
							 * end
							 */
							// obj.put("chapter", mchapter); // 把章节对象放进去
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_NEWSPAPERS))) {
						nchapter = getResourceService(request)
								.getNewsPapersChapterDescById(chapterId);
						if (nchapter != null) {
							obj.put("chapter", nchapter); // 把章节对象放进去
						}
					} else if (chapterId.startsWith(String
							.valueOf(ResourceType.TYPE_COMICS))) {
						cchapter = getResourceService(request)
								.getComicsChapterById(chapterId);
						if (cchapter != null) {
							obj.put("chapter", cchapter); // 把章节对象放进去
						}
					}
				}

				ResourceAll resource = getResourceService(request).getResource(
						resourceId);
				if (resource != null) {
					obj.put("resource", resource); // 把资源对象放进去
					obj.put("pageNum", pageNum);// 把页码放进去
					String imgUrl = CoverPreview.getPreview(
							getResourceService(request), resource, 51);// 把预览图放进去
					obj.put("preview", imgUrl);
					obj.put("author", getAuthorName(resource, request));
				}
				obj.put("bookMark", mark);// 新添加进去的 书签对象
				

				objs.add(obj);
			}
			map.put("objs", objs);
			map.put("showdel", showDelLink);

		}
		map.put("msg", msg);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service", getResourceService(request));
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
	private String getPageUrl(HttpServletRequest request, boolean isConfirm,
			String templateid, Map obj) {
		// String url=obj.get(ParameterConstants.PARAM_URL).toString();
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
		sb.append(ParameterConstants.BOOK_MARK_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.BOOK_MARK_ID));

		return sb.toString();
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
