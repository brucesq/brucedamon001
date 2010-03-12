package com.hunthawk.reader.pps.iphone;

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
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;

/**
 * ������ǩ ��ǩ����:search ����˵���� property: ��ǩ����(1.����� 2.�ı�����) isize��
 * ��Ӧ�ı����size����(property=1) columnId: ��ĿID ��������Դ�б�(property=2) title: ��������
 * (property=2) restype: �μ�ResourceType�ж���(property=2) ����ȷ����ѯĿ��(�������������ݿ��)
 * 1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ searchby: �������� (����չ) 1.������2.������ 3.�������� 4. ���ؼ���
 * Ŀǰֻ���ͼ����Դ defaultkey: Ĭ������ֵ add by liuxh 2009-9-2
 * 
 * @author liuxh
 * 
 */
public class SearchTag extends BaseTag {
	private  BussinessService bussinessService;
	private boolean isQuickSearch;

	public boolean isQuickSearch() {
		return isQuickSearch;
	}

	public void setQuickSearch(boolean isQuickSearch) {
		this.isQuickSearch = isQuickSearch;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {

		return linkTag(request, tagName);

	}

	private Map linkTag(HttpServletRequest request, String tagName) {
//		Map<String, String> res = new HashMap<String, String>();
	
		int columnId = getIntParameter("columnId", -1);
		int restype = getIntParameter("restype", 1);// Ĭ����ͼ��
		String text = getParameter("text","");
		// String
		// name=getParameter("iname",ParameterConstants.DEFAULT_INPUT_NAME);
		if (columnId < 0) {
			TagLogger.debug(tagName, "columnId����ֵ��Ч", request.getQueryString(),
					null);
			return null;
		}
		// ƴURL
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(ParameterConstants.PAGE);
		sb.append("=");
		sb.append(ParameterConstants.PAGE_COLUMN);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		sb.append(ParameterConstants.COLUMN_ID);
		sb.append("=");
		sb.append(columnId);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append("1");
		sb.append("&");
		sb.append(ParameterConstants.RESOURCE_TYPE);
		sb.append("=");
		sb.append(restype);
//		sb.append("&");
		
		// sb.append("&");
		// sb.append(ParameterConstants.IS_FIRST_SEARCH);

		Map velocityMap = new HashMap();
		

		// ������ѭ������Map
		List<Object> lsRess = new ArrayList<Object>();
		String[] pams = sb.toString().split("&");
		for (int i = 0; i < pams.length; i++) {
			String str[] = pams[i].split("=");
			/** ���浥����¼ */
			Map<String, String> obj = new HashMap<String, String>();
			if (!str[0].contains(ParameterConstants.SEARCH_TYPE)) {
				obj.put("key", i == 0 ? str[0]
						.substring((str[0].indexOf("?") + 1)) : str[0]);
				if (str.length > 1 && str[1] != null
						&& StringUtils.isNotEmpty(str[1]))
					obj.put("value", str[1]);
				else
					obj.put("value", "");
				lsRess.add(obj);
			}
		}
		velocityMap.put("text", text);
		velocityMap.put("objs", lsRess);
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance().parseVM(velocityMap, this,tagTem));
		
		/*Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), VmInstance.getInstance()
				.parseVM(velocityMap, this));*/
		return resultMap;
		// StringBuilder builder=new StringBuilder();
		//		
		// Go go = new Go();
		// go.setCharset("UTF-8");
		// go.setMethod("POST");
		// go.setUrl(sb.toString());
		// Map<String, String> postMap = new HashMap<String, String>();
		// postMap.put(ParameterConstants.SEARCH_PARAM_VALUE, "$searchname");
		// if(searchby==3){//���ؼ���
		// postMap.put(ParameterConstants.QUICK_SEARCH_LINK_NAME, title);
		// }
		// IPostfieldModel model = new SimplePostfieldModel(postMap);
		//
		// go.setPostfieldModel(model);
		//
		// Anchor anchor = new Anchor();
		// anchor.setGo(go);
		// anchor.setTitle(title);
		// anchor.setText(title);
		//
		// builder.append(anchor.renderComponent());

		// res.put(ParameterConstants.PRE_TAG_SUFFIX + tagName
		// + ParameterConstants.END_TAG_SUFFIX, builder.toString());
		// return res;
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
