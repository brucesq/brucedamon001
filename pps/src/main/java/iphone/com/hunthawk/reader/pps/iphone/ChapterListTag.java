package com.hunthawk.reader.pps.iphone;

import java.io.UnsupportedEncodingException;
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
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
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
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * 
 * @author BruceSun
 *
 */
public class ChapterListTag extends BaseTag {

	private BussinessService bussinessService;
	private ResourceService resourceService;
	private CustomService customService;
	private IphoneService iphoneService;
	private FeeLogicService feeLogicService;

	private static final int DEFAULT_PAGE_SIZE = 10;

	/** 不显示翻页相关的链接 */
	private boolean noPageLink;
	/** 在第几页显示此标签内容 */
	// private int number;
	/** 当前页 */
	private int currentPage;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		String mobile=RequestUtil.getMobile();
		String resourceId=URLUtil.getResourceId(request);
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		this.currentPage=request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));
		int pageSize = getIntParameter("pageSize", -1) < 0 ? DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		String resId = URLUtil.getResourceId(request);// 资源ID
		Map resultMap = new HashMap();
		if(resId!=null && !"".equals(resId)){
			if(!resId.startsWith(ResourceType.TYPE_BOOK.toString())){
				String value = "$#imnccolumns_list.noPageLink="+noPageLink+",pageSize="+pageSize+",tmd="+tagTemplateId+"#";
				resultMap.put(TagUtil.makeTag(tagName), value);
				return resultMap;
			}
		}else{
			TagLogger.debug(tagName, "章节列表标签资源Id为空", request.getQueryString(), null);
			return new HashMap();
		}
		ResourcePackReleation rel = null;
		if (ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1)!= -1) {
			rel = getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1));
		}
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		/** 判断是否导航*/
		if (!isNoPageLink()) {
			List<EbookChapterDesc> chaptersAll = getResourceService(request)
					.getEbookChapterDescsByResourceID(resId);
			Navigator navi = new Navigator(chaptersAll.size(), currentPage,
					pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rel, packId, month_fee_bag_id);
		//逻辑控制
			List<EbookChapterDesc> ebookchapters = getResourceService(request).getEbookChaptersByResourceID(resId, currentPage, pageSize);
			List<Object> lsRess = new ArrayList<Object>();
			int loop = 0;
			for (Iterator it = ebookchapters.iterator(); it.hasNext();) {
				loop++;
				EbookChapterDesc ebookChapter = (EbookChapterDesc) it.next();
				String chapterId = ebookChapter.getId();// 章节ID
				String chapName = ebookChapter.getName();// 章节名称
				StringBuilder sb = new StringBuilder();//url
				sb.append(request.getContextPath());
				/**章节控制点*/
				int choicePoint =rel==null?0: rel.getChoice()==null?0:rel.getChoice();
				if(feeMap==null  || ebookChapter.getChapterIndex()<choicePoint){
					sb.append(ParameterConstants.PORTAL_PATH);
					sb.append("?");
				}else{
					chapName = "$$" + chapName;//计费章节 名称前加$$
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
				URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID,
						request);
				sb.append(ParameterConstants.CHAPTER_ID);
				sb.append("=");
				sb.append(chapterId);
				sb.append("&");
				URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
				sb.append(ParameterConstants.PAGE_NUMBER);
				sb.append("=");
				sb.append(1);
				if(!resId.startsWith(ResourceAll.RESOURCE_TYPE_COMICS.toString())){//非漫画资源
					if (StringUtils.isNotEmpty(request.getParameter(ParameterConstants.WORDAGE))) {
						sb.append("&");
						sb.append(ParameterConstants.WORDAGE);
						sb.append("=");
						sb.append(request.getParameter(ParameterConstants.WORDAGE));
					} 
				}
				if(feeMap!=null){
					sb.append("&");
					sb.append(ParameterConstants.FEE_ID);
					sb.append("=");
					sb.append(feeMap.get("feeId"));
				}
				/** 保存单条记录 */
				Map<String, Object> obj = new HashMap<String, Object>();
				obj.put("url", sb.toString());
				try {
					obj.put("title", StrUtil.getLimitStr(chapName, ParameterConstants.CHAPTER_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				obj.put("chapter", ebookChapter);
				lsRess.add(obj);
			}//for end
			map.put("objs", lsRess);
			map.put("service",getResourceService(request));
			
			result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			
			//result = VmInstance.getInstance().parseVM(map, this);
			//resultMap.put(TagUtil.makeTag(tagName), result);
			return resultMap;
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
	
	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac.getBean("iphoneService");
		}
		return iphoneService;
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
}
