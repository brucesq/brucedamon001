package com.hunthawk.reader.pps.favorite;

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

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.custom.Favorites;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
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

/**
 * �ղ��б��ǩ 
 * ��ǩ���ƣ�favorite_list 
 * ����˵���� 
 * pageSize:ÿҳ��ʾ������ 
 * noPageLink:����ʾ��ҳ��ص�����
 * templateId:ɾ��ȷ��ҳģ��ID 
 * isConfirm:�Ƿ���ȷ��ɾ�� 1.ȷ����ʾ -1.ֱ��ɾ����ȷ�� 
 * showDelLink:�Ƿ���ʾɾ������
 * 
 * @author liuxh
 * 
 */
public class FavoriteListTag extends BaseTag {

	private CustomService customService;
	private BussinessService bussinessService;
	private ResourceService resourceService;
	private FeeLogicService feeLogicService;

	private static final int DEFAULT_PAGE_SIZE = 10; // Ĭ����ʾ10��
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
		
		boolean isConfirm=getIntParameter("isConfirm",1)>0;//�ж��Ƿ����ɾ��ȷ��  Ĭ��ȷ��
		boolean showDelLink=getIntParameter("showDelLink",1)>0;//�ж��Ƿ���ʾɾ������    Ĭ����ʾ
		this.noPageLink = getIntParameter("noPageLink", -1) < 0;
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		int pageSize = getIntParameter("pageSize", -1)<0?DEFAULT_PAGE_SIZE: getIntParameter("pageSize", -1);
		int currentPage =request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));// ��ǰҳ����Ĭ��Ϊ��һҳ

		// �õ��ֻ���
		String mobile=RequestUtil.getMobile();
		String productId=request.getParameter(ParameterConstants.PAGEGROUP_ID);
		int month_fee_bag_id =ParamUtil.getIntParameter(request, ParameterConstants.MONTH_FEE_BAG_ID, -1);
		int packId=ParamUtil.getIntParameter(request, ParameterConstants.FEE_BAG_ID, -1);
		List favorites = getCustomService(request).getUserFavoritesByPage(
				mobile, productId,pageSize, currentPage);
	
		//�ж��Ƿ񵼺�
		if(!isNoPageLink()){
			int totalCount=Integer.parseInt(getCustomService(request).getUserFavoritesResultCount(mobile, productId).toString());
//			int totalCount=getCustomService(request).getUserBookmarkResultCount(mobile, productId);
//			List resAll=getCustomService(request).getUserFavorites(mobile);
			Navigator navi=new Navigator(totalCount, currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}

		//ɾ��ģ��ID
		String templateId=getParameter("templateId", "");
		String msg="";
		// �Ѷ���浽list����
		List<Object> objs = new ArrayList<Object>();
		if (favorites == null || favorites.size()<1) {
			msg = "��Ŀǰû���ղ���Ƶ��";
		} else {
			msg="";
			// �����ӵ��б� ������Դ ������������ҳ
			for (Iterator it = favorites.iterator(); it.hasNext();) {
				Favorites fav = (Favorites) it.next();
				String contentId = fav.getContentId();// ����id
				int relId = fav.getPackRelationId();// ���۰�����id
				// ƴURL
				StringBuilder builder=new StringBuilder();
				builder.append(request.getContextPath());
				Map feeMap=null;
				ResourcePackReleation rel = null;
				if (relId!=0) {
					rel = getResourceService(request).getResourcePackReleation(relId);
				}
				if(rel!=null)
					feeMap=getFeeLogicService(request).isFee(productId, contentId, mobile, rel, rel.getPack().getId(), month_fee_bag_id);
				if(feeMap==null){
					builder.append(ParameterConstants.PORTAL_PATH);
					builder.append("?");
				}else{
					builder.append(feeMap.get("builder"));
				}
				builder.append(ParameterConstants.PAGE);
				builder.append("=");
				builder.append(ParameterConstants.PAGE_RESOURCE);
				builder.append("&");
				if(fav.getProductid()!=null && StringUtils.isNotEmpty(fav.getProductid())){
					builder.append(ParameterConstants.PRODUCT_ID);
					builder.append("=");
					builder.append(fav.getProductid());
					builder.append("&");
				}else{
					URLUtil.append(builder, ParameterConstants.PRODUCT_ID, request);
				}
				URLUtil.append(builder, ParameterConstants.PAGEGROUP_ID, request);
				URLUtil.append(builder, ParameterConstants.AREA_ID, request);
				if(fav.getColumnid()!=null && fav.getColumnid()>0){
					builder.append(ParameterConstants.COLUMN_ID);
					builder.append("=");
					builder.append(fav.getColumnid());
					builder.append("&");
				}else{
					URLUtil.append(builder, ParameterConstants.COLUMN_ID, request);
				}
				if(rel!=null){
					builder.append(ParameterConstants.FEE_BAG_ID);
					builder.append("=");
					builder.append(rel.getPack().getId());
					builder.append("&");
					builder.append(ParameterConstants.FEE_BAG_RELATION_ID);
					builder.append("=");
					builder.append(rel.getId());
					builder.append("&");
				}
				builder.append(ParameterConstants.RESOURCE_ID);
				builder.append("=");
				builder.append(contentId);
				builder.append("&");
				URLUtil.append(builder, ParameterConstants.CHANNEL_ID, request);
				URLUtil.append(builder, ParameterConstants.UNICOM_PT, request);
				builder.append(ParameterConstants.PAGE_NUMBER);
				builder.append("=");
				builder.append(1);
				if (feeMap != null) {
					builder.append("&");
					builder.append(ParameterConstants.FEE_ID);
					builder.append("=");
					builder.append(feeMap.get("feeId"));
				}
				String url=builder.toString();
//				String url = getPageUrl(request,contentId,relId);// ��ת���ص�URL
				ResourceAll resource = getResourceService(request).getResource(contentId);

				// ����obj
				Map<String, Object> obj = new HashMap<String, Object>();
				if(resource!=null){
					String tempTitle = resource.getName();// ͼ������
					obj.put("url", url);
					obj.put("value", tempTitle);
					String delurl=delUrl(request,isConfirm,"1",templateId,contentId);
					obj.put("urldel", delurl);
					obj.put("valuedel", "[ɾ]");
					obj.put("resource", resource);//�������Դ�����ȥ��Ϊ��ǩģ���ʹ��
					String imgUrl =CoverPreview.getPreview(getResourceService(request),resource,51);//��Ԥ��ͼ�Ž�ȥ
					obj.put("preview", imgUrl);
					obj.put("author", getAuthorName(resource, request));
					
					// ����objs
					objs.add(obj);
				}
			}
			map.put("objs", objs);
			map.put("showdel", showDelLink);
		}
		map.put("msg", msg);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service",getResourceService(request));
		//����ֻ���
		map.put("mobile", RequestUtil.getMobile());
		//����ֻ�����
		map.put("mobileType", RequestUtil.getMobileType());
		
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		/*result = VmInstance.getInstance().parseVM(map, this);
    	resultMap.put(TagUtil.makeTag(tagName),result);*/
    	
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

	/**
	 * 
	 */
	/**
	 * ҳ�漶�����+��ƷID+ҳ����ID+����ID+��ĿID+���۰�ID+���۰�����ID+��ԴID+�½�ID+�ƹ�����ID
	 * +��ͨPT����+ҳ��+ÿҳ��ʾ����+�Ʒ�ID
	 * 
	 * @param request
	 * @param currentPage
	 *            ��ǰҳ ����URL
	 * @param templateid
	 *            ɾ�� ����Դ URL
	 * @param contentid
	 *            ɾ������Դ�����贫�������ID����
	 * @return
	 */
	private String delUrl(HttpServletRequest request, boolean isConfirm,
			String currentPage, String templateid, String contentid) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		if (!isConfirm) {// ����ɾ��ȷ��
			sb.append(ParameterConstants.COMMON_PAGE);
			sb.append("=");
			sb.append(ParameterConstants.COMMON_PAGE_FAVORITE_DEL);
			sb.append("&");
		}
		
		sb.append(ParameterConstants.TEMPLATE_ID);
		sb.append("=");
		sb.append(templateid);
		sb.append("&");
		String url = request.getQueryString();
		sb.append(URLUtil.removeParameter(
				url.substring((url.indexOf("?") + 1)),
				ParameterConstants.TEMPLATE_ID));
		sb.append("&");
		sb.append(ParameterConstants.RESOURCE_ID);
		sb.append("=");
		sb.append(contentid);
		

		return sb.toString();
	}

	private String getAuthorName(ResourceAll resource,
			HttpServletRequest request) {
		String name = "";
		Integer[] authorids = resource.getAuthorIds();
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < authorids.length; i++) {
			ResourceAuthor author = getResourceService(request)
					.getResourceAuthor(authorids[i]);
			str.append((author.getPenName() == null || "".equals(author
					.getPenName())) ? author.getName() : author.getPenName());
			str.append(",");
			if (i == authorids.length - 1) {
				// ȥ�����һ��,
				str.replace(str.lastIndexOf(","), str.length(), "");
			}
		}
		try {
			name = StrUtil.getLimitStr(str.toString().trim(),
					ParameterConstants.AUTHOR_NAME_BYTES,
					ParameterConstants.REPLACE_SYMBOL);
		} catch (Exception e) {

			e.printStackTrace();
		}// ����
		return name;
	}
