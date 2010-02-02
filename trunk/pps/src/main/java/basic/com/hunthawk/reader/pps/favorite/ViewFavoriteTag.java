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
 * �ղع��ܱ�ǩ ��ǩ���ƣ�favorite_view ����˵���� title:������������ addsuccess:��� �ղسɹ���ʾ����
 * delsuccess:ɾ�� �ղسɹ���ʾ����
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
					.debug(tagName, "fn������ȡʧ��", request.getQueryString(), null);
		} else {
			if (flag
					.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_FAVORITE_ADD)) {// ��Ӳ���
				return addFavoriteFunction(request, tagName);
			} else if (flag
					.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_FAVORITE_DEL)) {// ɾ������
				return deleteFavoriteFunction(request, tagName);
			}
		}
		return new HashMap();
	}

	/**
	 * �ղ���ӷ���
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map addFavoriteFunction(HttpServletRequest request, String tagName) {
		boolean isAdd = true;
		String title = getParameter("title", "����");
		String rid = URLUtil.getResourceId(request);
		if (rid == null || "".equals(rid)) {
			TagLogger
					.debug(tagName, "��ԴID��ȡʧ��", request.getQueryString(), null);
			return new HashMap();
		}
		Favorites fav = new Favorites();
		/**
		 * modify by liuxh 09-11-12
		 * ���Ӳ�Ʒid ����Ŀid
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
		int flag=1;//Ĭ�ϳɹ�
		String error_info="";
		try {
			getCustomService(request).addFavorites(fav);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			TagLogger.debug(tagName, "����ղز���ʧ��", request.getQueryString(), e);
			ERROR_FLAG = true;
			flag=0;
			error_info=e.getMessage();
		}
		String addsuccess_msg = getParameter("addsuccess", "�Ѿ���������ӵ������ղؼ�");
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
		// ����ֻ���
		velocityMap.put("mobile", RequestUtil.getMobile());
		// ����ֻ�����
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());
		/**
		 * modify by liuxh 09-11-12
		 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
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
	 * �ղ�ɾ������
	 * 
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map deleteFavoriteFunction(HttpServletRequest request,
			String tagName) {
		boolean isAdd = false;
		String title = getParameter("title", "����");
		boolean ERROR_FLAG = false;
		String cid = request.getParameter(ParameterConstants.RESOURCE_ID);
		if ("".equals(cid)) {
			return new HashMap();
		}
		int flag=1;//Ĭ�ϳɹ�
		try {
//			System.out.println(RequestUtil.getMobile());
			getCustomService(request).deleteFavorites(RequestUtil.getMobile(),cid,request.getParameter(ParameterConstants.PRODUCT_ID));
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			ERROR_FLAG = true;
			flag=0;
			TagLogger.debug(tagName, "ɾ���ղ�ʧ��", request.getQueryString(), e);
			// e.printStackTrace();
		}
		String delsuccess_msg = getParameter("delsuccess", "�Ѿ��ɹ�������������ղؼ���ɾ��");
		if (ERROR_FLAG) {
			delsuccess_msg = "�ղ�ɾ������ʧ��";
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
		// ����ֻ���
		velocityMap.put("mobile", RequestUtil.getMobile());
		// ����ֻ�����
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());
		/**
		 * modify by liuxh 09-11-12
		 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
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
