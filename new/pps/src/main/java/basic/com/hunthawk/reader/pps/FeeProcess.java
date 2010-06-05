/**
 * 
 */
package com.hunthawk.reader.pps;

import java.util.Date;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.custom.UserBuyMonth;
import com.hunthawk.reader.domain.custom.UserOrderList;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.process.Redirect;

/**
 * @author BruceSun
 * 
 */
public class FeeProcess {

	private static CustomService customService;
	private static ResourceService resourceService;
	private static BussinessService bussinessService;

	public static void process(HttpServletRequest request) {
		String uri = request.getRequestURI();
		uri = uri.substring(request.getContextPath().length() + "/".length());
		int index = uri.indexOf("/");
//System.out.println("LLL:"+uri+":"+index);
		if (index > 0) {
			String feeUrl = uri.substring(0, index);
			
//System.out.println("LLL:"+feeUrl+":"+index);

			if (feeUrl.length() == 4) {
				// 判断是否是正常用户
				if (!isNormalUser(request)) {
					return;


















				}
				// processFee(feeUrl,request);
				String local = "http://local.iuni.com.cn?spurl="
						+ "http://"
						+ PageUtil.getDomainName(request.getRequestURL()
								.toString());
				String url = local + request.getContextPath() + "/a/" + uri
						+ "&" + request.getQueryString();
				System.out.println("FEE=" + url);
				Redirect.sendRedirect(url);
				// String isNeedPreix = request.getParameter("isprefix");
				// if("1".equals(isNeedPreix)){
				// String local = "http://local.iuni.com.cn?spurl="+"http://"
				// + PageUtil.getDomainName(request.getRequestURL()
				// .toString()) ;
				// String url = local + request.getContextPath() + "/a/" + uri +
				// "?"
				// + request.getQueryString();
				// System.out.println("FEE1="+url);
				// Redirect.sendRedirect(url);
				// Redirect.sendForward(url, "跳转中....", "");
				// }else{
				// String local = "http://local.iuni.com.cn?spurl="+"http://"
				// + PageUtil.getDomainName(request.getRequestURL()
				// .toString()) ;
				// String url = local + request.getContextPath() + "/" + uri +
				// "?"
				// + request.getQueryString()+"&isprefix=1";
				// System.out.println("FEE2="+url);
				// // Redirect.sendRedirect(url);
				// Redirect.sendForward(url, "跳转中....", "");
				// }
			} else if (feeUrl.length() == 1 && feeUrl.equals("a")) {
				uri = uri.substring(index + 1);
				index = uri.indexOf("/");
				feeUrl = uri.substring(0, index);
				if (feeUrl.length() == 4) {
					processFee(feeUrl, request);
					
					
					
					
					String startQuery = uri.substring(uri.indexOf("s.do")+4);
					startQuery = startQuery.replaceAll("\\?", "&");
					startQuery = startQuery.replaceAll("%26", "&");
					if(startQuery.startsWith("&")){
						startQuery = startQuery.substring(1);
					}
					
					String queryString = request.getQueryString();
//					queryString = queryString.replaceAll("\\?", "&");
//					queryString = queryString.replaceAll("%26", "&");
					
					String url = request.getContextPath()+
					ParameterConstants.PORTAL_PATH+"?"
							+ startQuery + "&" + queryString;
					
//					System.out.println("go:"+url);
					Redirect.sendRedirect(url);
				}
			}

		}
	}

	/**
	 * 判断用户是否是正常用户
	 * 
	 * @return
	 */
	protected static boolean isNormalUser(HttpServletRequest request) {
		String mobile = RequestUtil.getMobile();
		if (mobile.equals("10000000000")) {
			int type = RequestUtil.getMobileType();
			String key = "anonymous_login_" + type;
			String url = getBussinessService(request).getVariables(key)
					.getValue();
			Redirect.sendRedirect(url);
			return false;
		}
		return true;
	}

	private static int getIntParameter(Map<String, String> params, String key,
			int defaultId) {

		int value = defaultId;
		try {
			value = Integer.parseInt(params.get(key));
		} catch (Exception e) {

		}
		return value;
	}

	private static String getResourceId(Map<String, String> params) {
		String rid = params.get(ParameterConstants.RESOURCE_ID);
		if (StringUtils.isEmpty(rid)) {
			rid = params.get(ParameterConstants.CHAPTER_ID);
			if (StringUtils.isNotEmpty(rid)) {
				rid = rid.substring(0, rid.length() - 3);
			}
		}
		return rid;
	}

