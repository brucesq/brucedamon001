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

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 报纸/杂志等 栏目列表标签 标签名称： 参数说明: noPageLink:是否导航 (-1.否 1.是) pageSize:每页显示的资源个数
 * //showType:资源类型 (1图书，2报纸，3杂志，4漫画，5铃声，6视频)
 * 
 * @author liuxh
 * 
 */
public class MNColumnsListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private FeeLogicService feeLogicService;
	private BussinessService bussinessService;
	private static final int DEFAULT_PAGE_SIZE = 10; // 默认显示 10条
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
		// 得到资源类型
		int showType = Integer.parseInt(URLUtil.getResourceId(request)
				.substring(0, 1));
		if (showType == ResourceType.TYPE_MAGAZINE) {// 杂志
			return magazineTag(request, tagName);
		} else if (showType == ResourceType.TYPE_NEWSPAPERS) {// 报纸
			return newspapers(request, tagName);
		} else if (showType == ResourceType.TYPE_COMICS) {// 漫画
			return comicsTag(request, tagName);
		}
		return new HashMap();
	}

	/**
	 * 漫画 章节列表
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map comicsTag(HttpServletRequest request, String tagName) {
		
		String rid = URLUtil.getResourceId(request);
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));
		// 判断是否导航
		if (!isNoPageLink()) {
			int totalCount = getResourceService(request)
					.getComicsChaptersByResourceIDCount(rid);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		int relId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1);// 批价包关联关系ID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}
		List<ComicsChapter> comics = getResourceService(request)
				.getComicsChaptersByResourceId(rid, currentPage, pageSize);
		Map feeMap=getFeeLogicService(request).isFee(productId, rid, mobile, rel, packId, month_fee_bag_id);
		for (Iterator it = comics.iterator(); it.hasNext();) {
			loop++;
			ComicsChapter com = (ComicsChapter) it.next();
			String title = com.getName();
			String tomeId = com.getTomeId();

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());

			/**章节控制点*/
			int choicePoint =rel==null?0: rel.getChoice()==null?0:rel.getChoice();
			if(feeMap==null  || com.getChapterIndex()<choicePoint){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_DETAIL);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
			sb.append(ParameterConstants.CHAPTER_ID);
			sb.append("=");
			sb.append(com.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			if (tomeId != null && !"".equals(tomeId)) {
				EbookTome tome = getResourceService(request).getEbookTomeById(
						tomeId);
				obj.put("tomeTitle", tome.getName());
			}
			obj.put("url", sb.toString());
			obj.put("title", title);
			obj.put("chapter", com);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		
	/**	result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}

	/**
	 * 杂志
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map magazineTag(HttpServletRequest request, String tagName) {
		String rid = URLUtil.getResourceId(request);
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		// 是否导航
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		// 每页显示个数
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页

		// 判断是否导航
		if (!isNoPageLink()) {
			int totalCount = getResourceService(request)
					.getMagazineChaptersByResourceIDCount(rid);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		
		List<MagazineChapterDesc> magazineChapters = getResourceService(request)
				.getMagazineChaptersByResourceID(rid, currentPage, pageSize);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		int relId = ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID, -1);// 批价包关联关系ID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}
		Map feeMap=getFeeLogicService(request).isFee(productId, rid, mobile, rel, packId, month_fee_bag_id);
		for (Iterator it = magazineChapters.iterator(); it.hasNext();) {
			loop++;
			MagazineChapterDesc magazine = (MagazineChapterDesc) it.next();
			String title = magazine.getName();
			String tomeId = magazine.getTomeId();

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
//			/**章节控制点*/
			int choicePoint = rel.getChoice()==null?0:rel.getChoice();
			/**
			 * 修改计费控制点为卷
			 * modify by liuxh 09-11-06
			 */
			EbookTome tome=getResourceService(request).getEbookTomeById(magazine.getTomeId());
			if(feeMap==null  || tome.getTomeIndex()<choicePoint){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_DETAIL);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
			sb.append(ParameterConstants.CHAPTER_ID);
			sb.append("=");
			sb.append(magazine.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
				sb.append("&");
				sb.append(ParameterConstants.WORDAGE);
				sb.append("=");
				sb.append(request.getParameter(ParameterConstants.WORDAGE));
			} 
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			if (tomeId != null && !"".equals(tomeId)) {
//				EbookTome tome = getResourceService(request).getEbookTomeById(
//						tomeId);
				obj.put("tomeTitle", tome.getName());
			}
			obj.put("url", sb.toString());
			obj.put("title", title);
			obj.put("chapter", magazine);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
	/**	result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}

	/**
	 * 报纸
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map newspapers(HttpServletRequest request, String tagName) {
		// 是否显示标题
		String flag = request.getParameter(ParameterConstants.SHOW_FLAG); // show=1
		// 显示
		// show=0不显示
		boolean isShowTitle = flag == null ? true : (flag.equals("1") ? true
				: false);
		String tomeId = request.getParameter(ParameterConstants.TOME_ID);
		if (isShowTitle && tomeId == null) {// 显示文章标题
			return getChaptersByResourceID(request, tagName);
		} else {
			if (tomeId != null && !"".equals(tomeId)) {// 根据卷ID查询栏目下的文章列表
				return getChaptersByTomeID(request, tagName);
			} else {// 查询卷列表
				return getTomesList(request, tagName);
			}
		}
	}

	/**
	 * 卷列表
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map getTomesList(HttpServletRequest request, String tagName) {
		String tomeId = "";
		boolean isOut = false;// 是否弹出资费提示
		// 得到资源ID
		String rid = URLUtil.getResourceId(request);
		// 是否导航
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		// 每页显示个数
		int pageSize = getIntParameter("pageSize", -1);
		// 是否显示标题
		boolean isShowTitle = getIntParameter("isShowTitle", 1) > 0;// 默认显示
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		if (pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		int currentPage = 1;// 当前页数，默认为第一页
		try {
			currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} catch (Exception e) {
			TagLogger.debug(tagName, "出错原因=currentPage转整型时异常]", request
					.getQueryString(), e);
		}
		// 判断是否导航
		if (!isNoPageLink()) {
//			List resAll = new ArrayList();
			int totalCount = getResourceService(request).getEbookTomeCount(rid);
//			for (int i = 0; i < totalCount; i++) {
//				resAll.add(new Object());
//			}
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		int relId = -1;// 批价包关系ID
		List<EbookTome> newspagerTomes = getResourceService(request)
				.getEbookTomeByResourceId(rid,1,1000);
		System.out.println("资源" + rid + "卷列表个数为：" + newspagerTomes.size());
		for (Iterator it = newspagerTomes.iterator(); it.hasNext();) {
			loop++;
			EbookTome tome = (EbookTome) it.next();
			String tomeName = tome.getName();// 卷(栏目)名称
			tomeId = tome.getId();// 卷ID

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());

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
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rid);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeId);
			sb.append("&");
			sb.append(ParameterConstants.TOME_ID);
			sb.append("=");
			sb.append(tomeId);

			/** 保存单条记录 */
			Map<String, String> obj = new HashMap<String, String>();
			obj.put("url", sb.toString());
			obj.put("title", tomeName);
			lsRess.add(obj);
		}
		map.put("isTomeList", true);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
	}

	/**
	 * 查询章节列表 按资源ID查询
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map getChaptersByResourceID(HttpServletRequest request,
			String tagName) {
		String rid = URLUtil.getResourceId(request);
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));
		if (!isNoPageLink()) {
			int totalCount = getResourceService(request)
					.getNewsPapersChaptersByResourceIDCount(rid);
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<NewsPapersChapterDesc> newsPaperChapters = getResourceService(
				request).getNewsPapersChaptersByResourceID(rid, currentPage,
				pageSize);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		int relId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1);// 批价包关联关系ID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}
		Map feeMap=getFeeLogicService(request).isFee(productId, rid, mobile, rel, packId, month_fee_bag_id);
		for (Iterator it = newsPaperChapters.iterator(); it.hasNext();) {
			loop++;
			NewsPapersChapterDesc newsPaper = (NewsPapersChapterDesc) it.next();
			String title = newsPaper.getName();
			String tomeId = newsPaper.getTomeId();
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());

			/**章节控制点*/
			int choicePoint =rel==null?0: rel.getChoice()==null?0:rel.getChoice();
			if(feeMap==null  || newsPaper.getChapterIndex()<choicePoint){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_DETAIL);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rid);
			sb.append("&");
			sb.append(ParameterConstants.CHAPTER_ID);
			sb.append("=");
			sb.append(newsPaper.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
				sb.append("&");
				sb.append(ParameterConstants.WORDAGE);
				sb.append("=");
				sb.append(request.getParameter(ParameterConstants.WORDAGE));
			} 
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			// if(loop!=1){
			// if(tomeId!=start){
			EbookTome tome = getResourceService(request).getEbookTomeById(
					tomeId);
			// obj.put("num",String.valueOf(loop));
			obj.put("tomeTitle", tome.getName());
			// }
			// }
			obj.put("url", sb.toString());
			obj.put("title", title);
			obj.put("chapter", newsPaper);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", false);
		map.put("objs", lsRess);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		/**result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
	}

	/**
	 * 查询章节列表 按卷ID查询
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map getChaptersByTomeID(HttpServletRequest request, String tagName) {
		String tomeId = request.getParameter(ParameterConstants.TOME_ID);
		EbookTome tome = getResourceService(request).getEbookTomeById(tomeId);
		boolean isOut = false;// 是否弹出资费提示
		// 得到资源ID
		String rid = URLUtil.getResourceId(request);
		// 是否导航
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		// 每页显示个数
		int pageSize = getIntParameter("pageSize", -1);
		// 是否显示标题
		boolean isShowTitle = getIntParameter("isShowTitle", 1) > 0;// 默认显示
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		if (pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		int currentPage = 1;// 当前页数，默认为第一页
		try {
			currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} catch (Exception e) {
			TagLogger.debug(tagName, "出错原因=currentPage转整型时异常]", request
					.getQueryString(), e);
		}
		// 判断是否导航
		if (!isNoPageLink()) {
//			List resAll = new ArrayList();
			int totalCount = getResourceService(request)
					.getNewsPapersChapterDescCountByTomeId(tomeId);
//			for (int i = 0; i < totalCount; i++) {
//				resAll.add(new Object());
//			}
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<NewsPapersChapterDesc> newspapers = getResourceService(request)
				.getNewsPapersChapterDescByTomeId(tomeId, currentPage, pageSize);
		System.out.println("id为" + tomeId + "资源列表个数为" + newspapers.size());
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		int relId = ParamUtil.getIntParameter(request,
				ParameterConstants.FEE_BAG_RELATION_ID, -1);// 批价包关联关系ID
		ResourcePackReleation rel = null;
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}

		boolean isWhiteList = RequestUtil.isFeeDisabled();// 是否是白名单用户
		// 查询用户购买表如果 此表中存在这个资源则不添加计费路径
		boolean isBuy = getCustomService(request).isUserBuyBook(
				RequestUtil.getMobile(), rid);// 判断用户是否已购买

		for (Iterator it = newspapers.iterator(); it.hasNext();) {
			loop++;
			NewsPapersChapterDesc newsPaper = (NewsPapersChapterDesc) it.next();
			String title = newsPaper.getName();

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			if (isWhiteList || isBuy || rel == null
					|| rel.getPack().getType() == Constants.FEE_TYPE_FREE) {
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			} else {
				feeId = request.getParameter(ParameterConstants.FEE_ID);
				if (feeId == null || "".equals(feeId) || "null".equals(feeId)) {
					feeId = rel.getFeeId();
					if (feeId == null || "".equals(feeId)) {

						feeId = rel.getPack().getFeeId();
						if (feeId == null || "".equals(feeId)) {
							TagLogger.debug("ChapterListTag", "feeId为空",
									request.getQueryString(), null);
							return new HashMap();
						}
					}
				}
				Fee fee = getCustomService(request).getFee(feeId);

				isOut = fee.getIsout() == 1;// 是否弹资费提示

				if (!isOut) {
					sb.append("/");
					sb.append(fee.getUrl());
				}
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
				if (isOut) {
					sb.append(ParameterConstants.COMMON_PAGE);
					sb.append("=");
					sb.append(ParameterConstants.COMMON_PAGE_FEE);
					sb.append("&");
					sb.append(ParameterConstants.TEMPLATE_ID);
					sb.append("=");
					sb.append(fee.getTemplateId());
					sb.append("&");
				}

			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_DETAIL);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rid);
			sb.append("&");
			sb.append(ParameterConstants.CHAPTER_ID);
			sb.append("=");
			sb.append(newsPaper.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
				sb.append("&");
				sb.append(ParameterConstants.WORDAGE);
				sb.append("=");
				sb.append(request.getParameter(ParameterConstants.WORDAGE));
			} 
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeId);

			/** 保存单条记录 */
			Map<String, String> obj = new HashMap<String, String>();

			obj.put("url", sb.toString());
			obj.put("title", title);
			lsRess.add(obj);
		}
		map.put("isTomeList", false);
		map.put("byTomeId", true);
		map.put("tomeTitle", tome.getName());
		map.put("objs", lsRess);
		result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);
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
	private FeeLogicService getFeeLogicService(HttpServletRequest request) {
		if (feeLogicService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeLogicService = (FeeLogicService) wac.getBean("feeLogicService");
		}
		return feeLogicService;
	}
	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
	}
}
