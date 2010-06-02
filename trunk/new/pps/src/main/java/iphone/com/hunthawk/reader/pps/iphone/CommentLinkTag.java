package com.hunthawk.reader.pps.iphone;

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
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.components.wml.Anchor;
import com.hunthawk.tag.components.wml.Go;
import com.hunthawk.tag.components.wml.IPostfieldModel;
import com.hunthawk.tag.components.wml.SimplePostfieldModel;
import com.hunthawk.tag.vm.VmInstance;

/**
 * �������Ա�ǩ
 * ��ǩ���ƣ�icomment
 * ����˵����
 * 	templateId:	ģ��ID �������Գɹ���ʾҳ
 *  templateIdComment: �����б�ҳģ��ID
 *  boardId��	���ID
 *  msgType(*)��   �������� �ο�MsgRecord����� 1.���� 2.��Ŀ 3.��Ʒ 4.�û�����
 *  id��		    Ŀ������ID��(����ĿID����ƷID���Զ���ID����ԴID)  ��Ŀ�����ѡ���Զ���ʱ ��ID����
 *  			 ���IDΪ����ȡ��ǰURL�еĲ�����Ӧ��IDֵ
 *  customName���Զ������� msgType=4ʱ��Ҫ��д������
 * @author liuxh
 * 
 */
public class CommentLinkTag extends BaseTag {

	private boolean isCustomId;
	public boolean isCustomId() {
		return isCustomId;
	}
	public void setCustomId(boolean isCustomId) {
		this.isCustomId = isCustomId;
	}
	private InteractiveService interactiveService;
	private  BussinessService bussinessService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title=getParameter("title","����");
		int templateId=getIntParameter("templateId",-1);//���Գɹ���ʾҳģ��ID
		int msgType=getIntParameter("msgType",-1);
		int plate=getIntParameter("boardId",-1);
		String id="";
		if(msgType==MsgRecord.TYPE_CUSTOM){//�û�����
			id=getParameter("id","");
			if(StringUtils.isEmpty(id)){
				TagLogger.debug(tagName, "��������Ϊ�û�����ʱ��ID����ֵΪ��", request.getQueryString(), null);
				return new HashMap();
			}
		}else if(msgType==MsgRecord.TYPE_CONTENT){//����
			id=URLUtil.getResourceId(request);
		}else if(msgType==MsgRecord.TYPE_COLUMN){//��Ŀ
			id=request.getParameter(ParameterConstants.COLUMN_ID);
		}else if(msgType==MsgRecord.TYPE_PRODUCT){//��Ʒ
			id=request.getParameter(ParameterConstants.PRODUCT_ID);
		}
		if(StringUtils.isEmpty(id)){
			TagLogger.debug(tagName, "idΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		
		StringBuilder url=new StringBuilder();
		url.append(request.getContextPath());
		url.append(ParameterConstants.PORTAL_PATH);
		url.append("?");
		url.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.SEARCH_PARAM_VALUE,
				ParameterConstants.SEARCH_TYPE,ParameterConstants.QUICK_SEARCH_LINK_NAME,ParameterConstants.COMMENT_PARAM_VALUE,
				ParameterConstants.COMMENT_TARGET,ParameterConstants.COMMENT_PLATE,ParameterConstants.COMMENT_TARGET_ID,
				ParameterConstants.CUSTOM_KEY_VALUE,ParameterConstants.COMMON_PAGE));
		
		
		StringBuilder commentUrl=new StringBuilder(url);//����
		int templateId2=getIntParameter("templateIdComment",-1);
		commentUrl.append("&");
		commentUrl.append(ParameterConstants.TEMPLATE_ID);
		commentUrl.append("=");
		commentUrl.append(templateId2);
		if(templateId>0){
			url.append("&");
			url.append(ParameterConstants.TEMPLATE_ID);
			url.append("=");
			url.append(templateId);
		}
		
		
		Map velocityMap = new HashMap();
		
		if(msgType==MsgRecord.TYPE_CUSTOM){//ѡ���Զ���
			this.isCustomId=true;
			String customName=getParameter("customName","");
			velocityMap.put(ParameterConstants.CUSTOM_KEY_VALUE, customName);
		}
		velocityMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate));//���
		velocityMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType));//��������
		velocityMap.put(ParameterConstants.COMMENT_TARGET_ID, id);//Ŀ�����ID
		velocityMap.put("title", title);
		velocityMap.put("url", url.toString());
		velocityMap.put("commentUrl", commentUrl.toString());
		//������ѭ������Map
		List<Object> lsRess = new ArrayList<Object>();
		String[] pams=url.toString().split("&");
		for(int i=0;i<pams.length;i++){
			String str[]=pams[i].split("=");
			/** ���浥����¼ */
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
		//System.out.println("����URL-"+commentUrl.toString());
		velocityMap.put("objs", lsRess);
		velocityMap.put("isCustomId", isCustomId);
		
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
		/*Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));*/
		return resultMap;
//		int property=getIntParameter("property",-1);
//		if(property==1){//�����
//			return inputTag(request,tagName);
//		}else if(property==2){//���ܰ�ť
//			return buttonTag(request,tagName);
//		}
//		return null;
	}