//	private String getPageUrl(HttpServletRequest request,
//			String contentid,int relId) {
//		ResourcePackReleation rpr=getResourceService(request).getResourcePackReleation(relId);
//		if(rpr==null){
//			return request.getContextPath()+ParameterConstants.PORTAL_PATH+"?"+request.getQueryString();
//		}
//		ResourcePack rp=rpr.getPack();
//		String feeId=rpr.getFeeId();
//		boolean isWhite=RequestUtil.isFeeDisabled();
//		boolean isOrder=getCustomService(request).isUserBuyBook(RequestUtil.getMobile(), contentid);
//		if(feeId==null || "".equals(feeId)){
//			feeId=rp.getFeeId();
//			isOrder=getCustomService(request).isOrderMonth(RequestUtil.getMobile(), feeId);
//		}
//		Fee fee=getCustomService(request).getFee(feeId);
//		//ƴURL
//		StringBuilder sb = new StringBuilder();
//		sb.append(request.getContextPath());
//		if(fee!=null){
//			if(isWhite  || isOrder){//�ǰ������û������Ѿ�����
//				sb.append(ParameterConstants.PORTAL_PATH);
//				sb.append("?");
//			}else{
//				if(fee.getIsout()==0){//�����Ʒ�ҳ
//					sb.append(fee.getUrl());
//					sb.append("/");
//					sb.append(ParameterConstants.PORTAL_PATH);
//					sb.append("?");
//				}else{
//					sb.append(ParameterConstants.PORTAL_PATH);
//					sb.append("?");
//					sb.append(ParameterConstants.COMMON_PAGE);
//					sb.append("=");
//					sb.append(ParameterConstants.COMMON_PAGE_FEE);
//					sb.append("&");
//					sb.append(ParameterConstants.TEMPLATE_ID);
//					sb.append("=");
//					sb.append(fee.getTemplateId());
//					sb.append("&");
//				}
//				
//			}
//		}else{
//			sb.append(ParameterConstants.PORTAL_PATH);
//			sb.append("?");
//		}
//		sb.append(ParameterConstants.PAGE);
//		sb.append("=");
//		sb.append(ParameterConstants.PAGE_RESOURCE);
//		sb.append("&");
//		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
//		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
//		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
//		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
//		sb.append(ParameterConstants.FEE_BAG_ID);
//		sb.append("=");
//		sb.append(rpr.getPack().getId());
//		sb.append("&");
//		sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
//		sb.append("=");
//		sb.append(relId);
//		sb.append("&");
//		sb.append(ParameterConstants.RESOURCE_ID);
//		sb.append("=");
//		sb.append(contentid);
//		sb.append("&");
//		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
//		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
//		sb.append(ParameterConstants.PAGE_NUMBER);
//		sb.append("=");
//		sb.append(1);
//		if(StringUtils.isNotEmpty(feeId)){
//			sb.append("&");
//			sb.append(ParameterConstants.FEE_ID);
//			sb.append("=");
//			sb.append(feeId);
//		}
//		
//		return sb.toString();
//	}

}
