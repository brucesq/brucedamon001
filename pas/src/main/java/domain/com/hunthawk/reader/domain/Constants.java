/**
 * 
 */
package com.hunthawk.reader.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.hunthawk.framework.util.OrderedMap;

/**
 * @author BruceSun
 * 
 */
public class Constants {

	private static Map<String, Integer> BUSSINESSTYPES = new OrderedMap<String, Integer>();
	static {
		// 1. WAP 站点 2. 手机软件 3. 手持设备
		BUSSINESSTYPES.put("WAP", 1);
		BUSSINESSTYPES.put("PAD", 3);
		BUSSINESSTYPES.put("APP", 2);

	}

	private static Map<String, Integer> VERSIONTYPES = new OrderedMap<String, Integer>();
	static {
		VERSIONTYPES.put("3G", 3);
		VERSIONTYPES.put("WAP1.X", 1);
		VERSIONTYPES.put("WAP2.0", 2);

	}

	private static Map<String, Integer> PRODUCTSTATUS = new HashMap<String, Integer>();
	static {
		PRODUCTSTATUS.put("已上线", 0);
		PRODUCTSTATUS.put("待上线", 1);
		PRODUCTSTATUS.put("暂停", 2);
		PRODUCTSTATUS.put("下线", 3);
	}

	// 排序类型,0按照排序ID降序、1按照ID降序、2按照点击数降序、5按照排序ID升序、6按照ID升序
	private static Map<String, Integer> ORDERTYPE = new OrderedMap<String, Integer>();

	static {
		ORDERTYPE.put("排序号降序", 0);
		ORDERTYPE.put("ID降序", 1);
		ORDERTYPE.put("推荐指数降序", 2);
		ORDERTYPE.put("排序号和点击数降序", 3);
		ORDERTYPE.put("排序号升序", 5);
		ORDERTYPE.put("ID升序", 6);


		ORDERTYPE.put("搜索总排行", 20);
		ORDERTYPE.put("搜索月排行", 21);
		ORDERTYPE.put("搜索周排行", 22);
		ORDERTYPE.put("搜索日排行", 23);

		ORDERTYPE.put("收藏总排行", 30);
		ORDERTYPE.put("收藏月排行", 31);
		ORDERTYPE.put("收藏周排行", 32);
		ORDERTYPE.put("收藏日排行", 33);

		ORDERTYPE.put("订购总排行", 40);
		ORDERTYPE.put("订购月排行", 41);
		ORDERTYPE.put("订购周排行", 42);
		ORDERTYPE.put("订购日排行", 43);

		ORDERTYPE.put("留言总排行", 50);
		ORDERTYPE.put("留言月排行", 51);
		ORDERTYPE.put("留言周排行", 52);
		ORDERTYPE.put("留言日排行", 53);
		
		ORDERTYPE.put("访问量总排行", 60);
		ORDERTYPE.put("访问量月排行", 61);
		ORDERTYPE.put("访问量周排行", 62);
		ORDERTYPE.put("访问量日排行", 63);
		
		/**
		 * 添加投票排序
		 * @author penglei 2009.11.20
		 */
		ORDERTYPE.put("投票量总排行", 70);
		ORDERTYPE.put("投票量月排行", 71);
		ORDERTYPE.put("投票量周排行", 72);
		ORDERTYPE.put("投票量日排行", 73);
		
		ORDERTYPE.put("访问量和搜索量(搜索置顶)", 7);
	}

