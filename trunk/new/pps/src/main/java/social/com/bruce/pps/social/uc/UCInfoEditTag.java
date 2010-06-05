/**
 * 个人资料信息修改标签
 * 标签名称：uc_info_eidt
 * 参数说明：
 * 	templateId:信息修改结果提示页模版ID (如果修改成功则跳转回信息资料显示页，如果失败则提示修改失败)
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
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author liuxh
 *
 */
public class UCInfoEditTag extends BaseTag {

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
		/**得到修改的index值*/
		Integer index=Integer.parseInt(request.getParameter("item_index").toString());
		
		String mobile= RequestUtil.getMobile();
		
	
		/**从标签中获得修改结果页模版ID*/
		int templateId=getIntParameter("templateId",-1);
		if(templateId<0){
			TagLogger.debug(tagName, "模版ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		
		if(mobile==null || StringUtils.isEmpty(mobile)){
			return new HashMap();
		}
		if(index==null)
			index=1;
		Map<String,String> values=new HashMap<String,String>();
		values.put(ParameterConstants.TEMPLATE_ID, String.valueOf(templateId));
		
		/**返回资料显示页URL地址*/
		StringBuilder backUrl=new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(), ParameterConstants.TEMPLATE_ID,"item_index"));
	
		Map velocityMap = new HashMap();
		velocityMap.put("back", backUrl.toString());
		velocityMap.put("index", index);
		velocityMap.put("url", URLUtil.getUrl(values, request));
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));
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

}
