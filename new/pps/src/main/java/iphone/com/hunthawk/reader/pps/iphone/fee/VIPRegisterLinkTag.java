package com.hunthawk.reader.pps.iphone.fee;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * VIP注册链接
 * 标签名称：ivip_link
 * 参数说明：
 * 		templateId:通用模版页ID
 * 		title:链接文字
 * @author liuxh
 *
 */
public class VIPRegisterLinkTag extends BaseTag {

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String title=getParameter("title","");
		int templateId=getIntParameter("templateId",-1);
		if(templateId==-1){
			TagLogger.debug(tagName, "模版ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		StringBuilder builder=new StringBuilder();
		builder.append(request.getContextPath());
		builder.append(ParameterConstants.PORTAL_PATH);
		builder.append("?");
		builder.append(ParameterConstants.COMMON_PAGE);
		builder.append("=");
		builder.append(ParameterConstants.COMMON_PAGE_LINK);
		builder.append("&");
		builder.append(ParameterConstants.TEMPLATE_ID);
		builder.append("=");
		builder.append(templateId);
		builder.append("&");
		builder.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.PAGE_NUMBER,ParameterConstants.SEARCH_PARAM_VALUE,ParameterConstants.SEARCH_TYPE,ParameterConstants.QUICK_SEARCH_LINK_NAME,
				ParameterConstants.COMMENT_PARAM_VALUE,ParameterConstants.CUSTOM_KEY_VALUE,ParameterConstants.COMMENT_PLATE,ParameterConstants.COMMENT_TARGET,ParameterConstants.COMMENT_TARGET_ID,
				ParameterConstants.VOTE_VOTE_TYPE,ParameterConstants.VOTE_ITEM_ID,ParameterConstants.VOTE_CONTENT_ID,ParameterConstants.RESOURCE_TYPE));
		builder.append("&");
		builder.append(ParameterConstants.PAGE_NUMBER);
		builder.append("=");
		builder.append("1");
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url", builder.toString());
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));
		return resultMap;
	}

}
