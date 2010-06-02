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

import com.aspire.gentox.rollrule.DefaultRollRule;
import com.aspire.gentox.rollrule.IRollRule;
import com.aspire.gentox.rollrule.IntervalRandomRollRule;
import com.aspire.gentox.rollrule.IntevalPageRollRule;
import com.aspire.gentox.rollrule.PageRollRule;
import com.aspire.gentox.rollrule.PushRollRule;
import com.aspire.gentox.rollrule.RandomRollRule;
import com.aspire.gentox.rollrule.RollRuleContext;
import com.aspire.gentox.rollrule.RollRuleManager;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
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
import com.hunthawk.tag.util.ParamUtil;

/**
 * 资源滚动标签，可以通过轮循规则，调整信息显示顺序、显示时间、显示条数 
 * 参数说明： 
 * notDisSN：是否显示序号 ( 1.是 -1.否)
 * isFee：是否显示计费 ( 1.是 -1.否) 
 * isRoll：是否轮循 ( 1.是 -1.否) 
 * noPageLink：是否导航 ( 1.是 -1.否)
 * totalCount：轮循总范围(默认早多为200条) 
 * columnId：栏目ID 
 * rollCount：轮循条数 
 * rollTime：轮循时间 (单位：分)
 * rollRuleId：轮循规则 (置顶轮询规则=0,翻页轮询规则=1,随机获取轮询规则=2,
 * 间隔一定时间随机获取轮询规则=3,间隔一定时间_轮询一批记录=4) 
 * mix : 组合项 (推荐语+资源名称) 0.不组合 1.推荐语 
 * modify by liuxh :增加排序规则-->针对不轮循资源 
 * listCount:列表总范围 --针对不轮循的情况 如果未设置则默认为记录总数
 * 
 * param:筛选条件	1.全本 2.连载 3.已出版 4.未出版	add by liuxh 09-12-16
 * @author liuxh
 */
public class ResourceRollTag extends BaseTag {

	private static final long serialVersionUID = -498520875520787274L;

	private CustomService customService;

	private ResourceService resourceService;

	private BussinessService bussinessService;

	// private MemCachedClientWrapper memcachedClient;
	//
	// public MemCachedClientWrapper getMemcachedClient() {
	// return memcachedClient;
	// }

	private int show;

	public int getShow() {
		return show;
	}

	public void setShow(int show) {
		this.show = show;
	}

	/** 是否计费 */
	private boolean isFee;

	public boolean isFee() {
		return isFee;
	}

	/** 最大轮循范围 */
	private static final int MAXNUMBER_RANGE = 200;
	/** 显示条数 */
	private static final int DEFAULT_PAGE_SIZE = 20;
	/** 默认滚动显示条数 */
	private static final int DEFAULT_ROLL_COUNT = 5;
	/** 默认滚动显示时间，单位：分钟 */
	private static final int DEFAULT_ROLL_TIME = 2;
	/** 导航条默认显示的页码数 */
	private static final int DEFAULT_NAVIGATOR_SIZE = 5;
	/** 栏目Id */
	private int columnId;
	/** 批价包Id */
	private int resourcePackId;
	/** 链接文字 */
	private String titlePattern;
	/** 参与轮循总条数 */
	private int totalCount;
	/** 显示的轮循条数 */
	private int rollCount;
	/** 置顶条数，只适用于置顶轮循规则 */
	private int topCount;
	/** 轮循的时间，单位：分钟 */
	private int rollTime;
	/** 轮循规则 */
	private int rollRuleId;
	/** 不显示序号 */
	private boolean notDisSN;
	/** 下一页链接 */
	private String nextPageLink = "";
	/** 上一页链接 */
	private String prePageLink = "";
	/** 页数导航链接 */
	private String navigatorPageLink = "";
	/** 跳转页面链接 */
	private String gotoPageLink = "";
	/** 不显示翻页相关的链接 */
	private boolean noPageLink;
	/** 是否轮循 */
	private boolean isRoll;

	public boolean isRoll() {
		return isRoll;
	}

	public void setRool(boolean isRoll) {
		this.isRoll = isRoll;
	}

