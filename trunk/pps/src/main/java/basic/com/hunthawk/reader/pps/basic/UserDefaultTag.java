package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * �û��Զ����ǩ
 * ��ǩ���ƣ�userdeftag
 * ����˵����
 * id:�û��Զ����ǩID
 * @author liuxh
 *
 */
public class UserDefaultTag extends BaseTag {

	private BussinessService bussinessService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int tagId=getIntParameter("id",-1);
		if(tagId<0){
			TagLogger.debug(tagName, "�Զ����ǩIDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		UserDefTag userDefTag=getBussinessService(request).getUserDefTagById(tagId);
		String content="";
		if(userDefTag!=null){
			content=userDefTag.getContent();
		}else{
			TagLogger.debug(tagName, "idΪ"+tagId+"���û��Զ����ǩ������", request.getQueryString(), null);
			return new HashMap();
		}
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);
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
