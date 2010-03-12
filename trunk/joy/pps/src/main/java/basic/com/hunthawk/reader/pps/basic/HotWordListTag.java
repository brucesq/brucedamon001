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

import com.aspire.gentox.rollrule.DefaultRollRule;
import com.aspire.gentox.rollrule.IRollRule;
import com.aspire.gentox.rollrule.IntervalRandomRollRule;
import com.aspire.gentox.rollrule.IntevalPageRollRule;
import com.aspire.gentox.rollrule.PageRollRule;
import com.aspire.gentox.rollrule.PushRollRule;
import com.aspire.gentox.rollrule.RandomRollRule;
import com.aspire.gentox.rollrule.RollRuleContext;
import com.aspire.gentox.rollrule.RollRuleManager;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.statistics.StatData;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.StrUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
/**
 * 热词列表标签
 * 标签名称:hotWord_list
 * 参数说明：
 * 		showType 显示类型：11 书名，12 作者，13 关键字（默认 书名）
 * 		timeType 时间类型：0 总排行，3 月排行，2 周排行，1 日排行，
 * 		restype:  参见ResourceType中定义(property=2)  用于确定查询目标(即操作哪张数据库表)
 * 					1图书，2报纸，3杂志，4漫画，5铃声，6视频
 * 		isRoll  是否轮询：（-1 否，1 是）			
 *  		totalCount  轮询总条数（排行前多少位）
 *  	rollCount 轮询显示条数（系统默认5条）
 * 		columnId:	栏目ID 用于列检索出的资源列表(property=2)

 * 			
 * @author yuzs
 *
 */
public class HotWordListTag extends BaseTag {
	private BussinessService bussinessService;
	private InteractiveService interactiveService;
	private static final int DEFAULT_SIZE=10;
	/** 最大轮循范围 */
	private static final int MAXNUMBER_RANGE = 200;

	/** 默认滚动显示条数 */
	private static final int DEFAULT_ROLL_COUNT = 5;
	/** 显示的轮循条数 */
	private int rollCount;
	
	/** 参与轮循总条数 */
	private int totalCount;
	
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		
		
		int columnId=getIntParameter("columnId",-1);
		
		Columns column = getBussinessService(request).getColumns(columnId); // 栏目
		// 如果得不到栏目 则直接返回
		if (column == null) {
			TagLogger.debug(tagName, "获取栏目信息失败", request.getQueryString(), null);
			return new HashMap();
		}
		int showType = getIntParameter("showType",0);
		int timeType = getIntParameter("timeType",0);
		int restype=getIntParameter("restype",1);//默认是图书  
		int searchby=-1;
		int isRoll  = getIntParameter("isRoll",-1);
		int totalCount = getIntParameter("totalCount",50);//默认轮询的总条数
		
		int currentPage=request.getParameter(ParameterConstants.PAGE_NUMBER)==null?1:Integer.parseInt(request.getParameter(ParameterConstants.PAGE_NUMBER));
//		int pageSize=getIntParameter("pageSize",DEFAULT_SIZE);
		
		rollCount = getIntParameter("rollCount", -1);
		if (rollCount < 1) {
			rollCount = DEFAULT_ROLL_COUNT;
		}
		
		List<StatData> statDatas = null;
		List list = null;
		int paraTotalCount = -1;
		if(isRoll==1){  //轮询 按照 随机轮询规则
			// 轮循 从用户选择的最大轮循条数做为分页查询的size值
			// 取出符合条件的记录总数
			int recordCount =  getInteractiveService(request).getStatDataListCount(showType, timeType);
			//int recordCount = 100;
			totalCount = totalCount > MAXNUMBER_RANGE ? MAXNUMBER_RANGE: totalCount;// 进行边界校验
			// 如果最大范畴超过记录总数则最大范围为记录总数
			totalCount = totalCount > recordCount ? recordCount : totalCount;	
			
			currentPage=1;
	
			statDatas = getInteractiveService(request).getStatDataList(showType, timeType,totalCount);	

			paraTotalCount = totalCount;
			String ruleId = "statDatas_"+paraTotalCount + "_"
					+ rollCount + "_1x_" + 2;
			RollRuleManager rrm = RollRuleManager.getInstance();
			IRollRule rollRule  = new RandomRollRule(ruleId);
			rrm.addRollRule(rollRule);
			rollRule = rrm.getRollRule(ruleId);
			RollRuleContext rrc = new RollRuleContext(Long.valueOf(totalCount),
					currentPage, Integer.valueOf(rollCount));
			
			long[] indexes = rollRule.obtainIndexes(rrc);
		/*	if (resourcePackReleation.size() < indexes.length) {
				resourcePackReleation = getResourceService(request).getResourcePackReleationsByOrder(resourcePackId,currentPage - 1 < 0 ? 1 : currentPage - 1,totalCount,order,totalCount);
			}*/
			list = getIndexResourceBagRelations(indexes, statDatas);
			System.out.println("热词轮循 ---->;记录总数="+recordCount+";用户选取轮循范围="+totalCount+";轮循规则="+2+";页/条="+rollCount);			
		}else{
			statDatas = getInteractiveService(request).getStatDataList(showType, timeType,rollCount);//只查询前几条	
			list = statDatas;	
		}
	
		Map resultMap = new HashMap();
		String result = "";
		/** 存放列表资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lsRess = new ArrayList<Object>();
		int loop = 0;
		Iterator it = list.iterator();
		while(it.hasNext()){
			loop++;
			StatData sd = (StatData)it.next();
			StringBuilder sb=new StringBuilder();
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
			URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append("1");
			sb.append("&");
			sb.append(ParameterConstants.RESOURCE_TYPE);
			sb.append("=");
			sb.append(restype);
			if(showType==11)
				searchby = 1;
			if(showType==12)
				searchby = 2;
			if(showType==13)
				searchby = 4;
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_TYPE);
			sb.append("=");
			sb.append(searchby);
			
			sb.append("&");
			sb.append(ParameterConstants.SEARCH_PARAM_VALUE);
			sb.append("=");
			try {
				
				sb.append(java.net.URLEncoder.encode(sd.getContent().trim(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("url", sb.toString());
			//obj.put("title",author.getName());
			try {
				String title = StrUtil.getLimitStr(sd.getContent(), ParameterConstants.AUTHOR_NAME_BYTES, ParameterConstants.REPLACE_SYMBOL);
				title = StrUtil.toUnicode(title);
				obj.put("title",title );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lsRess.add(obj);
		}
		map.put("objs", lsRess);
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if(tagTemplateId > 0){
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this,tagTem);
		resultMap.put(TagUtil.makeTag(tagName), result);
		
		//result = VmInstance.getInstance().parseVM(map, this);
		//resultMap.put(TagUtil.makeTag(tagName), result);
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
	
}