	/** 列表总范围 --针对不参与轮循的资源 */
	private int listCount;

	public int getListCount() {
		return listCount;
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
	}

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		/**列表查询条件  add by liuxh 09-11-16*/
		int param =getIntParameter("param",-1);
		/**end*/
		
		
		this.show = getIntParameter("showtype", 1);// 默认显示点击数
		// 获取url 排序参数 start
		/**
		 * @author penglei 2009-10-29 13:34:17
		 * 
		 */
		int urlOrder = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDER, -1);
		int urlOrderSub = ParamUtil.getIntParameter(request,
				ParameterConstants.ORDERSUB, -1);
		// 获取url 排序参数 end

		// String mix = getParameter("mix", "1");// title组合 默认只显示资源名称不做组合
		// 1.判断是否是白名单用户
		// boolean isWhiteList = RequestUtil.isFeeDisabled();
		// 2.判断是否是已购买用户
		// boolean isBuy = false;
		// 3.判断是否是包月订购用户
		// boolean isMonth = false;

		// 用于计算标签解析时间
		long currentTime = System.currentTimeMillis();
		long tagBegin = currentTime;
		// 是否显示序号
		notDisSN = getIntParameter("isDisSN", -1) < 0 ? true : false;
		// 是否计费
		isFee = getIntParameter("isFee", -1) > 0 ? true : false; // (1.url体现计费代码
		// -1.url不体现
		// 计费代码)
		// 是否轮循
		isRoll = getIntParameter("isRoll", -1) > 0 ? true : false; // (1.轮循
		// -1.不轮循)
		// 是否有页面导航
		noPageLink = getIntParameter("pageLink", -1) > 0 ? false : true;// 上一页
		// 轮循总范围
		totalCount = getIntParameter("rollTotalCount", -1);// 得到用户选择轮循环的总条数
		// 列表总范围 --针对不参与轮循的资源
		listCount = getIntParameter("listCount", -1);
		// 得到栏目参数
		columnId = getIntParameter("columnId", -1);
		// 得到轮循条数
		rollCount = getIntParameter("rollCount", -1);
		// 滚动显示条数
		if (rollCount < 1) {
			rollCount = DEFAULT_ROLL_COUNT;
		}
		// 滚动显示条数
		if (rollCount < 1) {
			rollCount = DEFAULT_ROLL_COUNT;
		}
		TagLogger.debug(tagName, tagName + "=======开始解析", request
				.getQueryString(), null);
		// 得到参数设置的轮循时间
		rollTime = Integer.parseInt(getParameter("rollTime", "-1"));
		if (rollTime < 1) {
			rollTime = DEFAULT_ROLL_TIME;
		} else {// 将轮循时间延长三倍减少压力
			rollTime = rollTime * 3;
		}
		// 得到轮循规则
		rollRuleId = getIntParameter("rollRuleId", -1) < 0 ? 0
				: getIntParameter("rollRuleId", -1);

		/**
		 * *************************************** 参数获取结束
		 * *******************************************************
		 */
		if (columnId < 0) {
			// 从URL中获取columnId
			columnId = Integer.parseInt(request
					.getParameter(ParameterConstants.COLUMN_ID) == null ? "-1"
					: request.getParameter(ParameterConstants.COLUMN_ID));
		}
		if (columnId < 0) {
			TagLogger
					.debug(tagName, "获取栏目ID失败", request.getQueryString(), null);
			return new HashMap();
		}
		Columns column = getBussinessService(request).getColumns(columnId); // 栏目
		// 如果得不到栏目 则直接返回
		if (column == null) {
			TagLogger
					.debug(tagName, "获取栏目信息失败", request.getQueryString(), null);
			return new HashMap();
		}
		// 得到批价包的ID
		resourcePackId = column.getPricepackId() == null ? 0 : column
				.getPricepackId(); // 批价包ID
		// 根据批价包的ID查到批价包的相关信息
		ResourcePack resourcePack = getResourceService(request)
				.getResourcePack(resourcePackId);
		// 如果得不到批价包对象 ，则直接返回
		if (resourcePack == null)
			return new HashMap();

		int currentPage = request.getParameter(ParameterConstants.PAGE_NUMBER) == null ? 1
				: Integer.parseInt(request
						.getParameter(ParameterConstants.PAGE_NUMBER));// 当前页数，默认为第一页
		/**
		 * 判断是否从url里是否有排序字段存在。如果有，使用url排序字段排序，否则使用标签字段排序
		 * 
		 * @author penglei 2009-10-29 13:40:49
		 * 
		 */
		int order = 0;

		if (urlOrder > 0 && urlOrderSub > 0) {
			order = getResourceService(request).getUrlOrderType(urlOrder,
					urlOrderSub);
		} else {
			order = column.getOrderType();// 排序类型
		}
		// 结束

		// 记住初始的totalCount参数，用于构造ruleId
		int paraTotalCount = -1;
		List<ResourcePackReleation> resourcePackReleation = null;// 资源批价关系表列表集合
		List list = null;
		if (isNoPageLink()) {// 如果不显示导航 当前页重置为1
			currentPage = 1;
		}
		int navigatorPageSize = rollCount;// 每页显示的条数

		if (isRoll) {// 轮循 从用户选择的最大轮循条数做为分页查询的size值
			// 取出符合条件的记录总数
			int recordCount = getResourceService(request)
					.getResourcePackReleationsByOrderCount(resourcePackId,
							order, totalCount,param);
			totalCount = totalCount > MAXNUMBER_RANGE ? MAXNUMBER_RANGE
					: totalCount;// 进行边界校验
			// 如果最大范畴超过记录总数则最大范围为记录总数
			totalCount = totalCount > recordCount ? recordCount : totalCount;

			if (rollRuleId == ParameterConstants.ROLL_RULE_RANDOM
					|| rollRuleId == ParameterConstants.ROLL_RULE_RANDOM_INTERVAL) {// 不翻页
				currentPage = 1;
			}
			resourcePackReleation = getResourceService(request)
					.getResourcePackReleationsByOrder(resourcePackId, 1,
							totalCount, order, totalCount,param);

			if (rollRuleId == ParameterConstants.ROLL_RULE_RANDOM
					|| rollRuleId == ParameterConstants.ROLL_RULE_RANDOM_INTERVAL
					&& resourcePackReleation.size() < totalCount) {
				totalCount = resourcePackReleation.size();
			} else {// 翻页
				// 最后一页查询结果数不够 rollCount值时 重置rollCount的值
				if (currentPage == (totalCount % rollCount != 0 ? totalCount
						/ rollCount + 1 : totalCount / rollCount)) {
					rollCount = (totalCount - (currentPage - 1) * rollCount);
				}
			}
			paraTotalCount = totalCount;
			String ruleId = resourcePackId + "_" + paraTotalCount + "_"
					+ rollCount + "_" + rollTime + "_" + rollRuleId;
			RollRuleManager rrm = RollRuleManager.getInstance();
			IRollRule rollRule = getRollRule(ruleId);
			RollRuleContext rrc = new RollRuleContext( Long.valueOf(totalCount),
					currentPage, new Integer(rollCount));
			if (rollRuleId == ParameterConstants.ROLL_RULE_PUSH) {
				topCount = getIntParameter("topCount", -1);
				// 增加条件判断 防止置顶条数大于轮循总条数
				if (topCount > rollCount) {
					topCount = rollCount;
				} else {
					topCount = topCount < 0 ? ParameterConstants.TOP_COUNT
							: topCount;// 置顶条数
				}
				rrc.attach(Integer.valueOf(topCount));
			}
			long[] indexes = rollRule.obtainIndexes(rrc);

			// System.out.print("随机id集合=");
			// for(int i=0;i<indexes.length;i++){
			// System.out.print(indexes[i]+",");
			// }
			// System.out.println("");

			if (resourcePackReleation.size() < indexes.length) {
				resourcePackReleation = getResourceService(request)
						.getResourcePackReleationsByOrder(resourcePackId,
								currentPage - 1 < 0 ? 1 : currentPage - 1,
								totalCount, order, totalCount,param);
			}
			list = getIndexResourceBagRelations(indexes, resourcePackReleation);
			System.out.println("轮循 ---->批价包=" + resourcePackId + ";记录总数="
					+ recordCount + ";用户选取轮循范围=" + totalCount + ";轮循规则="
					+ rollRuleId + ";页/条=" + rollCount);
		} else {// 不轮循
			// 取出符合条件的记录总数
			int recordCount = getResourceService(request)
					.getResourcePackReleationsByOrderCount(resourcePackId,
							order, listCount,param);
			if (listCount < 0 || listCount > recordCount) {
				listCount = recordCount;// 如果未选择列表 或是列表数超出总数 则显示记录总数
			}

			rollCount = rollCount < 0 ? DEFAULT_PAGE_SIZE : rollCount;// 如果资源条数为负则采用默认大小20条
			resourcePackReleation = getResourceService(request)
					.getResourcePackReleationsByOrder(resourcePackId,
							currentPage, rollCount, order, listCount,param);
			list = resourcePackReleation;
			System.out.println("不轮循---->批价包=" + resourcePackId + ";记录总数="
					+ recordCount + ";用户选取不轮循的列表范围=" + listCount + ";页/条"
					+ rollCount);
		}

		/** 存放滚动资源 */
		Map<String, Object> map = new HashMap<String, Object>();
		Map resultMap = new HashMap();
		String result = "";
		/** 保存资源列表对象，返回界面显示 */
		List<Object> lsRess = new ArrayList<Object>();
		Iterator it = list.iterator();
		int loop = 0;
		while (it.hasNext()) {
			/** 保存单条记录 */
			Map<String, Object> obj = new HashMap<String, Object>();
			ResourcePackReleation resourceBagRelation = (ResourcePackReleation) it
					.next();
			ResourceAll resource = resourceService
					.getResource(resourceBagRelation.getResourceId());
			String title = "";
			loop++;
			// 判断是否不显示序号
			if (!isNotDisSN()) {
				title = rollCount * (currentPage - 1) + loop + "." + title;
			}
			/**
			 * 封装跳转URL 页面级别参数+产品ID+页面组ID+区域ID+栏目ID+批价包ID+批价包关联ID+资源ID+推广渠道ID
			 * +联通PT参数+页码 此URL 从地址里获得
			 */
			if (resource == null) {
				continue;
			}
			// isBuy=getCustomService(request).isUserBuyBook(RequestUtil.getMobile(),
			// resource.getId());
			// String feeId = resourcePack.getFeeId();// 计费ID
			// if(resourcePack.getType() == Constants.FEE_TYPE_FREE){
			// this.isFee=false;
			// }
			// if(feeId==null || "".equals(feeId)){
			// feeId = resourceBagRelation.getFeeId();
			// if(feeId==null || "".equals(feeId)){//免费资源
			// this.isFee=false;
			// }
			// }

			StringBuilder sb = new StringBuilder();
			sb.append(request.getContextPath());
			// if (isFee()) {
			// Fee fee=getCustomService(request).getFee(feeId);
			// if(isWhiteList || isBuy){//白名单用户或是 已购买用户不需要计费
			// sb.append(ParameterConstants.PORTAL_PATH);
			// sb.append("?");
			// }else{//正常计费
			// boolean isOut=fee.getIsout()==1;
			// if(!isOut){
			// sb.append("/");
			// sb.append(fee.getUrl());
			// }
			// sb.append(ParameterConstants.PORTAL_PATH);
			// sb.append("?");
			// if(isOut){
			// //判断是否是包月购买 如果是则有弹出页提示否则没有
			// int packId = column.getPricepackId();
			// if(packId>0){
			// // 根据批价包的ID查找相关信息
			// ResourcePack pack = getResourceService(request).getResourcePack(
			// packId);
			// if(pack!=null){
			// isMonth=getCustomService(request).isOrderMonth(RequestUtil.getMobile(),
			// pack.getFeeId());
			// }else{
			// isMonth=false;
			// }
			// if(!isMonth){//不是包月订购用户正常弹出资费提示页
			// sb.append(ParameterConstants.COMMON_PAGE);
			// sb.append("=");
			// sb.append(ParameterConstants.COMMON_PAGE_FEE);
			// sb.append("&");
			// sb.append(ParameterConstants.TEMPLATE_ID);
			// sb.append("=");
			// sb.append(fee.getTemplateId());// 得到模板ID
			// sb.append("&");
			// }
			//							
			// }else{
			//							
			// TagLogger.debug(tagName, "批价包ID为空", request.getQueryString(),
			// null);
			// return new HashMap();
			// }
			// }
			// }
			// } else {
			sb.append(ParameterConstants.PORTAL_PATH);
			sb.append("?");
			// }

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
			sb.append(resourcePackId);
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
			/**
			 * 添加排序到url上
			 * 
			 * @author penglei 2009-10-29 13:46:19
			 * 
			 */
			URLUtil.append(sb, ParameterConstants.ORDER, request);
			URLUtil.append(sb, ParameterConstants.ORDERSUB, request);
			/**
			 * end
			 */
			sb.append(ParameterConstants.PAGE_NUMBER);
			sb.append("=");
			sb.append(1);
			// // 如果计费添加feeId参数
			// if (isFee()) {
			// sb.append("&");
			// sb.append(ParameterConstants.FEE_ID);
			// sb.append("=");
			// sb.append(feeId);
			// }
			String url = URLUtil.trimUrl(sb).toString();
			obj.put("url", url);
			// StringBuilder linkname=new StringBuilder();
			// linkname.append(title);
			// String mixtitle=title;
			String mixtitle = title + resource.getName();
			// System.out.println("mix--->"+mix);
			// if(!mix.equals("1")){
			// List<String> mixparams = PpsUtil.getParameters(mix);
			// if(mixparams.size()<1 ){
			// linkname.append(resource.getName());
			// }
			// for(String str : mixparams){
			// System.out.println(str);
			// if(str.equalsIgnoreCase("name")){
			// linkname.append(resource.getName());
			// }else if(str.equalsIgnoreCase("bComment")){
			// linkname.append(resource.getBComment()==null?"":resource.getBComment());
			// }else if(str.equalsIgnoreCase("authorId")){
			// //根据作者ID查询作者信息
			// Integer [] authorIds=resource.getAuthorIds();
			// if(authorIds.length >0){
			// for(int i=0;i<authorIds.length;i++){
			// ResourceAuthor
			// author=getResourceService(request).getResourceAuthor(resource.getAuthorIds()[i]);
			// if(author!=null){
			// linkname.append((author.getPenName()==null ||
			// StringUtils.isEmpty(author.getPenName())?author.getName():author.getPenName()));
			// break;
			// }else{
			// continue;
			// }
			// }
			//							
			// }
			// }else if(str.equalsIgnoreCase("downnum")){
			// linkname.append(resource.getDownnum()==null?"":resource.getDownnum());
			// }
			// linkname.append(ParameterConstants.DEFAULT_SPLIT);
			// }
			// mixtitle=linkname.substring(0,linkname.lastIndexOf(ParameterConstants.DEFAULT_SPLIT));
			// }else{
			// linkname.append(resource.getName());
			// mixtitle+=linkname.toString();
			// }
			// 去掉最后一个：号
			try {
				obj.put("linkname", StrUtil.getLimitStr(mixtitle,
						ParameterConstants.RESOURCE_NAME_BYTES,
						ParameterConstants.REPLACE_SYMBOL));
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			obj.put("status", getBookStatus(resource.getIsFinished()));// 状态
			obj.put("preview", imagePreview(request, tagName, resource));// 资源预览图
			Integer[] authorids = resource.getAuthorIds();
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < authorids.length; i++) {
				ResourceAuthor author = getResourceService(request)
						.getResourceAuthor(authorids[i]);
				str.append((author.getPenName() == null || "".equals(author
						.getPenName())) ? author.getName() : author
						.getPenName());
				str.append(",");
				if (i == authorids.length - 1) {
					// 去掉最后一个,
					str.replace(str.lastIndexOf(","), str.length(), "");
				}
			}
			try {
				obj.put("author", StrUtil.getLimitStr(str.toString().trim(),
						ParameterConstants.AUTHOR_NAME_BYTES,
						ParameterConstants.REPLACE_SYMBOL));
			} catch (Exception e) {
				
				e.printStackTrace();
			}// 作者
			obj.put("downnum", String.valueOf(getResourceService(request)
					.getResourceVisits(resource.getId())));// 点击
			obj.put("searchnum", String
					.valueOf(resource.getSearchNum() == null ? 1 : (resource
							.getSearchNum()) + 1));// 搜索次数
			obj.put("favnum", String.valueOf(getCustomService(request)
					.getResourceFavoritesCount(resource.getId())));// 收藏
			obj.put("resource", resource);

			lsRess.add(obj);
		}

		map.put("objs", lsRess);
		map.put("strUtil", new StrUtil());
		map.put("previewUtil", new CoverPreview());
		map.put("service", getResourceService(request));
		map.put("cname", column.getName());
		/**
		 * 放入order到map里面
		 * 
		 * @author penglei 2009.10.29
		 */
		map.put("urlOrder", urlOrder);
		map.put("urlOrderSub", urlOrderSub);
		/**
		 * end
		 */
		// 判断如果轮循规则不是(2.随机获取轮询规则 3间隔一定时间随机获取轮询规则 )时则显示导航
		if (!isRoll
				|| (rollRuleId != ParameterConstants.ROLL_RULE_RANDOM && rollRuleId != ParameterConstants.ROLL_RULE_RANDOM_INTERVAL)) {
			// 判断是否导航
			if (!isNoPageLink()) {// 显示导航
				List listAll = new ArrayList();
				// for (int i = 0; i < (!isRoll?listCount:totalCount); i++) {
				// listAll.add(i);
				// }
				Navigator navi = new Navigator((!isRoll ? listCount
						: totalCount), currentPage, navigatorPageSize, 5);
				request.setAttribute(ParameterConstants.PAMS_NAVIGATOR, navi);
			}
		}
		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}
		result = DBVmInstance.getInstance().parseVM(map, this, tagTem);
		// result = VmInstance.getInstance().parseVM(map, this);
		resultMap.put(TagUtil.makeTag(tagName), result);
		TagLogger.debug("ResourceRoollTag", tagName + "=======解析耗时: "
				+ (System.currentTimeMillis() - tagBegin) + " ms", request
				.getQueryString(), null);
		return resultMap;
	}

	private String getBookStatus(Integer status) {
		if(status == null){
			return "";
		}
		String typeName = "";
		for (Map.Entry<String, Integer> entry : Constants.getResourceFinished()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return "(" + entry.getKey() + ")";
		}
		return typeName;
	}

	/**
	 * 拼最全的URL 列表有可能出现在产品页 栏目页 资源页 详情页
	 * 页面级别参数+产品ID+页面组ID+区域ID+栏目ID+批价包ID+批价包关联ID+资源ID+章节ID+推广渠道ID
	 * +联通PT参数+页码+每页显示字数+计费ID
	 * 
	 * @param request
	 * @param currentPage
	 *            当前页 导航URL
	 * @param templateid
	 *            删除 和资源 URL
	 * @param flag
	 *            删除和资源链接的判断标志
	 * @param contentid
	 *            删除和资源操作需传入的内容ID参数
	 * @return
	 */
	private String getPageUrl(HttpServletRequest request, String currentPage) {
		StringBuilder sb = new StringBuilder();
		// 得到当前工程的名称
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		URLUtil.append(sb, ParameterConstants.PAGE, request);
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.PRODUCT_ID))) {
			URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.PAGEGROUP_ID))) {
			URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.AREA_ID))) {
			URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.COLUMN_ID))) {
			URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_RELATION_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_BAG_RELATION_ID))) {
			URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.CHAPTER_ID))) {
			URLUtil.append(sb, ParameterConstants.CHAPTER_ID, request);
		}
		if (!StringUtils.isEmpty(request
				.getParameter(ParameterConstants.CHANNEL_ID))
				|| request.getParameter(ParameterConstants.CHANNEL_ID) == null) {
			URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		}
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append(currentPage);
		sb.append("&");
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.WORDAGE))) {
			URLUtil.append(sb, ParameterConstants.WORDAGE, request);
		}
		if (StringUtils.isNotEmpty(request
				.getParameter(ParameterConstants.FEE_ID))) {
			URLUtil.append(sb, ParameterConstants.FEE_ID, request);
		}
		return sb.toString();
	}

	/**
	 * 检索轮询规则
	 * 
	 * @param ruleId
	 * @return
	 */
	private IRollRule getRollRule(String ruleId) {
		RollRuleManager rrm = RollRuleManager.getInstance();
		IRollRule rollRule = rrm.getRollRule(ruleId);
		TagLogger.debug("ResorceRollTag",
				"[标签=ResourceRollTag,功能=调用通用工具接口获得滚动管理对象]", "", null);
		if (rollRule == null) {
			switch (rollRuleId) {
			case ParameterConstants.ROLL_RULE_PUSH:
				rollRule = new PushRollRule(ruleId, rollTime * 60);
				break;
			case ParameterConstants.ROLL_RULE_PAGE:
				rollRule = new PageRollRule(ruleId);
				break;
			case ParameterConstants.ROLL_RULE_RANDOM:
				rollRule = new RandomRollRule(ruleId);
				break;
			case ParameterConstants.ROLL_RULE_RANDOM_INTERVAL:
				rollRule = new IntervalRandomRollRule(ruleId, rollTime * 60);
				break;
			case ParameterConstants.ROLL_RULE_PAGE_INTERVAL:
				// 冒泡轮循时把置顶条数当冒泡条数
				if (topCount > 0) {
					if (topCount > rollCount)
						topCount = rollCount;
					rollRule = new IntevalPageRollRule(ruleId, rollTime * 60,
							topCount);
				} else
					rollRule = new IntevalPageRollRule(ruleId, rollTime * 60,
							rollCount);
				break;
			default:
				rollRule = new DefaultRollRule(ruleId, rollTime * 60);
				break;
			}
			rrm.addRollRule(rollRule);
		}
		// System.out.println("rollTime -->>" + rollTime);
		rollRule = rrm.getRollRule(ruleId);
		return rollRule;
	}

	/**
	 * 根据轮询规则，判断是否需要轮训
	 * 
	 * @return
	 */
	// private boolean isRollTimes(String key, long currentTime) {
	// TagLogger.debug("ResourceRollTag",
	// "[标签=ResourceRollTag,功能=判断是否需要轮训,资源滚动不为空,memkey=" + key
	// + ",rollTime=" + rollTime + "]", "", null);
	// if (rollTime <= 0) {
	// return false;
	// }
	// try {
	// // 轮循创建时间
	// Long strCtime = (Long) getMemcachedClient().get(key + "_time");
	// if (strCtime == null) {
	// getMemcachedClient().set(key + "_time", currentTime,
	// MemCachedClientWrapper.HOUR * 24);
	// getMemcachedClient().set(key + "_rollTime", "0",
	// MemCachedClientWrapper.HOUR * 24);
	// return true;
	// }
	// long times = ((currentTime - strCtime) / 1000 / 60) / rollTime;
	// // 当前轮循次数
	// String strRtime = (String) getMemcachedClient().get(
	// key + "_rollTime");
	// if (strRtime == null) {
	// strRtime = "0";
	// }
	// long timesBefore = Long.parseLong(strRtime);
	// if (times <= timesBefore) {
	// return false;
	// } else {
	// getMemcachedClient().set(key + "_rollTime", "" + times,
	// MemCachedClientWrapper.HOUR * 24);
	// }
	// } catch (Exception e) {
	// TagLogger.debug("ResourceRollTag",
	// "[标签=ResourceRollTag,功能=根据轮询规则，判断轮训次数,失败原因=在MEMCACHE中取时间戳时发生错误]memkey="
	// + key, "", e);
	// }
	// return true;
	// }
	private List getIndexResourceBagRelations(long[] indexes, List list) {
		List result = new ArrayList();
		for (int i = 0; i < indexes.length; i++) {
			// ResourcePackReleation rpr = (ResourcePackReleation) list
			// .get(Integer.parseInt(String.valueOf(indexes[i])));
			// System.out.println("***************" + rpr.getId());
			result.add(list.get(Integer.parseInt(String.valueOf(indexes[i]))));
		}
		return result;
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
						ebook.getId(), ebook.getBookPic(), 51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\""
								+ ebook.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("2")) {// 报纸
			NewsPapers n = (NewsPapers) resource;
			if (n.getImage().toLowerCase()
					.matches("[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						n.getId(), n.getImage(), 51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\"" + n.getName()
								+ "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("3")) {// 杂志
			Magazine magazine = (Magazine) resource;
			if (magazine.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						magazine.getId(), magazine.getImage(), 51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\""
								+ magazine.getName() + "\"/>");

				return sb.toString();
			}
		} else if (resource.getId().startsWith("4")) {// 漫画
			Comics comics = (Comics) resource;
			if (comics.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						comics.getId(), comics.getImage(), 51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" height=\"64\" width=\"48\" alt=\""
								+ comics.getName() + "\"/>");

				return sb.toString();
			}
		}else if (resource.getId().startsWith("6")) {// 视频
			Video video = (Video) resource;
			if (video.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						video.getId(), video.getImage(), 51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + video.getName() + "\"  />");

				return sb.toString();
			}
		}else if (resource.getId().startsWith(""+ResourceType.TYPE_INFO)) {// INFO
			Infomation info = (Infomation) resource;
			if (StringUtils.isNotEmpty(info.getImage())&&info.getImage().toLowerCase().matches(
					"[^.]+\\.(png|jpg|gif|jpeg)")) {
				String url = getResourceService(request).getPreviewCoverImg(
						info.getId(), info.getImage(), 51);
				// System.out.println("url--->"+url);
				sb.append("<img src=\"").append(url).append(
						"\" alt=\"" + info.getName() + "\"  />");

				return sb.toString();
			}
		}
		return "";
	}

	public String getNextPageLink() {
		return nextPageLink;
	}

	public void setNextPageLink(String nextPageLink) {
		this.nextPageLink = nextPageLink;
	}

	public String getPrePageLink() {
		return prePageLink;
	}

	public void setPrePageLink(String prePageLink) {
		this.prePageLink = prePageLink;
	}

	public String getNavigatorPageLink() {
		return navigatorPageLink;
	}

	public void setNavigatorPageLink(String navigatorPageLink) {
		this.navigatorPageLink = navigatorPageLink;
	}

	public String getGotoPageLink() {
		return gotoPageLink;
	}

	public void setGotoPageLink(String gotoPageLink) {
		this.gotoPageLink = gotoPageLink;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public int getResourcePackId() {
		return resourcePackId;
	}

	public void setResourcePackId(int resourcePackId) {
		this.resourcePackId = resourcePackId;
	}

	public String getTitlePattern() {
		return titlePattern;
	}

	public void setTitlePattern(String titlePattern) {
		this.titlePattern = titlePattern;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getRollCount() {
		return rollCount;
	}

	public void setRollCount(int rollCount) {
		this.rollCount = rollCount;
	}

	public int getTopCount() {
		return topCount;
	}

	public void setTopCount(int topCount) {
		this.topCount = topCount;
	}

	public int getRollTime() {
		return rollTime;
	}

	public void setRollTime(int rollTime) {
		this.rollTime = rollTime;
	}

	public int getRollRuleId() {
		return rollRuleId;
	}

	public void setRollRuleId(int rollRuleId) {
		this.rollRuleId = rollRuleId;
	}

	public void setNoPageLink(boolean noPageLink) {
		this.noPageLink = noPageLink;
	}

	public boolean isNoPageLink() {
		return noPageLink;
	}

	/**
	 * @return the notDisSN
	 */
	public boolean isNotDisSN() {
		return this.notDisSN;
	}

	/**
	 * @param notDisSN
	 *            the notDisSN to set
	 */
	public void setNotDisSN(boolean notDisSN) {
		this.notDisSN = notDisSN;
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
