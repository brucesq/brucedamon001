package com.hunthawk.reader.pps.interactive.msg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.PpsUtil;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 投票 鲜花 鸡蛋 提交标签 
 * 标签名称： vote_link
 * 参数说明： 
 * mix :链接组合策略 (图标+文字 、 图标、 文字 ) 
 * voteType:投票类型 (3.产品、2.栏目、1.内容、4.用户定制) 
 * itemId:项ID (如：鲜花、鸡蛋) 
 * voteId:投票ID 
 * templateId:提示成功模板ID
 * customId:用户调制ID
 * @author liuxh
 * 
 */
public class VoteTopBottomCountTag extends BaseTag {

	private InteractiveService interactiveService;
	private ResourceService resourceService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int  voteType=getIntParameter("voteType",1);
		String mix=getParameter("mix","");//默认文字
		int voteId=getIntParameter("voteId",-1);//投票ID
		int itemId=getIntParameter("itemId",-1);//子项ID
		int templateId=getIntParameter("templateId",-1);
		if(templateId<0){
			TagLogger.debug(tagName, "模版ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		if(voteId<0){
			TagLogger.debug(tagName, "投票ID为空", request.getQueryString(), null);
			return new HashMap();
		}	
		if(itemId<0){
			TagLogger.debug(tagName, "投票子项为空", request.getQueryString(), null);
			return new HashMap();
		}
		VoteItem voteItem=null;
		//获得投票项
		 List<VoteItem> voteItems=getInteractiveService(request).getVoteItems(voteId);
		 for(int i=0;i<voteItems.size();i++){
			 if(voteItems.get(i).getId().equals(itemId)){
				 voteItem=(VoteItem)voteItems.get(i);
				 break;
			 }
		 }
		 String cId="";
		 //获得选项投票数
		 Long count=0L;
		 if(voteType==VoteSubItem.TYPE_PRODUCT){//产品
			 String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
			 if(productId!=null && StringUtils.isNotEmpty(productId)){
				 count=getInteractiveService(request).getVoteItemCounts(voteType, itemId, null, null, productId, null);	
			 }
			 cId=productId;
		 }else if(voteType==VoteSubItem.TYPE_COLUMN){//栏目
			 String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
			 if(columnId!=null && StringUtils.isNotEmpty(columnId)){
				 count=getInteractiveService(request).getVoteItemCounts(voteType, itemId, null, Integer.parseInt(columnId), null, null);
			 }
			 cId=columnId;
		 }else if(voteType==VoteSubItem.TYPE_CONTENT){//内容
			 String contentId=URLUtil.getResourceId(request);
			 if(contentId!=null && StringUtils.isNotEmpty(contentId)){
				 count=getInteractiveService(request).getVoteItemCounts(voteType, itemId, contentId, null, null, null);		
			 }
			 cId=contentId;
		 }else if(voteType==VoteSubItem.TYPE_CUSTOM){//定制
			 String customId=getParameter("customId","");
			 if(StringUtils.isNotEmpty(customId)){
				 count=getInteractiveService(request).getVoteItemCounts(voteType, itemId, null, null, null, customId);	
			 }
			 cId=customId;
		 }
		 
		 if(voteItem!=null){
			 String title=voteItem.getName()+"("+count+")";
			 StringBuilder startext=new StringBuilder();
			 if(mix!=null && StringUtils.isNotEmpty(mix)){
				 List<String> mixparams = PpsUtil.getParameters(mix);
				 for(String str : mixparams){
					 if(str.equals("name")){
						 startext.append(voteItem.getName());
					 }
					 if(str.equals("imgId")){
						 //根据图片ID得到图的路径
						 int imgId=voteItem.getImgId();
						 String url = getResourceService(request).getMaterialImg(imgId);
						 startext.append("<img src=\"").append(url).append("\" alt=\"" + voteItem.getName() + "\"/>");
					 }
				 }
				 title=startext.toString()+"("+count+")";
			 }
			// title=startext.toString()+title;
			 StringBuilder sb=new StringBuilder();
			 sb.append(request.getContextPath());
			 sb.append(ParameterConstants.PORTAL_PATH);
			 sb.append("?");
			 sb.append(ParameterConstants.TEMPLATE_ID);
			 sb.append("=");
			 sb.append(templateId);
			 sb.append("&");
			 sb.append(request.getQueryString());
			 sb.append("&");
			 sb.append(ParameterConstants.VOTE_VOTE_TYPE);
			 sb.append("=");
			 sb.append(voteType);
			 sb.append("&");
			 sb.append(ParameterConstants.VOTE_ITEM_ID);
			 sb.append("=");
			 sb.append(itemId);
			 sb.append("&");
			 sb.append(ParameterConstants.VOTE_CONTENT_ID);
			 sb.append("=");
			 sb.append(cId);
			 
			Map velocityMap = new HashMap();
			velocityMap.put("title", title);
			velocityMap.put("url", URLUtil.trimUrl(sb).toString());
			velocityMap.put("strUtil", new StrUtil());
			// 添加手机号
			velocityMap.put("mobile", RequestUtil.getMobile());
			// 添加手机类型
			velocityMap.put("mobileType", RequestUtil.getMobileType());
			
			int tagTemplateId = this.getIntParameter("tmd", 0);
			TagTemplate tagTem = null;
			if(tagTemplateId > 0){
				tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			}

			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
			
		/*	Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName),VmInstance.getInstance()
					.parseVM(velocityMap, this));*/
			return resultMap;
		 }else{
			 return new HashMap();
		 }
	}

	private InteractiveService getInteractiveService(HttpServletRequest request) {
		if (interactiveService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService) wac
					.getBean("interactiveService");
		}
		return interactiveService;
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