	protected static void processFee(String feeUrl, HttpServletRequest request) {
		Fee fee = getCustomService(request).getFeeByUrl(feeUrl);
		if (fee == null)
			return;
		
		String uri = request.getRequestURI();
		String startQuery = uri.substring(uri.indexOf("s.do")+4);
		startQuery = startQuery.replaceAll("\\?", "&");
		startQuery = startQuery.replaceAll("%26", "&");
		if(startQuery.startsWith("&")){
			startQuery = startQuery.substring(1);
		}
		
		String queryString = startQuery +"&"+ request.getQueryString();
		
//		System.out.println("queryString:"+queryString);

		Map<String, String> parameters = URLUtil.getParameters(queryString);
		
		// 包月计费时所使用的批价包ID参数名优先于正常的批价包ID
		int packId = getIntParameter(parameters,
				ParameterConstants.MONTH_FEE_BAG_ID, -1);
		if (packId == -1) {
			packId = getIntParameter(parameters, ParameterConstants.FEE_BAG_ID,
					-1);
		}
		String mobile = RequestUtil.getMobile();
		UserOrderList list = new UserOrderList();
		String channelId = parameters.get(ParameterConstants.CHANNEL_ID);
		String productId = parameters.get(ParameterConstants.PRODUCT_ID);


		if (StringUtils.isEmpty(channelId) || "null".equals(channelId)) {
			channelId = getBussinessService(request).getDefaultChannelId(
					productId);
		}
		list.setChannelId(channelId);
		String contentId = getResourceId(parameters);
		list.setContentId(contentId);
		list.setCreateTime(new Date());
		list.setFeeId(fee.getId());
		list.setOrderType(fee.getType());
		list.setMobile(mobile);
		list.setPackId(packId);
		list.setPid(productId);
		list.setProductID(fee.getProductId());
		list.setServiceId(fee.getServiceId());
		list.setSpid(fee.getProvider().getProviderId());
		if (fee.getType() == Constants.ORDER_TYPE_VIEW) {// 按次业务,需要加入书包
			ResourcePack pack = getResourceService(request).getResourcePack(
					packId);
			if (pack == null) {
				TagLogger.error("FeeProcess", "计费时批价包对象不存在.", request
						.getQueryString(), null);
				list.setFeeType(Constants.FEE_TYPE_VIEW);
			} else {
				list.setFeeType(pack.getType());
			}

			getCustomService(request).addUserOrderList(list);
			BookBag bag = new BookBag();
			bag.setChannelId(list.getChannelId());
			bag.setContentId(list.getContentId());
			bag.setCreateTime(list.getCreateTime());
			bag.setFeeId(list.getFeeId());
			bag.setMobile(list.getMobile());
			bag.setPid(list.getPid());
			try {
				getCustomService(request).addBookbag(bag);
			} catch (Exception e) {
				TagLogger.error("FeeProcess", "添加书包出错!", request
						.getQueryString(), e);
			}
		} else if (fee.getType() == Constants.ORDER_TYPE_MONTH) {// 包月业务
			// 判断用户是否已经是包月用户了，如果是则不需要在进行记录
			if (getCustomService(request).isOrderMonth(mobile, fee.getId()))
				return;
			ResourcePack pack = getResourceService(request).getResourcePack(
					packId);
			if (pack == null) {
				TagLogger.error("FeeProcess", "计费时批价包对象不存在.", request
						.getQueryString(), null);
				list.setFeeType(Constants.FEE_TYPE_NORMAL);
			} else {
				list.setFeeType(pack.getType());
			}
			// list.setFeeType(pack.getType());
			getCustomService(request).addUserOrderList(list);
			UserBuyMonth month = new UserBuyMonth();
			month.setChannelId(list.getChannelId());
			month.setCreateTime(list.getCreateTime());
			month.setFeeId(list.getFeeId());
			month.setFeeType(list.getFeeType());
			month.setMobile(list.getMobile());
			month.setPid(list.getPid());
			month.setPackId(packId);
			getCustomService(request).addUserBuyMonth(month);

		} else if (fee.getType() == Constants.ORDER_TYPE_FREE) {// 免费业务
			list.setFeeType(Constants.FEE_TYPE_FREE);
			getCustomService(request).addUserOrderList(list);
		} else {
			TagLogger.error("FeeProcess", "未知计费类型：" + fee.getType(), request
					.getQueryString(), null);
		}

	}

	private static CustomService getCustomService(HttpServletRequest request) {
		if (customService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService) wac.getBean("customService");
		}
		return customService;
	}

	private static ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac.getBean("resourceService");
		}
		return resourceService;
	}

	private static BussinessService getBussinessService(
			HttpServletRequest request) {
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