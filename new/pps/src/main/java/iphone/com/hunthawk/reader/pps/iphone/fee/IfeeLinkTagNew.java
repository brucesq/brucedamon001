package com.hunthawk.reader.pps.iphone.fee;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeMsgService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.process.Redirect;

/**
 * 标签名称：ifee_link_new
 * 参数说明：
 * 		templateId:跳转模版ID	
 * @author liuxh
 *
 */
public class IfeeLinkTagNew extends BaseTag {

	private BussinessService bussinessService;
	private ResourceService resourceService;
	private IphoneService iphoneService;
	private CustomService customService;
	private FeeMsgService feeMsgService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		boolean isIphone=getIphoneService(request).isIphoneProduct(request.getParameter(ParameterConstants.PRODUCT_ID));
		if(isIphone){
			String mobile=RequestUtil.getMobile();
			//mobile="18601120940";
			if(mobile.equals("10000000000")){//未得到手机号 跳转到登陆页
				StringBuilder login=new StringBuilder();
				login.append(getBussinessService(request).getVariables("iphone_login_url").getValue());
				try {
					login.append(java.net.URLEncoder.encode(request.getRequestURL()+"?"+request.getQueryString(),"utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("login Url---->"+login);
				Redirect.sendRedirect(login.toString()) ;//跳转到登陆页面
				return new HashMap();
			}else{//能得到手机号     未计费则跳转到联通的统一计费页地址
				//mobile="13101010101";
				String resourceUrl=request.getRequestURL()+"?"+URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID);
				String resourceId=URLUtil.getResourceId(request);
				String relId=request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID);
//				String pid=request.getParameter(ParameterConstants.PRODUCT_ID);
				boolean isBuy=false;
				if(relId==null || "".equals(relId)){
					isBuy=true;
				}else{
					isBuy=getIphoneService(request).isUserBuy(mobile, request.getParameter(ParameterConstants.PRODUCT_ID), Integer.parseInt(request.getParameter(ParameterConstants.FEE_BAG_ID)), resourceId,relId);
				}
				//boolean isBuy =getIphoneService(request).isUserBuyBook(mobile, request.getParameter(ParameterConstants.PRODUCT_ID), Integer.parseInt(request.getParameter(ParameterConstants.FEE_BAG_ID)), resourceId);
				if(isBuy){//已购买   或批价包关联ID为0   跳到内容页
					System.out.println("内容页地址--->"+resourceUrl);
					Redirect.sendRedirect(resourceUrl) ;//跳转到内容页面
					return new HashMap();
				}else{
					String packId= request.getParameter(ParameterConstants.FEE_BAG_ID);
					ResourcePackReleation rpr=null;
					Fee fee=null;
					String feeCode="0";
					if(relId!=null && !"".equals(relId)){
						rpr=getResourceService(request).getResourcePackReleation(Integer.parseInt(relId));
						if(rpr!=null){
							fee=getCustomService(request).getFee(rpr.getFeeId());
							if(fee!=null)
								feeCode=fee.getCode();
						}
					}
					if(feeCode.equals("0") || packId==null || "".equals(packId) || rpr==null){//免费资源 不需要计费跳转
						Redirect.sendRedirect(resourceUrl) ;//跳转到内容页面
					}else{
						int templateId=getIntParameter("templateId",-1);
						String prono=getIphoneService(request).getIphoneProductNo(resourceId,packId,relId);
//						UserOrderBackMessage message=getIphoneService(request).searchOrder(mobile, prono, "1", "", "");
//						if(message.getFlag().equals("00000")){//订单有效
//							Redirect.sendRedirect(resourceUrl) ;//跳转到内容页面
//						}else{//订单无效   需要订购 
							//得到系统变量 iphone统一计费页地址
							String iphone_fee_url=getBussinessService(request).getVariables("iphone_fee_url").getValue();
							StringBuilder url=new StringBuilder();
							url.append(iphone_fee_url);//计费成功  跳转到我们平台的模版页  此模版用于实现订购数据记录
							StringBuilder templateUrl=new StringBuilder();
							templateUrl.append(request.getRequestURL());
							templateUrl.append("?");
							templateUrl.append(ParameterConstants.COMMON_PAGE);
							templateUrl.append("=");
							templateUrl.append(ParameterConstants.COMMON_PAGE_LINK);
							templateUrl.append("&");
							templateUrl.append(ParameterConstants.TEMPLATE_ID);
							templateUrl.append("=");
							templateUrl.append(templateId);
							templateUrl.append("&");
							templateUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID));
							try {
								url.append(java.net.URLEncoder.encode(templateUrl.toString(),"utf-8"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							url.append("&SPNO=");
							url.append(getBussinessService(request).getVariables("iphone_spid").getValue());
							url.append("&PRO_NO=");
							url.append(prono);
							Redirect.sendRedirect(url.toString());
//						}
					}
 				}
			}
			return new HashMap();
		}else{
			TagLogger.debug(tagName, "非Iphone产品", request.getQueryString(), null);
			return new HashMap();
		}
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
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
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
	private FeeMsgService getFeeMsgService(HttpServletRequest request) {
		if (feeMsgService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeMsgService = (FeeMsgService) wac.getBean("feeMsgService");
		}
		return feeMsgService;
	}
}
