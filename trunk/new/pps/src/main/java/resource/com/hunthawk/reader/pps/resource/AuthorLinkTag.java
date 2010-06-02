package com.hunthawk.reader.pps.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterCommonTextLink;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * 作者链接标签 针对一本书有多个作者 所以应该是一个循环列表
 * 标签名称：resource_author_link
 * 参数说明：
 * 		columnId:用于获取批价包ID
 * 		title:文字    add by liuxh 2009-9-3
 * 		split:分割符 <br/>
 * 		templateId:查询结果列表模板ID
 * 		number：在第几页之前出现(包含当前页)  add by  liuxh  2009-9-2
 * @author liuxh
 *
 */
public class AuthorLinkTag extends BaseTag {

	private ResourceService resourceService;
	private BussinessService bussinessService;
	private int number;
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	private String title;
	private String split;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSplit() {
		return split;
	}
	public void setSplit(String split) {
		this.split = split;
	}
	private int currentPage;
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		this.split=getParameter("split","");
		this.title=getParameter("title","").trim();
		String n=getParameter("number","0");
		String columnId=getParameter("columnId","");
		String pid=request.getParameter(ParameterConstants.FEE_BAG_ID);//批价包ID  
		Columns col=null;
		if(StringUtils.isNotEmpty(columnId)){
			col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
			if(col!=null){
				if(col.getPricepackId()!=null)
					if(col.getPricepackId()!=0)
						pid=col.getPricepackId().toString();
			}
		}
		try{
			Integer.parseInt(n);
		}catch(Exception ex){
			TagLogger.debug(tagName, "number属性值不是有效的属性值", request.getQueryString(), null);
			return new HashMap();
		}
		this.number=Integer.parseInt(n);
		this.currentPage=Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER)==null?"1":request.getParameter(ParameterConstants.PAGE_NUMBER));
		String resID=request.getParameter(ParameterConstants.RESOURCE_ID);
		ResourceAll resource=getResourceService(request).getResource(resID);
		
		List<Object> lsRess = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result="";
		//资源对应的作者可能是多个
		Integer[] authors=resource.getAuthorIds();
		for(int i=0;i<authors.length;i++){
			ResourceAuthor author=getResourceService(request).getResourceAuthor(authors[i]);
			if(author == null)
				continue;
			Map<String, Object> obj = new HashMap<String, Object>();
			String authorName=(author.getPenName()==null || "".equals(author.getPenName()))?author.getName():author.getPenName();
			//点击链接到此作者相关的图书列表
			int tempID=getIntParameter("templateId", -1);
			//System.out.println("作者关联作品templateId-->"+tempID);
			StringBuilder sb=new StringBuilder();
			sb.append(request.getContextPath());
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			URLUtil.append(sb, ParameterConstants.PAGE, request);
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			sb.append(ParameterConstants.FEE_BAG_ID);
			sb.append("=");
			sb.append(pid);
			sb.append("&");
			//URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
			URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
			if(tempID<0){
				TagLogger.debug(tagName, "标签模板ID为空", request.getQueryString(), null);
				return new HashMap();
			}else{
//				sb.append("&");
				sb.append(ParameterConstants.TEMPLATE_ID);
				sb.append("=");
				sb.append(tempID);
			}
			sb.append("&");
			sb.append(ParameterConstants.AUTHOR_ID);
			sb.append("=");
			sb.append(author.getId());
			String url=sb.toString();
			obj.put("url", url);
			obj.put("authorName", authorName.trim());
			obj.put("author", author);
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("authorName", getAuthorName(resource, request));
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		/*result =  VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);*/
		return resultMap;
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
			bussinessService = (BussinessService) wac.getBean("bussinessService");
		}
		return bussinessService;
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
				// 去掉最后一个,
				str.replace(str.lastIndexOf(","), str.length(), "");
			}
		}
		try {
			name = StrUtil.getLimitStr(str.toString().trim(),
					ParameterConstants.AUTHOR_NAME_BYTES,
					ParameterConstants.REPLACE_SYMBOL);
		} catch (Exception e) {

			e.printStackTrace();
		}// 作者
		return name;
	}
}
