package com.hunthawk.reader.pps.column;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 
 * @author liuxh
 * 
 */
public class ColumnTag extends BaseTag {

	private static BussinessService bussinessService;
	private static ResourceService resourceService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title = getParameter("title", "");
		String cid = getParameter("columnId", "-1");
		String showNum = getParameter("showNum", "0");

		String od = getParameter("od", "-1");
		String ods = getParameter("ods", "-1");

		int tagTemplateId = this.getIntParameter("tmd", 0);

		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);

		}

		if (Integer.parseInt(cid) < 0) {
			return null;
		}
		Columns col = null;
		col = getBussinessService(request)
		.getColumns(Integer.parseInt(cid));

		if ("".equals(title)) {
			// ²éÑ¯À¸±êÌâ
			// col = getBussinessService(request)
			// .getColumns(Integer.parseInt(cid));
			if (col == null) {
				return null;
			} else {
				title = col.getTitle();
			}
		}

		if (showNum.equals("1")) {
			if (col == null) {
				col = getBussinessService(request).getColumns(
						Integer.parseInt(cid));
			}
			if (col != null && col.getPricepackId() != null
					&& col.getPricepackId() > 0) {
				Long count = getResourceService(request)
						.getResourcePackReleationsCount(col.getPricepackId());
				if (count > 0)
					title += "(" + count + ")";
			}
		}

		Map velocityMap = new HashMap();
		velocityMap.put("title", title);

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
		sb.append(Integer.parseInt(cid) > 0 ? cid : col.getId());
		sb.append("&");

		if (Integer.parseInt(od) > 0 && Integer.parseInt(ods) > 0) {
			sb.append(ParameterConstants.ORDER);
			sb.append("=");
			sb.append(od);
			sb.append("&");
			sb.append(ParameterConstants.ORDERSUB);
			sb.append("=");
			sb.append(ods);
			sb.append("&");
		}

		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.trimURL(sb);
		velocityMap.put("url", sb.toString());

		String url = getBussinessService(request).getVariables("media_url")
				.getValue();
		if (col != null) {
			String icon_id = col.getIcon();
			if (StringUtils.isNotEmpty(icon_id)) {
				try {
					Material mater = getBussinessService(request).getMaterial(
							Integer.parseInt(icon_id));
					String imgName = mater.getFilename() + "."
							+ mater.getExtName();
					String imgUrl = "";
					imgUrl = url + imgName;
					velocityMap.put("img", imgUrl);
				} catch (Exception e) {

				}
			}
		}
		Map resultMap = new HashMap();
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