	/**
	 * 接受url过来的排序参数
	 * <ul>
	 * <li>1：点击排行榜</li>
	 * <li>2：搜索排行榜</li>
	 * <li>3：收藏排行榜</li>
	 * <li>4：订购排行榜</li>
	 * <li>5：留言排行榜</li>
	 * <li>6：访问量排行榜</li>
	 * </ul>
	 * 
	 * @author penglei 2009-10-29 12:36:53
	 * yuzs 2009-11-05 修改（添加上了 访问量排行）
	 * 
	 */
	private static Map<Integer, Map<Integer, Integer>> URLORDER = new OrderedMap<Integer, Map<Integer, Integer>>();
	static {
		Map<Integer, Integer> downNumMap = new OrderedMap<Integer, Integer>(); // 点击
		downNumMap.put(1, 2); // 点击总排行

		Map<Integer, Integer> searchNumMap = new OrderedMap<Integer, Integer>(); // 搜索
		searchNumMap.put(1, 20); // 搜索总排行
		searchNumMap.put(2, 21); // 搜索月排行
		searchNumMap.put(3, 22); // 搜索周排行
		searchNumMap.put(4, 23); // 搜索日排行

		Map<Integer, Integer> favNumMap = new OrderedMap<Integer, Integer>();// 收藏
		favNumMap.put(1, 30); // 收藏总排行
		favNumMap.put(2, 31); // 收藏月排行
		favNumMap.put(3, 32); // 收藏周排行
		favNumMap.put(4, 33); // 收藏日排行

		Map<Integer, Integer> orderNumMap = new OrderedMap<Integer, Integer>(); // 订购
		orderNumMap.put(1, 40); // 订购总排行
		orderNumMap.put(2, 41); // 订购月排行
		orderNumMap.put(3, 42); // 订购周排行
		orderNumMap.put(4, 43); // 订购日排行

		Map<Integer, Integer> msgNumMap = new OrderedMap<Integer, Integer>(); // 留言
		msgNumMap.put(1, 50); // 留言总排行
		msgNumMap.put(2, 51); // 留言月排行
		msgNumMap.put(3, 52); // 留言周排行
		msgNumMap.put(4, 53); // 留言日排行

		
		Map<Integer, Integer> rankingNumMap = new OrderedMap<Integer, Integer>(); // 留言
		rankingNumMap.put(1, 60); // 访问量总排行
		rankingNumMap.put(2, 61); // 访问量月排行
		rankingNumMap.put(3, 62); // 访问量周排行
		rankingNumMap.put(4, 63); // 访问量日排行
		
		/**
		 * 添加投票排序
		 * @author penglei 2009.11.20
		 */
		Map<Integer, Integer> voteNumMap = new OrderedMap<Integer, Integer>(); // 投票
		voteNumMap.put(1, 70); // 投票量总排行
		voteNumMap.put(2, 71); // 投票量月排行
		voteNumMap.put(3, 72); // 投票量周排行
		voteNumMap.put(4, 73); // 投票量日排行
		
		URLORDER.put(1, downNumMap);
		URLORDER.put(2, searchNumMap);
		URLORDER.put(3, favNumMap);
		URLORDER.put(4, orderNumMap);
		URLORDER.put(5, msgNumMap);
		URLORDER.put(6, rankingNumMap);
		URLORDER.put(7, voteNumMap);
	}

	public static Map<Integer, Map<Integer, Integer>> getUrlOrderMap() {
		return URLORDER;
	}

	// 产品状态
	public static final int PRODUCTSTATUS_PUBLISH = 0;
	public static final int PRODUCTSTATUS_CHECK = 1;
	public static final int PRODUCTSTATUS_PAUSE = 2;
	public static final int PRODUCTSTATUS_OFFLINE = 3;

	// 栏目状态
	public static final int COLUMNSTATUS_PUBLISH = 1;
	public static final int COLUMNSTATUS_OFFLINE = 2;
	public static final int COLUMNSTATUS_DELETE = 3;

	// 计费类型
	// 按次
	public static final int FEE_TYPE_VIEW = 1;
	// VIP
	public static final int FEE_TYPE_VIP = 2;
	// 内容控制
	public static final int FEE_TYPE_CHOICE = 3;
	// 常规包月
	public static final int FEE_TYPE_NORMAL = 4;
	// 免费
	public static final int FEE_TYPE_FREE = 5;

	private static Map<String, Integer> FEETYPE = new OrderedMap<String, Integer>();
	static {
		FEETYPE.put("免费", FEE_TYPE_FREE);
		FEETYPE.put("包月(VIP)", FEE_TYPE_VIP);
		FEETYPE.put("包月(内容控制)", FEE_TYPE_CHOICE);
		FEETYPE.put("包月(常规)", FEE_TYPE_NORMAL);
		FEETYPE.put("按次", FEE_TYPE_VIEW);
	}

	public static Map<String, Integer> getFeeTypeMap() {
		return FEETYPE;
	}

	public static Map<String, Integer> getOrderTypeMap() {
		return ORDERTYPE;
	}

