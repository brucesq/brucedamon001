/**
 * ��������ע������ǩ
 * ��ǩ����:ucregist_result
 */
package com.hunthawk.reader.pps.usercenter.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.UserCenterService;
import com.hunthawk.reader.pps.usercener.RegularUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author liuxh
 *
 */
public class UCRegistResultTag extends BaseTag {

	private UserCenterService userCenterService;
	private BussinessService bussinessService;
	/* (non-Javadoc)
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		/**�õ��ֻ��š��û�������֤�롢����*/
		String msg="";
		boolean err=false;
		String mobile=request.getParameter("mobile");
		String name=request.getParameter("name");
		String vcode=request.getParameter("vcode");
		String passwd=request.getParameter("passwd");
		System.out.println("regist result---->mobile="+mobile+" ; name="+name+" ; vcode="+vcode+" ; passwd="+passwd);
		try {
			RegularUtil.checkPhoneNum(mobile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			err=true;
			msg="�ֻ����벻�Ϸ�,ע��ʧ��!";
		}
		boolean effect=false;
		if(!err){
			/**���ö��Žӿ���֤������֤���Ƿ���Ч*/
			//@TODO
			System.out.println("���ö��Žӿ���֤������֤���Ƿ���Ч�Ĳ���...");
			effect=true;
			if(!effect)
				msg="��֤���������!";
		}
		
		StringBuilder sb=new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID));
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		if(!effect || err){
			Map velocityMap = new HashMap();
			velocityMap.put("success",false);
			velocityMap.put("url",sb.toString());
			velocityMap.put("msg", msg);
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
			return resultMap;
		}
		
		/**����Զ�̷�����ע�᷽������Ϣ���*/
		boolean regist=getUserCenterService(request).registUser(name, mobile, passwd);
		System.out.println("����Զ�̷�����ע�᷽������Ϣ���..."+regist);
		if(regist){
			msg="ע��ɹ�";
		}else{
			msg="ע��ʧ��!";
		}
		Map velocityMap = new HashMap();
		velocityMap.put("success",regist);
		velocityMap.put("url",sb.toString());
		velocityMap.put("msg", msg);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		return resultMap;
		
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
