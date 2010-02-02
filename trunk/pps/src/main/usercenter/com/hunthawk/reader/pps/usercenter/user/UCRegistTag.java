/**
 * 个人中心用户注册标签
 * 标签名称：ucregist_form
 * 参数说明：
 * 		templateId:结果提示页模版ID
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
		if(request.getParameter("mobile")==null && request.getParameter("name")==null){//注册第一步
			Map velocityMap = new HashMap();
			velocityMap.put("step",1);
			velocityMap.put("url",sb.toString());
			velocityMap.put("msg"," ");
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
			return resultMap;
		}else{//注册第二步
			/**得到参数*/
			String mobile=request.getParameter("mobile");
			String loginname=request.getParameter("name");
			String errorMsg="";//错误提示信息
			boolean err=false;//错误标识
			/**手机号验证*/
			try {
				RegularUtil.checkPhoneNum(mobile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				err=true;
				errorMsg="手机号码不合法";
				TagLogger.debug(tagName, "手机号码不合法",request.getQueryString(), null);
			}
			boolean exists=true;
			if(!err){
				/**登陆名唯一性验证*/
				exists=getUserCenterService(request).name_exists(StringUtils.trim(loginname));
				System.out.println("调用服务器方法校验用户名唯一性..."+exists);
				if(exists)
					errorMsg="用户名已经被使用!";
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
				/**下发短信通知*/
				//@TODO 
				System.out.println("调用短信接口,下发短信验证码请求通知...");
				/**到这一步我们无法获知用户获得到的短信验证码，结果标签中发送用户表单中输入的验证码进行验证*/
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