	// 订购类型
	// 按次
	public static final int ORDER_TYPE_VIEW = 3;
	// 包月
	public static final int ORDER_TYPE_MONTH = 2;
	// 免费
	public static final int ORDER_TYPE_FREE = 1;
	// Memcached key分隔符
	public static final String MEMCACHED_SLASH = ":";

	private static Map<String, Integer> CREDITS = new HashMap<String, Integer>();
	static {
		CREDITS.put("差", 1);
		CREDITS.put("中", 2);
		CREDITS.put("高", 3);
	}

	public static Map<String, Integer> getCredits() {
		return CREDITS;
	}

	public static final String[] STATUS = { "待审", "上线" };

	public static final int STATUS_WAITCHECK = 0;
	public static final int STATUS_PUBLISH = 1;

	public static final String[] PAGEGROUP_STATUS = { "已上线", "待上线", "暂停", "下线" };

	// 页面组状态
	public static final int PAGEGROUPSTATUS_PUBLISH = 0;
	public static final int PAGEGROUPSTATUS_CHECK = 1;
	public static final int PAGEGROUPSTATUS_PAUSE = 2;
	public static final int PAGEGROUPSTATUS_OFFLINE = 3;

	// 首字母
	private static Map<String, String> INITIALLETTER = new TreeMap<String, String>();
	static {
		INITIALLETTER.put("A", "A");
		INITIALLETTER.put("B", "B");
		INITIALLETTER.put("C", "C");
		INITIALLETTER.put("D", "D");
		INITIALLETTER.put("E", "E");
		INITIALLETTER.put("F", "F");
		INITIALLETTER.put("G", "G");
		INITIALLETTER.put("H", "H");
		INITIALLETTER.put("I", "I");
		INITIALLETTER.put("J", "J");
		INITIALLETTER.put("K", "K");
		INITIALLETTER.put("L", "L");
		INITIALLETTER.put("M", "M");
		INITIALLETTER.put("N", "N");
		INITIALLETTER.put("O", "O");
		INITIALLETTER.put("P", "P");
		INITIALLETTER.put("Q", "Q");
		INITIALLETTER.put("R", "R");
		INITIALLETTER.put("S", "S");
		INITIALLETTER.put("T", "T");
		INITIALLETTER.put("U", "U");
		INITIALLETTER.put("V", "V");
		INITIALLETTER.put("W", "W");
		INITIALLETTER.put("X", "X");
		INITIALLETTER.put("Y", "Y");
		INITIALLETTER.put("Z", "Z");
	}

	private static final Map<String, Integer> AUTHORSTATUS = new HashMap<String, Integer>();
	static {
		AUTHORSTATUS.put("已上线", 0);
		AUTHORSTATUS.put("下线", 1);
	}

	// 版权，资源状态
	private static final Map<String, Integer> REFERENSTATUS = new HashMap<String, Integer>();
	static {
		REFERENSTATUS.put("商用", 0);
		REFERENSTATUS.put("暂停", 1);
		REFERENSTATUS.put("隐藏", 2);
	}

	// 资源状态
	private static final Map<String, Integer> RESOURCESTATUS = new HashMap<String, Integer>();
	static {
		RESOURCESTATUS.put("商用", 0);
		RESOURCESTATUS.put("待审", 1);
		RESOURCESTATUS.put("隐藏", 2);
		RESOURCESTATUS.put("暂停", 3);
		RESOURCESTATUS.put("复审", 4);
		RESOURCESTATUS.put("否决", 5);
	}

