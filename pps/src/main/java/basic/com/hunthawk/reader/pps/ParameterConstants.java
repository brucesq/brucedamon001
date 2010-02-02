/**
 * 
 */
package com.hunthawk.reader.pps;

/**
 * @author BruceSun
 * 
 */
public class ParameterConstants {

	//
	public static final String PORTAL_PATH = "/s.do";
	// 页面级别 首页 列表页 资源页 详情页
	public static final String PAGE = "pg";

	// 产品ID
	public static final String PRODUCT_ID = "pd";
	// 页面组ID
	public static final String PAGEGROUP_ID = "gd";
	// 栏目ID
	public static final String COLUMN_ID = "cd";
	// 模板ID
	public static final String TEMPLATE_ID = "td";

	// 区域ID
	public static final String AREA_ID = "ad";
	// 计费ID
	public static final String FEE_ID = "ed";
	// 批价包ID
	public static final String FEE_BAG_ID = "fd";
	
	public static final String MONTH_FEE_BAG_ID = "pf";
	// 批价包关联ID
	public static final String FEE_BAG_RELATION_ID = "nd";
	// 资源ID
	public static final String RESOURCE_ID = "rd";
	// 推广渠道ID
	public static final String CHANNEL_ID = "fc";
	// 联通入口参数
	public static final String UNICOM_PT = "PT";
	// 章节ID
	public static final String CHAPTER_ID = "zd";
	// 页码
	public static final String PAGE_NUMBER = "pn";
	//余下全文
	public static final String REMAINING_TEXT = "ret";
	// 每页显示字数
	public static final String WORDAGE = "ps";
	// 图书类型
	public static final String BOOK_TYPE = "bt";
	//资源类型
	public static final String RESOURCE_TYPE="rt";
	//搜索条件  按(作者、书名等)
	public static final String SEARCH_TYPE="st";
	//搜索关键字
	public static final String SEARCH_PARAM_VALUE="key";
	//快速搜索链接名称
	public static final String QUICK_SEARCH_LINK_NAME="qkey";
	
	//作者ID
	public static final String AUTHOR_ID="au";

	// 书签ID
	public static final String BOOK_MARK_ID = "mid";

	// 首页
	public static final String PAGE_PRODUCT = "p";
	// 栏目页
	public static final String PAGE_COLUMN = "c";
	// 资源页
	public static final String PAGE_RESOURCE = "r";
	// 详情页
	public static final String PAGE_DETAIL = "d";
	
	// 下载页
	public static final String PAGE_DOWNLOAD = "t";

	// 功能性页面 添加书签\添加收藏，删除收藏等
	public static final String COMMON_PAGE = "fn";
	public static final String BAG_FUNCTION_FLAG="BFF";
	// 添加收藏
	public static final String COMMON_PAGE_FAVORITE_ADD = "fa";
	// 删除收藏
	public static final String COMMON_PAGE_FAVORITE_DEL = "fd";

	// 添加书签
	public static final String COMMON_PAGE_BOOKMARK_ADD = "ma";
	// 删除书签
	public static final String COMMON_PAGE_BOOKMARK_DEL = "md";
	// 添加书包
	public static final String COMMON_PAGE_BOOKBAG_ADD = "ba";
	// 删除书包
	public static final String COMMON_PAGE_BOOKBAG_DEL = "bd";
	//弹出计费页面
	public static final String COMMON_PAGE_FEE = "fee";
	//通用链接
	public static final String COMMON_PAGE_LINK = "cl";
	//版本信息
	public static final String VERSION_TYPE = "vt";
	
	//卷ID
	public static final String TOME_ID="tid";

	public static final String SHOW_FLAG="show";
	//漫画自动播放 timer
	public static final String TIMER="T";
	//漫画是否自动播放
	public static final String AUTO_PLAY="auto";
	
//	// 标签操作标识 (add or delete)
//	public static final String OPERATE_ACTION = "operate";
//	// 添加
//	public static final String OPERATE_ACTION_ADD = "add";
//	// 删除
//	public static final String OPERATE_ACTION_DELETE = "del";

