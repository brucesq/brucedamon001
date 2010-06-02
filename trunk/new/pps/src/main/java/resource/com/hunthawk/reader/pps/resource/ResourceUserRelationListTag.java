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
 * 记录阅读此书的最近的10个读者，然后关联这10个读者阅读过的书目，在这些书目中选择最新的20本出来显示为关联书目，其中显示数可以开放由编辑设置，不超过20本
 * 标签名称：userrelation_list
 * 参数说明：
 * 		pageSize: 取记录的条数 默认3条
 * 		新增参数 用于关联计费信息
 * 		packId:批价包ID  
 *      modify by liuxh  09-11-09 
 * @author liuxh
 *
 */
public class ResourceUserRelationListTag extends BaseTag {

	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	/**列表最大范围*/
	private final Integer MAX_COUTN=20;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int packId=getIntParameter("packId",-1);
		if(packId<0){
			TagLogger.debug(tagName, "批价包ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		
		int pageSize=getIntParameter("pageSize",3);//默认3条
		if(pageSize<0 || pageSize>MAX_COUTN)
			pageSize=MAX_COUTN;
		List<Integer> relids=getCustomService(request).getResourceUserRelation(URLUtil.getResourceId(request), packId,pageSize);
		
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		for(Iterator it=relids.iterator();it.hasNext();){
			ResourcePackReleation rpr=getResourceService(request).getResourcePackReleation(Integer.parseInt(it.next().toString()));
			if(rpr==null){
				continue;
			}
			ResourceAll resource=getResourceService(request).getResource(rpr.getResourceId());
			if(resource == null){
				TagLogger.error(tagName, rpr.getResourceId()+" 资源不存在", request.getQueryString(), null);
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
			
			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", builder.toString());
			obj.put("title",title);
			obj.put("resource", resource); //新添加上了 资源对象
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//把预览图放进去
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
