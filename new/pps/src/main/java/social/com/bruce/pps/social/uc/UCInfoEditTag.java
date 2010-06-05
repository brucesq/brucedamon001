/**
 * ����������Ϣ�޸ı�ǩ
 * ��ǩ���ƣ�uc_info_eidt
 * ����˵����
 * 	templateId:��Ϣ�޸Ľ����ʾҳģ��ID (����޸ĳɹ�����ת����Ϣ������ʾҳ�����ʧ������ʾ�޸�ʧ��)
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
		/**�õ��޸ĵ�indexֵ*/
		Integer index=Integer.parseInt(request.getParameter("item_index").toString());
		
		String mobile= RequestUtil.getMobile();
		
	
		/**�ӱ�ǩ�л���޸Ľ��ҳģ��ID*/
		int templateId=getIntParameter("templateId",-1);
		if(templateId<0){
			TagLogger.debug(tagName, "ģ��IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		
		if(mobile==null || StringUtils.isEmpty(mobile)){
			return new HashMap();
		}
		if(index==null)
			index=1;
		Map<String,String> values=new HashMap<String,String>();
		values.put(ParameterConstants.TEMPLATE_ID, String.valueOf(templateId));
		
		/**����������ʾҳURL��ַ*/
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
