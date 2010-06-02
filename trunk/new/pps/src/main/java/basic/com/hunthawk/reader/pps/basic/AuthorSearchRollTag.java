package com.hunthawk.reader.pps.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aspire.gentox.rollrule.IRollRule;
import com.aspire.gentox.rollrule.RandomRollRule;
import com.aspire.gentox.rollrule.RollRuleContext;
import com.aspire.gentox.rollrule.RollRuleManager;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.reader.pps.service.ResourceService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
/**
 * 作者排行搜索标签（列出搜索前多少的 作者，然后轮询显示几条）
 * 标签名称:AuthorSearch_Roll
 * 参数说明：
 * 		isRoll  是否轮询：（-1 否，1 是）			
 *  	rollCount 轮询显示条数（系统默认5条）
 * 		authors:作者列表 热词1-热词2-...按照这个规则来
 * 		columnId:用于获取批价包ID
 * 		templateId:查询结果列表模板ID
 * 		//number：在第几页之前出现(包含当前页) 
 * @author yuzs
 *	2009-11-10
 */
public class AuthorSearchRollTag extends BaseTag{
	private BussinessService bussinessService;
	private ResourceService resourceService;
	private InteractiveService interactiveService;
	/** 显示的轮循条数 */
	private int rollCount;
	/*private int number;
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	private int currentPage;
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}*/
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		String n=getParameter("number","0");
		int isRoll  = getIntParameter("isRoll",-1);
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
		//this.number=Integer.parseInt(n);
		//this.currentPage=Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER)==null?"1":request.getParameter(ParameterConstants.PAGE_NUMBER));
		//String resID=request.getParameter(ParameterConstants.RESOURCE_ID);
		//ResourceAll resource=getResourceService(request).getResource(resID);
		
		List<Object> lsRess = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result="";
	
		
		String authors = getParameter("authors","");
		String[] authorsMums= new String[]{};
		if(authors==null || StringUtils.isEmpty(authors)){
			TagLogger.debug(tagName, "未添加参与轮循的作者",request.getQueryString(), null);
			return new HashMap();
		}else{
			authorsMums=authors.split("-");
		}
		List<String> authorsList = new ArrayList<String>();
		if(authorsMums.length>0){
			for(int i=0;i<authorsMums.length;i++){
				authorsList.add(i,authorsMums[i]);
			}
		}
		rollCount = getIntParameter("rollCount", -1);
		if (rollCount < 1) {
			rollCount = 5;
		}
		List list = null;
		
		if(isRoll==1){
			//轮询
			
			if(rollCount>authorsMums.length)//如果每次轮询的个数大于总个数
				list = authorsList;
			else{
				String ruleId = "statDatas_"+ rollCount+ "_"
				+ authorsMums.length + "_1x_" + 2;
				RollRuleManager rrm = RollRuleManager.getInstance();
				IRollRule rollRule  = new RandomRollRule(ruleId);
				rrm.addRollRule(rollRule);
				rollRule = rrm.getRollRule(ruleId);
				RollRuleContext rrc = new RollRuleContext(Long.valueOf(authorsMums.length),
						-1, Integer.valueOf(rollCount));
				
				long[] indexes = rollRule.obtainIndexes(rrc);
				list = getIndexResourceBagRelations(indexes, authorsList);
			}
		}else{//不轮询
			
			list = authorsList;
		}
	
		
		for(int i=0;i<list.size();i++){
			
			ResourceAuthor author=getResourceService(request).getResourceAuthor(Integer.parseInt((String)list.get(i)));
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
			//URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
			sb.append( ParameterConstants.COLUMN_ID);
			sb.append("=");
			sb.append(columnId);
			sb.append("&");
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
				sb.append("&");
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
			obj.put("title", authorName.trim());
			obj.put("author", author);
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
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
	
	private InteractiveService getInteractiveService(HttpServletRequest request) {
		if (interactiveService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService) wac
					.getBean("interactiveService");
		}
		return interactiveService;
	}

	public int getRollCount() {
		return rollCount;
	}

	public void setRollCount(int rollCount) {
		this.rollCount = rollCount;
	}

	private List getIndexResourceBagRelations(long[] indexes, List list) {
		List result = new ArrayList();
		for (int i = 0; i < indexes.length; i++) {
			result.add(list.get(Integer.parseInt(String.valueOf(indexes[i]))));
		}
		return result;
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
