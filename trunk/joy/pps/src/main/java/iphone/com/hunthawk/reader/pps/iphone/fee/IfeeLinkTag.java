package com.hunthawk.reader.pps.iphone.fee;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.vm.VmInstance;
/**
 * 计费订购链接
 * 标签名称：ifee_link
 * 参数说明：
 * 		templateId:计费是否成功提示页
 * 		title:链接文字
 * @author liuxh
 *
 */
public class IfeeLinkTag extends BaseTag {

	private BussinessService bussinessService;
	private ResourceService resourceService;
	private IphoneService iphoneService;
	private CustomService customService;
	
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		
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
		}else{//能得到手机号
			String resourceId=URLUtil.getResourceId(request);
			//再次判断是否是已购买用户
			boolean isBuy =getIphoneService(request).isUserBuyBook(mobile, request.getParameter(ParameterConstants.PRODUCT_ID), Integer.parseInt(request.getParameter(ParameterConstants.FEE_BAG_ID)), resourceId);
			if(isBuy){//已购买  跳到内容页
				System.out.println("内容页地址--->"+request.getRequestURL()+"?"+URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID));
				Redirect.sendRedirect(request.getRequestURL()+"?"+URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID)) ;//跳转到内容页面
				return new HashMap();
			}else{//未购买   显示订购链接
				String title = getParameter("title","");
				String templateId=getParameter("templateId","");
				StringBuilder builder=new StringBuilder();
				builder.append(request.getContextPath());
				builder.append(ParameterConstants.PORTAL_PATH);
				builder.append("?");
				builder.append(ParameterConstants.TEMPLATE_ID);
				builder.append("=");
				builder.append(templateId);
				builder.append("&");
				builder.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID));
				Map velocityMap = new HashMap();
				// 将参数循环放入Map
				List<Object> lsRess = new ArrayList<Object>();
				String[] pams =builder.toString().split("&");
				for (int i = 0; i < pams.length; i++) {
					String str[] = pams[i].split("=");
					/** 保存单条记录 */
					Map<String, String> obj = new HashMap<String, String>();
					if (!str[0].contains(ParameterConstants.SEARCH_TYPE)) {
						obj.put("key", i == 0 ? str[0]
								.substring((str[0].indexOf("?") + 1)) : str[0]);
						if (str.length > 1 && str[1] != null
								&& StringUtils.isNotEmpty(str[1]))
							obj.put("value", str[1]);
						else
							obj.put("value", "");
						lsRess.add(obj);
					}
				}
				String channelPay=getIphoneService(request).getChannel(mobile).getCode();
				Fee fee=getIphoneService(request).getColumnFee(mobile, Integer.parseInt(request.getParameter(ParameterConstants.FEE_BAG_ID).toString()));
				String columnPay="";
				if(fee!=null){//说明批价包可以包月  否则不显示 栏目包月计费选项
					columnPay=fee.getCode();
				}
				String resourcePay=getIphoneService(request).getResourceFee(mobile, getCustomService(request).getFee(request.getParameter(ParameterConstants.FEE_ID))).getCode();
				velocityMap.put("channelPay", channelPay);
				velocityMap.put("columnPay", columnPay);
				velocityMap.put("resourcePay", resourcePay);
				velocityMap.put("objs", lsRess);
				velocityMap.put("title", title);
				velocityMap.put("url", builder.toString());
				velocityMap.put("mobile", mobile);
				velocityMap.put("content", getResourceService(request).getResource(URLUtil.getResourceId(request)).getName());
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance().parseVM(velocityMap, this));
				return resultMap;
			}
			
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
}
