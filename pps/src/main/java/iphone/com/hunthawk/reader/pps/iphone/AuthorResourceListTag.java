package com.hunthawk.reader.pps.iphone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
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
import com.hunthawk.tag.components.wml.Anchor;
import com.hunthawk.tag.components.wml.Go;
import com.hunthawk.tag.components.wml.IPostfieldModel;
import com.hunthawk.tag.components.wml.SimplePostfieldModel;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 作者相关资源列表标签 标签名称:author_books_list
 * 
 * @author liuxh 参数说明：
 * @param pageSize
 *            每页显示的资源条数
 * @param noPageLink
 *            不显示翻页相关的链接
 */
public class AuthorResourceListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private BussinessService bussinessService;

	private static final int DEFAULT_PAGE_SIZE = 5; // 默认显示5条
	/** 不显示翻页相关的链接 */
	private boolean noPageLink;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;

		// 得到批价包ID
		String pId = request.getParameter(ParameterConstants.FEE_BAG_ID);
		if (pId == null || StringUtils.isEmpty(pId)) {
			TagLogger.debug(tagName, "批价包Id为空，无法获取作者关联作品列表", request
					.getQueryString(), null);
			return new HashMap();
		}
		ResourcePack rp = getResourceService(request).getResourcePack(
				Integer.parseInt(pId));
		if (rp == null) {
			TagLogger.debug(tagName, "获取批价包信息失败,id为" + pId + "的批价包不存在", request
					.getQueryString(), null);
			return new HashMap();
		}

		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		ResourceAuthor author = getResourceService(request).getResourceAuthor(
				Integer.parseInt(request
						.getParameter(ParameterConstants.AUTHOR_ID)));
		// 每页显示资源条数
		int pageSize = getIntParameter("pageSize", -1);
		if (pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		int currentPage = 1;// 当前页数，默认为第一页
		try {
			currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} catch (Exception e) {
		}

		// 判断是否导航
		if (!isNoPageLink()) {
//			List resAll = new ArrayList();
			int totalCount = getResourceService(request)
					.getResourceCountByAuthorId(
							getResourceType(URLUtil.getResourceId(request)), rp,
							author.getId().toString());
//			for (int i = 0; i < totalCount; i++) {
//				resAll.add(new Object());
//			}
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
//		String feeId = "";
		List<ResourcePackReleation> rprs = getResourceService(request)
				.getResourceByAuthorId(getResourceType(URLUtil.getResourceId(request)), rp,
						author.getId().toString(), currentPage, pageSize);
//		List<String> rprs = getResourceService(request).getResourceByAuthorId(getResourceType(URLUtil.getResourceId(request)), rp,
//				author.getId().toString(), currentPage, pageSize);
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			//getResourceService(request).getResourcePackReleation(Integer.parseInt(it.next().toString()));
			ResourcePackReleation rpr = (ResourcePackReleation) it.next();
			ResourceAll res = getResourceService(request).getResource(
					rpr.getResourceId());
			// 拼URL
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
//			feeId = rpr.getFeeId();
//			if (feeId == null || "".equals(feeId)) {
//				feeId = rpr.getPack().getFeeId();
//			}
//			Fee fee =  getCustomService(request).getFee(feeId);
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(rpr.getPack().getId());
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(rpr.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(res.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
//			if (fee!= null) {
//				sb.append("&");
//				sb.append(ParameterConstants.FEE_ID);
//				sb.append("=");
//				sb.append(feeId);
//			}

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			String resName=res.getName();
			try {
				obj.put("title", StrUtil.getLimitStr(resName, ParameterConstants.RESOURCE_NAME_BYTES,ParameterConstants.REPLACE_SYMBOL));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			obj.put("status",  getBookStatus(res.getIsFinished()));//状态
			obj.put("preview", imagePreview(request,tagName,res));//资源预览图
			Integer [] authorids=res.getAuthorIds();
			String authorPenName=(author.getPenName()==null || "".equals(author.getPenName()))?author.getName():author.getPenName();
			try {
				obj.put("author", StrUtil.getLimitStr(authorPenName, ParameterConstants.AUTHOR_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//作者
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(res.getId())));//点击
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(res.getId())));//收藏
			obj.put("resource", res);
			lsRess.add(obj);
		}// for end
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
		// }
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		/*result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}
	private String imagePreview(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		// 判断资源类型(1图书，2报纸，3杂志，4漫画，5铃声，6视频)
		if (resource.getId().startsWith("1")) {// 图书
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				return getResourceService(request).getPreviewCoverImg(
						ebook.getId(), ebook.getBookPic(),51);
			}
		} else if (resource.getId().startsWith("2")) {// 报纸
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				return  getResourceService(request).getPreviewCoverImg(
						n.getId(), n.getImage(),51);
			}
		} else if (resource.getId().startsWith("3")) {// 杂志
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				return getResourceService(request).getPreviewCoverImg(
						magazine.getId(), magazine.getImage(),51);
			}
		} else if (resource.getId().startsWith("4")) {// 漫画
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				return getResourceService(request).getPreviewCoverImg(
						comics.getId(), comics.getImage(),51);
			}
		}
		return "";
	}
	private String getBookStatus(int status) {
		String typeName = "";
		for (Map.Entry<String, Integer> entry : Constants.getResourceFinished()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return "("+entry.getKey()+")";
		}
		return typeName;
	}
	private int getResourceType(String rid) {
		if(rid==null || StringUtils.isEmpty(rid))
			return ResourceType.TYPE_BOOK;
		else
			return Integer.parseInt(rid.substring(0, 1));
	}

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}

	private String getPageUrl(HttpServletRequest request, String currentPage) {
		StringBuilder sb = new StringBuilder();
		// 得到当前工程的名称
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		URLUtil.append(sb, ParameterConstants.PAGE, request);
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.PRODUCT_ID))) {
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.PAGEGROUP_ID))) {
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.AREA_ID))) {
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.COLUMN_ID))) {
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_RELATION_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_RELATION_ID))) {
			URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.CHAPTER_ID))) {
			URLUtil.append(sb, ParameterConstants.CHAPTER_ID, request);
		}
		if (!StringUtils.isEmpty(request
				.getParameter(ParameterConstants.CHANNEL_ID))
				|| request.getParameter(ParameterConstants.CHANNEL_ID) == null) {
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		}
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append(currentPage);
		sb.append("&");
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.WORDAGE))) {
			URLUtil.append(sb, ParameterConstants.WORDAGE, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.TEMPLATE_ID))) {
			URLUtil.append(sb, ParameterConstants.TEMPLATE_ID, request);
		}
		return sb.toString();
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
