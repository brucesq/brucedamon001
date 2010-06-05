/**
 * 个人中心注册结果标签
 * 标签名称:ucregist_result
 */
package com.bruce.pps.social.uc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.UserInfo;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.GuestService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author liuxh
 * 
 */
public class UCRegistResultTag extends BaseTag {

	private static GuestService guestService;
	private static BussinessService bussinessService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest,
	 *      java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		/** 得到手机号、用户名、验证码、密码 */
		boolean success = true;
		try {
			String msg = "";
			boolean err = false;
			String mobile = RequestUtil.getMobile();
			String nickname = ParamUtil.getParameter(request, "nickname");
			int gender = ParamUtil.getIntParameter(request, "gender", 2);
			String birthday = ParamUtil.getParameter(request, "birthday");
			String city = ParamUtil.getParameter(request, "city");
			UserInfo info = getGuestService(request).getUserInfo(mobile);
			info.setAddress(city);
			info.setNickname(nickname);
			info.setBirthday(birthday);
			info.setSex(gender);
			getGuestService(request).updateUserInfo(info);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		int tagTemplateId = getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		Map resultMap = new HashMap();
		Map velocityMap = new HashMap();
		velocityMap.put("success", success);
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));
		return resultMap;

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

	private GuestService getGuestService(HttpServletRequest request) {
		if (guestService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			guestService = (GuestService) wac.getBean("guestService");
		}
		return guestService;
	}

}
