package com.hunthawk.reader.pps.iphone.fee;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * iphone�ƷѶ������ܱ�ǩ
 * ��ǩ���ƣ�iview_fee
 * ����˵����
 * 		title:��������
 * @author liuxh
 *
 */
public class ViewFee extends BaseTag {

	private IphoneService iphoneService;
	private ResourceService resourceService;
	private CustomService customService;
	private boolean orderSuccess;
	public boolean isOrderSuccess() {
		return orderSuccess;
	}
	public void setOrderSuccess(boolean orderSuccess) {
		this.orderSuccess = orderSuccess;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title=getParameter("title","�����Ķ�");
		String mobile=RequestUtil.getMobile();
		//mobile="18601120940";
		int payType=ParamUtil.getIntParameter(request, "PayType", -1);
		if(payType<0){
			TagLogger.debug(tagName, "��������Ϊ��", request.getQueryString(), null);
			return new HashMap();
		}
		StringBuilder viewUrl=new StringBuilder();//��Ʒ��ҳURL
//		viewUrl.append(request.getContextPath());
//		viewUrl.append(ParameterConstants.PORTAL_PATH);
		viewUrl.append(request.getRequestURL());
		viewUrl.append("?");
		viewUrl.append(ParameterConstants.PAGE);
		viewUrl.append("=");
		viewUrl.append(ParameterConstants.PAGE_PRODUCT);
		viewUrl.append("&");
		URLUtil.append(viewUrl, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(viewUrl, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(viewUrl, ParameterConstants.AREA_ID, request);
		URLUtil.append(viewUrl, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(viewUrl, ParameterConstants.UNICOM_PT, request);
		
		StringBuilder builder=new StringBuilder();//�����ɹ���תҳ
		builder.append(request.getContextPath());
		builder.append(ParameterConstants.PORTAL_PATH);
		builder.append("?");
		builder.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.COMMON_PAGE,ParameterConstants.TEMPLATE_ID,"PayType"));
		
		Map velocityMap = new HashMap();
		String packId=request.getParameter(ParameterConstants.FEE_BAG_ID);
		String targetUrl="";
		if(payType==1){//1.Ƶ������
			Fee fee=getIphoneService(request).getChannel(mobile);//vip��� ������Ӧ�ļƷѴ���    eg: iphoneƵ������15
			String orderMes=getIphoneService(request).orderChannel(mobile, request.getParameter(ParameterConstants.PRODUCT_ID), Integer.parseInt(packId),fee, viewUrl.toString());
			if("".equals(orderMes)){//�����ɹ�
				this.orderSuccess=true;
				targetUrl=builder.toString();
			}else{//����ʧ�ܷ��ز�Ʒ��ҳ
				targetUrl=viewUrl.toString();
				this.orderSuccess=false;
				title="������ҳ";
				velocityMap.put("failMessage", orderMes);
			}
		}else if(payType==2){ //2.��Ŀ����
			StringBuilder columnPageUrl=new StringBuilder();//��Ŀ��ҳ
			columnPageUrl.append(request.getRequestURL());
			columnPageUrl.append("?");
			columnPageUrl.append(ParameterConstants.PAGE);
			columnPageUrl.append("=");
			columnPageUrl.append(ParameterConstants.PAGE_COLUMN);
			columnPageUrl.append("&");
			URLUtil.append(columnPageUrl, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(columnPageUrl, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(columnPageUrl, ParameterConstants.AREA_ID, request);
			URLUtil.append(columnPageUrl, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(columnPageUrl, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(columnPageUrl, ParameterConstants.UNICOM_PT, request);
			columnPageUrl.append(ParameterConstants.PAGE_NUMBER);
			columnPageUrl.append("=");
			columnPageUrl.append(1);
			columnPageUrl.append("&");
			URLUtil.append(columnPageUrl, ParameterConstants.FEE_ID, request);
			
			Fee fee=getIphoneService(request).getColumnFee(mobile, Integer.parseInt(packId));
			String orderMes=getIphoneService(request).orderColumn(mobile,  request.getParameter(ParameterConstants.PRODUCT_ID), Integer.parseInt(packId), fee,  Integer.parseInt(request.getParameter(ParameterConstants.COLUMN_ID)), columnPageUrl.toString());
			if("".equals(orderMes)){//�����ɹ�
				this.orderSuccess=true;
				targetUrl=builder.toString();
			}else{//����ʧ�ܷ��ز�Ʒ��ҳ
				targetUrl=viewUrl.toString();
				this.orderSuccess=false;
				title="������ҳ";
				velocityMap.put("failMessage", orderMes);
			}
		}else if(payType==3){//3.�����ƴ�
			StringBuilder resourcePageUrl=new StringBuilder();//��Դ����ҳ
			resourcePageUrl.append(request.getRequestURL());
			resourcePageUrl.append("?");
			resourcePageUrl.append(ParameterConstants.PAGE);
			resourcePageUrl.append("=");
			resourcePageUrl.append(ParameterConstants.PAGE_RESOURCE);
			resourcePageUrl.append("&");
			URLUtil.append(resourcePageUrl, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(resourcePageUrl, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(resourcePageUrl, ParameterConstants.AREA_ID, request);
			URLUtil.append(resourcePageUrl, ParameterConstants.COLUMN_ID, request);
			URLUtil.append(resourcePageUrl, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(resourcePageUrl, ParameterConstants.FEE_BAG_RELATION_ID, request);
			resourcePageUrl.append(ParameterConstants.RESOURCE_ID);
			resourcePageUrl.append("=");
			resourcePageUrl.append(URLUtil.getResourceId(request));
			resourcePageUrl.append("&");
			//URLUtil.append(resourcePageUrl, ParameterConstants.RESOURCE_ID, request);
			URLUtil.append(resourcePageUrl, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(resourcePageUrl, ParameterConstants.UNICOM_PT, request);
			resourcePageUrl.append(ParameterConstants.PAGE_NUMBER);
			resourcePageUrl.append("=");
			resourcePageUrl.append(1);
			resourcePageUrl.append("&");
			URLUtil.append(resourcePageUrl, ParameterConstants.FEE_ID, request);
			
			Fee fee=getIphoneService(request).getResourceFee(mobile, getCustomService(request).getFee(request.getParameter(ParameterConstants.FEE_ID)));
			String orderMes=getIphoneService(request).orderResource(mobile,  request.getParameter(ParameterConstants.PRODUCT_ID), Integer.parseInt(packId), fee,  URLUtil.getResourceId(request), Integer.parseInt( request.getParameter(ParameterConstants.FEE_BAG_RELATION_ID)), resourcePageUrl.toString());
			if("".equals(orderMes)){//�����ɹ�
				this.orderSuccess=true;
				targetUrl=builder.toString();
			}else{//����ʧ�� ���ز�Ʒ��ҳ
				targetUrl=viewUrl.toString();
				this.orderSuccess=false;
				title="������ҳ";
				velocityMap.put("failMessage", orderMes);
			}
		}
		velocityMap.put("mobile", mobile);
		velocityMap.put("title", title);
		velocityMap.put("url", targetUrl);
		if(payType==3){//����  ��Դ����+��Դ����
			String startstr="";
			if(URLUtil.getResourceId(request).startsWith(ResourceAll.RESOURCE_TYPE_BOOK.toString())){
				startstr="ͼ��";
			}else if(URLUtil.getResourceId(request).startsWith(ResourceAll.RESOURCE_TYPE_COMICS.toString())){
				startstr="����";
			}else if(URLUtil.getResourceId(request).startsWith(ResourceAll.RESOURCE_TYPE_MAGAZINE.toString())){
				startstr="��־";
			}else if(URLUtil.getResourceId(request).startsWith(ResourceAll.RESOURCE_TYPE_NEWSPAPER.toString())){
				startstr="��ֽ";
			}
			velocityMap.put("content", startstr+"-"+getResourceService(request).getResource(URLUtil.getResourceId(request)).getName());
		}else{
			velocityMap.put("content", getResourceService(request).getResource(URLUtil.getResourceId(request)).getName());
		}
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance().parseVM(velocityMap, this));
		return resultMap;
	}
	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac.getBean("iphoneService");
		}
		return iphoneService;
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
}
