/**
 * 
 */
package com.hunthawk.reader.pps.resource;

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
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author sunquanzhi
 * 
 */
public class MovieNavTag extends BaseTag {

	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;
	private ResourceService resourceService;
	private IphoneService iphoneService;
	private static final int DEFAULT_PAGE_SIZE = 5; // 默认分页 5条/页
	private static final int DEFAULT_MAX_COUNT = 20;// 默认列表最大范围 20条
	/** 不显示翻页相关的链接 */
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
		String productId = request.getParameter(ParameterConstants.PRODUCT_ID);
		boolean isIphone = getIphoneService(request).isIphoneProduct(productId);
		int month_fee_bag_id = ParamUtil.getIntParameter(request,
				ParameterConstants.MONTH_FEE_BAG_ID, -1);
		String resourceId = URLUtil.getResourceId(request);
		if (StringUtils.isEmpty(resourceId)) {
			TagLogger.error(tagName, "往期回顾标签获取不到资源ID",
					request.getQueryString(), null);
			return new HashMap();
		}
		// System.out.println("00011111asd");
		int columnId = getIntParameter("columnId", -1);
		if (columnId < 0) {
			columnId = ParamUtil.getIntParameter(request,
					ParameterConstants.COLUMN_ID, -1);
			if (columnId < 0) {
				TagLogger.error(tagName, "栏目ID为空", request.getQueryString(),
						null);
				return new HashMap();
			}
		}

		ResourceAll res = getResourceService(request).getResource(resourceId);

