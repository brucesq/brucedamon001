/**
 * 个人中心用户信息显示标签
 * 标签名称：
 * 		uc_info
 * 参数说明：
 * templateId:资料修改页模版ID
 */
package com.bruce.pps.social.uc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.UserInfo;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.GuestService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;

/**
 * @author liuxh
 * 
 */
public class UCInfoTag extends BaseTag {

	private GuestService guestService;
	private BussinessService bussinessService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.tag.BaseTag#parseTag(javax.servlet.http.HttpServletRequest,
	 *      java.lang.String)
	 */
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		int templateId = getIntParameter("templateId", -1);

		if (templateId < 0) {
			TagLogger.debug(tagName, "模版ID为空", request.getQueryString(), null);
			return new HashMap();
		}

		String uid = ParamUtil.getParameter(request, ParameterConstants.USER_ID);
		Boolean isEdit = true;
		UserInfo info = null;
		if(StringUtils.isNotEmpty(uid)){
			isEdit = false;
			info = getGuestService(request).getUserInfo(uid);
		}else{
			String mobile = RequestUtil.getMobile();
			info = getGuestService(request).getUserInfo(mobile);
		}
		

		if (info != null) {
			Map<String, Object> velocityMap = new HashMap<String, Object>();

			Map<String, String> values = new HashMap<String, String>();
			values.put(ParameterConstants.TEMPLATE_ID, String
					.valueOf(templateId));
			// values.put("mobile", new Des().encrypt(mobile));//将手机号码进行加密
			String editUrl = URLUtil.getUrl(values, request);
			velocityMap.put("editUrl", editUrl);
			velocityMap.put("indexMap", new Constants());
			velocityMap.put("isEdit", isEdit);
			velocityMap.put("resource", info);
			velocityMap.put("truename", info.getNickname());
			velocityMap.put("nickname", info.getNickname());
			velocityMap.put("question", "");
			velocityMap.put("answer", "");
			velocityMap.put("level", "");
			velocityMap.put("gender", info.getSexName());
			velocityMap.put("birthday", info.getBirthday());
			velocityMap.put("constellation", "");
			velocityMap.put("address", info.getAddress());
			velocityMap.put("sign", info.getSign());
			velocityMap.put("email", info.getEmail());
			velocityMap.put("qq", info.getQq());
			velocityMap.put("msn", "");
			velocityMap.put("headPic", info.getHeadPic());
			velocityMap.put("home", "");
			velocityMap.put("height", "");
			velocityMap.put("weight", "");
			velocityMap.put("bodytype", "");
			velocityMap.put("bloodtype", "");
			velocityMap.put("enjoyBooktype", "");
			velocityMap.put("personallty", "");
			velocityMap.put("nativePlace", "");
			velocityMap.put("education", "");
			velocityMap.put("interest", info.getInterest());
			velocityMap.put("income", "");
			velocityMap.put("job", info.getJob());
			velocityMap.put("feeling", "");
			velocityMap.put("maritalStatus", "");
			velocityMap.put("isSmoking", "");
			velocityMap.put("isDrinking", "");
			velocityMap.put("introduction", info.getDesc());
			velocityMap.put("enjoyBody", "");
			velocityMap.put("enjoyBook", "");

			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
					.parseVM(velocityMap, this, tagTem));
			return resultMap;
		} else {
			
			Map resultMap = new HashMap();
			resultMap.put(TagUtil.makeTag(tagName),"没有该用户相关信息");
			return resultMap;
		}
	}

	private GuestService getGuestService(HttpServletRequest request) {
		if (guestService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			guestService = (GuestService) wac.getBean("guestService");
		}
		return guestService;
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
