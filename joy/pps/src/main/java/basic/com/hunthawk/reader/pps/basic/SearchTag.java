package com.hunthawk.reader.pps.basic;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.components.wml.Anchor;
import com.hunthawk.tag.components.wml.Go;
import com.hunthawk.tag.components.wml.IPostfieldModel;
import com.hunthawk.tag.components.wml.SimplePostfieldModel;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * 搜索标签
 * 标签名称:search
 * 参数说明：
 * 		property:	标签类型(1.输入框 2.文本链接)
 * 		isize：		对应文本框的size属性(property=1)
 * 		columnId:	栏目ID 用于列资源列表(property=2)
 * 		title:		链接文字 			(property=2)
 * 		restype:	参见ResourceType中定义(property=2)  用于确定查询目标(即操作哪张数据库表)
 * 					1图书，2报纸，3杂志，4漫画，5铃声，6视频
 * 		searchby:	搜索条件  (可扩展)
 * 					1.按书名2.按作者 3.快速搜索 4. 按关键字  目前只针对图书资源
 * 		defaultkey: 默认搜索值   add by liuxh 2009-9-2
 * 							
 * @author liuxh
 *
 */
public class SearchTag extends BaseTag {
	private BussinessService bussinessService;
	private TagTemplate tagTem = null;

	private boolean isQuickSearch;
	public boolean isQuickSearch() {
		return isQuickSearch;
	}
	public void setQuickSearch(boolean isQuickSearch) {
		this.isQuickSearch = isQuickSearch;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String property=getParameter("property","");
		if(StringUtils.isEmpty(property)){
			TagLogger.debug("SearchTag", "property属性值无效", request.getQueryString(), null);
		}else{
			if("input".equalsIgnoreCase(property)){
				return  inputTag(request,tagName);
			}else if("link".equalsIgnoreCase(property)){
				return linkTag(request,tagName);
			}
		}
		return null;
	}
	private Map inputTag(HttpServletRequest request, String tagName){
		int size=getIntParameter("isize",ParameterConstants.DEFAULT_INPUT_SIZE);
		//String type=getParameter("itype",ParameterConstants.DEFAULT_INPUT_TYPE);
		//String name=getParameter("iname",ParameterConstants.DEFAULT_INPUT_NAME);
		String defaultValue=getParameter("defaultkey","");
		StringBuilder sb=new StringBuilder();
		sb.append("<input type=\"text\" name=\"searchname\" size=\""+size+"\" value=\""+defaultValue+"\"/>");
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),sb.toString());
		return resultMap;		
	}
	private Map linkTag(HttpServletRequest request, String tagName){
		int tagTemplateId = this.getIntParameter("tmd", 0);
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		String title=getParameter("title","");
		int columnId=getIntParameter("columnId",-1);
		int restype=getIntParameter("restype",1);//默认是图书  
		int searchby=getIntParameter("searchby",-1);
		//String name=getParameter("iname",ParameterConstants.DEFAULT_INPUT_NAME);
		if(columnId<0){
			TagLogger.debug(tagName, "columnId属性值无效", request.getQueryString(), null);
			return null;
		}
		//拼URL
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
		sb.append(columnId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		sb.append("&");
		sb.append(ParameterConstants.RESOURCE_TYPE);
		sb.append("=");
		sb.append(restype);
		sb.append("&");
		sb.append(ParameterConstants.SEARCH_TYPE);
		sb.append("=");
		sb.append(searchby);
//		sb.append("&");
//		sb.append(ParameterConstants.IS_FIRST_SEARCH);

		Map velocityMap = new HashMap();
		velocityMap.put("title", StringUtils.trim(title));
		String wmlurl=sb.toString();
		if(searchby==3){
			sb.append("&");
			sb.append(ParameterConstants.QUICK_SEARCH_LINK_NAME);
			sb.append("=");
			try {
				sb.append(java.net.URLEncoder.encode(title.trim(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.isQuickSearch=true;
			velocityMap.put("qvalue", StringUtils.trim(title));
		}else{
			this.isQuickSearch=false;
		}
		velocityMap.put("url", sb.toString());
		//将参数循环放入Map
		List<Object> lsRess = new ArrayList<Object>();
		String[] pams=sb.toString().split("&");
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
		velocityMap.put("searchby", searchby);
		velocityMap.put("isQuickSearch", isQuickSearch);
		velocityMap.put("objs", lsRess);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
//		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
//				.parseVM(velocityMap, this));
		return resultMap;
//		StringBuilder builder=new StringBuilder();
//		
//		Go go = new Go();
//		go.setCharset("UTF-8");
//		go.setMethod("POST");
//		go.setUrl(sb.toString());
//		Map<String, String> postMap = new HashMap<String, String>();
//		postMap.put(ParameterConstants.SEARCH_PARAM_VALUE, "$searchname");
//		if(searchby==3){//按关键字
//			postMap.put(ParameterConstants.QUICK_SEARCH_LINK_NAME, title);
//		}
//		IPostfieldModel model = new SimplePostfieldModel(postMap);
//
//		go.setPostfieldModel(model);
//
//		Anchor anchor = new Anchor();
//		anchor.setGo(go);
//		anchor.setTitle(title);
//		anchor.setText(title);
//
//		builder.append(anchor.renderComponent());

//		res.put(ParameterConstants.PRE_TAG_SUFFIX + tagName
//				+ ParameterConstants.END_TAG_SUFFIX, builder.toString());
//		return res;
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
