/**
 * ���������û���½�����ǩ
 * ��ǩ���ƣ�uclogin_result
 * ����˵����
 * 
 */
package com.bruce.pps.social.uc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.UserInfo;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.GuestService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Redirect;

/**
 * @author liuxh
 * 
 */
public class UCLoginResultTag extends BaseTag {

	private BussinessService bussinessService;
	private GuestService guestService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest,
	 *      java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		String password = request.getParameter("password");
		String loginname = request.getParameter("name");
		// System.out.println("login --->password="+password+" ;
		// name="+loginname);
		/** ��½ʧ�ܷ���URL */
		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(),
				ParameterConstants.TEMPLATE_ID));
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(loginname)) {
			com.hunthawk.tag.process.Refresh.pageRefresh(3, backUrl.toString());// 3����
			Map velocityMap = new HashMap();
			/**
			 * ��Ӳ�����ʶ 0.ʧ�� 1.�ɹ�
			 */
			velocityMap.put("flag", 0);
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
					.parseVM(velocityMap, this, tagTem));
			return resultMap;
		} else {
			/** ����Զ�̷����ж��Ƿ��ǺϷ��û� */
			UserInfo info = getGuestService(request).getUserInfoByName(
					loginname);
			if (info == null || !info.getPassword().equals(password)) {
				com.hunthawk.tag.process.Refresh.pageRefresh(3, backUrl
						.toString());// 3����
				Map velocityMap = new HashMap();
				/**
				 * ��Ӳ�����ʶ 0.ʧ�� 1.�ɹ�
				 */
				velocityMap.put("flag", 0);
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), DBVmInstance
						.getInstance().parseVM(velocityMap, this, tagTem));
				return resultMap;
			} else {
				/** ��½�ɹ���ת��URL��ַӦΪ����������ҳ��ַ��ͨ����ȡϵͳ������� */
				String cid = getParameter("columnId");
				StringBuilder sb = new StringBuilder();
				sb.append(request.getContextPath());
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
				sb.append(ParameterConstants.PAGE);
				sb.append("=");
				sb.append(ParameterConstants.PAGE_COLUMN);
				sb.append("&");
				URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(sb, ParameterConstants.AREA_ID, request);
				sb.append(ParameterConstants.COLUMN_ID);
				sb.append("=");
				sb.append(cid);
				sb.append("&");

				URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
				URLUtil.trimURL(sb);
				
				RequestUtil.addCookie("x-up-calling-line-id", info.getMobile());
				request.getSession().setAttribute("x-up-calling-line-id",
						info.getMobile());
				Redirect.sendRedirect(sb.toString());// ��ˢ����ת����½ҳ��
				return new HashMap();
			}
		}

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