	//是否是第一次查询标识 
	public static final String IS_FIRST_SEARCH="Y";

	public static final String PARAM_URL = "ul";

	public static final java.lang.String TAG_SCRIPT_PREFIX = "<%";

	public static final java.lang.String TAG_SCRIPT_SUFFIX = "%>";

	//置顶轮询规则=0
	public static final int ROLL_RULE_PUSH = 0;
	//翻页轮询规则=1
	public static final int ROLL_RULE_PAGE = 1;
	//随机获取轮询规则=2
	public static final int ROLL_RULE_RANDOM = 2;
	//间隔一定时间随机获取轮询规则=3
	public static final int ROLL_RULE_RANDOM_INTERVAL = 3;
	//间隔一定时间_轮询一批记录=4
	public static final int ROLL_RULE_PAGE_INTERVAL = 4;
	//资源轮换滚动默认置顶条数
	public static final int TOP_COUNT=2;

	public static final String MEMCACHED_TAG = "reader_tag_";
	
	public static final String PRE_TAG_SUFFIX = "$#";
	  
	public static final String END_TAG_SUFFIX = "#";
	
	public static final String NEXT_CHAPTER_LINK="下一章";
	public static final String PRE_CHAPTER_LINK="上一章";
	public static final String NEXT_RESOURCE_LINK="下一条";
	public static final String PRE_RESOURCE_LINK="上一条";
	
	//章节内容默认每页显示1000字
	public static final int CHAPTER_CONTENT_WORD_SET=1000;
	
	public static final String PAMS_NAVIGATOR="PAMS_NAVI";
	
	//默认输入框的大小(size)
	public static final int DEFAULT_INPUT_SIZE=15;
	//默认输入框的类型(type)
	public static final String DEFAULT_INPUT_TYPE="text";
	//默认输入框的名称(name)
	public static final String DEFAULT_INPUT_NAME="name";
	//连载订制或取消操作的标志
	public static final String ORDER_FLAG="flag";
	
	//留言相关参数
	//留言内容关键字
	public static final String COMMENT_PARAM_VALUE="MSG";
	//用户自定义key
	public static final String CUSTOM_KEY_VALUE="CK";
	//留言板块
	public static final String COMMENT_PLATE="CP";
	//留言类型
	public static final String COMMENT_TARGET="CT";
	//留言目标对象ID
	public static final String COMMENT_TARGET_ID="CTI";
	
	
	//投票相关参数
	//投票类型
	public static final String VOTE_VOTE_TYPE="VT";
	//投票项ID
	public static final String VOTE_ITEM_ID="II";
	//内容ID (产品、栏目、资源、用户定制ID)
	public static final String VOTE_CONTENT_ID="CI";
	
	//作者名称显示控制字节长度
	public static final int AUTHOR_NAME_BYTES=12;
	//资源名称显示字节长度
	public static final int RESOURCE_NAME_BYTES=24;
	//章节名称显示字节长度
	public static final int CHAPTER_NAME_BYTES=54;
	//栏目名称显示字节长度
	public static final int COLUMN_NAME_BYTES=24;
	//超过长度使用的替换占位符
	public static final String REPLACE_SYMBOL=".";
	//链接组合策略中 组合之间的分隔符号定义
	public static final String DEFAULT_SPLIT="-";
	
	/**
	 * url排序一级字段
	 * 
	 * @author penglei 2009-10-29 13:0:35
	 * 
	 */
	public static final String ORDER="od";
	
	/**
	 * url排序二级字段
	 * 
	 * @author penglei 2009-10-29 13:0:35
	 * 
	 */
	public static final String ORDERSUB="ods";
	
	public static final String VERSION_SET="version_set";
}
