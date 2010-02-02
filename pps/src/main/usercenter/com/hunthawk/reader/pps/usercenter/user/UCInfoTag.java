/**
 * 个人中心用户信息显示标签
 * 标签名称：
 * 		uc_info
 * 参数说明：
 * templateId:资料修改页模版ID
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

import com.hunthawk.UserCenter.domain.InformationPO;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.UserCenterService;
import com.hunthawk.reader.pps.usercener.Constants;
import com.hunthawk.reader.pps.usercener.Des;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author liuxh
 *
 */
public class UCInfoTag extends BaseTag {

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
		
		int templateId=getIntParameter("templateId",-1);
		
		if(templateId<0){
			TagLogger.debug(tagName, "模版ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		/**获取手机号进行解密*/
		if(request.getParameter("mobile")==null){
			TagLogger.debug(tagName, "获取不到手机号码", request.getQueryString(), null);
			return new HashMap();
		}
		String mobile =new Des().decrypt(request.getParameter("mobile"));
		System.out.println("解密后的手机号为-->"+mobile);
		/**根据手机号获取资料信息*/
		InformationPO infoPO=getUserCenterService(request).getUserInfo(mobile);
		if(infoPO!=null){
			System.out.println("nickname="+infoPO.nickname);
			Map<String, Object> velocityMap = new HashMap<String, Object>();
			
			Map<String,String> values=new HashMap<String,String>();
			values.put(ParameterConstants.TEMPLATE_ID, String.valueOf(templateId));
//			values.put("mobile", new Des().encrypt(mobile));//将手机号码进行加密
			String editUrl=URLUtil.getUrl(values, request);
			velocityMap.put("editUrl", editUrl);
			velocityMap.put("indexMap", new Constants());
			
			velocityMap.put("resource", infoPO);
			velocityMap.put("truename", infoPO.truename);
			velocityMap.put("nickname", infoPO.nickname);
			velocityMap.put("question", infoPO.question);
			velocityMap.put("answer", infoPO.answer);
			velocityMap.put("level", infoPO.level);
			velocityMap.put("gender", infoPO.gender);
			velocityMap.put("birthday", infoPO.birthday);
			velocityMap.put("constellation", infoPO.constellation);
			velocityMap.put("address", infoPO.address);
			velocityMap.put("sign", infoPO.sign);
			velocityMap.put("email", infoPO.email);
			velocityMap.put("qq", infoPO.QQ);
			velocityMap.put("msn", infoPO.MSN);
			velocityMap.put("headPic", infoPO.headPic);
			velocityMap.put("home", infoPO.home);
			velocityMap.put("height", infoPO.height);
			velocityMap.put("weight", infoPO.weight);
			velocityMap.put("bodytype", infoPO.bodytype);
			velocityMap.put("bloodtype", infoPO.bloodtype);
			velocityMap.put("enjoyBooktype", infoPO.enjoyBooktype);
			velocityMap.put("personallty", infoPO.personallty);
			velocityMap.put("nativePlace", infoPO.nativePlace);
			velocityMap.put("education", infoPO.education);
			velocityMap.put("interest", infoPO.interest);
			velocityMap.put("income", infoPO.income);
			velocityMap.put("job", infoPO.job);
			velocityMap.put("feeling", infoPO.feeling);
			velocityMap.put("maritalStatus", infoPO.maritalStatus);
			velocityMap.put("isSmoking", infoPO.smokingStatus);
			velocityMap.put("isDrinking", infoPO.drinkStatus);
			velocityMap.put("introduction", infoPO.introduction);
			velocityMap.put("enjoyBody", infoPO.enjoyBody);
			velocityMap.put("enjoyBook", infoPO.enjoyBook);
			
			
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
					.parseVM(velocityMap, this, tagTem));
			return resultMap;
		}else{
			return new HashMap();
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
