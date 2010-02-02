package com.hunthawk.reader.pps.fee;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * N选X功能标签 标签名称：special_month_view 参数说明： title:返回链接文字
 * 
 * @author liuxh
 * 
 */
public class ViewSpecialMonthTag extends BaseTag {
	private CustomService customService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		System.out.println("id---->"
				+ request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		// 得到允许选择的个数
		int allowCount = Integer.parseInt(request.getParameter("allow"));
		// 得到用户的历史选择的个数
		int userCount = Integer.parseInt(request.getParameter("old"));
		String msg = "";
		int flag=1;//默认操作成功
		if(allowCount==userCount){
			msg="您已经选择了"+allowCount+"本书，不能再进行选择操作了！";
			
		}else{
			// 获取相关参数
			String pid = request.getParameter(ParameterConstants.PRODUCT_ID) == null ? ""
					: request.getParameter(ParameterConstants.PRODUCT_ID);
			String feeId = request.getParameter(ParameterConstants.FEE_ID) == null ? ""
					: request.getParameter(ParameterConstants.FEE_ID);
			String mobile = RequestUtil.getMobile();
			String packId = request.getParameter(ParameterConstants.FEE_BAG_ID) == null ? ""
					: request.getParameter(ParameterConstants.FEE_BAG_ID);
			String channelId = request.getParameter(ParameterConstants.CHANNEL_ID);
			if (StringUtils.isEmpty(pid) || StringUtils.isEmpty(feeId)
					|| StringUtils.isEmpty(packId)) {
				TagLogger.debug(tagName, "参数为空", request.getQueryString(), null);
				return new HashMap();
			}
			
			boolean error = false;
			try {
				String resourceId = request
						.getParameter(ParameterConstants.SEARCH_PARAM_VALUE);
				getCustomService(request).addChoiceBook(pid, resourceId, feeId,
						mobile, Integer.parseInt(packId), channelId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				TagLogger.debug(tagName, "N选X记录用户选择情况时出错",
						request.getQueryString(), e);
				error = true;
				flag=0;
			}
			if (!error) {
				msg = "您已经成功选择了一本图书！";
				userCount++;
			}else{
				msg="操作失败!";
			}
			
		}
		
		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(),
				ParameterConstants.TEMPLATE_ID, "old", "allow"));
		Map velocityMap = new HashMap();
		if (allowCount == userCount) {
			velocityMap.put("title", getParameter("title", "返回上级"));
		} else {
			velocityMap.put("title", getParameter("title", "返回继续选择"));
		}
		velocityMap.put("url", backUrl);
		velocityMap.put("successTitle", msg);
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", flag);
		/**
		 * end
		 */
		String content = VmInstance.getInstance().parseVM(velocityMap, this);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);
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
}
