package com.hunthawk.reader.pps.iphone.bussiness;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Redirect;
import com.hunthawk.tag.vm.VmInstance;
/**
 * ��ͷ��ӭ��ǩ
 * ��ǩ���ƣ�iwelcome
 * ����˵����
 * 		tmd:��ʽģ��ID
 * @author liuxh
 *
 */
public class WelcomeTag extends BaseTag {

	private TagTemplate tagTem = null;
	private BussinessService bussinessService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
			
		}else{
			tagTem = null;
		}
		
		String balance =  getBussinessService(request).getVariables("iphone_balance_url").getValue();//����ѯ��ַ
		String record= getBussinessService(request).getVariables("iphone_order_record_url").getValue();//������¼�鿴��ַ
		String login= getBussinessService(request).getVariables("iphone_login_url").getValue();//��¼ע���ַ
		
		Map velocityMap = new HashMap();
		String mobile=RequestUtil.getMobile();
		if(mobile.equals("10000000000")){//δ�õ��ֻ��� ��ʾ��½ ע�ᰴť
			StringBuilder loginUrl=new StringBuilder();
			loginUrl.append(getBussinessService(request).getVariables("iphone_login_url").getValue());
			try {
				loginUrl.append(java.net.URLEncoder.encode(request.getRequestURL()+"?"+request.getQueryString(),"utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("login Url---->"+login);
			velocityMap.put("loginUrl", loginUrl.toString());
		}else{//�õ��ֻ���  ��ʾ  ������¼ ��ѯ��ť
			velocityMap.put("recordUrl", record);
			velocityMap.put("balanceUrl", balance);
		}
		velocityMap.put("mobile", mobile);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
//		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance().parseVM(velocityMap, this));
		return resultMap;
	}
	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
	}
}
