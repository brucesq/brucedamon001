package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapterDesc;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapterDesc;
import com.hunthawk.reader.domain.resource.NewsPapersChapterDesc;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.util.ParamUtil;
/**
 * 标题标签
 * 标签名称：title
 * 参数说明：
 * 		name:指定标题的名称
 * @author liuxh
 *
 */
public class TitleTag extends BaseTag {

	private CustomService customService;
	private BussinessService bussinessService;
	private ResourceService resourceService;
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		
		String flag=request.getParameter(ParameterConstants.PAGE);
		String title=getParameter("name","");
		if(StringUtils.isEmpty(title)){
			if(flag.equals(ParameterConstants.PAGE_PRODUCT)){//产品页
				String productId=request.getParameter(ParameterConstants.PAGEGROUP_ID);
				PageGroup pro=null;
				if(productId!=null && !"".equals(productId)){
					pro=getBussinessService(request).getPageGroup(Integer.parseInt(productId));
					title=pro.getPkName();
				}else{
					title="产品页";
				}
			}else if(flag.equals(ParameterConstants.PAGE_COLUMN)){//栏目页
				String columnId=request.getParameter(ParameterConstants.COLUMN_ID);
				Columns col=null;
				if(columnId!=null && !"".equals(columnId)){
					col=getBussinessService(request).getColumns(Integer.parseInt(columnId));
					title=col.getName();
				}else{
					title="栏目页";
				}
			}else if(flag.equals(ParameterConstants.PAGE_DETAIL)){//详情页
				title="详情页";
				/**取章节名称*/
				String chapterId=request.getParameter(ParameterConstants.CHAPTER_ID);
				if(!StringUtils.isEmpty(chapterId)){
					if(chapterId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){
						EbookChapterDesc bChapter=getResourceService(request).getEbookChapterDesc(chapterId);
						if(bChapter!=null)
							title=bChapter.getName();
					}else if(chapterId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){
						ComicsChapter cChapter=getResourceService(request).getComicsChapterById(chapterId);
						if(cChapter!=null)
							title=cChapter.getName();
					}else if(chapterId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
						MagazineChapterDesc mChapter=getResourceService(request).getMagazineChapterDescById(chapterId);
						if(mChapter!=null){
							EbookTome tome=getResourceService(request).getEbookTomeById(mChapter.getTomeId());
							if(tome!=null)
								title=tome.getName();
						}
					}else if(chapterId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){
						NewsPapersChapterDesc nChapter=getResourceService(request).getNewsPapersChapterDescById(chapterId);
						if(nChapter!=null)
							title=nChapter.getName();
					}
				}else{
					ResourceAll res=getResourceService(request).getResource(URLUtil.getResourceId(request));
					if(res!=null ){
						title=res.getName();
					}
				}
			}else if(flag.equals(ParameterConstants.PAGE_RESOURCE)){//资源页
				ResourceAll res=getResourceService(request).getResource(URLUtil.getResourceId(request));
				if(res!=null ){
					title=res.getName();
				}else{
					title="资源页";
				}
				
			}else{
				title="消息页";
			}
		}
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), title);
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
}
