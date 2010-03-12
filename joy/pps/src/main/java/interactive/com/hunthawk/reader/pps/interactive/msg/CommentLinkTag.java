package com.hunthawk.reader.pps.interactive.msg;

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
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * 评论链接标签 
 * 标签名称：comment_link
 * 参数说明：
 *  property:	   1.输入框 2.功能按钮			*去掉此参数
 *  isize：		对应文本框的size属性
 * 	templateId:	模板ID 发表留言成功提示页
 *  boardId：	板块ID
 *  msgType(*)：   留言类型 参考MsgRecord定义的 1.内容 2.栏目 3.产品 4.用户定制
 *  id：		    目标对象的ID号(如栏目ID、产品ID、自定义ID、资源ID)  当目标对象选择自定义时 此ID必填
 *  			 如果ID为空则取当前URL中的参数对应的ID值
 *  customName：自定义名称 msgType=4时需要填写该名称
 *  title:		链接文字 
 *  split:分隔符号  (<br/>)  add by liuxh 09-9-21  用于解决输入框与提交链接之间控制是否折行
 *  isShowCount:是否显示评论数量 (1.是 -1.否)  默认不显示     *去掉此参数
 * @author liuxh
 * 
 */
public class CommentLinkTag extends BaseTag {

	private String split;
	public String getSplit() {
		return split;
	}
	public void setSplit(String split) {
		this.split = split;
	}
	private boolean isCustomId;
	public boolean isCustomId() {
		return isCustomId;
	}
	public void setCustomId(boolean isCustomId) {
		this.isCustomId = isCustomId;
	}
	private InteractiveService interactiveService;
	private BussinessService bussinessService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		this.split=getParameter("split","");
//		Map<String, String> res = new HashMap<String, String>();
		String title=getParameter("title","发言");
		int templateId=getIntParameter("templateId",-1);//留言成功提示页模板ID
		int msgType=getIntParameter("msgType",-1);
		int plate=getIntParameter("boardId",-1);
		String id="";
		if(msgType==MsgRecord.TYPE_CUSTOM){//用户定制
			id=getParameter("id","");
			if(StringUtils.isEmpty(id)){
				TagLogger.debug(tagName, "留言类型为用户定制时，ID属性值为空", request.getQueryString(), null);
				return new HashMap();
			}
		}else if(msgType==MsgRecord.TYPE_CONTENT){//内容
			id=URLUtil.getResourceId(request);
		}else if(msgType==MsgRecord.TYPE_COLUMN){//栏目
			id=request.getParameter(ParameterConstants.COLUMN_ID);
		}else if(msgType==MsgRecord.TYPE_PRODUCT){//产品
			id=request.getParameter(ParameterConstants.PRODUCT_ID);
		}
		if(StringUtils.isEmpty(id)){
			TagLogger.debug(tagName, "id为空", request.getQueryString(), null);
			return new HashMap();
		}
		
		StringBuilder url=new StringBuilder();
		url.append(request.getContextPath());
		url.append(ParameterConstants.PORTAL_PATH);
		url.append("?");
		url.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.SEARCH_PARAM_VALUE,ParameterConstants.SEARCH_TYPE,ParameterConstants.QUICK_SEARCH_LINK_NAME));
		if(templateId>0){
			url.append("&");
			url.append(ParameterConstants.TEMPLATE_ID);
			url.append("=");
			url.append(templateId);
		}
		
		
		Map velocityMap = new HashMap();
		
		if(msgType==MsgRecord.TYPE_CUSTOM){//选择自定义
			this.isCustomId=true;
			String customName=getParameter("customName","");
			velocityMap.put(ParameterConstants.CUSTOM_KEY_VALUE, customName);
		}
		velocityMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate));//板块
		velocityMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType));//留言类型
		velocityMap.put(ParameterConstants.COMMENT_TARGET_ID, id);//目标对象ID
		velocityMap.put("title", title);
		velocityMap.put("url", url.toString());
		//将参数循环放入Map
		List<Object> lsRess = new ArrayList<Object>();
		String[] pams=url.toString().split("&");
		for(int i=0;i<pams.length;i++){
			String str[]=pams[i].split("=");
			/** 保存单条记录 */
			Map<String, String> obj = new HashMap<String, String>();
			if(!str[0].contains(ParameterConstants.SEARCH_TYPE)){
				obj.put("key", i==0?str[0].substring((str[0].indexOf("?")+1)):str[0]);
				if(str.length>1 &&  str[1]!=null && StringUtils.isNotEmpty(str[1]))
					obj.put("value", str[1]);
				else
					obj.put("value", "");
				lsRess.add(obj);
			}
		}
		velocityMap.put("objs", lsRess);
		velocityMap.put("isCustomId", isCustomId);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
	/*	Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));
		*/
		return resultMap;
//		int property=getIntParameter("property",-1);
//		if(property==1){//输入框
//			return inputTag(request,tagName);
//		}else if(property==2){//功能按钮
//			return buttonTag(request,tagName);
//		}
//		return null;
	}
