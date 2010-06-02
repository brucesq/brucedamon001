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
 * ͶƱ �ʻ� ���� �ύ��ǩ 
 * ��ǩ���ƣ� vote_link
 * ����˵���� 
 * mix :������ϲ��� (ͼ��+���� �� ͼ�ꡢ ���� ) 
 * voteType:ͶƱ���� (3.��Ʒ��2.��Ŀ��1.���ݡ�4.�û�����) 
 * itemId:��ID (�磺�ʻ�������) 
 * voteId:ͶƱID 
 * templateId:��ʾ�ɹ�ģ��ID
 * customId:�û�����ID
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
		String mix=getParameter("mix","");//Ĭ������
		int voteId=getIntParameter("voteId",-1);//ͶƱID
		int itemId=getIntParameter("itemId",-1);//����ID
		int templateId=getIntParameter("templateId",-1);
		if(templateId<0){
			TagLogger.debug(tagName, "ģ��IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		if(voteId<0){
			TagLogger.debug(tagName, "ͶƱIDΪ��", request.getQueryString(), null);
			return new HashMap();
		}	
		if(itemId<0){
			TagLogger.debug(tagName, "ͶƱ����Ϊ��", request.getQueryString(), null);
			return new HashMap();
		}
		VoteItem voteItem=null;
		//���ͶƱ��
		 List<VoteItem> voteItems=getInteractiveService(request).getVoteItems(voteId);
		 for(int i=0;i<voteItems.size();i++){
			 if(voteItems.get(i).getId().equals(itemId)){
				 voteItem=(VoteItem)voteItems.get(i);
				 break;
			 }
		 }
		 String cId="";
		 //���ѡ��ͶƱ��
		 Long count=0L;
		 if(voteType==VoteSubItem.TYPE_PRODUCT){//��Ʒ
			 String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
			 if(productId!=null && StringUtils.isNotEmpty(productId)){
				 count=getInteractiveService(request).getVoteItemCounts(voteType, itemId, null, null, productId, null);	
			 }
			 cId=productId;
		 }else if(voteType==VoteSubItem.TYPE_COLUMN){//��Ŀ
			 String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
			 if(columnId!=null && StringUtils.isNotEmpty(columnId)){
				 count=getInteractiveService(request).getVoteItemCounts(voteType, itemId, null, Integer.parseInt(columnId), null, null);
			 }
			 cId=columnId;
		 }else if(voteType==VoteSubItem.TYPE_CONTENT){//����
			 String contentId=URLUtil.getResourceId(request);
			 if(contentId!=null && StringUtils.isNotEmpty(contentId)){
				 count=getInteractiveService(request).getVoteItemCounts(voteType, itemId, contentId, null, null, null);		
			 }
			 cId=contentId;
		 }else if(voteType==VoteSubItem.TYPE_CUSTOM){//����
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
						 //����ͼƬID�õ�ͼ��·��
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
			// ����ֻ���
			velocityMap.put("mobile", RequestUtil.getMobile());
			// ����ֻ�����
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
