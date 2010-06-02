/**
 * 
 */
package com.hunthawk.reader.pps.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author BruceSun
 *
 */
public class ColumnNav extends BaseTag {
	private  BussinessService bussinessService;
	
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		int columnId = ParamUtil.getIntParameter(request, ParameterConstants.COLUMN_ID, -1);
		if(columnId == -1)
			return new HashMap();
		Columns column = getBussinessService(request).getColumns(columnId);
		if(column == null)
			return new HashMap();
		List<Map> objs = new ArrayList<Map>();
		boolean flag = true;
		int i=0;
		while(flag){
			i++;
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
			sb.append(column.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.trimURL(sb);
			Map obj = new HashMap();
			obj.put("url", sb.toString());
			obj.put("column", column);
			objs.add(obj);
			if(column.getParent() != null){
				column = column.getParent();
				columnId = column.getId();
			}else{
				flag = false;
			}
			if(i>10){
				flag = false;
			}
		}
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		List<Map> newObjs = new ArrayList<Map>(); 
		for(int kk=0;kk<objs.size();kk++){
			newObjs.add(objs.get(objs.size()-1-kk));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objs", newObjs);
		String result = DBVmInstance.getInstance().parseVM(map, this, tagTem);

		Map resultMap = new HashMap();
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
}
