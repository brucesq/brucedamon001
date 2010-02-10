package com.hunthawk.reader.pps.column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.PpsUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 栏目轮循标签 
 * 标签名称：column_roll_list 
 * 参数说明：columnids:参与轮循栏目的ID集合    栏目ID之间用“-”符号隔开
 * 			mix:链接组合策略 (栏目名称 、资源数)
 * @author liuxh
 * 
 */
public class ColumnRollTag extends BaseTag {

	private BussinessService bussinessService;
	private ResourceService resourceService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String[] strTest = new String[]{};
		String mix=getParameter("mix","");
		// 得到栏目ID的集合
		String columnids=getParameter("columnids","");
		int pageSize = getIntParameter("pageSize",5);
		if(columnids==null || StringUtils.isEmpty(columnids)){
			TagLogger.debug(tagName, "未选择参与轮循的栏目",request.getQueryString(), null);
			return new HashMap();
		}else{
			strTest=columnids.split("-");
		}
		List list = new ArrayList();
		for (int i = 0; i < strTest.length; i++) {
			list.add(i, strTest[i]);
		}
		Integer [] ids=new Integer[strTest.length];//随机排序后的id集合
		for (int i = 0; i < strTest.length; i++) {
			double random = Math.random();
			int temp = (int) (random * (strTest.length - i));
			ids[i]=Integer.parseInt(list.get(temp).toString());
			list.remove(temp);
		}
		
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		int i=0;
		for(Integer columnId:ids){
			if(++i>pageSize)
				break;
			Columns col=getBussinessService(request).getColumns(columnId);
			String title="";
			if(mix==null || StringUtils.isEmpty(mix)){
				title=col.getName();
			}else{
				StringBuffer temp=new StringBuffer();
				List<String> mixparams = PpsUtil.getParameters(mix);
				for(String str : mixparams){
					if(str.equals("name")){
						temp.append(col.getName());
					}
					if(str.equals("count")){//栏目关联批价包中的资源个数
						Long count=0L;
						if(col.getPricepackId()!=null){
							count = getResourceService(request).getResourcePackReleationsCount(col.getPricepackId());
						}
						temp.append("(").append(count).append(")");
					}
				}
				title=temp.toString();
			}
			
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
			sb.append(col.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.trimURL(sb);
			
			/** 保存单条记录 */
			Map<String, String> obj = new HashMap<String, String>();
			obj.put("url", sb.toString());
			obj.put("title",title);
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		result = VmInstance.getInstance().parseVM(map, this);
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
	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac
					.getBean("resourceService");
		}
		return resourceService;
	}
	public static void main(String[] args) {
		Object[] intTest =  { 2527, 2525, 2523, 2524, 2521 };
		List list = new ArrayList();
		for (int i = 0; i < intTest.length; i++) {
			list.add(i, intTest[i]);
		}
		Integer [] ids=new Integer[intTest.length];
		for (int i = 0; i < intTest.length; i++) {
			double random = Math.random();
			int temp = (int) (random * (intTest.length - i));
			ids[i]=(Integer)list.get(temp);
			list.remove(temp);
		}
		
		for(Integer id:ids){
			System.out.print(id+" ");
		}
	}

}
