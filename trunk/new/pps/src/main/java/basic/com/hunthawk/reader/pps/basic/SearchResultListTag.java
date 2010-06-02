package com.hunthawk.reader.pps.basic;

import java.io.UnsupportedEncodingException;
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

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.PpsUtil;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.StatisticsLog;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * ��������б��ǩ
 * ��ǩ���ƣ�search_result
 * ����˵����
 * pageSize:ÿҳ��ʾ����Դ����
 * noPageLink:����ʾ��ҳ��ص�����	
 * mix:������ϲ���           	
 * @author liuxh
 *
 */
public class SearchResultListTag extends BaseTag {

	private ResourceService resourceService;
	private CustomService customService;
	private BussinessService bussinessService;
	private FeeLogicService feeLogicService;
	
	private static final int DEFAULT_PAGE_SIZE = 5; //Ĭ����ʾ5��
	/** ����ʾ��ҳ��ص����� */
	private boolean noPageLink;
	private Integer urlOrder;
	private Integer urlOrderSub;
	
	public Integer getUrlOrder() {
		return urlOrder;
	}
	public void setUrlOrder(Integer urlOrder) {
		this.urlOrder = urlOrder;
	}
	public Integer getUrlOrderSub() {
		return urlOrderSub;
	}
	public void setUrlOrderSub(Integer urlOrderSub) {
		this.urlOrderSub = urlOrderSub;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		/**Ĭ������ͼ��*/
		int restype=ParamUtil.getIntParameter(request, ParameterConstants.RESOURCE_TYPE, 1);//request.getParameter(ParameterConstants.RESOURCE_TYPE);//��Դ������
		/**Ĭ�ϰ�����*/
		int searchby=ParamUtil.getIntParameter(request, ParameterConstants.SEARCH_TYPE, 1);//request.getParameter(ParameterConstants.SEARCH_TYPE);//��������
		
		/**��ȡurl ������� start
		 * @author yuzs 2009-11-05
		 */
		urlOrder = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDER, -1);
		urlOrderSub = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDERSUB, -1);
		/**
		 * ����
		 */
		
//		Map map=new HashMap();
		//1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ
