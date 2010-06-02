/**
 * 
 */
package com.hunthawk.reader.pps.fee;

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
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 按次订购处理
 * @author BruceSun
 *
 */
@SuppressWarnings("unchecked")
public class ViewFeeTag extends BaseTag {

	/* (non-Javadoc)
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	private TagTemplate tagTem;
	private BussinessService bussinessService;
	private boolean showFee;
	public boolean isShowFee() {
		return showFee;
	}


	public void setShowFee(boolean showFee) {
		this.showFee = showFee;
	}


	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// 按次类型,1普通按次，2VIP按次
		//链接显示文字
		//订购成功后显示文字
		//FEEID按次计费ID，如果不指定就使用URL上批价包关联的计费ID
		//批价包ID，如果是VIP按次，需要指定批价包ID，程序判断用户是否是VIP用户，如果不是，先显示VIP订购地址
		//
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		
		String price="";
		int packId = getIntParameter("packid", -1);
		int type = getIntParameter("type", 1);
//		String title = getParameter("title", "");
		String successTitle = getParameter("succtitle", "");
		
		String feeId = getParameter("feeid", "");
		
		Map resultMap = new HashMap();
		
		if(StringUtils.isEmpty(feeId)){
			int relId = ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID,-1);
			if(relId > 0){
				ResourcePackReleation rel = getResourceService(request).getResourcePackReleation(relId);
				feeId = rel.getFeeId();	
			}
		}
		if(StringUtils.isEmpty(feeId)){
			TagLogger.error(tagName, "获取不到计费ID信息", request.getQueryString(), null);
			resultMap.put(TagUtil.makeTag(tagName), "");
			return resultMap;
		}
		String cid = ParamUtil.getParameter(request, ParameterConstants.RESOURCE_ID);
		if(StringUtils.isEmpty(cid)){
			TagLogger.error(tagName, "获取不到内容ID信息", request.getQueryString(), null);
			resultMap.put(TagUtil.makeTag(tagName), "");
			return resultMap;
		}
		String mobile = RequestUtil.getMobile();
		boolean isOrder = getCustomService(request).isUserBuyBook(mobile, cid);
		String url = request.getQueryString();
		if(!isOrder){//如果已经订购该本书的用户不需要在计费
			if(type == 1){//普通按次
				Fee fee = getCustomService(request).getFee(feeId);
				price=fee.getCode();
				url = URLUtil.removeParameter(url,ParameterConstants.FEE_ID);
				url += "&" + ParameterConstants.FEE_ID + "=" + fee.getId();
				StringBuilder builder = new StringBuilder();
				if(fee.getIsout() == 0){
					builder.append(request.getContextPath());
					builder.append( "/");
					builder.append(fee.getUrl());
					builder.append(ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( url);
				}else{
					builder.append( request.getContextPath());
					builder.append( ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( ParameterConstants.COMMON_PAGE);
					builder.append("=");
					builder.append(ParameterConstants.COMMON_PAGE_FEE);
					builder.append("&");
					builder.append(ParameterConstants.TEMPLATE_ID);
					builder.append("=");
					builder.append(fee.getTemplateId());
					builder.append("&");
					builder.append(url);
				}
				url = builder.toString();
			}else if(type ==2){//VIP按次
				ResourcePack pack =  null;
				if(packId > 0){
					pack =  getResourceService(request).getResourcePack(packId);
				}
				if(pack == null){
					TagLogger.error(tagName, "VIP按次计费类型批价包不存在", request.getQueryString(), null);
					resultMap.put(TagUtil.makeTag(tagName), "");
					return resultMap;
				}
				/**
				 * 修改VIP计费逻辑
				 * 1.判断是否是VIP用户 是-> 2.判断当前资源是否是VIP批价包中的资源 是-> 取此资源关联的feeId
				 * 																 否-> 不显示
				 * 					  否->显示VIP订购链接
				 * modify  by liuxh  09-11-02
				 */
				/**判断是否是VIP用户*/
				boolean isVip = getCustomService(request).isOrderMonth(mobile, pack.getFeeId());
				Fee fee = null;
				if(isVip){
					/**判断此条资源是否存在于vip批价包中*/
					feeId=getVIPFeeId(request,packId);
					System.out.println("feeId="+feeId);
					if(!"".equals(feeId)){
						fee=getCustomService(request).getFee(feeId);
						price=fee.getCode();
						this.showFee=true;
						url = URLUtil.removeParameter(url,ParameterConstants.FEE_ID);
						url += "&" + ParameterConstants.FEE_ID + "=" + fee.getId();
						Map<String,String> values=new HashMap<String,String>();
						values.put(ParameterConstants.FEE_BAG_ID, String.valueOf(pack.getId()));
						ResourcePackReleation rpr=getResourceService(request).getResourcePackReleation(packId, URLUtil.getResourceId(request));
						values.put(ParameterConstants.FEE_BAG_RELATION_ID,String.valueOf(rpr.getId()));
						List<String> list=new ArrayList();
						list.add(ParameterConstants.FEE_BAG_ID);
						list.add(ParameterConstants.FEE_BAG_RELATION_ID);
						url=URLUtil.urlChangeParam(url,values, list);
//						System.out.println("--------->"+url);
					}else{
						TagLogger.error(tagName, "id为"+URLUtil.getResourceId(request)+"的资源不存在于VIP按次计费类型的批价包中", request.getQueryString(), null);
						resultMap.put(TagUtil.makeTag(tagName), "");
						return resultMap;
					}
				}else{
					/**非VIP用户显示VIP订购地址*/
					this.showFee=false;
					fee = getCustomService(request).getFee(pack.getFeeId());
					url = URLUtil.removeParameter(url,ParameterConstants.FEE_ID,ParameterConstants.MONTH_FEE_BAG_ID);
					url += "&" + ParameterConstants.FEE_ID + "=" + fee.getId()+"&"+ParameterConstants.MONTH_FEE_BAG_ID+"="+pack.getId();
				}
				/**
				 * end  
				 */
//				boolean isVip = getCustomService(request).isOrderMonth(mobile, pack.getFeeId());
//				if(isVip){
//					fee = getCustomService(request).getFee(feeId);
//					url = URLUtil.removeParameter(url,ParameterConstants.FEE_ID);
//					url += "&" + ParameterConstants.FEE_ID + "=" + fee.getId();
//					
//				}else{
//					fee = getCustomService(request).getFee(pack.getFeeId());
//					url = URLUtil.removeParameter(url,ParameterConstants.FEE_ID,ParameterConstants.MONTH_FEE_BAG_ID);
//					url += "&" + ParameterConstants.FEE_ID + "=" + fee.getId()+"&"+ParameterConstants.MONTH_FEE_BAG_ID+"="+pack.getId();
//					
//				}
				StringBuilder builder = new StringBuilder();
				if(fee.getIsout() == 0){
					builder.append(request.getContextPath());
					builder.append( "/");
					builder.append(fee.getUrl());
					builder.append(ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( url);
				}else{
					builder.append( request.getContextPath());
					builder.append( ParameterConstants.PORTAL_PATH);
					builder.append( "?");
					builder.append( ParameterConstants.COMMON_PAGE);
					builder.append("=");
					builder.append(ParameterConstants.COMMON_PAGE_FEE);
					builder.append("&");
					builder.append(ParameterConstants.TEMPLATE_ID);
					builder.append("=");
					builder.append(fee.getTemplateId());
					builder.append("&");
					builder.append(url);
				}
				url = builder.toString();
				
			}
		}
		Map velocityMap = new HashMap();
//		velocityMap.put("title", title);
		velocityMap.put("showFee", this.showFee);
		velocityMap.put("price", price);
		velocityMap.put("url", url);
		velocityMap.put("successTitle", successTitle);
		velocityMap.put("isOrder", isOrder);
//		String content  = VmInstance.getInstance().parseVM(velocityMap, this);
		/**
		 * 标签模版可配置
		 * modify by liuxh 09-11-04
		 */
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
		
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
	}
		
		
	private String getVIPFeeId(HttpServletRequest request,int packId){
		String resourceId=URLUtil.getResourceId(request);
		/**获取当前批价包下的资源*/
		List<ResourcePackReleation> rprs=getResourceService(request).getResourcePackReleations(packId, 1, Integer.MAX_VALUE);
		for(Iterator it=rprs.iterator();it.hasNext();){
			ResourcePackReleation rpr=(ResourcePackReleation)it.next();
			if(rpr.getResourceId().equals(resourceId)){
				return rpr.getFeeId();
			}
		}
		return "";
	}

	private static CustomService customService;
	private static ResourceService resourceService;

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
