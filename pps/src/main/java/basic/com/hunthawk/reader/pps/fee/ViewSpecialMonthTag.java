package com.hunthawk.reader.pps.fee;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * NѡX���ܱ�ǩ ��ǩ���ƣ�special_month_view ����˵���� title:������������
 * 
 * @author liuxh
 * 
 */
public class ViewSpecialMonthTag extends BaseTag {
	private CustomService customService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		System.out.println("id---->"
				+ request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		// �õ�����ѡ��ĸ���
		int allowCount = Integer.parseInt(request.getParameter("allow"));
		// �õ��û�����ʷѡ��ĸ���
		int userCount = Integer.parseInt(request.getParameter("old"));
		String msg = "";
		int flag=1;//Ĭ�ϲ����ɹ�
		if(allowCount==userCount){
			msg="���Ѿ�ѡ����"+allowCount+"���飬�����ٽ���ѡ������ˣ�";
			
		}else{
			// ��ȡ��ز���
			String pid = request.getParameter(ParameterConstants.PRODUCT_ID) == null ? ""
					: request.getParameter(ParameterConstants.PRODUCT_ID);
			String feeId = request.getParameter(ParameterConstants.FEE_ID) == null ? ""
					: request.getParameter(ParameterConstants.FEE_ID);
			String mobile = RequestUtil.getMobile();
			String packId = request.getParameter(ParameterConstants.FEE_BAG_ID) == null ? ""
					: request.getParameter(ParameterConstants.FEE_BAG_ID);
			String channelId = request.getParameter(ParameterConstants.CHANNEL_ID);
			if (StringUtils.isEmpty(pid) || StringUtils.isEmpty(feeId)
					|| StringUtils.isEmpty(packId)) {
				TagLogger.debug(tagName, "����Ϊ��", request.getQueryString(), null);
				return new HashMap();
			}
			
			boolean error = false;
			try {
				String resourceId = request
						.getParameter(ParameterConstants.SEARCH_PARAM_VALUE);
				getCustomService(request).addChoiceBook(pid, resourceId, feeId,
						mobile, Integer.parseInt(packId), channelId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				TagLogger.debug(tagName, "NѡX��¼�û�ѡ�����ʱ����",
						request.getQueryString(), e);
				error = true;
				flag=0;
			}
			if (!error) {
				msg = "���Ѿ��ɹ�ѡ����һ��ͼ�飡";
				userCount++;
			}else{
				msg="����ʧ��!";
			}
			
		}
		
		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(),
				ParameterConstants.TEMPLATE_ID, "old", "allow"));
		Map velocityMap = new HashMap();
		if (allowCount == userCount) {
			velocityMap.put("title", getParameter("title", "�����ϼ�"));
		} else {
			velocityMap.put("title", getParameter("title", "���ؼ���ѡ��"));
		}
		velocityMap.put("url", backUrl);
		velocityMap.put("successTitle", msg);
		/**
		 * modify by liuxh 09-11-12
		 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
		 */
		velocityMap.put("flag", flag);
		/**
		 * end
		 */
		String content = VmInstance.getInstance().parseVM(velocityMap, this);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);
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
}
