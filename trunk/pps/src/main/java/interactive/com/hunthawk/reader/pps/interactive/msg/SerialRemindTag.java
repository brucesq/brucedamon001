package com.hunthawk.reader.pps.interactive.msg;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.inter.Reservation;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
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
import com.hunthawk.tag.vm.VmInstance;
/**
 * �����������ӱ�ǩ
 * ��ǩ���ƣ�remind_link
 * ����˵����
 * 		//property:	1.��������2.ȡ������			ȥ���˲���
 * 		templateId:	ģ����ʾҳģ��ID		
 * 		//title:		�������� 				ȥ���˲���
 * @author liuxh
 *
 */
public class SerialRemindTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String feeId = request.getParameter("feeid");
		if(StringUtils.isEmpty(feeId)){
			int relId=ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_RELATION_ID,-1);
			int packId=ParamUtil.getIntParameter(request,ParameterConstants.FEE_BAG_ID,-1);
			if(relId > 0){
				ResourcePackReleation rpl=getResourceService(request).getResourcePackReleation(relId);
				feeId=rpl.getFeeId();
				if(feeId==null || StringUtils.isEmpty(feeId)){
					feeId=rpl.getPack().getFeeId();
				}
			}
		}
		
		String mobile=RequestUtil.getMobile();
		String contentid=URLUtil.getResourceId(request);
		if(contentid==null || StringUtils.isEmpty(contentid)){
			TagLogger.debug(tagName, "����IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		if(contentid.startsWith(String.valueOf(ResourceType.TYPE_BOOK)) || contentid.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){
			/**�ж���Դ�Ƿ���������Դ*/
			ResourceAll resource=getResourceService(request).getResource(contentid);
			if(resource!=null && resource.getIsFinished()==2){//������Դ
				//�ж��Ƿ��Ѿ���Ԥ���û�   �ǣ���ʾȡ������   ������ʾԤ������
				Reservation reservation=getCustomService(request).getReservationByContetnId(mobile, contentid);
				if(reservation==null){//δԤ���� ��ʾԤ������
					return showOrderTag(request,tagName,feeId);
				}else{//��ʾȡ������
					return showCacelTag(request,tagName,feeId);
				}
			}else{
				return new HashMap();
			}
		}else{
			return new HashMap();
		}
	}
	public Map showOrderTag(HttpServletRequest request, String tagName,String feeId){
		int templateId=getIntParameter("templateId",-1);
		if(templateId<0){
			TagLogger.debug(tagName, "ģ��IDΪ��", request.getQueryString(), null);
		}
//		
		StringBuilder sb=new StringBuilder();
		sb.append( request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.TEMPLATE_ID);
		sb.append("=");
		sb.append(templateId);
		sb.append("&");
		sb.append(URLUtil.removeParameter(request.getQueryString(),ParameterConstants.TEMPLATE_ID));
		if(feeId!=null && StringUtils.isNotEmpty(feeId)){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeId);
		}
		Map velocityMap = new HashMap();
		velocityMap.put("orderTitle", "����");
		velocityMap.put("orderUrl", sb.toString());
		velocityMap.put("isOrder", true);
		
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
		/*Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance().parseVM(velocityMap, this));*/
		
		return resultMap;
	}
	public Map showCacelTag(HttpServletRequest request, String tagName,String feeId){
		int templateId=getIntParameter("templateId",-1);
		if(templateId<0){
			TagLogger.debug("SerialRemindTag", "ģ��IDΪ��", request.getQueryString(), null);
		}
//		String title=getParameter("title","ȡ����������");
//		
		StringBuilder sb=new StringBuilder();
		sb.append( request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.TEMPLATE_ID);
		sb.append("=");
		sb.append(templateId);
		sb.append("&");
		sb.append(URLUtil.removeParameter(request.getQueryString(),ParameterConstants.TEMPLATE_ID));
		if(feeId!=null && StringUtils.isNotEmpty(feeId)){
			sb.append("&");
			sb.append(ParameterConstants.FEE_ID);
			sb.append("=");
			sb.append(feeId);
		}
		sb.append("&");
		sb.append(ParameterConstants.ORDER_FLAG);
		sb.append("=");
		sb.append("1");
		Map velocityMap = new HashMap();
		velocityMap.put("cancelTitle", "ȡ��");
		velocityMap.put("cancelUrl", sb.toString());
		velocityMap.put("isOrder", false);
		
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
		/*Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance().parseVM(velocityMap, this));*/
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
