/**
 * ���������û�ע���ǩ
 * ��ǩ���ƣ�ucregist_form
 * ����˵����
 * 		templateId:�����ʾҳģ��ID
 */
package com.hunthawk.reader.pps.usercenter.user;

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
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.UserCenterService;
import com.hunthawk.reader.pps.usercener.RegularUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author liuxh
 *
 */
public class UCRegistTag extends BaseTag {

	private UserCenterService userCenterService;
	private BussinessService bussinessService;
	/* (non-Javadoc)
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		StringBuilder sb=new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(request.getQueryString());
		System.out.println("mobile="+request.getParameter("mobile")+" ; name="+request.getParameter("name"));
		if(request.getParameter("mobile")==null && request.getParameter("name")==null){//ע���һ��
			Map velocityMap = new HashMap();
			velocityMap.put("step",1);
			velocityMap.put("url",sb.toString());
			velocityMap.put("msg"," ");
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
			return resultMap;
		}else{//ע��ڶ���
			/**�õ�����*/
			String mobile=request.getParameter("mobile");
			String loginname=request.getParameter("name");
			String errorMsg="";//������ʾ��Ϣ
			boolean err=false;//�����ʶ
			/**�ֻ�����֤*/
			try {
				RegularUtil.checkPhoneNum(mobile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				err=true;
				errorMsg="�ֻ����벻�Ϸ�";
				TagLogger.debug(tagName, "�ֻ����벻�Ϸ�",request.getQueryString(), null);
			}
			boolean exists=true;
			if(!err){
				/**��½��Ψһ����֤*/
				exists=getUserCenterService(request).name_exists(StringUtils.trim(loginname));
				System.out.println("���÷���������У���û���Ψһ��..."+exists);
				if(exists)
					errorMsg="�û����Ѿ���ʹ��!";
			}
			if(exists || err){
				Map velocityMap = new HashMap();
				velocityMap.put("step",1);
				velocityMap.put("url",sb.toString());
				velocityMap.put("msg", errorMsg);
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
				return resultMap;
			}else{
				/**�·�����֪ͨ*/
				//@TODO 
				System.out.println("���ö��Žӿ�,�·�������֤������֪ͨ...");
				/**����һ�������޷���֪�û���õ��Ķ�����֤�룬�����ǩ�з����û������������֤�������֤*/
				int templateId=getIntParameter("templateId",-1);
				if(templateId<0){
					return new HashMap();
				}
				Map<String,String> values=new HashMap<String,String>();
				values.put(ParameterConstants.TEMPLATE_ID, String.valueOf(templateId));
				String url=URLUtil.getUrl(values, request);
				Map velocityMap = new HashMap();
				velocityMap.put("step",2);
				velocityMap.put("msg"," ");
				velocityMap.put("url",url);
				velocityMap.put("mobile",mobile);
				velocityMap.put("name",loginname);
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
				return resultMap;
			}
			
		}
		
		
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
	private UserCenterService getUserCenterService(HttpServletRequest request) {
		if (userCenterService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			userCenterService = (UserCenterService) wac
					.getBean("userCenterService");
		}
		return userCenterService;
	}
}
