/**
 * ���������û�ע���ǩ
 * ��ǩ���ƣ�ucregist_form
 * ����˵����
 * 		templateId:�����ʾҳģ��ID
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
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.GuestService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author liuxh
 * 
 */
public class UCRegistTag extends BaseTag {

	private GuestService guestService;
	private BussinessService bussinessService;

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

		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(request.getQueryString());

		if (request.getParameter("name") == null) {// ע���һ��
			Map velocityMap = new HashMap();
			velocityMap.put("step", 1);
			velocityMap.put("url", sb.toString());
			velocityMap.put("msg", "");
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
					.parseVM(velocityMap, this, tagTem));
			return resultMap;
		} else {
			/** �õ����� */

			String errorMsg = "";// ������ʾ��Ϣ
			boolean err = false;// �����ʶ
			String loginname = request.getParameter("name");

			String passwd = request.getParameter("passwd");
			String passwd2 = request.getParameter("passwd2");
			if (StringUtils.isEmpty(loginname)) {
				err = true;
				errorMsg = "�ʺŲ���Ϊ��";
			} else if (loginname.length() < 4 || loginname.length() > 16) {
				err = true;
				errorMsg = "�˻��������ǳ���4-16λ֮�䡢Ӣ��Сд��a~z��������(0~9)������»������";
			} else {
				boolean exists = getGuestService(request).isNameExists(
						StringUtils.trim(loginname));
				if (exists) {
					err = true;
					errorMsg = "�û����Ѿ���ʹ��!";
				}
			}

			if (!err) {
				if (StringUtils.isEmpty(passwd) || !passwd.equals(passwd2)) {
					err = true;
					errorMsg = "�����ȷ�����벻һ��";
				}
			}

			if (err) {
				Map velocityMap = new HashMap();
				velocityMap.put("step", 1);
				velocityMap.put("url", sb.toString());
				velocityMap.put("msg", errorMsg);
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), DBVmInstance
						.getInstance().parseVM(velocityMap, this, tagTem));
				return resultMap;
			} else {
				String mobile = RequestUtil.getMobile();
				if (mobile.startsWith("3")) {
					mobile = getGuestService(request).registerNewMobile(
							loginname, passwd);
					RequestUtil.addCookie("x-up-calling-line-id", mobile);
					request.getSession().setAttribute("x-up-calling-line-id",
							mobile);
				} else {
					getGuestService(request).registerNewMobile(mobile,
							loginname, passwd);
				}

				int templateId = getIntParameter("templateId", -1);
				if (templateId < 0) {
					return new HashMap();
				}
				Map<String, String> values = new HashMap<String, String>();
				values.put(ParameterConstants.TEMPLATE_ID, String
						.valueOf(templateId));
				String url = URLUtil.getUrl(values, request);
				Map velocityMap = new HashMap();
				velocityMap.put("step", 2);
				velocityMap.put("msg", " ");
				velocityMap.put("url", url);
				velocityMap.put("mobile", mobile);
				velocityMap.put("name", loginname);
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), DBVmInstance
						.getInstance().parseVM(velocityMap, this, tagTem));
				return resultMap;
			}
		}

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
