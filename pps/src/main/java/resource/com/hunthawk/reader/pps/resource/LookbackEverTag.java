package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
/**
 * ���ڻع˱�ǩ
 * @author liuxh 09-11-11
 * ��ǩ���ƣ�lookback_list
 * ����˵����
 * 		order: 0.����  1.���� (�鲿�����ڿ���)
 * 		columnId:��ĿID ���ڻ�ȡ���۰�
 * 		listCount:�б�������
 *  	pageSize:ÿҳ��ʾ����Դ��
 *  	noPageLink:����ʾ��ҳ��ص�����
 */
public class LookbackEverTag extends BaseTag {

	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;
	private ResourceService resourceService;
	private IphoneService iphoneService;
	private static final int DEFAULT_PAGE_SIZE = 5; //Ĭ�Ϸ�ҳ   	5��/ҳ
	private static final int DEFAULT_MAX_COUNT=20;//Ĭ���б����Χ 20��
	/** ����ʾ��ҳ��ص����� */
	private boolean noPageLink;
	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		boolean isIphone=getIphoneService(request).isIphoneProduct(productId);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		String resourceId=URLUtil.getResourceId(request);
		if(StringUtils.isEmpty(resourceId)){
			TagLogger.debug(tagName, "���ڻع˱�ǩ��ȡ������ԴID", request.getQueryString(), null);
			return new HashMap();
		}
		
		int columnId=getIntParameter("columnId",-1);
		if(columnId<0){
			columnId=ParamUtil.getIntParameter(request, ParameterConstants.COLUMN_ID, -1);
			if(columnId<0){
				TagLogger.debug(tagName, "��ĿIDΪ��", request.getQueryString(), null);
				return new HashMap();
			}
		}
		ResourceAll res=getResourceService(request).getResource(resourceId);
		int listCount=getIntParameter("listCount",-1)<0?DEFAULT_MAX_COUNT:getIntParameter("listCount",-1);
		int order=getIntParameter("order",0);//����ʽ  Ĭ�ϰ��鲿/�ڿ�������
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		int pageSize = getIntParameter("pageSize", -1) < 0 ? DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));
	
		int totalCount=0;
		try{
			totalCount=getResourceService(request).getDivisionsCount(String.valueOf(columnId), res);
		}catch(Exception ex){
			ex.printStackTrace();
			TagLogger.debug(tagName, "��ȡ������Դ�б�ʧ��", request.getQueryString(), ex);
			return new HashMap();
		}
		if(listCount>totalCount)
			listCount=totalCount;
		if (!isNoPageLink()) {
			Navigator navi = new Navigator(listCount, currentPage,
					pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
		}
		
		List<ResourcePackReleation> rprs=null;
		try {
			 rprs=getResourceService(request).findDivisions(String.valueOf(columnId), res,currentPage,pageSize,listCount,order);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TagLogger.debug(tagName, "��ȡ������Դ�б�ʧ��", request.getQueryString(), e);
			return new HashMap();
		}
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		for(Iterator it=rprs.iterator();it.hasNext();){
			ResourcePackReleation rpr=(ResourcePackReleation)it.next();
			ResourceAll resourceAll = getResourceService(request).getResource(rpr.getResourceId());
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=null;
			if(!isIphone){//��IPHONE��Ʒ��ӼƷ��߼�
				feeMap=getFeeLogicService(request).isFee(productId, resourceId, RequestUtil.getMobile(), rpr, rpr.getPack().getId(), month_fee_bag_id);
				if(feeMap==null){
					sb.append(ParameterConstants.PORTAL_PATH);
					sb.append("?");
				}else{
					sb.append(feeMap.get("builder"));
				}
			}else{
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append("r");
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID,
							request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			sb.append(ParameterConstants.COLUMN_ID);
			sb.append("=");
			sb.append(columnId);
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(rpr.getPack().getId());
			sb.append("&");
			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
			sb.append("=");
			sb.append(rpr.getId());
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_ID);
			sb.append("=");
			sb.append(rpr.getResourceId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if (feeMap!= null) {
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			obj.put("title", resourceAll.getName());
//			System.out.println(resourceAll.getDivision()+" : "+resourceAll.getDivisionContent());
			obj.put("resource", resourceAll); // ��������� ��Դ����
			String imgUrl = CoverPreview.getPreview(getResourceService(request), resourceAll, 51);// ��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			obj.put("author",getAuthorName(resourceAll, request));
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service", getResourceService(request));
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
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
	private FeeLogicService getFeeLogicService(HttpServletRequest request) {
		if (feeLogicService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeLogicService = (FeeLogicService) wac
					.getBean("feeLogicService");
		}
		return feeLogicService;
	}
	private ResourceService getResourceService(HttpServletRequest request) {
		if (resourceService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			resourceService = (ResourceService) wac
					.getBean("resourceService");
		}
		return resourceService;
	}
	private IphoneService getIphoneService(HttpServletRequest request) {
		if (iphoneService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			iphoneService = (IphoneService) wac
					.getBean("iphoneService");
		}
		return iphoneService;
	}
	private String getAuthorName(ResourceAll resource,HttpServletRequest request){
		Integer [] authorids=resource.getAuthorIds();
		StringBuilder str=new StringBuilder();
		String name="";
		for(int i=0;i<authorids.length;i++){
			ResourceAuthor author=getResourceService(request).getResourceAuthor(authorids[i]);
			str.append((author.getPenName()==null || "".equals(author.getPenName()))?author.getName():author.getPenName());
			str.append(",");
			if(i==authorids.length-1){
				//ȥ�����һ��,
				str.replace(str.lastIndexOf(","), str.length(), "");
			}
		}
		String authorPenName=str.toString().trim();
		try {
			name = StrUtil.getLimitStr(authorPenName, ParameterConstants.AUTHOR_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//����
		return name;
	}
}
