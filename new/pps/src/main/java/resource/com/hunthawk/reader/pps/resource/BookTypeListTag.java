package com.hunthawk.reader.pps.resource;

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

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * 图书分类链接 
 * 标签名称：resource_type_link
 * 参数说明：
 * 			split:分割符号<br/>
 * 			title:文字
 * 			number: 在第几页之前显示 (包含当前页) 
 * @author liuxh
 *
 */
public class BookTypeListTag extends BaseTag {

	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	private String split;
	public String getSplit() {
		return split;
	}
	public void setSplit(String split) {
		this.split = split;
	}
	private int number;
	private int currentPage;
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	private ResourceService resourceService;
	private BussinessService bussinessService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		this.split=getParameter("split","");
		this.title=getParameter("title","");
		String n=getParameter("number","0");
		try{
			Integer.parseInt(n);
		}catch(Exception ex){
			TagLogger.debug(tagName,"number属性值不是有效果的属性值", request.getQueryString(), null);
			return new HashMap();
		}
		this.number=Integer.parseInt(n);
		this.currentPage=Integer.parseInt((request.getParameter(ParameterConstants.PAGE_NUMBER)==null?"1":request.getParameter(ParameterConstants.PAGE_NUMBER)));
		String resId=request.getParameter(ParameterConstants.RESOURCE_ID);
		List typeIds=getResourceService(request).getResourceTypeID(resId);
//		System.out.println("TYPS SIze:"+typeIds.size());
		String result="";
		Map resultMap = new HashMap();
		if(typeIds==null || typeIds.size()<1){
			result ="未知类型";
			resultMap.put(TagUtil.makeTag(tagName), result);
			TagLogger.debug("ResourceTypeListTag", "id为"+resId+"的资源,类型id不存在,查询失败", request.getQueryString(), null);
		}else{
			List<Object> lsRess = new ArrayList<Object>();
			Map<String, Object> map = new HashMap<String, Object>();
			for(Iterator it=typeIds.iterator();it.hasNext();){
				Map<String, String> obj = new HashMap<String, String>();
				int id=Integer.parseInt(it.next().toString());
				String title=getBookType(request,id);
				if(StringUtils.isEmpty(title)){
					title="";
				}
				int pageGroupId=Integer.parseInt(request.getParameter(ParameterConstants.PAGEGROUP_ID));
				Columns column=getBussinessService(request).getColumnsByResourceType(pageGroupId, id);
				if(column==null){
					TagLogger.debug(tagName, "获取栏目失败",request.getQueryString(), null);
					continue;
				}
				StringBuilder sb=new StringBuilder();
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
				URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
				sb.append(ParameterConstants.PAGE_NUMBER);
				sb.append("=");
				sb.append(1);
				String url=sb.toString();
				obj.put("url", url);
				obj.put("title", title);
				lsRess.add(obj);
			}
			map.put("objs", lsRess);
			int tagTemplateId = this.getIntParameter("tmd", 0);
			TagTemplate tagTem = null;
			if(tagTemplateId > 0){
				tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			}
			result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
			resultMap.put(TagUtil.makeTag(tagName), result);
			
		/*	result =  VmInstance.getInstance().parseVM(map, this);
			resultMap.put(TagUtil.makeTag(tagName), result);*/
		}
		return resultMap;
	//	String property=getParameter("property","3");
		//String type=getParameter("type","1");
//		if(property.equals("2")){
//			return userSpecifyType(request,tagName);
//		}else if(property.equals("3")){
//			return specialType(request,tagName);
//		}
//		return new HashMap();
	}
	private String getBookType(HttpServletRequest request,int type){
		try{
			return getResourceService(request).getResourceType(type).getName();
		}catch(Exception ex){
			TagLogger.debug("ResourceTypeListTag", "id为"+type+"的资源类型名称查询失败", request.getQueryString(),ex);
		}
		return "未知类型";
	}
