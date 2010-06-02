package com.hunthawk.reader.timer.job;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 统计数据清零(日，周，月)
 * 
 * @author penglei
 * 
 */
public class StatDataZeroClear {

	protected static Logger logger = Logger.getLogger(StatDataZeroClear.class);

	private HibernateGenericController controller;

	private SystemService systemService;

	private MemCachedClientWrapper memcached;

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void doJob() {
		logger.info("[统计数据清零 ]--------start");
		doSearchZeroClear(); 	//搜索清零
		doFavoritesZeroClear();	//收藏清零
		doUserBuyZeroClear();	//订购清零
		doMsgRecordZeroClear();	//留言清零
		doVoteZeroClear();
		logger.info("[统计数据清零 ]---------end");
	}

	private void doSearchZeroClear() {
//		Date endDate = new Date();
//		Date startDate = DateUtils.addDays(endDate, -1);
//		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
//		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
//		Date[] strDates = {
//				ToolDateUtil.stringToDate(ToolDateUtil.dateToString(DateUtils
//						.addDays(endDate, -1), "yyyyMMdd"), "yyyyMMdd"),
//				ToolDateUtil.stringToDate(ToolDateUtil.dateToString(DateUtils
//						.addDays(endDate, -7), "yyyyMMdd"), "yyyyMMdd"),
//				ToolDateUtil.stringToDate(ToolDateUtil.dateToString(DateUtils
//						.addDays(endDate, -30), "yyyyMMdd"), "yyyyMMdd"), };
//		String searchDatas[] = { "searchNumDate", "searchNumWeek",
//				"searchNumMonth" };
//		for (int i = 0; i < searchDatas.length; i++) {
//			String hql = "update ResourceAll r set r."
//					+ searchDatas[i]
//					+ " = 0 where not exists ( select s.content from StatData s where s.type = ? and createTime between ? and ? and s.content is not null and s.content=r.id group by s.content) and r."
//					+ searchDatas[i] + " > 0";
//			try {
//				controller.executeUpdate(hql, 2, strDates[i], endDate);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

	}

	private void doFavoritesZeroClear() {
//		String favoritesDatas[] = { "favNumDate", "favNumWeek", "favNumMonth" };
//		Date endDate = new Date();
//		Date[] startDates = { DateUtils.addDays(endDate, -1),
//				DateUtils.addDays(endDate, -7), DateUtils.addDays(endDate, -30) };
//		for (int i = 0; i < startDates.length; i++) {
//			String hql = "update ResourceAll r set r."
//					+ favoritesDatas[i]
//					+ " = 0 where not exists (select f.contentId from Favorites f where f.createTime between ? and ? and f.contentId is not null and f.contentId=r.id group by f.contentId) and r."
//					+ favoritesDatas[i] + " > 0";
//			try {
//				controller.executeUpdate(hql, startDates[i], endDate);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}

	}

	private void doUserBuyZeroClear() {

//		String orderDatas[] = { "orderNumDate", "orderNumWeek", "orderNumMonth" };
//		Date endDate = new Date();
//		Date[] startDates = { DateUtils.addDays(endDate, -1),
//				DateUtils.addDays(endDate, -7), DateUtils.addDays(endDate, -30) };
//		for (int i = 0; i < startDates.length; i++) {
//			String hql = "update ResourceAll r set r."
//					+ orderDatas[i]
//					+ " = 0 where not exists (select u.contentId from UserBuy u where u.createTime between ? and ? and u.contentId is not null and u.contentId = r.id group by u.contentId) and r."
//					+ orderDatas[i] + " > 0";
//			try {
//				controller.executeUpdate(hql, startDates[i], endDate);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}

	}

	private void doMsgRecordZeroClear() {

//		String msgRecordDatas[] = { "msgNumDate", "msgNumWeek", "msgNumMonth" };
//		Date endDate = new Date();
//		Date[] startDates = { DateUtils.addDays(endDate, -1),
//				DateUtils.addDays(endDate, -7), DateUtils.addDays(endDate, -30) };
//		for (int i = 0; i < startDates.length; i++) {
//			// 1：发布未审；2：发布已审
//			Integer msgType = 1; // 对内容留言
//			String hql = "update ResourceAll r set r."
//					+ msgRecordDatas[i]
//					+ " = 0 where not exists (select m.contentId,count(m.mobile) from MsgRecord m where m.status in (1,2) and m.msgType= ? and m.createTime between ? and ? and m.contentId is not null and m.contentId =r.id group by m.contentId) and r."
//					+ msgRecordDatas[i] + " > 0";
//			try {
//				controller.executeUpdate(hql, msgType, startDates[i], endDate);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

	}

	private void doVoteZeroClear() {
//		String voteDatas[][] = {
//				{ "voteNumDate", "voteNumWeek", "voteNumMonth" } };
//		Date endDate = new Date();
//		Date[] startDates = { DateUtils.addDays(endDate, -1),
//				DateUtils.addDays(endDate, -7), DateUtils.addDays(endDate, -30) };
//		String[] voteTypes={"vote_Result"}; 
//		for (int j = 0; j < voteDatas.length; j++) {
//			
//			for (int i = 0; i < startDates.length; i++) {
//				Integer voteType = Integer.parseInt(systemService.getVariables(voteTypes[j]).getValue());
//				logger.info("投票类型[清零]:"+voteType);
//				String hql = "update ResourceAll r set r."
//						+ voteDatas[j][i]
//						+ " = 0 where not exists (select v.contentId,count(v.id) from VoteResult v where v.contentId is not null and v.itemId =? and v.createTime between ? and ? and v.contentId is not null and v.contentId =r.id group by v.contentId) and r."
//						+ voteDatas[j][i] + ">0";
//				try {
//					controller.executeUpdate(hql, voteType, startDates[i],
//							endDate);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
