package com.hunthawk.reader.pps.interactive.msg;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourcePack;
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
/**
 * 连载功能标签显示连载订制功能结果
 * 标签名称：remind_view
 * 参数说明：
 * 		succtitle:订制成功显示内容
 * 		canceltitle：取消成功显示内容
 * 		title:	 	链接文本
 * 		
 * @author liuxh
 *
 */
public class ViewSerialRemindTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title = getParameter("title", "");//链接显示文字
		String successTitle = getParameter("succtitle", "");//订制成功后显示文字
		String cancelTitle=getParameter("canceltitle","");//取消成功后显示文字 
		String feeId = request.getParameter(ParameterConstants.FEE_ID)==null?"":request.getParameter(ParameterConstants.FEE_ID);
		String mobile=RequestUtil.getMobile();
		String cid=URLUtil.getResourceId(request);
		int relId=ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID,-1);
		if(StringUtils.isEmpty(feeId)){
			int packId=ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_ID,-1);
			if(packId > 0){
				ResourcePack rp=getResourceService(request).getResourcePack(packId);
				feeId=rp.getFeeId();
			}
		}
		if(StringUtils.isEmpty(feeId)){
			System.out.println("计费ID为空");
			TagLogger.debug(tagName, "计费ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		if(StringUtils.isEmpty(feeId)){
			TagLogger.error(tagName, "获取不到计费ID信息", request.getQueryString(), null);
			return new HashMap();
		}
		if(relId<0){
			TagLogger.debug(tagName, "批价包关联ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		if(StringUtils.isEmpty(cid)){
			TagLogger.debug(tagName, "内容ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		
		//判断是订制还是取消操作
		String flag=request.getParameter(ParameterConstants.ORDER_FLAG);
		int sign=1;//默认操作成功
		if(flag==null){//订制
			try{
				getCustomService(request).addReservation(mobile, cid, relId);
			}catch(Exception ex){
				TagLogger.debug(tagName, "订制连载提醒操作失败", request.getQueryString(), ex);
				successTitle="您已经订制了连载提醒功能";
				sign=0;
			}
			cancelTitle="";
		}else{//取消
			try{
				getCustomService(request).deleteReservation(mobile, cid);
			}catch(Exception ex){
				TagLogger.debug(tagName, "取消连载提醒操作失败", request.getQueryString(), ex);
				cancelTitle="对不起您未订制连载提醒功能!";
				sign=0;
			}
			successTitle="";
		}
		
		
		StringBuilder backUrl = new StringBuilder();
		backUrl.append( request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.ORDER_FLAG));
		
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("successTitle", successTitle);
		velocityMap.put("cancelTitle", cancelTitle);
		//velocityMap.put("isOrder", isOrder);
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
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
		/*String content  = VmInstance.getInstance().parseVM(velocityMap, this);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);*/
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
	
	private BussinessService bussinessService;
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