//	private Map userSpecifyType(HttpServletRequest request, String tagName){
//		int typeId=getIntParameter("typeId",-1);
//		int columnId=getIntParameter("columnId",-1);
//		String title=getParameter("title","");
//		if(columnId<0){
//			TagLogger.debug("ResourceTypeListTag", "栏目ID为空", request.getQueryString(), null);
//			return null;
//		}
//		List<Object> lsRess = new ArrayList<Object>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		String result="";
//		Map resultMap = new HashMap();
//		if(typeId<0){
//			TagLogger.debug(tagName, "typeId不是有效的属性值", request.getQueryString(), null);
//			return new HashMap();
//		}else{
//			
//			Map<String, String> obj = new HashMap<String, String>();
//			if(StringUtils.isEmpty(title)){
//				title=getBookType(request,typeId);
//				if(StringUtils.isEmpty(title)){
//					title="未知类型";
//				}
//			}
//			//String feeId=
//			String url=getUrl(request,columnId,typeId).toString();
//			obj.put("url", url);
//			obj.put("title", title);
//			lsRess.add(obj);
//		}
//		map.put("objs", lsRess);
//		result =  VmInstance.getInstance().parseVM(map, this);
//		resultMap.put(TagUtil.makeTag(tagName), result);
//		return resultMap;
//	}
//	/**
//	 * 查询结果显示在栏目页
//	 * @param request
//	 * @param tagName
//	 * @return
//	 */
//	private Map specialType(HttpServletRequest request, String tagName){
//		this.currentPage=Integer.parseInt((request.getParameter(ParameterConstants.PAGE_NUMBER)==null?"1":request.getParameter(ParameterConstants.PAGE_NUMBER)));
//		String resId=request.getParameter(ParameterConstants.RESOURCE_ID);
//		int columnId=getIntParameter("columnId",-1);
//		if(columnId<0){
//			TagLogger.debug("ResourceTypeListTag", "栏目ID为空", request.getQueryString(), null);
//			return null;
//		}
//		List typeIds=getResourceService(request).getResourceTypeID(resId);
//		String result="";
//		Map resultMap = new HashMap();
//		if(typeIds==null || typeIds.size()<1){
//			result ="未知类型";
//			resultMap.put(TagUtil.makeTag(tagName), result);
//			TagLogger.debug("ResourceTypeListTag", "id为"+resId+"的资源,类型id不存在,查询失败", request.getQueryString(), null);
//		}else{
//			List<Object> lsRess = new ArrayList<Object>();
//			Map<String, Object> map = new HashMap<String, Object>();
//			for(Iterator it=typeIds.iterator();it.hasNext();){
//				Map<String, String> obj = new HashMap<String, String>();
//				int id=Integer.parseInt(it.next().toString());
//				String title=getBookType(request,id);
//				if(StringUtils.isEmpty(title)){
//					title="未知";
//				}
//				String url=getUrl(request,columnId,id).toString();
//				obj.put("url", url);
//				obj.put("title", title);
//				lsRess.add(obj);
//			}
//			map.put("objs", lsRess);
//			result =  VmInstance.getInstance().parseVM(map, this);
//			resultMap.put(TagUtil.makeTag(tagName), result);
//		}
//		return resultMap;
//	}
//	private StringBuilder getUrl(HttpServletRequest request,int columnId,int typeId){
//		StringBuilder url=new StringBuilder();
//		url.append(request.getContextPath());
//		url.append(ParameterConstants.PORTAL_PATH);
//		url.append("?");
//		url.append(ParameterConstants.PAGE);
//		url.append("=");
//		url.append(ParameterConstants.PAGE_COLUMN);
//		url.append("&");
//		URLUtil.append(url, ParameterConstants.PRODUCT_ID, request);
//		URLUtil.append(url, ParameterConstants.PAGEGROUP_ID, request);
//		URLUtil.append(url, ParameterConstants.AREA_ID, request);
//		url.append(ParameterConstants.COLUMN_ID);
//		url.append("=");
//		url.append(columnId);
//		url.append("&");
//		URLUtil.append(url, ParameterConstants.CHANNEL_ID, request);
//		URLUtil.append(url, ParameterConstants.UNICOM_PT, request);
//		url.append(ParameterConstants.PAGE_NUMBER);
//		url.append("=");
//		url.append("1");
//		url.append("&");
//		url.append(ParameterConstants.BOOK_TYPE);
//		url.append("=");
//		url.append(typeId);
//		return url;
//	}
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
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
	}
}
