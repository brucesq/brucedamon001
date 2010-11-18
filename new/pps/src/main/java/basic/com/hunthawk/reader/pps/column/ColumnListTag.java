package com.hunthawk.reader.pps.column;

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
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
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
 * 栏目列表标签   非iphone版
 * 标签名称：column_list
 * 参数说明：
 * 		columnId:栏目ID
 * 		pageSize:每页显示的条数
 * 		noPageLink:不显示翻页相关的链接
 * 		listCount:列表范围 (用于控制显示总数量)  add by liuxh 2009-09-23
 * @author liuxh
 *
 */
public class ColumnListTag extends BaseTag {

	private  BussinessService bussinessService;
	private ResourceService resourceService;
	private CustomService customService;
	private static final int DEFAULT_PAGE_SIZE = 10; // 默认显示10条
	/** 不显示翻页相关的链接 */
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
					TagLogger.debug(tagName, "页面组ID为空", request.getQueryString(), null);
					return new HashMap();
				}else{//列当前页面组下的栏目
					columns=getBussinessService(request).getColumnsByPageGroupId(Integer.parseInt(pageGroupId), currentPage, pageSize);
					totalCount=getBussinessService(request).getColumnsByPageGroupId(Integer.parseInt(pageGroupId), 1, 1000).size();
				}
			}
		}
		//排序规则  0按照排序ID降序、1按照ID降序、2按照点击数降序、5按照排序ID升序、6按照ID升序
		int order=0;//默认按排序ID降序
		if(StringUtils.isNotEmpty(columnId)){//列指定/默认栏目的子栏目或是资源
			Columns col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
			if(col==null){
				TagLogger.debug(tagName, "id为"+columnId+"的栏目不存在!", request.getQueryString(), null);
				return new HashMap();
			}else{
				order=col.getOrderType();
				packId=col.getPricepackId();
				//根据批价包ID判断是否有子栏目
				if(packId!=0){//没有子栏目--资源列表
					flag="resource";
//					rprs =getResourceService(request).getResourcePackReleations(packId, currentPage, pageSize);
//					totalCount=getResourceService(request).getResourcePackReleations(packId, 1, 10000).size();
					rprs =getResourceService(request).getResourcePackReleationsByOrder(packId, currentPage, pageSize,order,listCount,-1);
					totalCount=getResourceService(request).getResourcePackReleationsByOrderCount(packId,order,listCount,-1);
				}else{//可能有子栏目--栏目列表
					columns=getBussinessService(request).getColumnChilds(Integer.parseInt(columnId),currentPage,pageSize,order);
					totalCount=getBussinessService(request).getColumnChilds(Integer.parseInt(columnId),1,1000,order).size();
				}
			}
		}
		System.out.println("记录总数="+totalCount+"; 列表范围="+listCount);
		//判断是否导航
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
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		if(rprs!=null){//资源列表
			if(rprs.size()<1){
				TagLogger.debug(tagName, "无资源信息", request.getQueryString(), null);
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
					/** 保存单条记录*/
					Map<String, String> obj = new HashMap<String, String>();
					obj.put("url", url);
					obj.put("linkname",linkname);
					obj.put("status",  getBookStatus(resource.getIsFinished()));//状态
					obj.put("preview", imagePreview(request,tagName,resource));//资源预览图
					Integer [] authorids=resource.getAuthorIds();
					StringBuilder str=new StringBuilder();
					for(int i=0;i<authorids.length;i++){
						ResourceAuthor author=getResourceService(request).getResourceAuthor(authorids[i]);
						str.append((author.getPenName()==null || "".equals(author.getPenName()))?author.getName():author.getPenName());
						str.append(",");
						if(i==authorids.length-1){
							//去掉最后一个,
							str.replace(str.lastIndexOf(","), str.length(), "");
						}
					}
					obj.put("author", str.toString().trim());//作者
					obj.put("downnum", String.valueOf(getResourceService(request).getResourceVisits(resource.getId())));//点击
					obj.put("favnum", String.valueOf(getCustomService(request).getResourceFavoritesCount(resource.getId())));//收藏
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
//				result = VmInstance.getInstance().parseVM(map, this);
				resultMap.put(TagUtil.makeTag(tagName), result);
				return resultMap;
			}
		}else{//栏目列表
			if(columns==null || columns.size()<1){
				TagLogger.debug(tagName, "无栏目信息", request.getQueryString(), null);
				return new HashMap();
			}else{
				for(Iterator it=columns.iterator();it.hasNext();){
					Columns c=(Columns)it.next();
					String title=c.getName();
					Long count=0L;
					if(c.getPricepackId()!=null){
						count = getResourceService(request).getResourcePackReleationsCount(c.getPricepackId());
					}
					
					String preview="";
					Columns column=getBussinessService(request).getColumns(c.getId());
					if(column!=null){
						String icon_id=column.getIcon();
						if(icon_id==null || StringUtils.isEmpty(icon_id)){
							//return new HashMap();
						}else{
							String url = getBussinessService(request).getVariables("media_url").getValue();
							Material mater =  getBussinessService(request).getMaterial(Integer.parseInt(icon_id));
							String imgName=mater.getFilename()+"." + mater.getExtName();
							String imgUrl="";
							if (imgName.toLowerCase().matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
								imgUrl=url+imgName;
							}else{
								//TagLogger.debug(tagName,"图片格式错误", request.getQueryString(), null);
								//return new HashMap();
							}
							preview=imgUrl;
						}
					}else{
						//System.out.println("column==================null");
					}
					
//					Variables var = getBussinessService(request).getVariables("sorticon_url");
//					String preview=var.getValue();
//					int columnType=c.getColumnType();//0 普通 1 分类 2 搜索 
//					if(columnType==0 || columnType==2){
//						//默认预览图地址
//						preview+="0.png";
//					}else{
//						int resourceTypeId=c.getResourceTypeId();
//						preview+=resourceTypeId+".png";
//					}
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
					
					/** 保存单条记录 */
					Map<String, String> obj = new HashMap<String, String>();
					obj.put("url", sb.toString());
					obj.put("title",title);
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
//				result = VmInstance.getInstance().parseVM(map, this);
				resultMap.put(TagUtil.makeTag(tagName), result);
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
	 * 预览图
	 * 
	 * @param tag
	 * @param resource
	 * @return
	 * @throws ProcessingException
	 */
	private String imagePreview(HttpServletRequest request, String tagName,
			ResourceAll resource) {
		StringBuilder sb = new StringBuilder();
		// 判断资源类型(1图书，2报纸，3杂志，4漫画，5铃声，6视频)
		if (resource.getId().startsWith("1")) {// 图书
			Ebook ebook = (Ebook) resource;
			if (ebook.getBookPic().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						ebook.getId(), ebook.getBookPic());
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" alt=\"" + ebook.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("2")) {// 报纸
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						n.getId(), n.getImage());
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\"  alt=\"" + n.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("3")) {// 杂志
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						magazine.getId(), magazine.getImage());
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\"  alt=\"" + magazine.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("4")) {// 漫画
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						comics.getId(), comics.getImage());
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\"  alt=\"" + comics.getName() + "\"/>");

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
}
