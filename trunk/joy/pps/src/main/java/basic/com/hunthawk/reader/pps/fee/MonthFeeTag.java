/**
 * 
 */
package com.hunthawk.reader.pps.fee;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * ���¶�������
 * 
 * @author BruceSun
 * 
 */
@SuppressWarnings("unchecked")
public class MonthFeeTag extends BaseTag {

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// ���۰�ID
		// Ŀ���ַ���ͣ����ر�ҳ,Ŀ����Ŀҳ
		// Ŀ����ĿID,������ر�ҳ����ҪĿ����Ŀҳ
		int packId = getIntParameter("packid", -1);
		int type = getIntParameter("type", 0);
		int columnId = getIntParameter("columnid", 0);
		String title = getParameter("title", "");
		String url = "";
		if (packId > 0) {
			ResourcePack pack = getResourceService(request).getResourcePack(
					packId);
			if (pack != null) {
				Fee fee = getCustomService(request).getFee(pack.getFeeId());
				if (fee != null) {
					if (fee.getType() == Constants.ORDER_TYPE_MONTH) {
						String targetUrl = "";
						if (type == 0) {// ���ر�ҳ
							targetUrl = request.getQueryString();
						} else if (type == 1) {// ��Ŀҳ
							StringBuilder builder = new StringBuilder();
							builder.append(ParameterConstants.PAGE);
							builder.append("=");
							builder.append(ParameterConstants.PAGE_COLUMN);
							builder.append("&");
							URLUtil.append(builder,
									ParameterConstants.PRODUCT_ID, request);
							URLUtil.append(builder,
									ParameterConstants.PAGEGROUP_ID, request);
							URLUtil.append(builder, ParameterConstants.AREA_ID,
									request);
							builder.append(ParameterConstants.COLUMN_ID);
							builder.append("=");
							builder.append(columnId);
							builder.append("&");
							URLUtil.append(builder,
									ParameterConstants.CHANNEL_ID, request);
							URLUtil.append(builder,
									ParameterConstants.UNICOM_PT, request);
							URLUtil.trimURL(builder);
							targetUrl = builder.toString();
						} else {
							TagLogger.error(tagName, "δ֪Ŀ���ַ����", request
									.getQueryString(), null);
						}
						if (targetUrl.length() > 0) {
							targetUrl += "&" + ParameterConstants.FEE_ID + "="
									+ fee.getId()+"&"+ParameterConstants.MONTH_FEE_BAG_ID
									+"="+packId;
							StringBuilder builder = new StringBuilder();
							if (fee.getIsout() == 0) {
								builder.append( request.getContextPath());
								builder.append("/");
								builder.append(fee.getUrl());
								builder.append(ParameterConstants.PORTAL_PATH);
								builder.append("?");
								builder.append(targetUrl);

							} else {
								builder.append( request.getContextPath());
								builder.append(ParameterConstants.PORTAL_PATH);
								builder.append("?");
								builder.append(ParameterConstants.COMMON_PAGE);
								builder.append("=");
								builder.append(ParameterConstants.COMMON_PAGE_FEE);
								builder.append("&");
								builder.append(ParameterConstants.TEMPLATE_ID);
								builder.append("=");
								builder.append(fee.getTemplateId());
								builder.append("&");
								builder.append(targetUrl);
							}
							url = builder.toString();
							Map velocityMap = new HashMap();
							velocityMap.put("title", title);
							velocityMap.put("url", url);
							url = VmInstance.getInstance().parseVM(velocityMap, this);
						}

					} else {
						TagLogger.error(tagName, "�ƷѶ������Ͳ��ǰ�������", request
								.getQueryString(), null);
					}
				} else {
					TagLogger.error(tagName, "�ƷѶ��󲻴���", request
							.getQueryString(), null);
				}
			} else {
				TagLogger.error(tagName, "���۰�ID������", request.getQueryString(),
						null);
			}
		} else {
			TagLogger.error(tagName, "���۰�ID����Ϊ��", request.getQueryString(),
					null);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), url);
		return resultMap;
	}

	private static CustomService customService;
	private static ResourceService resourceService;

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
}
