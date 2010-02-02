package com.hunthawk.reader.pps.favorite;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.Favorites;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * 收藏功能标签 标签名称：favorite_view 参数说明： title:返回链接文字 addsuccess:添加 收藏成功显示文字
 * delsuccess:删除 收藏成功显示文字
 * 
 * @author liuxh
 * 
 */
public class ViewFavoriteTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String flag = request.getParameter(ParameterConstants.COMMON_PAGE);
		if (StringUtils.isEmpty(flag)) {
			TagLogger
					.debug(tagName, "fn参数获取失败", request.getQueryString(), null);
		} else {
			if (flag
					.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_FAVORITE_ADD)) {// 添加操作
				return addFavoriteFunction(request, tagName);
			} else if (flag
					.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_FAVORITE_DEL)) {// 删除操作
				return deleteFavoriteFunction(request, tagName);
			}
		}
		return new HashMap();
	}

	/**
	 * 收藏添加方法
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map addFavoriteFunction(HttpServletRequest request, String tagName) {
		boolean isAdd = true;
		String title = getParameter("title", "返回");
		String rid = URLUtil.getResourceId(request);
		if (rid == null || "".equals(rid)) {
			TagLogger
					.debug(tagName, "资源ID获取失败", request.getQueryString(), null);
			return new HashMap();
		}
		Favorites fav = new Favorites();
		/**
		 * modify by liuxh 09-11-12
		 * 增加产品id 和栏目id
		 */
		fav.setProductid(request.getParameter(ParameterConstants.PRODUCT_ID));
		fav.setColumnid(ParamUtil.getIntParameter(request, ParameterConstants.COLUMN_ID, -1));
		/**
		 * end
		 */
		fav.setContentId(rid);
		fav.setCreateTime(new Date());
		fav.setMobile(RequestUtil.getMobile());
		fav.setPackRelationId(ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID, 0));
		boolean ERROR_FLAG = false;
		int flag=1;//默认成功
		String error_info="";
		try {
			getCustomService(request).addFavorites(fav);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			TagLogger.debug(tagName, "添加收藏操作失败", request.getQueryString(), e);
			ERROR_FLAG = true;
			flag=0;
			error_info=e.getMessage();
		}
		String addsuccess_msg = getParameter("addsuccess", "已经将本书添加到您的收藏夹");
		if (ERROR_FLAG) {
			addsuccess_msg = error_info;
		}
		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl
				.append(URLUtil.removeParameter(request.getQueryString(),
						ParameterConstants.TEMPLATE_ID,
						ParameterConstants.COMMON_PAGE));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("addsuccess", addsuccess_msg);
		// 添加手机号
		velocityMap.put("mobile", RequestUtil.getMobile());
		// 添加手机类型
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", flag);
		/**
		 * end
		 */
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));

		/*
		 * String content = VmInstance.getInstance().parseVM(velocityMap, this);
		 * Map resultMap = new HashMap();
		 * resultMap.put(TagUtil.makeTag(tagName), content);
		 */
		return resultMap;
	}

	/**
	 * 收藏删除方法
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map deleteFavoriteFunction(HttpServletRequest request,
			String tagName) {
		boolean isAdd = false;
		String title = getParameter("title", "返回");
		boolean ERROR_FLAG = false;
		String cid = request.getParameter(ParameterConstants.RESOURCE_ID);
		if ("".equals(cid)) {
			return new HashMap();
		}
		int flag=1;//默认成功
		try {
//			System.out.println(RequestUtil.getMobile());
			getCustomService(request).deleteFavorites(RequestUtil.getMobile(),cid,request.getParameter(ParameterConstants.PRODUCT_ID));
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			ERROR_FLAG = true;
			flag=0;
			TagLogger.debug(tagName, "删除收藏失败", request.getQueryString(), e);
			// e.printStackTrace();
		}
		String delsuccess_msg = getParameter("delsuccess", "已经成功将本书从您的收藏夹中删除");
		if (ERROR_FLAG) {
			delsuccess_msg = "收藏删除操作失败";
		}
		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(),
				ParameterConstants.TEMPLATE_ID, ParameterConstants.COMMON_PAGE,
				ParameterConstants.RESOURCE_ID));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("delsuccess", delsuccess_msg);
		// 添加手机号
		velocityMap.put("mobile", RequestUtil.getMobile());
		// 添加手机类型
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", flag);
		/**
		 * end
		 */

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));

		/*
		 * String content = VmInstance.getInstance().parseVM(velocityMap, this);
		 * Map resultMap = new HashMap();
		 * resultMap.put(TagUtil.makeTag(tagName), content);
		 */
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
