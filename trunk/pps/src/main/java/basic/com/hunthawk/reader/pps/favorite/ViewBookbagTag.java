package com.hunthawk.reader.pps.favorite;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * ������ܱ�ǩ
 * ��ǩ���ƣ�bookbag_view
 * ����˵����
 * 		title:������������
 * 		addsuccess:��� �ղسɹ���ʾ����
 * 		delsuccess:ɾ�� �ղسɹ���ʾ����
 * @author liuxh
 *
 */
public class ViewBookbagTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		//String flag=request.getParameter(ParameterConstants.BAG_FUNCTION_FLAG);
		String flag=request.getParameter(ParameterConstants.COMMON_PAGE);
		if(StringUtils.isEmpty(flag)){
			TagLogger.debug(tagName, "fn������ȡʧ��", request.getQueryString(), null);
		}else{
			if(flag.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_BOOKBAG_ADD)){//��Ӳ���
				return addBookBagFunction(request,tagName);
			}else if(flag.equalsIgnoreCase(ParameterConstants.COMMON_PAGE_BOOKBAG_DEL)){//ɾ������
				return deleteBookBagFunction(request,tagName);
			}
		}
		return new HashMap();
	}

	/**
	 * �����ӷ���
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map addBookBagFunction(HttpServletRequest request, String tagName){
		boolean isAdd=true;
		String title=getParameter("title","����");
		String rid=URLUtil.getResourceId(request);
		if(rid==null || "".equals(rid)){
			TagLogger.debug(tagName, "��ԴID��ȡʧ��", request.getQueryString(),null);
			return new HashMap();
		}
		boolean ERROR_FLAG=false;
		BookBag bag=new BookBag();
		bag.setContentId(rid);
		bag.setCreateTime(new Date());
		bag.setFeeId(request.getParameter(ParameterConstants.FEE_ID));
		bag.setMobile(RequestUtil.getMobile());
		bag.setPid(request.getParameter(ParameterConstants.PRODUCT_ID));
		bag.setChannelId(request.getParameter(ParameterConstants.CHANNEL_ID));
		int flag=1;//Ĭ�ϳɹ�
		try {
			getCustomService(request).addBookbag(bag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			flag=0;
			TagLogger.debug(tagName, "������ʧ��", request.getQueryString(), e);
			ERROR_FLAG=true;
		}
		String addsuccess_msg=getParameter("addsuccess","�Ѿ���������ӵ��������");
		if(ERROR_FLAG){
			addsuccess_msg="����������Ѿ���ӹ��Ȿ��";
		}
		
		StringBuilder backUrl = new StringBuilder();
		backUrl.append( request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMON_PAGE));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("addsuccess", addsuccess_msg);
		/**
		 * modify by liuxh 09-11-12
		 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
		 */
		velocityMap.put("flag", flag);
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
		
	/*	String content  = VmInstance.getInstance().parseVM(velocityMap, this);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);*/
		
		return resultMap;
	}
	/**
	 * ���ɾ������
	 * @param request
	 * @param tagName
	 * @return
	 */
	private Map deleteBookBagFunction(HttpServletRequest request, String tagName){
		boolean isAdd=false;
		String title=getParameter("title","����");
		boolean ERROR_FLAG=false;
		String cid=request.getParameter(ParameterConstants.RESOURCE_ID);
	
		if("".equals(cid)){
			return new HashMap();
		}
		int flag=1;//Ĭ�ϳɹ�
		try {
			getCustomService(request).deleteUserBookbag(RequestUtil.getMobile(),cid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			flag=0;
			TagLogger.debug(tagName, "���ɾ��ʧ��", request.getQueryString(), e);
			ERROR_FLAG=true;
		}
		String delsuccess_msg=getParameter("delsuccess","�Ѿ�����������������ɾ��");
		if(ERROR_FLAG){
			delsuccess_msg="���ɾ��ʧ��";
		}
		StringBuilder backUrl = new StringBuilder();
		backUrl.append( request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,ParameterConstants.COMMON_PAGE));
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", backUrl);
		velocityMap.put("isAdd", isAdd);
		velocityMap.put("delsuccess", delsuccess_msg);
		// ����ֻ���
		velocityMap.put("mobile", RequestUtil.getMobile());
		// ����ֻ�����
		velocityMap.put("mobileType", RequestUtil.getMobileType());
		velocityMap.put("strUtil", new StrUtil());
		/**
		 * modify by liuxh 09-11-12
		 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
		 */
		velocityMap.put("flag", flag);
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
	private CustomService getCustomService(HttpServletRequest request){
		if(customService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			customService = (CustomService)wac.getBean("customService");
		}
		return customService;
	}
	private ResourceService getResourceService(HttpServletRequest request){
		if(resourceService == null){
			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService)wac.getBean("resourceService");
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
