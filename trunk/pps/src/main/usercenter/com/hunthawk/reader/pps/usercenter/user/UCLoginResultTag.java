/**
 * 个人中心用户登陆结果标签
 * 标签名称：uclogin_result
 * 参数说明：
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
		/**登陆失败返回URL*/
		StringBuilder backUrl=new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID));
		if(StringUtils.isEmpty(password) || StringUtils.isEmpty(loginname) ){
			com.hunthawk.tag.process.Refresh.pageRefresh(3, backUrl.toString());// 3秒钟
			Map velocityMap = new HashMap();
			/**
			 * 添加操作标识  0.失败 1.成功
			 */
			velocityMap.put("flag",0);
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
					.parseVM(velocityMap, this, tagTem));
			return resultMap;
		}else{
			/**调用远程方法判断是否是合法用户*/
			LoginPO login=getUserCenterService(request).login(loginname, password, 1);
			if(login==null){//用户名密码错误
				com.hunthawk.tag.process.Refresh.pageRefresh(3, backUrl.toString());// 3秒钟
				Map velocityMap = new HashMap();
				/**
				 * 添加操作标识  0.失败 1.成功
				 */
				velocityMap.put("flag",0);
				Map resultMap = new HashMap();
				resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
						.parseVM(velocityMap, this, tagTem));
				return resultMap;
			}else{
				/**登陆成功跳转的URL地址应为个人中心首页地址，通过读取系统变量获得*/
				StringBuilder goUrl=new StringBuilder();
				goUrl.append(getBussinessService(request).getVariables("UC_HOME_URL").getValue());
				goUrl.append("&mobile=");
				/**手机号加密*/
				goUrl.append(new Des().encrypt(login.mobile));
				Redirect.sendRedirect(goUrl.toString()) ;//无刷新跳转到登陆页面
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
