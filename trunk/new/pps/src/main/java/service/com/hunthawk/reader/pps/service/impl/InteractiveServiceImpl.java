/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.InExpression;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.memcached.NullObject;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.inter.MsgBoard;
import com.hunthawk.reader.domain.inter.MsgRecord;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.domain.statistics.StatData;

import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.reader.pps.service.KeyWordService;

/**
 * @author BruceSun
 * 
 */
public class InteractiveServiceImpl implements InteractiveService {

	private static Logger logger = Logger
			.getLogger(InteractiveServiceImpl.class);

	private static Logger voteLog = Logger
			.getLogger("com.hunthawk.reader.vote");

	private HibernateGenericController controller;

	private MemCachedClientWrapper memcached;
	
	private KeyWordService keyWordService;
	
	public void setKeyWordService(KeyWordService keyWordService){
		this.keyWordService = keyWordService;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	private MsgBoard getMsgBoard(int boardId) {
		// CMS更新时清空该缓存
		String key = Utility.getMemcachedKey(MsgBoard.class, String
				.valueOf(boardId));
		Object board = null;
		try {
			board = memcached.getAndSaveLocalMedium(key);
		} catch (Exception e) {
			logger.error("从Memcached中获取留言板信息出错", e);
		}
		if (board == null) {
			board = controller.get(MsgBoard.class, boardId);
			if (board == null) {
				board = new NullObject();
			}
			memcached.setAndSaveLocalMedium(key, board,
					48 * MemCachedClientWrapper.HOUR);
		}
		if (board instanceof NullObject) {
			return null;
		}
		return (MsgBoard) board;
	}

	public Integer addMsgRecord(int msgType, int boardId, String mobile,
			String content, Integer parentId, String contentId,
			Integer columnId, String productId, String customId,
			String customName) {

		content = URLUtil.chineseFilter(content, 2);

		MsgBoard board = getMsgBoard(boardId);
		if (board == null || board.getStatus().equals(0)) {
			return 4;
		}
		if (StringUtils.isBlank(content)) {
			return 5;
		}
		Integer result = 1;
		MsgRecord record = new MsgRecord();
		record.setMsgType(msgType);
		record.setBoardId(boardId);
		record.setMobile(mobile);
		record.setContent(content);
		record.setReason(0);
		record.setCreateTime(new Date());
		record.setModifyTime(new Date());

		if (isNotEmptyInteger(parentId)) {
			MsgRecord parent = new MsgRecord();
			parent.setId(parentId);
			record.setParent(parent);
		}
		if (StringUtils.isNotEmpty(contentId)) {
			record.setContentId(contentId);
		}
		if (isNotEmptyInteger(columnId)) {
			record.setColumnId(columnId);
		}
		if (StringUtils.isNotEmpty(productId)) {
			record.setProductId(productId);
		}
		if (StringUtils.isNotEmpty(customId)) {
			record.setCustomId(customId);
		}
		if (StringUtils.isNotEmpty(customName)) {
			record.setCustomId(customName);
		}
		if (board.getAuditing() == 1) {
			// 先审后发
			record.setStatus(MsgRecord.MSG_STATUS_WAIT_ADUIT);
			result = 2;
		} else {
			// 先发后审
			if (hasDirtyWord(content)) {
				record.setStatus(MsgRecord.MSG_STATUS_HIDED);
				record.setReason(4);
				result = 3;
			} else {
				record.setStatus(MsgRecord.MSG_STATUS_PUB_NOADUIT);
			}

		}
		// 清缓存
		if (result == 1) {
			String key = getMsgRecordKey(msgType, boardId, contentId, columnId,
					productId, customId, new ArrayList<HibernateExpression>());
			clearMsgRecordCache(key);
		}
		controller.save(record);
		return result;
	}

	public Integer getMsgRecordCount(int msgType, int boardId,
			String contentId, Integer columnId, String productId,
			String customId) {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		String key = getMsgRecordKey(msgType, boardId, contentId, columnId,
				productId, customId, expressions);
		key += "count";
		Integer count = null;
		try {
			count = (Integer) memcached.getAndSaveLocal(key);
			if (count != null)
				return count;
		} catch (Exception e) {
			logger.error("从Memcached中获取留言总数信息出错", e);
		}
		count = controller.getResultCount(MsgRecord.class, expressions)
				.intValue();
		memcached.setAndSaveLocal(key, count, 48 * MemCachedClientWrapper.HOUR);
		return count;
	}

	public List<MsgRecord> getMsgRecordList(int msgType, int boardId,
			String contentId, Integer columnId, String productId,
			String customId, int pageNo, int pageSize) {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		String key = getMsgRecordKey(msgType, boardId, contentId, columnId,
				productId, customId, expressions);
		List<MsgRecord> records = null;
		boolean isCache = false;
		if (pageNo < 3 && (pageSize == 3 || pageSize == 10 || pageSize == 20)) {
			isCache = true;
			key += "no" + pageNo + "size" + pageSize;
		}
		if (isCache) {
			try {
				records = (List<MsgRecord>) memcached.get(key);
				if (records != null)
					return records;
			} catch (Exception e) {
				logger.error("从Memcached中获取留言总数信息出错", e);
			}
		}

		records = controller.findBy(MsgRecord.class, pageNo, pageSize,
				"createTime", false, expressions);
		if (isCache) {
			memcached.set(key, records, 48 * MemCachedClientWrapper.HOUR);
		}
		return records;
	}

	/**
	 * 判断是否是可用的整形
	 * 
	 * @param i
	 * @return
	 */
	private boolean isNotEmptyInteger(Integer i) {
		if (i == null || i <= 0)
			return false;
		return true;
	}

	/**
	 * 判断是否有敏感字
	 * 
	 * @param content
	 * @return
	 */
	private boolean hasDirtyWord(String content) {
//		return DirtyFilter.check(content);
		return keyWordService.hasKeyWord(content);
	}

	/**
	 * 获取缓存key及查询条件
	 * 
	 * @param msgType
	 * @param boardId
	 * @param parentId
	 * @param contentId
	 * @param columnId
	 * @param productId
	 * @param customId
	 * @param expressions
	 * @return
	 */
	private String getMsgRecordKey(int msgType, int boardId, String contentId,
			Integer columnId, String productId, String customId,
			Collection<HibernateExpression> expressions) {
		String key = Utility.getMemcachedKey(MsgRecord.class, String
				.valueOf(boardId));
		key += Constants.MEMCACHED_SLASH;
		switch (msgType) {
		case MsgRecord.TYPE_CUSTOM:
			key += "custom" + customId;
			HibernateExpression customEx = new CompareExpression("customId",
					customId, CompareType.Equal);
			expressions.add(customEx);
			break;
		case MsgRecord.TYPE_PRODUCT:
			key += "product" + productId;
			HibernateExpression productEx = new CompareExpression("productId",
					productId, CompareType.Equal);
			expressions.add(productEx);
			break;
		case MsgRecord.TYPE_COLUMN:
			key += "column" + columnId;
			HibernateExpression columnEx = new CompareExpression("columnId",
					columnId, CompareType.Equal);
			expressions.add(columnEx);
			break;
		case MsgRecord.TYPE_CONTENT:
			key += "content" + contentId;
			HibernateExpression contentEx = new CompareExpression("contentId",
					contentId, CompareType.Equal);
			expressions.add(contentEx);
			break;
		}
		HibernateExpression ex = new CompareExpression("boardId", boardId,
				CompareType.Equal);
		expressions.add(ex);

		ex = new InExpression("status", new Integer[] {
				MsgRecord.MSG_STATUS_PUB_ADUIT,
				MsgRecord.MSG_STATUS_PUB_NOADUIT });
		expressions.add(ex);
		
		return key;
	}

	/**
	 * 清空缓存
	 * 
	 * @param key
	 */
	private void clearMsgRecordCache(String key) {
		String countKey = key + "count";
		memcached.deleteForLocal(countKey);
		Integer[] sizes = { 3, 10, 20 };
		Integer[] pageNos = { 1, 2 };
		for (Integer size : sizes) {
			for (Integer pageNo : pageNos) {
				String tmpKey = key + "no" + pageNo + "size" + size;
				memcached.deleteForLocal(tmpKey);
			}
		}

	}

	public List<VoteItem> getVoteItems(int voteId) {
		List<VoteItem> items = null;
		String key = Utility.getMemcachedKey(VoteItem.class, String
				.valueOf(voteId));
		try {
			items = (List<VoteItem>) memcached.getAndSaveLocalMedium(key);
			if (items != null)
				return items;
		} catch (Exception e) {
			logger.error("从Memcached中获取投票项出错!", e);
		}

		items = controller.findBy(VoteItem.class, "voteId", voteId,
				"voteNumber", true);
		memcached.setAndSaveLocalMedium(key, items,
				22 * MemCachedClientWrapper.HOUR);
		return items;
	}

	/**
	 * 获得选项投票数
	 * 
	 * @param voteType
	 *            投票类型参见VoteSubItem
	 * @param itemId
	 *            选项ID
	 * @param contentId
	 *            内容ID
	 * @param columnId
	 *            栏目ID
	 * @param productId
	 *            产品ID
	 * @param customId
	 *            自定义ID
	 * @return
	 */
	public Long getVoteItemCounts(int voteType, int itemId, String contentId,
			Integer columnId, String productId, String customId) {
		String id = getVoteKey(voteType, itemId, contentId, columnId,
				productId, customId);
		String key = Utility.getMemcachedKey(VoteSubItem.class, id);
		Long count = null;
		try {
			count = memcached.getCounter(key);
			if (count >= 0) {
				return count;
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取投票数量出错!", e);
		}
		count = getVoteCount(id);
		memcached.storeCounter(key, count);
		return count;
	}

	/**
	 * 新增投票，不符合投票规则抛出异常
	 * 
	 * @param voteType
	 * @param itemId
	 * @param contentId
	 * @param columnId
	 * @param productId
	 * @param customId
	 * @param isVote
	 * @param voteMaxNumber
	 * @return
	 * @throws Exception
	 */
	public Long vote(int voteType, int itemId, String contentId,
			Integer columnId, String productId, String customId, String mobile,
			Integer isVote, Integer voteMaxNumber) throws Exception {
		String mobilekey = Utility.getMemcachedKey(VoteSubItem.class, "mobile",
				mobile);
		try {
			Object obj = memcached.get(mobilekey);
			if (obj != null) {
				throw new Exception("您投票时间间隔过短，请过一段时间再投.");
			}
		} catch (Exception e) {
			logger.error("从Memcached中获取用户投票频繁度出错!", e);
		}
		String userkey = getVoteKey(voteType, contentId, columnId, productId,
				customId);
		userkey = Utility.getMemcachedKey(VoteSubItem.class, "mobile", mobile,
				userkey);

		if (isVote > 0) {
			Integer userVoteCount = 0;
			try {
				userVoteCount = (Integer) memcached.get(userkey);
			} catch (Exception e) {
				logger.error("从Memcached中获取用户投票数量出错!", e);
			}
			userVoteCount = userVoteCount == null ? 0 : userVoteCount;
			if (userVoteCount >= voteMaxNumber) {
				throw new Exception("每个投票用户每天只能投" + voteMaxNumber + "票.");
			}
			memcached.set(userkey, userVoteCount + 1,
					24 * MemCachedClientWrapper.HOUR);
		}

		Long count = 0L;
		String id = getVoteKey(voteType, itemId, contentId, columnId,
				productId, customId);
		String key = Utility.getMemcachedKey(VoteSubItem.class, id);
		try {
			count = memcached.incr(key);
		} catch (Exception e) {
			logger.error("从Memcached中增加资源投票信息出错!", e);
		}
		if (count <= 0) {
			count = getVoteCount(id);
			memcached.storeCounter(key, ++count);
		}
		if (!VOTE_KEYS.containsKey(id)) {
			Object[] values = new Object[6];
			values[0] = voteType;
			values[1] = itemId;
			values[2] = contentId;
			values[3] = columnId;
			values[4] = productId;
			values[5] = customId;
			VOTE_KEYS.put(id, values);
		}
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(voteType);
		builder.append("][");
		builder.append(itemId);
		builder.append("][");
		builder.append(contentId);
		builder.append("][");
		builder.append(columnId);
		builder.append("][");
		builder.append(productId);
		builder.append("][");
		builder.append(customId);
		builder.append("][");
		builder.append(mobile);
		builder.append("]");
		voteLog.info(builder.toString());
		memcached.set(mobilekey, 1, 5 * MemCachedClientWrapper.SECOND);

		return count;
	}

	private Long getVoteCount(String id) {
		VoteSubItem item = controller.get(VoteSubItem.class, id);
		Long count = null;
		if (item != null) {
			count = item.getVoteValue().longValue();
		} else {
			count = 0L;
		}
		return count;
	}

	private String getVoteKey(int voteType, String contentId, Integer columnId,
			String productId, String customId) {
		String key = "";
		switch (voteType) {
		case VoteSubItem.TYPE_CUSTOM:
			key += "s_" + customId;
			break;
		case VoteSubItem.TYPE_PRODUCT:
			key += "p_" + productId;
			break;
		case VoteSubItem.TYPE_COLUMN:
			key += "c_" + columnId;
			break;
		case VoteSubItem.TYPE_CONTENT:
			key += "r_" + contentId;
			break;
		}
		return key;
	}

	private String getVoteKey(int voteType, int itemId, String contentId,
			Integer columnId, String productId, String customId) {
		String key = "";
		switch (voteType) {
		case VoteSubItem.TYPE_CUSTOM:
			key += "s_" + customId + "_" + itemId;
			break;
		case VoteSubItem.TYPE_PRODUCT:
			key += "p_" + productId + "_" + itemId;
			break;
		case VoteSubItem.TYPE_COLUMN:
			key += "c_" + columnId + "_" + itemId;
			break;
		case VoteSubItem.TYPE_CONTENT:
			key += "r_" + contentId + "_" + itemId;
			break;
		}
		return key;
	}

	public Map<String, Object[]> getAllVoteKey() {
		Map<String, Object[]> allKeys = new HashMap<String, Object[]>();
		allKeys.putAll(VOTE_KEYS);
		VOTE_KEYS.clear();
		return allKeys;
	}

	private static Map<String, Object[]> VOTE_KEYS = new HashMap<String, Object[]>();

	@SuppressWarnings("unchecked")
	public int getStatDataListCount(int showType, int timeType) {
		Integer count = 0;

		String key = Utility.getMemcachedKey(StatData.class, String
				.valueOf(showType), String.valueOf(timeType), "count");

		try {
			count = (Integer) memcached.getAndSaveLocalMedium(key);
			if (count != null)
				return count;
		} catch (Exception e) {
			logger.error("从Memcached中获取敏感词列表信息出错!", e);
		}

		String hql = "select count(distinct s.content) from StatData s where s.type = ? ";

		List param = new ArrayList();

		if (showType == 0)
			param.add(11);
		else
			param.add(showType);
		if (timeType != 0) {
			hql += " and s.createTime between ? and ?";
			param.add(getDate(timeType));
			param.add(new Date());
		}

		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);
		List<Long> counts = controller.findBy(hql, arr);
		count = counts.get(0).intValue();

		memcached
				.setAndSaveLocalMedium(key, count, MemCachedClientWrapper.HOUR);
		return count;

	}

	@SuppressWarnings("unchecked")
	public List<StatData> getStatDataList(int showType, int timeType,
			int totalCount) {
		String key = Utility.getMemcachedKey(StatData.class, String
				.valueOf(showType), String.valueOf(timeType), String
				.valueOf(totalCount));
		List<StatData> rels = null;

		try {
			rels = (List<StatData>) memcached.getAndSaveLocalMedium(key);
			if (rels != null)
				return rels;
		} catch (Exception e) {
			logger.error("从Memcached中获取敏感词列表信息出错!", e);
		}

		String hql = "select s.content,sum(s.views) from StatData s where s.type = ?";

		List param = new ArrayList();

		if (showType == 0)
			param.add(11);
		else
			param.add(showType);
		if (timeType != 0) {
			hql += " and s.createTime between ? and ?";
			param.add(getDate(timeType));
			param.add(new Date());
		}

		hql += " group by s.type,s.content order by sum(s.views) desc";

		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);

		List<Object[]> list = controller.findBy(hql, 1, totalCount, arr); // 查询热词的前200个

		rels = new ArrayList<StatData>();

		for (Object[] obj : list) {
			StatData statData = new StatData();
			statData.setContent(obj[0].toString());
			statData.setViews(Integer.valueOf(obj[1].toString()));
			rels.add(statData);
		}
		memcached.setAndSaveLocalMedium(key, rels, MemCachedClientWrapper.HOUR); // 放入内存1小时

		return rels;
	}

	protected Date getDate(int date) {
		Calendar today = Calendar.getInstance();
		// Date[] returnDate = new Date[2];

//		int month = today.get(Calendar.MONTH); // 得到当前月
//		int currentYear = today.get(Calendar.YEAR); // 得到当前年
//		String[] formate = new String[] { "yyyy-MM-dd hh:mm:ss" };

		if (date == 1) { // 日排行
			return DateUtils.addDays(today.getTime(), -1); // 当前时间减去1天--昨天
		} else if (date == 2) { // 周排行
			return DateUtils.addDays(today.getTime(), -7); // 当前时间减去1周--
		} else if (date == 3) {
			return DateUtils.addDays(today.getTime(), -30); // 当前时间减去1月
		} else {
			return DateUtils.addDays(today.getTime(), -1);
		}
	}

	public static void main(String args[]) {
		InteractiveServiceImpl is = new InteractiveServiceImpl();
		Date dates = is.getDate(3);
		System.out.println("-----" + dates);
		System.out.println("-----" + new Date());
		/*
		 * List<StatData> list = is.getStatDataList(11, 2, 0, 100);
		 * System.out.println("-----"+list.size());
		 */
	}
}
