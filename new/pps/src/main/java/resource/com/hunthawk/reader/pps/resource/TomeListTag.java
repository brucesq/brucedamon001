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

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
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
 * 卷列表标签
 * 标签名称：tome_list
 * 参数说明：
 * 		pageSize:每页显示的资源数
 * 		noPageLink:不显示翻页相关的链接
 * @author liuxh	09-11-05
 *
 */
public class TomeListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;
	private IphoneService iphoneService;
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

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
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
		this.currentPage=ParamUtil.getIntParameter(request, ParameterConstants.PAGE_NUMBER, 1);
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		int pageSize = getIntParameter("pageSize", -1) < 0 ? DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		
		String resourceId=URLUtil.getResourceId(request);
		if(StringUtils.isEmpty(resourceId)){
			TagLogger.debug(tagName, "资源ID为空,卷列表标签解析失败!", request.getQueryString(), null);
			return new HashMap();
		}
		int totalCount = getResourceService(request).getEbookTomeCount(resourceId);
		if (!isNoPageLink()) {
			Navigator navi = new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		List<EbookTome> newspagerTomes = getResourceService(request).getEbookTomeByResourceId(resourceId,currentPage,pageSize);
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result = "";
		
		/**杂志计费相关参数*/
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		ResourcePackReleation rel = null;
		int relId = ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID, -1);// 批价包关联关系ID
		if (relId != -1) {
			rel = getResourceService(request).getResourcePackReleation(relId);
		}
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, RequestUtil.getMobile(), rel, packId, month_fee_bag_id);
		/**end*/
		for (Iterator it = newspagerTomes.iterator(); it.hasNext();) {
			EbookTome tome = (EbookTome) it.next();
			String tomeName = tome.getName();// 卷(栏目)名称
			String tomeId = tome.getId();// 卷ID
			
			String chapId="";
			/**根据tomeId查询章节列表 得到第一章的ID*/
			if(tomeId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//图书
				List<EbookChapterDesc> books=getResourceService(request).getEbookChapterDescByTomeId(tomeId,1,totalCount);
				if(books.size()>0){
					chapId=books.get(0).getId();
				}else{
					TagLogger.debug(tagName, "卷ID为"+tomeId+"的图书无章节信息", request.getQueryString(), null);
				}
			}else if(tomeId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){//漫画
				List<ComicsChapter> comics=getResourceService(request).getComicsChapterByTomeId(tomeId,1,totalCount);
				if(comics.size()>0){
					chapId=comics.get(0).getId();
				}else{
					TagLogger.debug(tagName, "卷ID为"+tomeId+"的漫画无章节信息", request.getQueryString(), null);
				}
			}else if(tomeId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//杂志
				List<MagazineChapterDesc> magazines=getResourceService(request).getMagazineChapterDescByTomeId(tomeId,1,totalCount);
				if(magazines.size()>0){
					chapId=magazines.get(0).getId();
				}else{
					TagLogger.debug(tagName, "卷ID为"+tomeId+"的杂志无章节信息", request.getQueryString(), null);
				}
			}else if(tomeId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){//报纸
				List<NewsPapersChapterDesc> newspapers =getResourceService(request).getNewsPapersChapterDescByTomeId(tomeId,1,totalCount);
				if(newspapers.size()>0){
					chapId=newspapers.get(0).getId();
				}else{
					TagLogger.debug(tagName, "卷ID为"+tomeId+"的报纸无章节信息", request.getQueryString(), null);
				}
			}
			if(StringUtils.isEmpty(chapId))
				continue;
//				return new HashMap();
			
			String  url="";
			/**
			 * 杂志资源 增加计费逻辑
			 * modify by liuxh 09-11-06
			 */
			if(tomeId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
				int chapterIndex=tome.getTomeIndex();//卷序号
				StringBuilder sb=new StringBuilder();
				sb.append(request.getContextPath());
				/**章节控制点*/
				int choicePoint =rel==null?0:rel.getChoice()==null?0:rel.getChoice();
				if(feeMap==null  || chapterIndex<choicePoint){
					sb.append(ParameterConstants.PORTAL_PATH);
					sb.append("?");
				}else{
					if(getIphoneService(request).isIphoneProduct(productId))
						tomeName="$$"+tomeName;
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
				sb.append(chapId);
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
				url=sb.toString();
			}
			/**
			 * end
			 */
			else{
				Map<String,String> values=new HashMap<String,String>();
				values.put(ParameterConstants.PAGE, ParameterConstants.PAGE_DETAIL);
				values.put(ParameterConstants.CHAPTER_ID, chapId);
				values.put(ParameterConstants.PAGE_NUMBER, "1");
				List<String> blackName=new ArrayList();
				blackName.add(ParameterConstants.TEMPLATE_ID);
				blackName.add(ParameterConstants.COMMON_PAGE);
				url=URLUtil.urlChange(request, values, blackName);
			}

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url",url);
			obj.put("title", tomeName);
			obj.put("tome", tome);
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
//		result = VmInstance.getInstance().parseVM(map, this);
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
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
	private FeeLogicService getFeeLogicService(HttpServletRequest request) {
		if (feeLogicService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeLogicService = (FeeLogicService) wac
					.getBean("feeLogicService");
		}
		return feeLogicService;
	}
	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac
					.getBean("iphoneService");
		}
		return iphoneService;
	}
}
