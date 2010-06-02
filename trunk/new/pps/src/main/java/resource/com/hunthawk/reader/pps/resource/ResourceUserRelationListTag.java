package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
/**
 * ��¼�Ķ�����������10�����ߣ�Ȼ�������10�������Ķ�������Ŀ������Щ��Ŀ��ѡ�����µ�20��������ʾΪ������Ŀ��������ʾ�����Կ����ɱ༭���ã�������20��
 * ��ǩ���ƣ�userrelation_list
 * ����˵����
 * 		pageSize: ȡ��¼������ Ĭ��3��
 * 		�������� ���ڹ����Ʒ���Ϣ
 * 		packId:���۰�ID  
 *      modify by liuxh  09-11-09 
 * @author liuxh
 *
 */
public class ResourceUserRelationListTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	/**�б����Χ*/
	private final Integer MAX_COUTN=20;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int packId=getIntParameter("packId",-1);
		if(packId<0){
			TagLogger.debug(tagName, "���۰�IDΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		
		int pageSize=getIntParameter("pageSize",3);//Ĭ��3��
		if(pageSize<0 || pageSize>MAX_COUTN)
			pageSize=MAX_COUTN;
		List<Integer> relids=getCustomService(request).getResourceUserRelation(URLUtil.getResourceId(request), packId,pageSize);
		
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		for(Iterator it=relids.iterator();it.hasNext();){
			ResourcePackReleation rpr=getResourceService(request).getResourcePackReleation(Integer.parseInt(it.next().toString()));
			if(rpr==null){
				continue;
			}
			ResourceAll resource=getResourceService(request).getResource(rpr.getResourceId());
			if(resource == null){
				TagLogger.error(tagName, rpr.getResourceId()+" ��Դ������", request.getQueryString(), null);
				continue;
			}
			String title=resource.getName();
			StringBuilder builder=new StringBuilder();
			builder.append(request.getContextPath());
			builder.append(ParameterConstants.PORTAL_PATH);
			builder.append("?");
			builder.append(ParameterConstants.PAGE);
			builder.append("=");
			builder.append(ParameterConstants.PAGE_RESOURCE);
			builder.append("&");
			URLUtil.append(builder, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(builder, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(builder, ParameterConstants.AREA_ID, request);
			URLUtil.append(builder, ParameterConstants.COLUMN_ID, request);
			builder.append(ParameterConstants.FEE_BAG_ID);
			builder.append("=");
			builder.append(rpr.getPack().getId());
			builder.append("&");
			builder.append(ParameterConstants.FEE_BAG_RELATION_ID);
			builder.append("=");
			builder.append(rpr.getId());
			builder.append("&");
			builder.append(ParameterConstants.RESOURCE_ID);
			builder.append("=");
			builder.append(resource.getId());
			builder.append("&");
			URLUtil.append(builder, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(builder, ParameterConstants.UNICOM_PT, request);
			builder.append(ParameterConstants.PAGE_NUMBER);
			builder.append("=");
			builder.append(1);
			
			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", builder.toString());
			obj.put("title",title);
			obj.put("resource", resource); //��������� ��Դ����
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		/*result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
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
	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac.getBean("resourceService");
		}
		return resourceService;
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
