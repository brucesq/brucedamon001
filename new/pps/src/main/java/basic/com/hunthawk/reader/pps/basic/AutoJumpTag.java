/**
 * �Զ���ת��ǩ
 */
package com.hunthawk.reader.pps.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author liuxh ��ǩ���ƣ�auto_jump�Զ���ת��ǩ ����˵���� title:title��ʾ���� text:ҳ����ʾ����
 *         target:Ŀ���ַ timer:��ת���ʱ��
 */
public class AutoJumpTag extends BaseTag {

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
		String text = getParameter("text", "");
		String title = getParameter("title", "");
		String target = getParameter("target", "");
		/** ��ת���ʱ�� Ĭ��3�� */
		int timer = getIntParameter("timer", 3);
		int type = getIntParameter("type", 1);
		Map resultMap = new HashMap();
		// if(target==null || StringUtils.isEmpty(target)){
		// String value = "$#back.title="+title+",tmd="+tagTemplateId+"#";
		// resultMap.put(TagUtil.makeTag(tagName), value);
		// target=getJumpUrl(request);
		// }
		// Map velocityMap = new HashMap();
		// velocityMap.put("title", title);
		// velocityMap.put("url", target);
		// velocityMap.put("message", text);
		// resultMap.put(TagUtil.makeTag(tagName),
		// DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		if (type == 1) {
			target = getCurrentUrl(request);
		} else if (type == 2) {
			target = getJumpUrl(request);
		}
		// System.out.println("Target:"+target);
		if (timer == 0) {
			Redirect.sendRedirect(target);
		} else {
			com.hunthawk.tag.process.Refresh.pageRefresh(timer, target);
		}
		return resultMap;
	}

	private String getCurrentUrl(HttpServletRequest request) {
		Map params = new HashMap();
		Random ran = new Random();
		params.put("random", "" + ran.nextInt());

		return URLUtil.urlChange(request, params, new ArrayList());
	}

	private String getJumpUrl(HttpServletRequest request) {

		int td = ParamUtil.getIntParameter(request,
				ParameterConstants.TEMPLATE_ID, -1);
		String fn = request.getParameter(ParameterConstants.COMMON_PAGE);
		if (td != -1 && !ParameterConstants.COMMON_PAGE_FEE.equals(fn)) {
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(URLUtil.removeParameter(request.getQueryString(),
					ParameterConstants.TEMPLATE_ID,
					ParameterConstants.COMMON_PAGE,
					ParameterConstants.AUTHOR_ID,
					ParameterConstants.VOTE_CONTENT_ID,
					ParameterConstants.VOTE_ITEM_ID,
					ParameterConstants.VOTE_VOTE_TYPE,
					ParameterConstants.VERSION_TYPE));
			return sb.toString();
		}
		String url = "";
		String page = request.getParameter(ParameterConstants.PAGE);
		if (page.equals(ParameterConstants.PAGE_PRODUCT)) {// ��Ʒҳ
			String unicom_pt = request
					.getParameter(ParameterConstants.UNICOM_PT);
			if (unicom_pt != null && !"".equals(unicom_pt)) {
				url = unicom_pt;
			} else {
				return "";
			}
		} else {
			StringBuilder back = new StringBuilder();
			back.append(request.getContextPath());
			back.append(ParameterConstants.PORTAL_PATH);
			back.append("?");
			back.append(ParameterConstants.PAGE);
			back.append("=");
			if (page.equals(ParameterConstants.PAGE_COLUMN)) {// ��Ŀҳ-->��Ʒҳ
//				back.append(ParameterConstants.PAGE_PRODUCT);
//				back.append("&");
//				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
//				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
//				URLUtil.append(back, ParameterConstants.AREA_ID, request);
//				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
//				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				
				int column_id = ParamUtil.getIntParameter(request,
						ParameterConstants.COLUMN_ID, -1);
				if (column_id > 0) {
					Columns obj = getBussinessService(request).getColumns(
							column_id);
					if (obj != null) {
						Columns parentObj = obj.getParent();
						if (parentObj != null) {// �и���Ŀ ������Ŀҳ
							back.append(ParameterConstants.PAGE_COLUMN);
							back.append("&");
							URLUtil.append(back, ParameterConstants.PRODUCT_ID,
									request);
							back.append(ParameterConstants.PAGEGROUP_ID);
							back.append("=");
							back.append(parentObj.getPagegroup().getId());
							back.append("&");
							URLUtil.append(back, ParameterConstants.AREA_ID,
									request);
							back.append(ParameterConstants.COLUMN_ID);
							back.append("=");
							back.append(parentObj.getId());
							back.append("&");
							URLUtil.append(back, ParameterConstants.CHANNEL_ID,
									request);
							URLUtil.append(back, ParameterConstants.UNICOM_PT,
									request);
							back.append(ParameterConstants.PAGE_NUMBER);
							back.append("=");
							back.append(1);
							back.append("&");
							URLUtil.append(back, ParameterConstants.FEE_ID,
									request);
						} else {// �޸���Ŀ ���ز�Ʒҳ
							back.append(ParameterConstants.PAGE_PRODUCT);
							back.append("&");
							URLUtil.append(back, ParameterConstants.PRODUCT_ID,
									request);
							URLUtil.append(back,
									ParameterConstants.PAGEGROUP_ID, request);
							URLUtil.append(back, ParameterConstants.AREA_ID,
									request);
							URLUtil.append(back, ParameterConstants.CHANNEL_ID,
									request);
							URLUtil.append(back, ParameterConstants.UNICOM_PT,
									request);
						}
					} else {
						TagLogger.info("auto_jump", "idΪ" + column_id
								+ "����Ŀ�����ڣ���L��ַ��һ����Ч��ַ", request
								.getQueryString(), null);
					}
				}
				
				
			} else if (page.equals(ParameterConstants.PAGE_RESOURCE)) {// ����ҳ-->��Ŀҳ
				back.append(ParameterConstants.PAGE_COLUMN);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				back.append(ParameterConstants.PAGE_NUMBER);
				back.append("=");
				back.append(1);
				back.append("&");
				URLUtil.append(back, ParameterConstants.FEE_ID, request);
			} else if (page.equals(ParameterConstants.PAGE_DETAIL)) {// ����ҳ-->����ҳ
				back.append(ParameterConstants.PAGE_RESOURCE);
				back.append("&");
				URLUtil.append(back, ParameterConstants.PRODUCT_ID, request);
				URLUtil.append(back, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(back, ParameterConstants.AREA_ID, request);
				URLUtil.append(back, ParameterConstants.COLUMN_ID, request);
				URLUtil.append(back, ParameterConstants.FEE_BAG_ID, request);
				URLUtil.append(back, ParameterConstants.FEE_BAG_RELATION_ID,
						request);
				back.append(ParameterConstants.RESOURCE_ID);
				back.append("=");
				back.append(URLUtil.getResourceId(request));
				back.append("&");
				URLUtil.append(back, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(back, ParameterConstants.UNICOM_PT, request);
				back.append(ParameterConstants.PAGE_NUMBER);
				back.append("=");
				back.append(1);
				back.append("&");
				URLUtil.append(back, ParameterConstants.FEE_ID, request);
			}
			url = URLUtil.trimUrl(back).toString();
		}
		return url;
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