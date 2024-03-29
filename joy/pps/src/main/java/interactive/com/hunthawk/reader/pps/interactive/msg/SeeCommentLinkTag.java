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
 * 查看评论标签
 * 标签名称：see_comment
 * 参数说明：
 *  isShowCount:是否显示评论数量 (1.是 -1.否)  默认不显示
 *  templateId:	评论列表模板ID 
 *  boardId：	板块ID
 *  msgType(*)：   留言类型 参考MsgRecord定义的 1.内容 2.栏目 3.产品 4.用户定制
 *  id：		    目标对象的ID号(如栏目ID、产品ID、自定义ID、资源ID)  当目标对象选择自定义时 此ID必填
 *  			 如果ID为空则取当前URL中的参数对应的ID值
 *  customName：自定义名称 msgType=4时需要填写该名称
 *  title:		链接文字 
 * @author liuxh
 *
 */
public class SeeCommentLinkTag extends BaseTag {

	private boolean isCustomId;
	public boolean isCustomId() {
		return isCustomId;
	}
	public void setCustomId(boolean isCustomId) {
		this.isCustomId = isCustomId;
	}
	private InteractiveService interactiveService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		boolean isShowCount=getParameter("isShowCount","-1").equals("1");
//		Map<String, String> res = new HashMap<String, String>();
		String title=getParameter("title","发言");
		int templateId=getIntParameter("templateId",-1);//留言列表页模板ID
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
			//velocityMap.put(ParameterConstants.CUSTOM_KEY_VALUE, customName);
			url.append("&");
			url.append(ParameterConstants.CUSTOM_KEY_VALUE);
			url.append("=");
			url.append(customName);
		}
//		velocityMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate));//板块
//		velocityMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType));//留言类型
//		velocityMap.put(ParameterConstants.COMMENT_TARGET_ID, id);//目标对象ID
		url.append("&");
		url.append(ParameterConstants.COMMENT_PLATE);
		url.append("=");
		url.append( String.valueOf(plate));
		url.append("&");
		url.append(ParameterConstants.COMMENT_TARGET);
		url.append("=");
		url.append(String.valueOf(msgType));
		url.append("&");
		url.append(ParameterConstants.COMMENT_TARGET_ID);
		url.append("=");
		url.append(id);
		
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
		if(isShowCount){
			if(title.indexOf("!count!")>0){
				title = title.replaceAll("!count!", String.valueOf(msgCount));
			}else{
				title = title+(isShowCount?"("+msgCount+"评)":"");
			}
		}
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
		velocityMap.put("msgCount", msgCount);
		
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
	/*	Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));*/
		return resultMap;
	}
	private InteractiveService getInteractiveService(HttpServletRequest request){
		if(interactiveService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService)wac.getBean("interactiveService");
		}
		return interactiveService;
	}
	private BussinessService bussinessService;
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