//	/**
//	 * �ı������
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
		String title=getParameter("title","����");
		int templateId=getIntParameter("templateId",-1);//���Գɹ���ʾҳģ��ID
		int msgType=getIntParameter("msgType",-1);
		int plate=getIntParameter("boardId",-1);
		String id="";
		if(msgType==MsgRecord.TYPE_CUSTOM){//�û�����
			id=getParameter("id","");
			if(StringUtils.isEmpty(id)){
				TagLogger.debug(tagName, "��������Ϊ�û�����ʱ��ID����ֵΪ��", request.getQueryString(), null);
				return new HashMap();
			}
		}else if(msgType==MsgRecord.TYPE_CONTENT){//����
			id=URLUtil.getResourceId(request);
		}else if(msgType==MsgRecord.TYPE_COLUMN){//��Ŀ
			id=request.getParameter(ParameterConstants.COLUMN_ID);
		}else if(msgType==MsgRecord.TYPE_PRODUCT){//��Ʒ
			id=request.getParameter(ParameterConstants.PRODUCT_ID);
		}
		if(StringUtils.isEmpty(id)){
			TagLogger.debug(tagName, "idΪ��", request.getQueryString(), null);
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
//		postMap.put(ParameterConstants.COMMENT_PARAM_VALUE, "$msg");//��������
//		postMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate));//���
//		postMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType));//��������
//		postMap.put(ParameterConstants.COMMENT_TARGET_ID, id);//Ŀ�����ID
		if(msgType==MsgRecord.TYPE_CUSTOM){//ѡ���Զ���
			this.isCustomId=true;
			String customName=getParameter("customName","");
			velocityMap.put(ParameterConstants.CUSTOM_KEY_VALUE, customName);
		}
		velocityMap.put(ParameterConstants.COMMENT_PLATE, String.valueOf(plate));//���
		velocityMap.put(ParameterConstants.COMMENT_TARGET, String.valueOf(msgType));//��������
		velocityMap.put(ParameterConstants.COMMENT_TARGET_ID, id);//Ŀ�����ID
//		IPostfieldModel model = new SimplePostfieldModel(postMap);
//
//		go.setPostfieldModel(model);
//
//		Anchor anchor = new Anchor();
//		anchor.setGo(go);
		int msgCount=0;
		if(isShowCount){
			//��ѯ��������
			if(msgType==MsgRecord.TYPE_CUSTOM){//�û�����
				if(StringUtils.isEmpty(id)){
					TagLogger.debug(tagName, "��������Ϊ�û�����ʱ��ID����ֵΪ��", request.getQueryString(), null);
					return new HashMap();
				}
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, null, null, null, id);
			}else if(msgType==MsgRecord.TYPE_CONTENT){//����
				id=id==null?URLUtil.getResourceId(request):id;
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, id, null, null, null);
			}else if(msgType==MsgRecord.TYPE_COLUMN){//��Ŀ
				id=id==null?request.getParameter(ParameterConstants.COLUMN_ID):id;
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, null, Integer.parseInt(id), null, null);
			}else if(msgType==MsgRecord.TYPE_PRODUCT){//��Ʒ
				id=id==null?request.getParameter(ParameterConstants.PRODUCT_ID):id;
				msgCount= getInteractiveService(request).getMsgRecordCount(msgType, plate, null, null, id, null);
			}
		}
		velocityMap.put("title", title+(isShowCount?"("+msgCount+"��)":""));
		velocityMap.put("url", url.toString());
		//������ѭ������Map
		List<Object> lsRess = new ArrayList<Object>();
		String[] pams=url.toString().split("&");
		for(int i=0;i<pams.length;i++){
			String str[]=pams[i].split("=");
			/** ���浥����¼ */
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
		
		/*Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));*/
		return resultMap;
		
//		anchor.setTitle(title+(isShowCount?"("+msgCount+"��)":""));
//		anchor.setText(title+(isShowCount?"("+msgCount+"��)":""));
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
