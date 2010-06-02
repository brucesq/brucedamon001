/**
 * 
 */
package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.device.UAInfo;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * @author BruceSun
 *
 */
public class UebDownloadTag extends BaseTag {

	private static ResourceService resourceService;
	private static CustomService customService;
	private static FeeLogicService feeLogicService;
	
	private int number;
	private int currentPage;
	private String split;
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	
	
	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		String resourceId=URLUtil.getResourceId(request);
		String mobile=RequestUtil.getMobile();
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		boolean isFee=getIntParameter("isfee",1)>0;//默认收费
		String title = getParameter("title","UEB下载");
		split = getParameter("split", "");
		String n = getParameter("number", "");
		if (!StringUtils.isEmpty(n)) {
			try {
				this.number = Integer.parseInt(n);
			} catch (Exception e) {
				TagLogger.debug(tagName, "标签number属性值无效", request
						.getQueryString(), e);
			}
		} else {
			this.number = 1;// 默认第一页显示
		}
		if (request.getParameter(ParameterConstants.PAGE_NUMBER) != null
				&& !"".equals(request
						.getParameter(ParameterConstants.PAGE_NUMBER))) {
			this.currentPage = Integer.parseInt(request
					.getParameter(ParameterConstants.PAGE_NUMBER));
		} else {
			this.currentPage = 1;
		}
		int relId = ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID,-1);
		if(relId == -1){
			TagLogger.debug(tagName,"无批价关联信息", request.getQueryString(), null);
			return new HashMap();
		}
		ResourcePackReleation rel = null;
		if (relId!= -1) {
			rel = getResourceService(request).getResourcePackReleation(ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_RELATION_ID, -1));
		}
		StringBuilder feeUrl=new StringBuilder();
		if(isFee){
			Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rel, packId, month_fee_bag_id);
			if(feeMap==null){
				isFee=false;
			}else{
				feeUrl.append(request.getContextPath());
				feeUrl.append(feeMap.get("builder"));
				feeUrl.append(ParameterConstants.PAGE);
				feeUrl.append("=");
				feeUrl.append(ParameterConstants.PAGE_RESOURCE);
				feeUrl.append("&");
				URLUtil.append(feeUrl, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.AREA_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.FEE_BAG_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.FEE_BAG_RELATION_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.RESOURCE_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(feeUrl, ParameterConstants.UNICOM_PT, request);
				URLUtil.append(feeUrl, ParameterConstants.PAGE_NUMBER, request);
				if(feeMap!=null){
					feeUrl.append("&");
					feeUrl.append(ParameterConstants.FEE_ID);
					feeUrl.append("=");
					feeUrl.append(feeMap.get("feeId"));
				}
			}
		}else{
			isFee=false;
		}
		
		String[] uebs =  getResourceService(request).getUebAddress(relId);
		String downloadUrl = "";
		UAInfo ua = RequestUtil.getUAInfo();
		if(ua.getWidth() < 176){
			downloadUrl = uebs[0];
		}else if(ua.getWidth() < 240){
			downloadUrl = uebs[1];
		}else{
			downloadUrl = uebs[2];
		}
		Map velocityMap = new HashMap();
		
		
		velocityMap.put("title", title);
		velocityMap.put("url", (isFee?feeUrl.toString():downloadUrl));
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));
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
}
