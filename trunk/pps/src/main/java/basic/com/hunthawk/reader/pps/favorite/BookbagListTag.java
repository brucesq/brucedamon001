package com.hunthawk.reader.pps.favorite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * ����б��ǩ ��ǩ���ƣ�bookbag_list ����˵���� pageSize:ÿҳ��ʾ������ noPageLink:����ʾ��ҳ��ص�����
 * templateId:ɾ��ȷ��ҳģ��ID isConfirm:�Ƿ���ȷ��ɾ�� 1.ȷ����ʾ -1.ֱ��ɾ����ȷ�� showDelLink:�Ƿ���ʾɾ������
// * columnids:��ĿID����  ���ڻ�ȡ��ͬ���Ͳ�Ʒ�µ���Դ �ò�ͬ��ģ��  �Ⱥ�˳�� ͼ��-��ֽ-��־-����
 * bColumnid:ͼ����Ŀ
 * mColumnid:��־��Ŀ
 * nColumnid����ֽ��Ŀ
 * cColumnid��������Ŀ
 * @author liuxh
 * 
 */
public class BookbagListTag extends BaseTag {

	private CustomService customService;
	private BussinessService bussinessService;
	private ResourceService resourceService;

	private static final int DEFAULT_PAGE_SIZE = 10; // Ĭ����ʾ10��
	/** ����ʾ��ҳ��ص����� */
	private boolean noPageLink;

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub

		/**
		 * modify by liuxh 09-11-12
		 * ���ӱ�ǩ ���� 
		 */
			int bColumnid=getIntParameter("bColumnid",-1);
			int mColumnid=getIntParameter("mColumnid",-1);
			int nColumnid=getIntParameter("nColumnid",-1);
			int cColumnid=getIntParameter("cColumnid",-1);
			Integer[] cids=new Integer[]{bColumnid,nColumnid,mColumnid,cColumnid};
			
		/**
		 * end
		 */
		Map resultMap = new HashMap();
		String result = "";

		boolean isConfirm = getIntParameter("isConfirm", 1) > 0;// �ж��Ƿ����ɾ��ȷ��
		// Ĭ��ȷ��
		boolean showDelLink = getIntParameter("showDelLink", 1) > 0;// �ж��Ƿ���ʾɾ������
		// Ĭ����ʾ
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ

		// �õ��ֻ���
		String mobile = RequestUtil.getMobile();
		List bookBags = getCustomService(request).getUserBookbagsByPage(mobile,"",
				pageSize, currentPage);

		// �ж��Ƿ񵼺�
		if (!isNoPageLink()) {
			List resAll = getCustomService(request).getUserBookbag(mobile);
			Navigator navi = new Navigator(resAll.size(), currentPage,
					pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}

		// ɾ��ģ��ID
		String templateId = getParameter("templateId", "");
		String msg = "";
		// �Ѷ���浽list����
		List<Object> objs = new ArrayList<Object>();
		if (bookBags == null || bookBags.size() < 1) {
			msg = "��Ŀǰû��Ԥ��ͼ�顣";
		} else {
			msg = "";
			for (Iterator it = bookBags.iterator(); it.hasNext();) {
				BookBag bag = (BookBag) it.next();
				String rid = bag.getContentId();// ����id
				String channelId = bag.getChannelId();// ����ID
				String pid = bag.getPid();// ��ƷID
				// ��װ����
				Map mapBag = new HashMap();
				mapBag.put(ParameterConstants.PRODUCT_ID, pid);
				mapBag.put(ParameterConstants.RESOURCE_ID, rid);
				Integer columnId=cids[Integer.parseInt(rid.substring(0,1))-1];
				if(columnId<0){
					TagLogger.debug(tagName, "��ĿID������ϢΪ�ջ�ָ������ĿID���ϲ�����ȫ(4��)", request.getQueryString(), null);
					columnId=ParamUtil.getIntParameter(request, ParameterConstants.COLUMN_ID, -1);
				}
				mapBag.put(ParameterConstants.COLUMN_ID, columnId);
				String url = getPageUrl(request, mapBag);// ��ԴURL
				ResourceAll resource = getResourceService(request).getResource(
						rid);

				if (resource == null) {
					continue;
				}
				// ����obj
				Map<String, Object> obj = new HashMap<String, Object>();
				String tempTitle = resource.getName();// ͼ������
				obj.put("url", url);
				obj.put("value", tempTitle);
				// obj.put("delete", delete.toLowerCase());
				String delurl = delUrl(request, isConfirm, "1", templateId,
						mapBag);
				obj.put("urldel", delurl);
				obj.put("valuedel", "[ɾ]");
				obj.put("resource", resource); // ��������� ��Դ����
				String imgUrl = CoverPreview.getPreview(
						getResourceService(request), resource, 51);// ��Ԥ��ͼ�Ž�ȥ
				obj.put("preview", imgUrl);
				// ����objs
				objs.add(obj);
			}
			map.put("objs", objs);
			map.put("showdel", showDelLink);
		}
		map.put("msg", msg);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
		// ����ֻ���
		map.put("mobile", RequestUtil.getMobile());
		// ����ֻ�����
		map.put("mobileType", RequestUtil.getMobileType());
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);

		/*
		 * result = VmInstance.getInstance().parseVM(map, this);
		 * resultMap.put(TagUtil.makeTag(tagName), result);
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

	/**
	 * 
	 */
	/**
	 * ҳ�漶�����+��ƷID+ҳ����ID+����ID+��ĿID+���۰�ID+���۰�����ID+��ԴID+�½�ID+�ƹ�����ID
	 * +��ͨPT����+ҳ��+ÿҳ��ʾ����+�Ʒ�ID
	 * 
	 * @param request
	 * @param currentPage
	 *            ��ǰҳ ����URL
	 * @param templateid
	 *            ɾ�� ����Դ URL
	 * @param flag
	 *            ɾ������Դ���ӵ��жϱ�־
	 * @param contentid
	 *            ɾ������Դ�����贫�������ID����
	 * @return
	 */
	private String getPageUrl(HttpServletRequest request, Map obj) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_RESOURCE);
		sb.append("&");
		sb.append(ParameterConstants.PRODUCT_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.PRODUCT_ID));
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		sb.append(ParameterConstants.COLUMN_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.COLUMN_ID));
		sb.append("&");
//		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		sb.append(ParameterConstants.RESOURCE_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.RESOURCE_ID));
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append(1);

		return sb.toString();
	}

	private String delUrl(HttpServletRequest request, boolean isConfirm,
			String currentPage, String templateid, Map obj) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		if (!isConfirm) {// ����ɾ��ȷ��
			sb.append(ParameterConstants.COMMON_PAGE);
			sb.append("=");
			sb.append(ParameterConstants.COMMON_PAGE_BOOKMARK_DEL);
			sb.append("&");
		}
		sb.append(ParameterConstants.TEMPLATE_ID);
		sb.append("=");
		sb.append(templateid);
		sb.append("&");
		String url = request.getQueryString();
		sb.append(URLUtil.removeParameter(
				url.substring((url.indexOf("?") + 1)),
				ParameterConstants.TEMPLATE_ID));
		sb.append("&");
		sb.append(ParameterConstants.RESOURCE_ID);
		sb.append("=");
		sb.append(obj.get(ParameterConstants.RESOURCE_ID));

		return sb.toString();
	}
}