	private static Map<String, String> AREA_CODE = new TreeMap<String, String>();
	static {
		AREA_CODE.put("全网", "001");
		AREA_CODE.put("内蒙", "010");
		AREA_CODE.put("北京", "011");
		AREA_CODE.put("天津", "013");
		AREA_CODE.put("山东", "017");
		AREA_CODE.put("河北", "018");
		AREA_CODE.put("山西", "019");
		AREA_CODE.put("澳门", "022");
		AREA_CODE.put("安徽", "030");
		AREA_CODE.put("上海", "031");
		AREA_CODE.put("江苏", "034");
		AREA_CODE.put("浙江", "036");
		AREA_CODE.put("福建", "038");
		AREA_CODE.put("海南", "050");
		AREA_CODE.put("广东", "051");
		AREA_CODE.put("广西", "059");
		AREA_CODE.put("青海", "070");
		AREA_CODE.put("湖北", "071");
		AREA_CODE.put("湖南", "074");
		AREA_CODE.put("江西", "075");
		AREA_CODE.put("河南", "076");
		AREA_CODE.put("西藏", "079");
		AREA_CODE.put("四川", "081");
		AREA_CODE.put("重庆", "083");
		AREA_CODE.put("陕西", "084");
		AREA_CODE.put("贵州", "085");
		AREA_CODE.put("云南", "086");
		AREA_CODE.put("甘肃", "087");
		AREA_CODE.put("宁夏", "088");
		AREA_CODE.put("新疆", "089");
		AREA_CODE.put("吉林", "090");
		AREA_CODE.put("辽宁", "091");
		AREA_CODE.put("黑龙江", "097");

	}

	// 资源总大类
	private static Map<String, Integer> RESOURCETYPE = new HashMap<String, Integer>();
	static {
		RESOURCETYPE.put("图书", 1);
		RESOURCETYPE.put("报纸", 2);
		RESOURCETYPE.put("杂志", 3);
		RESOURCETYPE.put("漫画", 4);
		RESOURCETYPE.put("铃声", 5);
		RESOURCETYPE.put("视频", 6);
		RESOURCETYPE.put("资讯", 7);
	}

	// 资源是否全本
	private static Map<String, Integer> ISFINISHED = new HashMap<String, Integer>();
	static {
		ISFINISHED.put("全本", 1);
		ISFINISHED.put("连载", 2);
	}

	// 资源是否(专有，出版，首发，引进)
	private static Map<String, Integer> YESNO = new HashMap<String, Integer>();
	static {
		YESNO.put("否", 2);
		YESNO.put("是", 1);
	}

	// 资源推荐指数
	private static Map<String, Integer> RESOURCEEXP = new HashMap<String, Integer>();
	static {
		RESOURCEEXP.put("1", 1);
		RESOURCEEXP.put("2", 2);
		RESOURCEEXP.put("3", 3);
		RESOURCEEXP.put("4", 4);
		RESOURCEEXP.put("5", 5);
	}

	// 业务类型
	public static Map<String, Integer> getBussinessTypes() {
		return BUSSINESSTYPES;
	}

	// 版本类型
	public static Map<String, Integer> getVersionTypes() {
		return VERSIONTYPES;
	}

	public static String getBussinessTypeName(Integer id) {
		for (Map.Entry<String, Integer> entry : BUSSINESSTYPES.entrySet()) {
			if (entry.getValue().equals(id)) {
				return entry.getKey();
			}
		}
		return "";
	}

	// 产品状态
	public static Map<String, Integer> getProductStatus() {
		return PRODUCTSTATUS;
	}

	public static String getProductStatusName(Integer status) {
		for (Map.Entry<String, Integer> entry : Constants.getProductStatus()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return entry.getKey();
		}
		return "";
	}

	// 首字母
	public static Map<String, String> getInitialLetter() {
		return INITIALLETTER;
	}

	// 作者状态
	public static Map<String, Integer> getAuthorStatus() {
		return AUTHORSTATUS;
	}

	// 版权状态
	public static Map<String, Integer> getReferenStatus() {
		return REFERENSTATUS;
	}

	// 资源状态
	public static Map<String, Integer> getResourceStatus() {
		return RESOURCESTATUS;
	}

	public static Map<String, String> getAreaCode() {
		return AREA_CODE;
	}

	// 资源大类
	public static Map<String, Integer> getResourceType() {
		return RESOURCETYPE;
	}

	// 资源是否全本
	public static Map<String, Integer> getResourceFinished() {
		return ISFINISHED;
	}

	// 资源是否
	public static Map<String, Integer> getResourceYesNo() {
		return YESNO;
	}

	// 资源推荐指数
	public static Map<String, Integer> getRESOURCEEXP() {
		return RESOURCEEXP;
	}

	public static String getAreaName(String aid) {
		for (Map.Entry<String, String> entry : AREA_CODE.entrySet()) {
			if (entry.getValue().equals(aid)) {
				return entry.getKey();
			}
		}
		return "";
	}
	
	public static final java.lang.String SPLIT_DOC_TAG = "#news page split tag#";
}
