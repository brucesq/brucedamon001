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

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StatisticsLog;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
/**
 * 漫画自动播放标签
 * 标签名称：auto_play
 * 		参数说明：tmd  模版标签ID
 * @author liuxh
 *
 */
public class AutoPlayTag extends BaseTag {

	private TagTemplate tagTem ;
	private BussinessService bussinessService;
	private boolean showInput;

	public boolean isShowInput() {
		return showInput;
	}

	public void setShowInput(boolean showInput) {
		this.showInput = showInput;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		
		String resourceId=URLUtil.getResourceId(request);
		/**非漫画资源不显示此标签*/
		if(StringUtils.isEmpty(resourceId) || !resourceId.substring(0,1).startsWith(String.valueOf(ResourceType.TYPE_COMICS))){
			return new HashMap();
		}
		int tagTemplateId = this.getIntParameter("tmd", 0);
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		String timer=StringUtils.trimToNull(request.getParameter(ParameterConstants.TIMER));
		if(timer==null){
			TagLogger.debug(tagName, "timer输入框的值为空", request.getQueryString(), null);
		}
		
		String title ="";
		boolean isAuto=ParamUtil.getIntParameter(request, ParameterConstants.AUTO_PLAY, -1)>0;
		this.showInput=!isAuto;
		Map<String,String> values=new HashMap<String,String>();
		values.put(ParameterConstants.PAGE_NUMBER, "1");
		values.put(ParameterConstants.AUTO_PLAY, "1");
		if(timer!=null)
			values.put(ParameterConstants.TIMER, timer);
		String url=URLUtil.getUrl(values, request);
		if(isAuto){
			title="停止播放";
			url=URLUtil.removeParameter(url, ParameterConstants.AUTO_PLAY,ParameterConstants.TIMER);
		}else{
			title="播放";
			url=URLUtil.getUrl(values, request);
		}
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", url);
		velocityMap.put("showInput", showInput);
		//将参数循环放入Map
		List<Object> lsRess = new ArrayList<Object>();
		String[] pams=url.split("&");
		for(int i=0;i<pams.length;i++){
			String str[]=pams[i].split("=");
			/** 保存单条记录 */
			Map<String, String> obj = new HashMap<String, String>();
			if(!str[0].contains(ParameterConstants.SEARCH_TYPE)){
				obj.put("key", i==0?str[0].substring((str[0].indexOf("?")+1)):str[0]);
				if(str.length>1 &&  str[1]!=null && StringUtils.isNotEmpty(str[1])){
					obj.put("value", str[1]);
				}else
					obj.put("value", "");
				
			}else{
				if(Integer.parseInt(str[1])>0){
					obj.put("key", i==0?str[0].substring((str[0].indexOf("?")+1)):str[0]);
					obj.put("value", str[1]);
				}
			}
			lsRess.add(obj);
		}
		velocityMap.put("objs", lsRess);
		Map resultMap = new HashMap();
		String result = DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem);
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
