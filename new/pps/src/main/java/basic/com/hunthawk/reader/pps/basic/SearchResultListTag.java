package com.hunthawk.reader.pps.basic;

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
import com.hunthawk.reader.pps.PpsUtil;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StatisticsLog;
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
 * 搜索结果列表标签
 * 标签名称：search_result
 * 参数说明：
 * pageSize:每页显示的资源条数
 * noPageLink:不显示翻页相关的链接	
 * mix:链接组合策略           	
 * @author liuxh
 *
 */
public class SearchResultListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;
	
	private static final int DEFAULT_PAGE_SIZE = 5; //默认显示5条
	/** 不显示翻页相关的链接 */
	private boolean noPageLink;
	private Integer urlOrder;
	private Integer urlOrderSub;
	
	public Integer getUrlOrder() {
		return urlOrder;
	}
	public void setUrlOrder(Integer urlOrder) {
		this.urlOrder = urlOrder;
	}
	public Integer getUrlOrderSub() {
		return urlOrderSub;
	}
	public void setUrlOrderSub(Integer urlOrderSub) {
		this.urlOrderSub = urlOrderSub;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		/**默认搜索图书*/
		int restype=ParamUtil.getIntParameter(request, ParameterConstants.RESOURCE_TYPE, 1);//request.getParameter(ParameterConstants.RESOURCE_TYPE);//资源大类型
		/**默认按名称*/
		int searchby=ParamUtil.getIntParameter(request, ParameterConstants.SEARCH_TYPE, 1);//request.getParameter(ParameterConstants.SEARCH_TYPE);//搜索条件
		
		/**获取url 排序参数 start
		 * @author yuzs 2009-11-05
		 */
		urlOrder = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDER, -1);
		urlOrderSub = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDERSUB, -1);
		/**
		 * 结束
		 */
		
//		Map map=new HashMap();
		//1图书，2报纸，3杂志，4漫画，5铃声，6视频
