package com.hunthawk.reader.pps.interactive.msg;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
/**
 * ���ع��ܱ�ǩ��ʾ���ض��ƹ��ܽ��
 * ��ǩ���ƣ�remind_view
 * ����˵����
 * 		succtitle:���Ƴɹ���ʾ����
 * 		canceltitle��ȡ���ɹ���ʾ����
 * 		title:	 	�����ı�
 * 		
 * @author liuxh
 *
 */
public class ViewSerialRemindTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title = getParameter("title", "");//������ʾ����
		String successTitle = getParameter("succtitle", "");//���Ƴɹ�����ʾ����
		String cancelTitle=getParameter("canceltitle","");//ȡ���ɹ�����ʾ���� 
		String feeId = request.getParameter(ParameterConstants.FEE_ID)==null?"":request.getParameter(ParameterConstants.FEE_ID);
		String mobile=RequestUtil.getMobile();
		String cid=URLUtil.getResourceId(request);
		int relId=ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID,-1);
		if(StringUtils.isEmpty(feeId)){
			int packId=ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_ID,-1);
			if(packId > 0){
				ResourcePack rp=getResourceService(request).getResourcePack(packId);
				feeId=rp.getFeeId();
			}
		}
		if(StringUtils.isEmpty(feeId)){
			System.out.println("�Ʒ�IDΪ��");
			TagLogger.debug(tagName, "�Ʒ�IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		if(StringUtils.isEmpty(feeId)){
			TagLogger.error(tagName, "��ȡ�����Ʒ�ID��Ϣ", request.getQueryString(), null);
			return new HashMap();
		}
		if(relId<0){
			TagLogger.debug(tagName, "���۰�����IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		if(StringUtils.isEmpty(cid)){
			TagLogger.debug(tagName, "����IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		
		//�ж��Ƕ��ƻ���ȡ������
		String flag=request.getParameter(ParameterConstants.ORDER_FLAG);
		int sign=1;//Ĭ�ϲ����ɹ�
		if(flag==null){//����
			try{
				getCustomService(request).addReservation(mobile, cid, relId);
			}catch(Exception ex){
				TagLogger.debug(tagName, "�����������Ѳ���ʧ��", request.getQueryString(), ex);
				successTitle="���Ѿ��������������ѹ���";
				sign=0;
			}
			cancelTitle="";
		}else{//ȡ��
			try{
				getCustomService(request).deleteReservation(mobile, cid);
			}catch(Exception ex){
				TagLogger.debug(tagName, "ȡ���������Ѳ���ʧ��", request.getQueryString(), ex);
				cancelTitle="�Բ�����δ�����������ѹ���!";
				sign=0;
			}
			successTitle="";
		}
		
		
		StringBuilder backUrl = new StringBuilder();
		backUrl.append( request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.ORDER_FLAG));
		
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("successTitle", successTitle);
		velocityMap.put("cancelTitle", cancelTitle);
		//velocityMap.put("isOrder", isOrder);
		/**
		 * modify by liuxh 09-11-12
		 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
		 */
		velocityMap.put("flag", sign);
		/**
		 * end
		 */
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
		/*String content  = VmInstance.getInstance().parseVM(velocityMap, this);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);*/
		return resultMap;
	}
	private CustomService getCustomService(HttpServletRequest request) {
		if (customService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService) wac.getBean("customService");
		}
		return customService;
	}
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