//	/**
//	 * 文本输入框
//	 * @param request
//	 * @param tagName
//	 * @return
//	 */
//	private Map inputTag(HttpServletRequest request, String tagName){
//		int size=getIntParameter("isize",ParameterConstants.DEFAULT_INPUT_SIZE);
//		StringBuilder sb=new StringBuilder();
//		sb.append("<input type=\"text\" name=\"msg\" size=\""+size+"\"/>");
//		Map resultMap = new HashMap();
//		resultMap.put(TagUtil.makeTag(tagName),sb.toString());
//		return resultMap;
//	}
	private Map buttonTag(HttpServletRequest request, String tagName){
		
		boolean isShowCount=getParameter("isShowCount","-1").equals("1");
//		Map<String, String> res = new HashMap<String, String>();
		String title=getParameter("title","发言");
		int templateId=getIntParameter("templateId",-1);//留言成功提示页模板ID
		int msgType=getIntParameter("msgType",-1);
		int plate=getIntParameter("boardId",-1);
		String id="";
		if(msgType==MsgRecord.TYPE_CUSTOM){//用户定制
			id=getParameter("id","");
			if(StringUtils.isEmpty(id)){
				TagLogger.debug(tagName, "留言类型为用户定制时，ID属性值为空", request.getQueryString(), null);
				return new HashMap();
			}
		}else if(msgType==MsgRecord.TYPE_CONTENT){//内容
			id=URLUtil.getResourceId(request);
		}else if(msgType==MsgRecord.TYPE_COLUMN){//栏目
			id=request.getParameter(ParameterConstants.COLUMN_ID);
		}else if(msgType==MsgRecord.TYPE_PRODUCT){//产品
			id=request.getParameter(ParameterConstants.PRODUCT_ID);
		}
		if(StringUtils.isEmpty(id)){
			TagLogger.debug(tagName, "id为空", request.getQueryString(), null);
			return new HashMap();
		}
		
		StringBuilder url=new StringBuilder();
		url.append(request.getContextPath());
		url.append(ParameterConstants.PORTAL_PATH);
		url.append("?");
		url.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.SEARCH_PARAM_VALUE,ParameterConstants.SEARCH_TYPE,ParameterConstants.QUICK_SEARCH_LINK_NAME));
		if(templateId>0){
			url.append("&");
			url.append(ParameterConstants.TEMPLATE_ID);
			url.append("=");
			url.append(templateId);
		}
		
		
		Map velocityMap = new HashMap();
		
//		StringBuilder builder=new StringBuilder();
//		
//		Go go = new Go();
//		go.setCharset("UTF-8");
//		go.setMethod("POST");
//		go.setUrl(url.toString());
//		Map<String, String> postMap = new HashMap<String, String>();
//		postMap.put(ParameterConstants.COMMENT_PARAM_VALUE, "$msg");//留言内容
//		postMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate));//板块
//		postMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType));//留言类型
//		postMap.put(ParameterConstants.COMMENT_TARGET_ID, id);//目标对象ID
		if(msgType==MsgRecord.TYPE_CUSTOM){//选择自定义
			this.isCustomId=true;
			String customName=getParameter("customName","");
			velocityMap.put(ParameterConstants.CUSTOM_KEY_VALUE, customName);
		}
		velocityMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate));//板块
		velocityMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType));//留言类型
		velocityMap.put(ParameterConstants.COMMENT_TARGET_ID, id);//目标对象ID
//		IPostfieldModel model = new SimplePostfieldModel(postMap);
//
//		go.setPostfieldModel(model);
//
//		Anchor anchor = new Anchor();
//		anchor.setGo(go);
		int msgCount=0;
		if(isShowCount){
			//查询留言条数
			if(msgType==MsgRecord.TYPE_CUSTOM){//用户定制
				if(StringUtils.isEmpty(id)){
					TagLogger.debug(tagName, "留言类型为用户定制时，ID属性值为空", request.getQueryString(), null);
					return new HashMap();
				}
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, null, null, null, id);
			}else if(msgType==MsgRecord.TYPE_CONTENT){//内容
				id=id==null?URLUtil.getResourceId(request):id;
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, id, null, null, null);
			}else if(msgType==MsgRecord.TYPE_COLUMN){//栏目
				id=id==null?request.getParameter(ParameterConstants.COLUMN_ID):id;
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, null, Integer.parseInt(id), null, null);
			}else if(msgType==MsgRecord.TYPE_PRODUCT){//产品
				id=id==null?request.getParameter(ParameterConstants.PRODUCT_ID):id;
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, null, null, id, null);
			}
		}
		velocityMap.put("title", title+(isShowCount?"("+msgCount+"评)":""));
		velocityMap.put("url", url.toString());
		//将参数循环放入Map
		List<Object> lsRess = new ArrayList<Object>();
		String[] pams=url.toString().split("&");
		for(int i=0;i<pams.length;i++){
			String str[]=pams[i].split("=");
			/** 保存单条记录 */
			Map<String, String> obj = new HashMap<String, String>();
			if(!str[0].contains(ParameterConstants.SEARCH_TYPE)){
				obj.put("key", i==0?str[0].substring((str[0].indexOf("?")+1)):str[0]);
				if(str.length>1 &&  str[1]!=null && StringUtils.isNotEmpty(str[1]))
					obj.put("value", str[1]);
				else
					obj.put("value", "");
				lsRess.add(obj);
			}
		}
		velocityMap.put("objs", lsRess);
		velocityMap.put("isCustomId", isCustomId);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));
		return resultMap;
		
//		anchor.setTitle(title+(isShowCount?"("+msgCount+"评)":""));
//		anchor.setText(title+(isShowCount?"("+msgCount+"评)":""));
//
//		builder.append(anchor.renderComponent());
//
//		res.put(ParameterConstants.PRE_TAG_SUFFIX + tagName
//				+ ParameterConstants.END_TAG_SUFFIX, builder.toString());
//		return res;
	}
	private InteractiveService getInteractiveService(HttpServletRequest request){
		if(interactiveService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService)wac.getBean("interactiveService");
		}
		return interactiveService;
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
