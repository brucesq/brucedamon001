package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

public class InitialAuthorLinkTag extends BaseTag {

	private BussinessService bussinessService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		Map resultMap = new HashMap();
		String result = "";
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		List resultList = createInitals(request, tagName);
		Map<String, Object> map = new HashMap<String, Object>();
		if (resultList != null) {
			map.put("objs", resultList);
		} else {
			return new HashMap();
		}
		result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);
		return resultMap;
	}

	private List createInitals(HttpServletRequest request, String tagName) {
		List resultList = null;
		String columnId = getParameter("columnId", "");
		ResourcePack rp = null;
		if (StringUtils.isNotEmpty(columnId)) {
			Columns column = getBussinessService(request).getColumns(
					Integer.parseInt(columnId));
			if (column == null) {

				TagLogger.debug(tagName, "获取栏目失败,id为" + columnId + "的栏目不存在！",
						request.getQueryString(), null);
				return resultList;
			}
		} else {
			TagLogger.debug(tagName, "栏目Id为空", request.getQueryString(), null);
			return resultList;
		}
		resultList = new ArrayList();
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < str.length(); i++) {
			Map<String, Object> map = new OrderedMap<String, Object>();
			String temp = str.charAt(i) + "";
			Map<String, String> values = new OrderedMap<String, String>();
			values.put(ParameterConstants.COLUMN_ID, columnId);
			values.put(ParameterConstants.PAGE, "c");
			values.put("inital", temp);

			String url = URLUtil.getUrl(values, request);
			map.put("title", temp);
			map.put("url", url);
			resultList.add(map);
		}
		return resultList;
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