//		switch(Integer.parseInt(restype)){
//			case 1:map=searchBook(request,tagName,searchby);break;
//		}
		return searchResources(request,tagName,searchby,restype);
	}
	/**
	 * �������
	 * @param request
	 * @param tagName
	 * @param searchBy	����ȡֵ���� search��ǩ��searchby����(����չ)
	 * @return  1.�������ؼ���2.������ 3.�������� 4. ���ؼ���  5.��������
	 */
	public Map searchResources(HttpServletRequest request, String tagName,int searchBy,int restype){
		if(searchBy==2){//������
			return searchResourcesByAuthor(request,tagName,searchBy,restype);
		}else if(searchBy==1){//������ 
			return searchResourcesByName(request,tagName,searchBy,restype);
		}else if(searchBy==3 || searchBy==4){
			return searchResourcesByKey(request,tagName,searchBy,restype);
		}else if(searchBy==5){//��������
			return searchResourcesByPublishing(request,tagName,searchBy,restype);
		}
		return new HashMap();
	}
	
	/**
	 * ������������
	 * @param request
	 * @param tagName
	 * @param searchBy
	 * @param restype
	 * @return
	 */
	private Map searchResourcesByPublishing(HttpServletRequest request, String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		String mix = getParameter("mix", "");// title��� Ĭ��ֻ��ʾ��Դ���Ʋ������
		//�õ�������ֵ
		String value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		if(value==null){
			TagLogger.debug(tagName, "����������ֵΪ��", request.getQueryString(), null);
			return new HashMap();
		}else{
			value=URLUtil.chineseFilter(value, 2);
			//��¼��־ 
			/**
			 * ������Դ����
			 * modify by liuxh 09-11-17
			 */
			StatisticsLog.logStat(Integer.parseInt(restype+"5"), value);
			/**
			 * end
			 */
		}
		
		System.out.println("����ֵ="+value);
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage = request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1: Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "��ĿIdΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "��ȡ��Ŀ��Ϣʧ��", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "��ȡ���۰���Ϣʧ��", request.getQueryString(), null);
				return new HashMap();
			}
		}
		/**
		 * �ж��Ƿ��url���Ƿ��������ֶδ��ڡ�����У�ʹ��url�����ֶ����򣬷���ʹ�ñ�ǩ�ֶ�����
		 * @author yuzs 2009-11-05
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// ��������
		}
		/**
		 * ����
		 */
		int showTotal=0;
		int totalCount=0;
		//�ж��Ƿ񵼺�
		if(!isNoPageLink()){
			totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
			showTotal=totalCount;
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype, searchBy, value,rp,currentPage, pageSize,order);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr = (ResourcePackReleation)it.next();
			String resourceId=rpr.getResourceId();
			//������ԴID��ѯ��Դ
			ResourceAll resource=getResourceService(request).getResource(resourceId);
			if(resource!=null){
				//��¼��־ 
				StatisticsLog.logStat(2, resource.getId());
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resource.getId(), mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
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
			sb.append(resourceId);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			//����������ز��� 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.SEARCH_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			sb.append(value);

			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {
					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// ��������ID��ѯ������Ϣ
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("��)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //��������� ��Դ����
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//�������
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//��������
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//�ղ�
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("totalCount", totalCount);
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
		return resultMap;
	}
	/**
	 * ���ؼ�������
	 * @param request
	 * @param tagName
	 * @param searchBy
	 * @return
	 */
	private Map searchResourcesByKey(HttpServletRequest request, String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String mix=getParameter("mix","");
		String value="";
		if(searchBy==3){
			value=StringUtils.trimToNull(request.getParameter(ParameterConstants.QUICK_SEARCH_LINK_NAME));
		}else if(searchBy==4){
			value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		}
		if(value==null){
			TagLogger.debug(tagName, "����ֵΪ��", request.getQueryString(), null);
			return new HashMap();
		}else{
//			try {
//				value=java.net.URLDecoder.decode(value,"UTF-8");
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			value=URLUtil.chineseFilter(value, 2);
			//��¼��־ 
			/**modify by liuxh 09-11-17 ������Դ����*/
			StatisticsLog.logStat(Integer.parseInt(restype+"3"), value);
			/**end*/
		}
		System.out.println("����ֵ="+value);
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		// ÿҳ��ʾ��Դ����
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "��ĿIdΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "��ȡ��Ŀ��Ϣʧ��", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "��ȡ���۰���Ϣʧ��", request.getQueryString(), null);
				return new HashMap();
			}
		}
		/**
		 * �ж��Ƿ��url���Ƿ��������ֶδ��ڡ�����У�ʹ��url�����ֶ����򣬷���ʹ�ñ�ǩ�ֶ�����
		 * 
		 * @author yuzs 2009-11-05
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// ��������
		}
		/**
		 * ����
		 */
		int totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
		//�ж��Ƿ񵼺�
		if(!isNoPageLink()){
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype, searchBy, value,rp,currentPage, pageSize,order);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr = (ResourcePackReleation)it.next();
			String resourceId=rpr.getResourceId();
			//������ԴID��ѯ��Դ
			ResourceAll resource=getResourceService(request).getResource(resourceId);
			if(resource!=null){
				//��¼��־ 
				StatisticsLog.logStat(2,resource.getId());//������
			}
			// ƴURL
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
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
			sb.append(resourceId);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			//����������ز��� 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(searchBy);
			sb.append("&");
			if(searchBy==3){
				sb.append(ParameterConstants.QUICK_SEARCH_LINK_NAME);
			}else{
				sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			}
			sb.append("=");
			sb.append(value);

			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {

					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// ��������ID��ѯ������Ϣ
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("��)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //��������� ��Դ����
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//�������
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//��������
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//�ղ�
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("objs", lsRess);
		map.put("totalCount", totalCount);
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
	/**
	 * ������
	 * @param request
	 * @param tagName
	 * @param searchBy
	 * @return
	 */
	public Map searchResourcesByName(HttpServletRequest request, String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		String mix=getParameter("mix","");
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		//�õ�������ֵ
		String value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		if(value==null){//��������Ϊ�� Ĭ�ϲ�ѯ���������б�
			TagLogger.debug(tagName, "����������ֵΪ��", request.getQueryString(), null);
		}else{
			
//			try {
//				value=java.net.URLDecoder.decode(value,"UTF-8");
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			value=URLUtil.chineseFilter(value, 2);
			//��¼��־ 
			/**modify by liuxh 09-11-17 ������Դ����*/
			StatisticsLog.logStat(Integer.parseInt(restype+"1"), value);//������
			/**end*/
		}
		System.out.println("����ֵ="+value);
		// ÿҳ��ʾ��Դ����
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE:getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "��ĿIdΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "��ȡ��Ŀ��Ϣʧ��", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "��ȡ���۰���Ϣʧ��", request.getQueryString(), null);
				return new HashMap();
			}
		}
		
		/**
		 * �ж��Ƿ��url���Ƿ��������ֶδ��ڡ�����У�ʹ��url�����ֶ����򣬷���ʹ�ñ�ǩ�ֶ�����
		 * 
		 * @author yuzs 2009-11-05
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// ��������
		}
		/**
		 * ����
		 */
		
		int totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
		//�ж��Ƿ񵼺�
		if(!isNoPageLink()){
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype,searchBy, value,rp,currentPage, pageSize,order);
		//System.out.println("�������е�ֵ��--->"+value);
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr = (ResourcePackReleation)it.next();
			String resourceId=rpr.getResourceId();
			//������ԴID��ѯ��Դ
			ResourceAll resource=getResourceService(request).getResource(resourceId);
			if(resource!=null){
				//��¼��־
				StatisticsLog.logStat(2, resource.getId());
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resourceId, mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
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
			sb.append(resourceId);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			//����������ز��� 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.SEARCH_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			sb.append(value);

			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {

					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// ��������ID��ѯ������Ϣ
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("��)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //��������� ��Դ����
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//�������
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//��������
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//�ղ�
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("objs", lsRess);
		map.put("totalCount", totalCount);
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
	/**
	 * ������
	 * @param request
	 * @param tagName
	 * @return
	 */
	public Map searchResourcesByAuthor(HttpServletRequest request,String tagName,int searchBy,int restype){
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PRODUCT_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		
		
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		String mix = getParameter("mix", "");// title��� Ĭ��ֻ��ʾ��Դ���Ʋ������
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		//����URL������ID ��ѯ��Ӧ����Դ
//		String authorId=request.getParameter(ParameterConstants.AUTHOR_ID);
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		int currentPage =request
		.getParameter(ParameterConstants.PAGE_NUMBER)==null?1: Integer.parseInt(request
				.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ
		//�õ�������ֵ
		String value=StringUtils.trimToNull(request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE));
		if(value==null){//��������Ϊ�� Ĭ�ϲ�ѯ���������б�
			TagLogger.debug(tagName, "����������ֵΪ��", request.getQueryString(), null);
		}else{
//			try {
//				value=java.net.URLDecoder.decode(value,"UTF-8");
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			value=URLUtil.chineseFilter(value, 2);
			//��¼����ֵ
			/**modify by liuxh 09-11-17 ������Դ����*/
			if(restype!=ResourceType.TYPE_MAGAZINE && restype!=ResourceType.TYPE_NEWSPAPERS)
				StatisticsLog.logStat(Integer.parseInt(restype+"2"), value);//������
			/**end*/
		}
		System.out.println("����ֵ="+value);
		String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
		if(columnId==null || StringUtils.isEmpty(columnId)){
			TagLogger.debug(tagName, "��ĿIdΪ��", request.getQueryString(), null);
			return new HashMap();
		}
		Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
		ResourcePack rp=null;
		if(col==null){
			TagLogger.debug(tagName, "��ȡ��Ŀ��Ϣʧ��", request.getQueryString(), null);
			return new HashMap();
		}else{
			rp=getResourceService(request).getResourcePack(col.getPricepackId());
			if(rp==null){
				TagLogger.debug(tagName, "��ȡ���۰���Ϣʧ��", request.getQueryString(), null);
				return new HashMap();
			}
		}
		/**
		 * �ж��Ƿ��url���Ƿ��������ֶδ��ڡ�����У�ʹ��url�����ֶ����򣬷���ʹ�ñ�ǩ�ֶ�����
		 * 
		 * @author yuzs 2009-11-05
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = col.getOrderType();// ��������
		}
		/**
		 * ����
		 */
		int totalCount=getResourceService(request).searchResourceCount(restype,searchBy,value,rp);
		//�ж��Ƿ񵼺�
		if(!isNoPageLink()){
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		String feeId = "";
		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(restype, searchBy, value,rp,currentPage, pageSize,order);
		for (Iterator it = rprs.iterator(); it.hasNext();) {
			loop++;
			ResourcePackReleation rpr=(ResourcePackReleation)it.next();
			ResourceAll resource=getResourceService(request).getResource(rpr.getResourceId());
			if(resource!=null){
				//��¼����ֵ
				StatisticsLog.logStat(2, resource.getId());//������
			}
			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			Map feeMap=getFeeLogicService(request).isFee(productId, resource.getId(), mobile, rpr, packId, month_fee_bag_id);
			if(feeMap==null){
				sb.append(ParameterConstants.PORTAL_PATH);
				sb.append("?");
			}else{
				sb.append(feeMap.get("builder"));
			}
			sb.append(ParameterConstants.PAGE);
			sb.append("=");
			sb.append(ParameterConstants.PAGE_RESOURCE);
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
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
			sb.append(resource.getId());
			sb.append("&");
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			if(feeMap!=null){
				sb.append("&");
				sb.append(ParameterConstants.FEE_ID);
				sb.append("=");
				sb.append(feeMap.get("feeId"));
			}
			//����������ز��� 
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.RESOURCE_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(request.getParameter(ParameterConstants.SEARCH_TYPE));
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			sb.append(value);
			
			/** ���浥����¼ */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			StringBuilder linkname=new StringBuilder();
			if(StringUtils.isNotEmpty(mix)){
				List<String> mixparams = PpsUtil.getParameters(mix);
				for (String str : mixparams) {

					if (str.equalsIgnoreCase("name")) {
						linkname.append(resource.getName());
					} else if (str.equalsIgnoreCase("bComment")) {
						linkname.append(resource.getBComment() == null ? ""
								: resource.getBComment());
						linkname.append(":");
					} else if (str.equalsIgnoreCase("authorId")) {
						// ��������ID��ѯ������Ϣ
						Integer[] authorIds = resource.getAuthorIds();
						if (authorIds.length > 0) {
							for (int i = 0; i < authorIds.length; i++) {
								ResourceAuthor author = getResourceService(
										request).getResourceAuthor(
										resource.getAuthorIds()[i]);
								if (author != null) {
									linkname.append("(");
									linkname
											.append((author.getPenName() == null
													|| StringUtils
															.isEmpty(author
																	.getPenName()) ? author
													.getName()
													: author.getPenName()));
									linkname.append(")");
									break;
								} else {
									continue;
								}
							}

						}
					} else if (str.equalsIgnoreCase("downnum")) {
						linkname.append("(");
						linkname.append(getResourceService(request)
								.getResourceVisits(resource.getId()));
						linkname.append("��)");
					}
				}
			}else{
				linkname.append(resource.getName());
			}
			obj.put("title", linkname.toString());
			obj.put("resource", resource); //��������� ��Դ����
			String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//��Ԥ��ͼ�Ž�ȥ
			obj.put("preview", imgUrl);
			/**
			 * yuzs 2009-11-05
			 */
			obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//�������
			obj.put("searchnum", String.valueOf(resource.getSearchNum()==null?1:(resource.getSearchNum())+1));//��������
			obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//�ղ�
			/**
			 * end
			 */
			lsRess.add(obj);
		}// for end
		map.put("totalCount", totalCount);
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
//		String authorId=request.getParameter(ParameterConstants.AUTHOR_ID);
//		if(StringUtils.isEmpty(authorId)){//�������IDΪ�� �����������б�
//			return AuthorListMap(request,tagName);
//		}else{//��������ص���Դ
//			return ResourceListMap(request,tagName);
//		}
	}
//	/**
//	 * �����߲�ѯ�������б�
//	 * @return
//	 */
//	public Map AuthorListMap(HttpServletRequest request,String tagName){
//		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
//		Map resultMap = new HashMap();
//		String result = "";
//		/** ����б���Դ */
//		Map<String, Object> map = new HashMap<String, Object>();
//		//�õ�������ֵ
//		String value=request.getParameter(ParameterConstants.SEARCH_PARAM_VALUE);
//		// ÿҳ��ʾ��Դ����
//		int pageSize = getIntParameter("pageSize", -1);
//		if (pageSize < 0) {
//			pageSize = DEFAULT_PAGE_SIZE;
//		}
//		int currentPage = 1;// ��ǰҳ����Ĭ��Ϊ��һҳ
//		try {
//			currentPage = Integer.parseInt(request
//					.getParameter(ParameterConstants.PAGE_NUMBER));
//		} catch (Exception e) {}
//		System.out.println("�������е�ֵ��--->"+value);
//		List <ResourceAuthor> authors=getResourceService(request).searchResult(value,currentPage,pageSize);
//		//�ж��Ƿ񵼺�
//		if(!isNoPageLink()){
//			List all=new ArrayList();
//			//�õ�����
//			int totalCount=getResourceService(request).searchResultCount(value);
//			for(int i=0;i<totalCount;i++){
//				all.add(new Object());
//			}
//			Navigator navi=new Navigator(all, currentPage, pageSize, 5);
//			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
//		}
//		List<Object> lsRess = new ArrayList<Object>();
//		int loop = 0;
//		for(Iterator it=authors.iterator();it.hasNext();){
//			loop++;
//			ResourceAuthor author=(ResourceAuthor)it.next();
//			//ƴURL
//			StringBuilder sb=new StringBuilder();
//			sb.append(request.getContextPath());
//			sb.append(ParameterConstants.PORTAL_PATH);
//			sb.append("?");
//			URLUtil.append(sb, ParameterConstants.PAGE, request);
//			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
//			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
//			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
//			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
//			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
//			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
//			URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
//			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
//			URLUtil.append(sb, ParameterConstants.RESOURCE_TYPE, request);
//			URLUtil.append(sb, ParameterConstants.SEARCH_TYPE, request);
//			sb.append(ParameterConstants.AUTHOR_ID);
//			sb.append("=");
//			sb.append(author.getId());
//			/** ���浥����¼ */
//			Map<String, String> obj = new HashMap<String, String>();
//			obj.put("url", sb.toString());
//			obj.put("title", author.getName());
//			lsRess.add(obj);
//		}
//		map.put("objs", lsRess);
//		result = VmInstance.getInstance().parseVM(map, this);
//		resultMap.put(TagUtil.makeTag(tagName), result);
//		return resultMap;
//	}
//	/**
//	 * �����߲�ѯ�����߶�Ӧ����Դ�б�
//	 * @return
//	 */
//	public Map ResourceListMap(HttpServletRequest request,String tagName){
//		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
//		boolean isOut = false;// �Ƿ񵯳��ʷ���ʾ
//		boolean isFee=false;
//		boolean isMonth=false;
//		Map resultMap = new HashMap();
//		String result = "";
//		/** ����б���Դ */
//		Map<String, Object> map = new HashMap<String, Object>();
//		//����URL������ID ��ѯ��Ӧ����Դ
//		String authorId=request.getParameter(ParameterConstants.AUTHOR_ID);
//		int pageSize = getIntParameter("pageSize", -1);
//		if (pageSize < 0) {
//			pageSize = DEFAULT_PAGE_SIZE;
//		}
//		int currentPage = 1;// ��ǰҳ����Ĭ��Ϊ��һҳ
//		try {
//			currentPage = Integer.parseInt(request
//					.getParameter(ParameterConstants.PAGE_NUMBER));
//		} catch (Exception e) {}
//	
//		//�ж��Ƿ񵼺�
//		if(!isNoPageLink()){
//			List resAll=new ArrayList();
//			int totalCount=getResourceService(request).searchResourceCount(ResourceType.TYPE_BOOK,2,authorId);
//			for(int i=0;i<totalCount;i++){
//				resAll.add(new Object());
//			}
//			Navigator navi=new Navigator(resAll, currentPage, pageSize, 5);
//			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
//		}
//		List<Object> lsRess = new ArrayList<Object>();
//		int loop = 0;
//		String feeId = "";
//		List<ResourcePackReleation> rprs=getResourceService(request).searchResource(ResourceType.TYPE_BOOK, 2, authorId, currentPage, pageSize);
//		for (Iterator it = rprs.iterator(); it.hasNext();) {
//			loop++;
//			ResourcePackReleation rpr=(ResourcePackReleation)it.next();
//			ResourceAll resource=getResourceService(request).getResource(rpr.getResourceId());
//			// ƴURL
//			StringBuilder sb = new StringBuilder();
//			sb.append(request.getContextPath());
//			boolean isWhiteList = RequestUtil.isFeeDisabled();// �Ƿ��ǰ������û�
//			// ��ѯ�û��������� �˱��д��������Դ����ӼƷ�·��
//			boolean isBuy = getCustomService(request).isUserBuyBook(
//					RequestUtil.getMobile(), resource.getId());// �ж��û��Ƿ��ѹ���
//			
//			feeId = rpr.getFeeId();
//			if(feeId==null || "".equals(feeId)){
//				feeId=rpr.getPack().getFeeId();
//				if(feeId==null || "".equals(feeId)){
//					isFee=false;
//				}else{
//					isFee=true;
//				}
//			}
//			
//			if (isWhiteList || isBuy || !isFee) {// �������û������ѹ����û� ���Ʒ�
//				sb.append(ParameterConstants.PORTAL_PATH);
//				sb.append("?");
//			} else {
//				Fee fee = getCustomService(request).getFee(feeId);
//				isOut = fee.getIsout() == 1;// �Ƿ��ʷ���ʾ
//				if(!isOut){
//					sb.append("/");
//					sb.append(fee.getUrl());
//				}
//				sb.append(ParameterConstants.PORTAL_PATH);
//				sb.append("?");
//				if (isOut) {
//					//����ǰ��¶����û� �ղ�������ʾ
//					isMonth=getCustomService(request).isOrderMonth(RequestUtil.getMobile(), feeId);
//					if(!isMonth){
//						sb.append(ParameterConstants.COMMON_PAGE);
//						sb.append("=");
//						sb.append(ParameterConstants.COMMON_PAGE_FEE);
//						sb.append("&");
//						sb.append(ParameterConstants.TEMPLATE_ID);
//						sb.append("=");
//						sb.append(fee.getTemplateId());
//						sb.append("&");
//					}
//				}
//
//			}
//			sb.append(ParameterConstants.PAGE);
//			sb.append("=");
//			sb.append(ParameterConstants.PAGE_RESOURCE);
//			sb.append("&");
//			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
//			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
//			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
//			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
//			sb.append(ParameterConstants.FEE_BAG_ID);
//			sb.append("=");
//			sb.append(rpr.getPack().getId());
//			sb.append("&");
//			sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
//			sb.append("=");
//			sb.append(rpr.getId());
//			sb.append("&");
//			sb.append(ParameterConstants.RESOURCE_ID);
//			sb.append("=");
//			sb.append(resource.getId());
//			sb.append("&");
//			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
//			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
//			sb.append(ParameterConstants.PAGE_NUMBER);
//			sb.append("=");
//			sb.append("1");
//			if(isFee && !isMonth && !isBuy ){
//				sb.append("&");
//				sb.append(ParameterConstants.FEE_ID);
//				sb.append("=");
//				sb.append(feeId);
//			}
//
//			/** ���浥����¼ */
//			Map<String, String> obj = new HashMap<String, String>();
//			obj.put("url", sb.toString());
//			obj.put("title", resource.getName());
//			lsRess.add(obj);
//		}// for end
//		map.put("objs", lsRess);
//		result = VmInstance.getInstance().parseVM(map, this);
//		resultMap.put(TagUtil.makeTag(tagName), result);
//		return resultMap;
//	}
	
	private String getBookStatus(int status) {
		String typeName = "";
		for (Map.Entry<String, Integer> entry : Constants.getResourceFinished()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return "("+entry.getKey()+")";
		}
		return typeName;
	}
	private String imagePreview(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		StringBuilder sb = new StringBuilder();
		// �ж���Դ����(1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ)
		if (resource.getId().startsWith("1")) {// ͼ��
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						ebook.getId(), ebook.getBookPic(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + ebook.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("2")) {// ��ֽ
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						n.getId(), n.getImage(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + n.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("3")) {// ��־
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						magazine.getId(), magazine.getImage(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + magazine.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("4")) {// ����
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						comics.getId(), comics.getImage(),51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + comics.getName() + "\"/>");

				return sb.toString();
			}
		}
		return "";
	}
	
	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
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
	private FeeLogicService getFeeLogicService(HttpServletRequest request) {
		if (feeLogicService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			feeLogicService = (FeeLogicService) wac.getBean("feeLogicService");
		}
		return feeLogicService;
	}
}
