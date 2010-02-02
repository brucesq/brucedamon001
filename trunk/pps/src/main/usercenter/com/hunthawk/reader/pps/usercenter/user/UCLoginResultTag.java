/**
 * ���������û���½�����ǩ
 * ��ǩ���ƣ�uclogin_result
 * ����˵����
 * 
 */
package com.hunthawk.reader.pps.usercenter.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.UserCenter.domain.LoginPO;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.UserCenterService;
import com.hunthawk.reader.pps.usercener.Des;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Redirect;

/**
 * @author liuxh
 *
 */
public class UCLoginResultTag extends BaseTag {

	private BussinessService bussinessService;
	private UserCenterService userCenterService;
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
		
		String password=request.getParameter("password");
		String loginname=request.getParameter("name");
//		System.out.println("login  --->password="+password+" ; name="+loginname);
		/**��½ʧ�ܷ���URL*/
		StringBuilder backUrl=new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID));
		if(StringUtils.isEmpty(password) || StringUtils.isEmpty(loginname) ){
			com.hunthawk.tag.process.Refresh.pageRefresh(3, backUrl.toString());// 3����
			Map velocityMap = new HashMap();
			/**
			 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
			 */
			velocityMap.put("flag",0);
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
					.parseVM(velocityMap, this, tagTem));
			return resultMap;
		}else{
			/**����Զ�̷����ж��Ƿ��ǺϷ��û�*/
			LoginPO login=getUserCenterService(request).login(loginname, password, 1);
			if(login==null){//�û����������
				com.hunthawk.tag.process.Refresh.pageRefresh(3, backUrl.toString());// 3����
				Map velocityMap = new HashMap();
				/**
				 * ��Ӳ�����ʶ  0.ʧ�� 1.�ɹ�
				 */
				velocityMap.put("flag",0);
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
						.parseVM(velocityMap, this, tagTem));
				return resultMap;
			}else{
				/**��½�ɹ���ת��URL��ַӦΪ����������ҳ��ַ��ͨ����ȡϵͳ�������*/
				StringBuilder goUrl=new StringBuilder();
				goUrl.append(getBussinessService(request).getVariables("UC_HOME_URL").getValue());
				goUrl.append("&mobile=");
				/**�ֻ��ż���*/
				goUrl.append(new Des().encrypt(login.mobile));
				Redirect.sendRedirect(goUrl.toString()) ;//��ˢ����ת����½ҳ��
				return new HashMap();
			}
		}
		
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
