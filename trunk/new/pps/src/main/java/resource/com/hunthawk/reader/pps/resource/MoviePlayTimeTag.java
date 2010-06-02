/**
 * 
 */
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

import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.pps.ParameterConstants;
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
public class MoviePlayTimeTag extends BaseTag {

	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;
	private ResourceService resourceService;
	private IphoneService iphoneService;
	private static final int DEFAULT_PAGE_SIZE = 5; //默认分页   	5条/页
	private static final int DEFAULT_MAX_COUNT=20;//默认列表最大范围 20条
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
		
		String resourceId=URLUtil.getResourceId(request);
		if(StringUtils.isEmpty(resourceId)){
			TagLogger.error(tagName, "往期回顾标签获取不到资源ID", request.getQueryString(), null);
			return new HashMap();
		}
//		System.out.println("00011111asd");
		int columnId=getIntParameter("columnId",-1);
		if(columnId<0){
			columnId=ParamUtil.getIntParameter(request, ParameterConstants.COLUMN_ID, -1);
			if(columnId<0){
				TagLogger.error(tagName, "栏目ID为空", request.getQueryString(), null);
				return new HashMap();
			}
		}
		HashMap resultMap = new HashMap();
		ResourceAll res=getResourceService(request).getResource(resourceId);
		int type = getIntParameter("type",1);
		if(type == 1){
			if(StringUtils.isNotEmpty(res.getDivisionContent())){
				resultMap.put(TagUtil.makeTag(tagName), res.getDivisionContent());
			}else{
				resultMap.put(TagUtil.makeTag(tagName), res.getName());
			}
			return resultMap;
		}
		
		
		if(StringUtils.isEmpty(res.getDivisionContent())){
			String s = "";
			if(res instanceof Video){
				s =  ((Video)res).getFormatPlayTime();
			}
			
			resultMap.put(TagUtil.makeTag(tagName), s);
			return resultMap;
		}
	
		
		List<ResourcePackReleation> rprs=null;
		try {
			 rprs=getResourceService(request).findDivisions(String.valueOf(columnId), res,false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TagLogger.error(tagName, "获取往期资源列表失败", request.getQueryString(), e);
			return new HashMap();
		}
		
		
		Integer playTime = 0;
		/** 存放列表资源 */
//		System.out.println("size:"+rprs.size());
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		for(Iterator it=rprs.iterator();it.hasNext();){
			ResourcePackReleation rpr=(ResourcePackReleation)it.next();
			ResourceAll resourceAll = getResourceService(request).getResource(rpr.getResourceId());
			
			if(resourceAll==null){
//				System.out.println("is null:"+rpr.getResourceId());
				continue;
			}
			if(resourceAll instanceof Video){
				playTime +=  ((Video)resourceAll).getPlayTime();
			}
//			System.out.println("PlayTime:"+playTime);
		}
		int hour = playTime/3600;
		int minute = (playTime%3600)/60;
		int second = ((playTime%3600)%60);
		String s = "";
		s=StringUtils.leftPad(""+hour,2,'0')+":";
		s = s+StringUtils.leftPad(""+minute,2,'0')+":"+StringUtils.leftPad(""+second,2,'0');
		resultMap.put(TagUtil.makeTag(tagName), s);
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
			feeLogicService = (FeeLogicService) wac
					.getBean("feeLogicService");
		}
		return feeLogicService;
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
	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac
					.getBean("iphoneService");
		}
		return iphoneService;
	}
	
}