		List<ResourcePackReleation> rprs = null;
		try {
			rprs = getResourceService(request).findDivisions(
					String.valueOf(columnId), res, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TagLogger.error(tagName, "获取往期资源列表失败", request.getQueryString(), e);
			return new HashMap();
		}

		String title = getParameter("title", "全部剧集");
		int type = getIntParameter("type", 1);
		Map resultMap = new HashMap();
		Map velocityMap = new HashMap();
		{
			int templateId = getIntParameter("templateId", -1);
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			sb.append(ParameterConstants.COMMON_PAGE);
			sb.append("=");
			sb.append(ParameterConstants.COMMON_PAGE_LINK);
			sb.append("&");
			sb.append(ParameterConstants.TEMPLATE_ID);
			sb.append("=");
			sb.append(templateId);
			sb.append("&");
			// sb.append(URLUtil.removeParameter(request.getQueryString(),
			// ParameterConstants.PAGE_NUMBER));
			sb.append(URLUtil.removeParameter(request.getQueryString(),
					ParameterConstants.PAGE_NUMBER,
					ParameterConstants.SEARCH_PARAM_VALUE,
					ParameterConstants.SEARCH_TYPE,
					ParameterConstants.QUICK_SEARCH_LINK_NAME,
					ParameterConstants.COMMENT_PARAM_VALUE,
					ParameterConstants.CUSTOM_KEY_VALUE,
					ParameterConstants.COMMENT_PLATE,
					ParameterConstants.COMMENT_TARGET,
					ParameterConstants.COMMENT_TARGET_ID,
					ParameterConstants.VOTE_VOTE_TYPE,
					ParameterConstants.VOTE_ITEM_ID,
					ParameterConstants.VOTE_CONTENT_ID,
					ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			
			velocityMap.put("allurl", sb.toString());
			velocityMap.put("size", rprs.size());
		}
		String result = "";
		ResourceAll preResource = null;
		ResourceAll nextResource = null;
		ResourcePackReleation preRel = null;
		ResourcePackReleation nextRel = null;
		boolean isPre = false;
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			ResourcePackReleation rpr = (ResourcePackReleation) it.next();
			ResourceAll resourceAll = getResourceService(request).getResource(
					rpr.getResourceId());

			if (resourceAll == null) {
				// System.out.println("is null:"+rpr.getResourceId());
				continue;
			}
			if(isPre){
				nextResource = resourceAll;
				nextRel = rpr;
				break;
			}
			if (resourceAll.getId().equals(resourceId)) {
				isPre = true;
			}
			if (!isPre) {
				preResource = resourceAll;
				preRel = rpr;
			}
		}
		int preHas = 0;
		int nextHas = 0;
		if(preResource != null){
			preHas = 1;
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap = null;
			
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append("r");
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			sb.append(ParameterConstants.COLUMN_ID);
			sb.append("=");
			sb.append(columnId);
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(preRel.getPack().getId());
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(preRel.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(preRel.getResourceId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if (feeMap != null) {
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			
			velocityMap.put("preurl", sb.toString());
			velocityMap.put("pretitle", preResource.getBComment());
			// System.out.println(resourceAll.getDivision()+" :
			// "+resourceAll.getDivisionContent());
			velocityMap.put("preresource", preResource); // 新添加上了 资源对象
			String imgUrl = CoverPreview.getPreview(
					getResourceService(request), preResource, 51);// 把预览图放进去
			velocityMap.put("prepreview", imgUrl);
			velocityMap.put("preauthor", getAuthorName(preResource, request));
			
		}
		
		if(nextResource != null){
			nextHas=1;
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap = null;
			
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append("r");
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			sb.append(ParameterConstants.COLUMN_ID);
			sb.append("=");
			sb.append(columnId);
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(nextRel.getPack().getId());
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(nextRel.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(nextRel.getResourceId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if (feeMap != null) {
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			
			velocityMap.put("nexturl", sb.toString());
			velocityMap.put("nexttitle", nextResource.getBComment());
			// System.out.println(resourceAll.getDivision()+" :
			// "+resourceAll.getDivisionContent());
			velocityMap.put("nextresource", nextResource); // 新添加上了 资源对象
			String imgUrl = CoverPreview.getPreview(
					getResourceService(request), nextResource, 51);// 把预览图放进去
			velocityMap.put("nextpreview", imgUrl);
			velocityMap.put("nextauthor", getAuthorName(nextResource, request));
			
		}
		velocityMap.put("preHas", preHas);
		velocityMap.put("nextHas", nextHas);
		velocityMap.put("strUtil", new StrUtil());
		velocityMap.put("previewUtil", new CoverPreview());
		velocityMap.put("service", getResourceService(request));
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		// System.out.println("4444asd");
		result = DBVmInstance.getInstance().parseVM(velocityMap, this, tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
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

	private FeeLogicService getFeeLogicService(HttpServletRequest request) {
		if (feeLogicService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeLogicService = (FeeLogicService) wac.getBean("feeLogicService");
		}
		return feeLogicService;
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

	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac.getBean("iphoneService");
		}
		return iphoneService;
	}

	private String getAuthorName(ResourceAll resource,
			HttpServletRequest request) {
		Integer[] authorids = resource.getAuthorIds();
		StringBuilder str = new StringBuilder();
		String name = "";
		for (int i = 0; i < authorids.length; i++) {
			ResourceAuthor author = getResourceService(request)
					.getResourceAuthor(authorids[i]);
			str.append((author.getPenName() == null || "".equals(author
					.getPenName())) ? author.getName() : author.getPenName());
			str.append(",");
			if (i == authorids.length - 1) {
				// 去掉最后一个,
				str.replace(str.lastIndexOf(","), str.length(), "");
			}
		}
		String authorPenName = str.toString().trim();
		try {
			name = StrUtil.getLimitStr(authorPenName,
					ParameterConstants.AUTHOR_NAME_BYTES,
					ParameterConstants.REPLACE_SYMBOL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 作者
		return name;
	}

	/**
	 * 分页
	 * 
	 * @param list
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	private List page(List list, int pageNo, int pageSize) {
		if (list == null || list.size() < 2) {
			return list;
		}
		int start = pageSize * (pageNo - 1);
		int end = pageSize * pageNo;
		start = start > list.size() - 1 ? list.size() - 1 : start;
		end = end > list.size() ? list.size() : end;
		return list.subList(start, end);
	}

}
