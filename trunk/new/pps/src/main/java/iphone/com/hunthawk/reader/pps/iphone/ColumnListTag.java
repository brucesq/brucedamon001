package com.hunthawk.reader.pps.iphone;

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

import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.pps.CoverPreview;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.reader.tag.util.Navigator;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * ��Ŀ�б��ǩ
 * ��ǩ���ƣ�icolumn_list
 * ����˵����
 * 		columnId:��ĿID
 * 		pageSize:ÿҳ��ʾ������
 * 		noPageLink:����ʾ��ҳ��ص�����
 * 		listCount:�б�Χ (���ڿ�����ʾ������)  add by liuxh 2009-09-23
 * @author liuxh
 *
 */
public class ColumnListTag extends BaseTag {

	private  BussinessService bussinessService;
	private ResourceService resourceService;
	private CustomService customService;
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
		String flag="column";
		Columns col=null;
		int listCount=getIntParameter("listCount",-1);
		int currentPage=Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER)==null?"1":request.getParameter(ParameterConstants.PAGE_NUMBER));
		int pageSize=getIntParameter("pageSize",DEFAULT_PAGE_SIZE);
		String columnId=getParameter("columnId","");
		String pageGroupId="";
		int packId=0;
		int totalCount=0;
		List<Columns> columns=null;
		List<ResourcePackReleation> rprs=null;
		if(StringUtils.isEmpty(columnId)){
			columnId=request.getParameter(ParameterConstants.COLUMN_ID);
			if(columnId==null || StringUtils.isEmpty(columnId)){
				pageGroupId=request.getParameter(ParameterConstants.PAGEGROUP_ID);
				if(StringUtils.isEmpty(pageGroupId) || pageGroupId==null){
					TagLogger.debug(tagName, "ҳ����IDΪ��", request.getQueryString(), null);
					return new HashMap();
				}else{//�е�ǰҳ�����µ���Ŀ
					columns=getBussinessService(request).getColumnsByPageGroupId(Integer.parseInt(pageGroupId), currentPage, pageSize);
					totalCount=getBussinessService(request).getColumnsByPageGroupId(Integer.parseInt(pageGroupId), 1, 1000).size();
				}
			}
		}
		//�������  0��������ID����1����ID����2���յ��������5��������ID����6����ID����
		int order=0;//Ĭ�ϰ�����ID����
		if(columnId!=null || StringUtils.isNotEmpty(columnId)){//��ָ��/Ĭ����Ŀ������Ŀ������Դ
			col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
			if(col==null){
				TagLogger.debug(tagName, "idΪ"+columnId+"����Ŀ������!", request.getQueryString(), null);
				return new HashMap();
			}else{
				packId=col.getPricepackId();
				order=col.getOrderType();
				//�������۰�ID�ж��Ƿ�������Ŀ
				if(packId!=0){//û������Ŀ
					flag="resource";
	//				rprs =getResourceService(request).getResourcePackReleations(packId, currentPage, pageSize);
	//				totalCount=getResourceService(request).getResourcePackReleations(packId, 1, 10000).size();
					rprs =getResourceService(request).getResourcePackReleationsByOrder(packId, currentPage, pageSize,order,listCount,-1);
					totalCount=getResourceService(request).getResourcePackReleationsByOrderCount(packId,order,listCount,-1);
				}else{//����������Ŀ
					columns=getBussinessService(request).getColumnChilds(Integer.parseInt(columnId),currentPage,pageSize,order);
					totalCount=getBussinessService(request).getColumnChilds(Integer.parseInt(columnId),1,1000,order).size();
				}
			}
		}
		System.out.println("��¼����="+totalCount+"; �б�Χ="+listCount);
		//�ж��Ƿ񵼺�
		if(!isNoPageLink()){
//			List resAll=new ArrayList();
//			for(int i=0;i<(listCount<0 || listCount>totalCount?totalCount:listCount);i++){
//				resAll.add(i);
//			}
			Navigator navi=new Navigator((listCount<0 || listCount>totalCount?totalCount:listCount), currentPage, pageSize, 5);
			request.setAttribute(ParameterConstants.PAMS_NAVIGATOR,navi);
		}
		Map resultMap = new HashMap();
		String result = "";
		/** ����б���Դ */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		if(rprs!=null){//��Դ�б�
			if(rprs.size()<1){
				TagLogger.debug(tagName, "����Դ��Ϣ", request.getQueryString(), null);
				return new HashMap();
			}else{
				for(Iterator it=rprs.iterator();it.hasNext();){
					ResourcePackReleation resourceBagRelation = (ResourcePackReleation) it.next();
					ResourceAll resource = resourceService.getResource(resourceBagRelation.getResourceId());
					if(resource == null){
						continue;
					}
					String linkname=resource.getName();
					
					StringBuilder sb = new StringBuilder();
					sb.append(request.getContextPath());
					sb.append(ParameterConstants.PORTAL_PATH);
					sb.append("?");
					sb.append(ParameterConstants.PAGE);
					sb.append("=");
					sb.append(ParameterConstants.PAGE_RESOURCE);
					sb.append("&");
					URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
					URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
					URLUtil.append(sb, ParameterConstants.AREA_ID, request);
					sb.append(ParameterConstants.COLUMN_ID);
					sb.append("=");
					sb.append(columnId);
					sb.append("&");
					sb.append(ParameterConstants.FEE_BAG_ID);
					sb.append("=");
					sb.append(packId);
					sb.append("&");
					sb.append(ParameterConstants.FEE_BAG_RELATION_ID);
					sb.append("=");
					sb.append(resourceBagRelation.getId());
					sb.append("&");
					sb.append(ParameterConstants.RESOURCE_ID);
					sb.append("=");
					sb.append(resource.getId());
					sb.append("&");
					URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
					URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
					sb.append(ParameterConstants.PAGE_NUMBER);
					sb.append("=");
					sb.append(1);
					
					String url = URLUtil.trimUrl(sb).toString();
					/** ���浥����¼*/
					Map<String, Object> obj = new HashMap<String, Object>();
					obj.put("url", url);
					try {
						obj.put("linkname",StrUtil.getLimitStr(linkname, ParameterConstants.RESOURCE_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					obj.put("status",  getBookStatus(resource.getIsFinished()));//״̬
					obj.put("preview", imagePreview(request,tagName,resource));//��ԴԤ��ͼ
					Integer [] authorids=resource.getAuthorIds();
					StringBuilder str=new StringBuilder();
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
						obj.put("author", StrUtil.getLimitStr(authorPenName, ParameterConstants.AUTHOR_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//����
					obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//���
					obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//�ղ�
					obj.put("resource", resource);
					lsRess.add(obj);
				}//for end
				map.put("objs", lsRess);
				map.put("flag", flag);
				map.put("strUtil", new StrUtil());
				map.put("previewUtil", new CoverPreview());
				map.put("service",getResourceService(request));
				map.put("more", getColumnsMore(request, columnId));
				map.put("cname", col.getName());
				
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
		}else{//��Ŀ�б�
			if(columns==null || columns.size()<1){
				TagLogger.debug(tagName, "����Ŀ��Ϣ", request.getQueryString(), null);
				return new HashMap();
			}else{
				for(Iterator it=columns.iterator();it.hasNext();){
					Columns c=(Columns)it.next();
					String title=c.getName();
					Long count=0L;
					if(c.getPricepackId()!=null){
						count = getResourceService(request).getResourcePackReleationsCount(c.getPricepackId());
					}
					Variables var = getBussinessService(request).getVariables("sorticon_url");
					String preview=var.getValue();
					int columnType=c.getColumnType();//0 ��ͨ 1 ���� 2 ���� 
					if(columnType==0 || columnType==2){
						//Ĭ��Ԥ��ͼ��ַ
						preview+="0.png";
					}else{
						int resourceTypeId=c.getResourceTypeId();
						preview+=resourceTypeId+".png";
					}
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
					sb.append(c.getId());
					sb.append("&");
					URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
					URLUtil.trimURL(sb);
					
					/** ���浥����¼ */
					Map<String, String> obj = new HashMap<String, String>();
					obj.put("url", sb.toString());
					try {
						obj.put("title",StrUtil.getLimitStr(title, ParameterConstants.COLUMN_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//��Ŀ����
					obj.put("preview", preview);
					if (count > 0){
						obj.put("count", "("+count+")");
					}
					lsRess.add(obj);
				}//for end		
				map.put("objs", lsRess);
				map.put("flag", flag);
				
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
		}
	}
	private String getBookStatus(int status) {
		String typeName = "";
		for (Map.Entry<String, Integer> entry : Constants.getResourceFinished()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return "("+entry.getKey()+")";
		}
		return typeName;
	}
	/**
	 * Ԥ��ͼ
	 * 
	 * @param tag
	 * @param resource
	 * @return
	 * @throws ProcessingException
	 */
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
						"\" height=\"64\"  width=\"48\"  alt=\"" + comics.getName() + "\"/>");

				return sb.toString();
			}
		}
		return "";
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
	
	private String getColumnsMore(HttpServletRequest request,String columnId){
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
		URLUtil.trimURL(sb);
		return sb.toString();
	}
}