//		switch(Integer.parseInt(restype)){
//			case 1:map=searchBook(request,tagName,searchby);break;
//		}
		return searchResources(request,tagName,searchby,restype);
	}
	/**
	 * 搜索结果
	 * @param request
	 * @param tagName
	 * @param searchBy	参数取值参照 search标签的searchby属性(可扩展)
	 * @return  1.按书名关键字2.按作者 3.快速搜索 4. 按关键字  5.按出版社
	 */
	public Map searchResources(HttpServletRequest request, String tagName,int searchBy,int restype){
		if(searchBy==2){//按作者
			return searchResourcesByAuthor(request,tagName,searchBy,restype);
		}else if(searchBy==1){//按书名 
			return searchResourcesByName(request,tagName,searchBy,restype);
		}else if(searchBy==3 || searchBy==4){
			return searchResourcesByKey(request,tagName,searchBy,restype);
		}else if(searchBy==5){//按出版社
			return searchResourcesByPublishing(request,tagName,searchBy,restype);
		}
		return new HashMap();
	}
	
	/**
	 * 按出版社搜索
	 * @param request
	 * @param tagName
	 * @param searchBy
	 * @param restype
	 * @return
	 */
	private Map searchResourcesByPublishing(HttpServletRequest request, String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		String mix = getParameter("mix", "");// title组合 默认只显示资源名称不做组合
		//得到输入框的值
		String value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		if(value==null){
			TagLogger.debug(tagName, "搜索输入框的值为空", request.getQueryString(), null);
			return new HashMap();
		}else{
			value=URLUtil.chineseFilter(value, 2);
			//记录日志 
			/**
			 * 增加资源类型
			 * modify by liuxh 09-11-17
			 */
			StatisticsLog.logStat(Integer.parseInt(restype+"5"), value);
			/**
			 * end
			 */
		}
		
		System.out.println("搜索值="+value);
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage = request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1: Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "栏目Id为空", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "获取栏目信息失败", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "获取批价包信息失败", request.getQueryString(), null);
				return new HashMap();
			}
		}
		/**
		 * 判断是否从url里是否有排序字段存在。如果有，使用url排序字段排序，否则使用标签字段排序
		 * @author yuzs 2009-11-05
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// 排序类型
		}
		/**
		 * 结束
		 */
		int showTotal=0;
		int totalCount=0;
		//判断是否导航
		if(!isNoPageLink()){
			totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
			showTotal=totalCount;
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype, searchBy, value,rp,currentPage, pageSize,order);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr = (ResourcePackReleation)it.next();
			String resourceId=rpr.getResourceId();
			//根据资源ID查询资源
			ResourceAll resource=getResourceService(request).getResource(resourceId);
			if(resource!=null){
				//记录日志 
				StatisticsLog.logStat(2, resource.getId());
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resource.getId(), mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
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
			sb.append(resourceId);
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
			//带上搜索相关参数 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.SEARCH_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			sb.append(value);

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {
					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// 根据作者ID查询作者信息
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("次)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //新添加上了 资源对象
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//把预览图放进去
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//点击次数
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//搜索次数
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//收藏
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("totalCount", totalCount);
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
	}
	/**
	 * 按关键字搜索
	 * @param request
	 * @param tagName
	 * @param searchBy
	 * @return
	 */
	private Map searchResourcesByKey(HttpServletRequest request, String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String mix=getParameter("mix","");
		String value="";
		if(searchBy==3){
			value=StringUtils.trimToNull(request.getParameter(ParameterConstants.QUICK_SEARCH_LINK_NAME));
		}else if(searchBy==4){
			value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		}
		if(value==null){
			TagLogger.debug(tagName, "搜索值为空", request.getQueryString(), null);
			return new HashMap();
		}else{
//			try {
//				value=java.net.URLDecoder.decode(value,"UTF-8");
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			value=URLUtil.chineseFilter(value, 2);
			//记录日志 
			/**modify by liuxh 09-11-17 增加资源类型*/
			StatisticsLog.logStat(Integer.parseInt(restype+"3"), value);
			/**end*/
		}
		System.out.println("搜索值="+value);
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		// 每页显示资源条数
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "栏目Id为空", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "获取栏目信息失败", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "获取批价包信息失败", request.getQueryString(), null);
				return new HashMap();
			}
		}
		/**
		 * 判断是否从url里是否有排序字段存在。如果有，使用url排序字段排序，否则使用标签字段排序
		 * 
		 * @author yuzs 2009-11-05
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// 排序类型
		}
		/**
		 * 结束
		 */
		int totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
		//判断是否导航
		if(!isNoPageLink()){
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype, searchBy, value,rp,currentPage, pageSize,order);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr = (ResourcePackReleation)it.next();
			String resourceId=rpr.getResourceId();
			//根据资源ID查询资源
			ResourceAll resource=getResourceService(request).getResource(resourceId);
			if(resource!=null){
				//记录日志 
				StatisticsLog.logStat(2,resource.getId());//按书名
			}
			// 拼URL
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
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
			sb.append(resourceId);
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
			//带上搜索相关参数 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(searchBy);
			sb.append("&");
			if(searchBy==3){
				sb.append(ParameterConstants.QUICK_SEARCH_LINK_NAME);
			}else{
				sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			}
			sb.append("=");
			sb.append(value);

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {

					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// 根据作者ID查询作者信息
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("次)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //新添加上了 资源对象
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//把预览图放进去
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//点击次数
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//搜索次数
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//收藏
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("objs", lsRess);
		map.put("totalCount", totalCount);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
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
	/**
	 * 按书名
	 * @param request
	 * @param tagName
	 * @param searchBy
	 * @return
	 */
	public Map searchResourcesByName(HttpServletRequest request, String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String mix=getParameter("mix","");
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		//得到输入框的值
		String value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		if(value==null){//如果输入框为空 默认查询所有作者列表
			TagLogger.debug(tagName, "搜索输入框的值为空", request.getQueryString(), null);
		}else{
			
//			try {
//				value=java.net.URLDecoder.decode(value,"UTF-8");
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			value=URLUtil.chineseFilter(value, 2);
			//记录日志 
			/**modify by liuxh 09-11-17 增加资源类型*/
			StatisticsLog.logStat(Integer.parseInt(restype+"1"), value);//按书名
			/**end*/
		}
		System.out.println("搜索值="+value);
		// 每页显示资源条数
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "栏目Id为空", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "获取栏目信息失败", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "获取批价包信息失败", request.getQueryString(), null);
				return new HashMap();
			}
		}
		
		/**
		 * 判断是否从url里是否有排序字段存在。如果有，使用url排序字段排序，否则使用标签字段排序
		 * 
		 * @author yuzs 2009-11-05
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// 排序类型
		}
		/**
		 * 结束
		 */
		
		int totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
		//判断是否导航
		if(!isNoPageLink()){
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype,searchBy, value,rp,currentPage, pageSize,order);
		//System.out.println("搜索框中的值是--->"+value);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr = (ResourcePackReleation)it.next();
			String resourceId=rpr.getResourceId();
			//根据资源ID查询资源
			ResourceAll resource=getResourceService(request).getResource(resourceId);
			if(resource!=null){
				//记录日志
				StatisticsLog.logStat(2, resource.getId());
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
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
			sb.append(resourceId);
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
			//带上搜索相关参数 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.SEARCH_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			sb.append(value);

			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {

					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// 根据作者ID查询作者信息
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("次)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //新添加上了 资源对象
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//把预览图放进去
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//点击次数
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//搜索次数
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//收藏
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("objs", lsRess);
		map.put("totalCount", totalCount);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
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
	/**
	 * 按作者
	 * @param request
	 * @param tagName
	 * @return
	 */
	public Map searchResourcesByAuthor(HttpServletRequest request,String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		String mix = getParameter("mix", "");// title组合 默认只显示资源名称不做组合
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		//根据URL中作者ID 查询对应的资源
//		String authorId=request.getParameter(ParameterConstants.AUTHOR_ID);
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		int currentPage =request
		.getParameter(ParameterConstants.PAGE_NUMBER)==null?1: Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页
		//得到输入框的值
		String value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		if(value==null){//如果输入框为空 默认查询所有作者列表
			TagLogger.debug(tagName, "搜索输入框的值为空", request.getQueryString(), null);
		}else{
//			try {
//				value=java.net.URLDecoder.decode(value,"UTF-8");
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			value=URLUtil.chineseFilter(value, 2);
			//记录搜索值
			/**modify by liuxh 09-11-17 增加资源类型*/
			if(restype!=ResourceType.TYPE_MAGAZINE && restype!=ResourceType.TYPE_NEWSPAPERS)
				StatisticsLog.logStat(Integer.parseInt(restype+"2"), value);//按书名
			/**end*/
		}
		System.out.println("搜索值="+value);
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "栏目Id为空", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "获取栏目信息失败", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "获取批价包信息失败", request.getQueryString(), null);
				return new HashMap();
			}
		}
		/**
		 * 判断是否从url里是否有排序字段存在。如果有，使用url排序字段排序，否则使用标签字段排序
		 * 
		 * @author yuzs 2009-11-05
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// 排序类型
		}
		/**
		 * 结束
		 */
		int totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
		//判断是否导航
		if(!isNoPageLink()){
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype, searchBy, value,rp,currentPage, pageSize,order);
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr=(ResourcePackReleation)it.next();
			ResourceAll resource=getResourceService(request).getResource(rpr.getResourceId());
			if(resource!=null){
				//记录搜索值
				StatisticsLog.logStat(2, resource.getId());//按作者
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resource.getId(), mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
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
			sb.append(resource.getId());
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
			//带上搜索相关参数 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.SEARCH_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			sb.append(value);
			
			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {

					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// 根据作者ID查询作者信息
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("次)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //新添加上了 资源对象
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//把预览图放进去
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//点击次数
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//搜索次数
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//收藏
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("totalCount", totalCount);
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
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
//		String authorId=request.getParameter(ParameterConstants.AUTHOR_ID);
//		if(StringUtils.isEmpty(authorId)){//如果作者ID为空 则先列作者列表
//			return AuthorListMap(request,tagName);
//		}else{//列作者相关的资源
//			return ResourceListMap(request,tagName);
//		}
	}
//	/**
//	 * 按作者查询的作者列表
//	 * @return
//	 */
//	public Map AuthorListMap(HttpServletRequest request,String tagName){
//		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
//		Map resultMap = new HashMap();
//		String result = "";
//		/** 存放列表资源 */
//		Map<String, Object> map = new HashMap<String, Object>();
//		//得到输入框的值
//		String value=request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE);
//		// 每页显示资源条数
//		int pageSize = getIntParameter("pageSize", -1);
//		if (pageSize < 0) {
//			pageSize = DEFAULT_PAGE_SIZE;
//		}
//		int currentPage = 1;// 当前页数，默认为第一页
//		try {
//			currentPage = Integer.parseInt(request
//					.getParameter(ParameterConstants.PAGE_NUMBER));
//		} catch (Exception e) {}
//		System.out.println("搜索框中的值是--->"+value);
//		List <ResourceAuthor> authors=getResourceService(request).searchResult(value,currentPage,pageSize);
//		//判断是否导航
//		if(!isNoPageLink()){
//			List all=new ArrayList();
//			//得到总数
//			int totalCount=getResourceService(request).searchResultCount(value);
//			for(int i=0;i<totalCount;i++){
//				all.add(new Object());
//			}
//			Navigator navi=new Navigator(all, currentPage, pageSize, 5);
//			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
//		}
//		List<Object> lsRess = new ArrayList<Object>();
//		int loop = 0;
//		for(Iterator it=authors.iterator();it.hasNext();){
//			loop++;
//			ResourceAuthor author=(ResourceAuthor)it.next();
//			//拼URL
//			StringBuilder sb=new StringBuilder();
//			sb.append(request.getContextPath());
//			sb.append(ParameterConstants.PORTAL_PATH);
//			sb.append("?");
//			URLUtil.append(sb, ParameterConstants.PAGE, request);
//			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
//			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
//			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
//			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
//			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
//			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
//			URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
//			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
//			URLUtil.append(sb, ParameterConstants.RESOURCE_TYPE, request);
//			URLUtil.append(sb, ParameterConstants.SEARCH_TYPE, request);
//			sb.append(ParameterConstants.AUTHOR_ID);
//			sb.append("=");
//			sb.append(author.getId());
//			/** 保存单条记录 */
//			Map<String, String> obj = new HashMap<String, String>();
//			obj.put("url", sb.toString());
//			obj.put("title", author.getName());
//			lsRess.add(obj);
//		}
//		map.put("objs", lsRess);
//		result = VmInstance.getInstance().parseVM(map, this);
//		resultMap.put(TagUtil.makeTag(tagName), result);
//		return resultMap;
//	}
//	/**
//	 * 按作者查询的作者对应的资源列表
//	 * @return
//	 */
//	public Map ResourceListMap(HttpServletRequest request,String tagName){
//		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
//		boolean isOut = false;// 是否弹出资费提示
//		boolean isFee=false;
//		boolean isMonth=false;
//		Map resultMap = new HashMap();
//		String result = "";
//		/** 存放列表资源 */
//		Map<String, Object> map = new HashMap<String, Object>();
//		//根据URL中作者ID 查询对应的资源
//		String authorId=request.getParameter(ParameterConstants.AUTHOR_ID);
//		int pageSize = getIntParameter("pageSize", -1);
//		if (pageSize < 0) {
//			pageSize = DEFAULT_PAGE_SIZE;
//		}
//		int currentPage = 1;// 当前页数，默认为第一页
//		try {
//			currentPage = Integer.parseInt(request
//					.getParameter(ParameterConstants.PAGE_NUMBER));
//		} catch (Exception e) {}
//	
//		//判断是否导航
//		if(!isNoPageLink()){
//			List resAll=new ArrayList();
//			int totalCount=getResourceService(request).searchResourceCount(ResourceType.TYPE_BOOK,2,authorId);
//			for(int i=0;i<totalCount;i++){
//				resAll.add(new Object());
//			}
//			Navigator navi=new Navigator(resAll, currentPage, pageSize, 5);
//			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
//		}
//		List<Object> lsRess = new ArrayList<Object>();
//		int loop = 0;
//		String feeId = "";
//		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(ResourceType.TYPE_BOOK, 2, authorId, currentPage, pageSize);
//		for (Iterator it = rprs.iterator(); it.hasNext();) {
//			loop++;
//			ResourcePackReleation rpr=(ResourcePackReleation)it.next();
//			ResourceAll resource=getResourceService(request).getResource(rpr.getResourceId());
//			// 拼URL
//			StringBuilder sb = new StringBuilder();
//			sb.append(request.getContextPath());
//			boolean isWhiteList = RequestUtil.isFeeDisabled();// 是否是白名单用户
//			// 查询用户购买表如果 此表中存在这个资源则不添加计费路径
//			boolean isBuy = getCustomService(request).isUserBuyBook(
//					RequestUtil.getMobile(), resource.getId());// 判断用户是否已购买
//			
//			feeId = rpr.getFeeId();
//			if(feeId==null || "".equals(feeId)){
//				feeId=rpr.getPack().getFeeId();
//				if(feeId==null || "".equals(feeId)){
//					isFee=false;
//				}else{
//					isFee=true;
//				}
//			}
//			
//			if (isWhiteList || isBuy || !isFee) {// 白名单用户或是已购买用户 不计费
//				sb.append(ParameterConstants.PORTAL_PATH);
//				sb.append("?");
//			} else {
//				Fee fee = getCustomService(request).getFee(feeId);
//				isOut = fee.getIsout() == 1;// 是否弹资费提示
//				if(!isOut){
//					sb.append("/");
//					sb.append(fee.getUrl());
//				}
//				sb.append(ParameterConstants.PORTAL_PATH);
//				sb.append("?");
//				if (isOut) {
//					//如果是包月订购用户 刚不弹出提示
//					isMonth=getCustomService(request).isOrderMonth(RequestUtil.getMobile(), feeId);
//					if(!isMonth){
//						sb.append(ParameterConstants.COMMON_PAGE);
//						sb.append("=");
//						sb.append(ParameterConstants.COMMON_PAGE_FEE);
//						sb.append("&");
//						sb.append(ParameterConstants.TEMPLATE_ID);
//						sb.append("=");
//						sb.append(fee.getTemplateId());
//						sb.append("&");
//					}
//				}
//
//			}
//			sb.append(ParameterConstants.PAGE);
//			sb.append("=");
//			sb.append(ParameterConstants.PAGE_RESOURCE);
//			sb.append("&");
//			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
//			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
//			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
//			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
//			sb.append(ParameterConstants.FEE_BAG_ID);
//			sb.append("=");
//			sb.append(rpr.getPack().getId());
//			sb.append("&");
//			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
//			sb.append("=");
//			sb.append(rpr.getId());
//			sb.append("&");
//			sb.append(ParameterConstants.RESOURCE_ID);
//			sb.append("=");
//			sb.append(resource.getId());
//			sb.append("&");
//			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
//			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
//			sb.append(ParameterConstants.PAGE_NUMBER);
//			sb.append("=");
//			sb.append("1");
//			if(isFee && !isMonth && !isBuy ){
//				sb.append("&");
//				sb.append(ParameterConstants.FEE_ID);
//				sb.append("=");
//				sb.append(feeId);
//			}
//
//			/** 保存单条记录 */
//			Map<String, String> obj = new HashMap<String, String>();
//			obj.put("url", sb.toString());
//			obj.put("title", resource.getName());
//			lsRess.add(obj);
//		}// for end
//		map.put("objs", lsRess);
//		result = VmInstance.getInstance().parseVM(map, this);
//		resultMap.put(TagUtil.makeTag(tagName), result);
//		return resultMap;
//	}
	
	private String getBookStatus(int status) {
		String typeName = "";
		for (Map.Entry<String, Integer> entry : Constants.getResourceFinished()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return "("+entry.getKey()+")";
		}
		return typeName;
	}
	private String imagePreview(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		StringBuilder sb = new StringBuilder();
		// 判断资源类型(1图书，2报纸，3杂志，4漫画，5铃声，6视频)
		if (resource.getId().startsWith("1")) {// 图书
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						ebook.getId(), ebook.getBookPic(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + ebook.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("2")) {// 报纸
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						n.getId(), n.getImage(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + n.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("3")) {// 杂志
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						magazine.getId(), magazine.getImage(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + magazine.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("4")) {// 漫画
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						comics.getId(), comics.getImage(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + comics.getName() + "\"/>");

				return sb.toString();
			}
		}
		return "";
	}
	
	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
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
