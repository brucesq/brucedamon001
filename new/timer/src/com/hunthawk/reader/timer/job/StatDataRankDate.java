package com.hunthawk.reader.timer.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;
import com.hunthawk.reader.timer.service.VoteService;

/**
 * 日统计任务
 * 
 * @author penglei 2009.11.06
 * 
 */
public class StatDataRankDate {

	protected static Logger logger = Logger.getLogger(StatDataRankDate.class);

	private HibernateGenericController controller;

	private SystemService systemService;

	private MemCachedClientWrapper memcached;

	private VoteService voteService;

	private StatDataRankSummary statDataRankSummary;

	public void setStatDataRankSummary(StatDataRankSummary statDataRankSummary) {
		this.statDataRankSummary = statDataRankSummary;
	}

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void setVoteService(VoteService voteService) {
		this.voteService = voteService;
	}

	public void setHibernateGenericController(
			HibernateGenericController hibernateGenericController) {
		this.controller = hibernateGenericController;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void doJob() {
		long start = System.currentTimeMillis();

		try { // 做清零动作 (包含日，周，月)
			logger.info("StatDataZeroClear DO Clear" + new Date());
			StatDataZeroClear statDataZeroClear = new StatDataZeroClear();
			statDataZeroClear.setHibernateGenericController(controller);
			statDataZeroClear.setMemcached(memcached);
			statDataZeroClear.setSystemService(systemService);
			statDataZeroClear.doJob();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("StatDataRankDate Start:[日统计]");
		try {
			logger.info("StatDataRankDate DO doStatisticsJob" + new Date());
			doStatisticsJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			logger.info("StatDataRankDate DO doFavorites" + new Date());
			doFavorites(); // 日收藏总数
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			logger.info("StatDataRankDate DO doUserBuy" + new Date());
			doUserBuy(); // 日订购统计
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			logger.info("StatDataRankDate DO doMsgRecord" + new Date());
			doMsgRecord();// 日留言统计
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			logger.info("StatDataRankDate DO doVoteSubItem" + new Date());
			doVoteSubItem();// 日投票统计
		} catch (Exception e) {
			e.printStackTrace();
		}
		// try{
		// logger.info("StatDataRankDate DO doResourceDT"+new Date());
		// doResourceDT();// 统计远程服务器的PV
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		logger.info("StatDataRankDate End:[日统计]"
				+ (System.currentTimeMillis() - start));

		try {
			logger.info("statDataRankSummary DO all Start[日]" + new Date());
//			statDataRankSummary.doJob(); // 统计汇总（人气，搜索，收藏，订购，留言，PV，投票）
			statDataRankSummary.doVoteSubItem();
			statDataRankSummary.doFavorites();
			statDataRankSummary.doMsgRecord();
			logger.info("statDataRankSummary DO all End[日]" + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 统计周
		StatDataRankWeek sdrw = new StatDataRankWeek();
		sdrw.setHibernateGenericController(controller);
		sdrw.setSystemService(systemService);
		sdrw.setMemcached(memcached);
		sdrw.doJob();

		logger.info("StatDataRankWeek end....");
		// 统计月
		StatDataRankMonth sdrm = new StatDataRankMonth();
		sdrm.setHibernateGenericController(controller);
		sdrm.setSystemService(systemService);
		sdrm.setMemcached(memcached);
		sdrm.doJob();
		logger.info("StatDataRankMonth end....");
	}

	private void doStatisticsJob() {
		String hql = "select s.content,sum(views) from StatData s where s.type = ? and createTime between ? and ? and s.content is not null group by s.content";

		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		logger.info("StartDate:"
				+ ToolDateUtil.dateToString(startDate,
						ToolDateUtil.DATETIME_FORMAT));
		logger.info("End:"
				+ ToolDateUtil.dateToString(endDate,
						ToolDateUtil.DATETIME_FORMAT));

		int i = 0;
		int pageNo = 1;
		int pageSize = 100;
		boolean hasResult = true;
		int num = 0;
		// long count = controller.getResultCount(hql, 1, startDate, endDate);
		// logger.info("doStatisticsJob DOWN count SIZE="+count);
		while (hasResult) {
			logger.info("doStatisticsJob DOWN start");
			List<Object[]> clickResult = controller.findBy(hql, pageNo,
					pageSize, 4, startDate, endDate); // 下载

			// logger.info("doStatisticsJob DOWN
			// SIZE="+clickResult.size()+":NUM="+(++num));

			if (clickResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}

			for (Object[] objs : clickResult) {

				ResourceAll resource = controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setDownNumDate(((Long) objs[1]).intValue());
					if (resource.getDownnum() != null) {
						resource.setDownnum(resource.getDownnum()
								+ resource.getDownNumDate());
					} else {
						resource.setDownnum(resource.getDownNumDate());
					}
					logger.info("down" + resource.getId() + ":"
							+ resource.getDownnum() + ":"
							+ resource.getDownNumDate());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);
				} else {
					continue;
				}

			}
			logger.info("doStatisticsJob DOWN closeSession. NUM=" + (num));
//			SessionFactoryUtils.closeSession(session);
			logger.info("doStatisticsJob DOWN closeSession. NUM=" + (num));
		}

		logger.info("doStatisticsJob DOWN END SIZE=" + i + " :NUM=" + (num));
		num = 0;
		pageNo = 1;
		i = 0;
		hasResult = true;

		while (hasResult) {
			logger.info("Start doStatisticsJob .....");
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 2, startDate, endDate); // 搜索
			logger.info("doStatisticsJob Search SIZE=" + searchResult.size()
					+ ":NUM=" + (++num));

			if (searchResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}

			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setSearchNumDate(((Long) objs[1]).intValue());
					if (resource.getSearchNum() != null) {
						resource.setSearchNum(resource.getSearchNum()
								+ resource.getSearchNumDate());
					} else {
						resource.setSearchNum(resource.getSearchNumDate());
					}
					logger.info("search" + resource.getId() + ":"
							+ resource.getSearchNum() + ":"
							+ resource.getSearchNumDate());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);
				} else {
					continue;
				}

			}

		}
		logger.info("doStatisticsJob Search END SIZE=" + i + " :NUM=" + (num));

		num = 0;
		pageNo = 1;
		i = 0;
		hasResult = true;

		while (hasResult) {
			logger.info("Start doStatisticsJob .....");
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 1, startDate, endDate); // 点击
			logger.info("doStatisticsJob view SIZE=" + searchResult.size()
					+ ":NUM=" + (++num));

			if (searchResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}

			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setRankingNumDate(((Long) objs[1]).intValue());
					if (resource.getRankingNum() != null) {
						resource.setRankingNum(resource.getRankingNum()
								+ resource.getRankingNumDate());
					} else {
						resource.setRankingNum(resource.getRankingNumDate());
					}
					logger.info("search" + resource.getId() + ":"
							+ resource.getRankingNumDate() + ":"
							+ resource.getRankingNum());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);
				} else {
					continue;
				}
				
			

			}
		}
		logger.info("doStatisticsJob view END SIZE=" + i + " :NUM=" + (num));
	}

	/***************************************************************************
	 * 日收藏统计
	 * 
	 * @author penglei
	 */
	private void doFavorites() {
		String hql = "select f.contentId,count(f.mobile) from Favorites f where f.createTime between ? and ? and f.contentId is not null group by f.contentId";
		String type = "Favorites";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		universalMethod(hql, type, new Object[] { startDate, endDate });

	}

	/***************************************************************************
	 * 日订购统计
	 * 
	 * @author penglei
	 */
	private void doUserBuy() {
		String hql = "select u.contentId,count(u.mobile) from UserBuy u where u.createTime between ? and ? and u.contentId is not null group by u.contentId";
		String type = "UserBuy";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		universalMethod(hql, type, new Object[] { startDate, endDate });

	}

	/***************************************************************************
	 * 日留言统计
	 * 
	 * @author penglei
	 */
	private void doMsgRecord() {
		String hql = "select m.contentId,count(m.mobile) from MsgRecord m where m.status in(:values) and m.msgType= :msgType and m.createTime between :startDate and :endDate and m.contentId is not null group by m.contentId";
		String type = "MsgRecord";
		Object[] status = { 1, 2 };// 1：发布未审；2：发布已审
		Integer msgType = 1; // 对内容留言
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("values", status);
		params.put("msgType", msgType);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * 日投票统计
	 * 
	 * @author penglei
	 */
	private void doVoteSubItem() {
		String hql = "select v.contentId,count(v.id) from VoteResult v where v.contentId is not null and v.itemId = :itemId and v.createTime between :startDate and :endDate and v.contentId is not null group by v.contentId";
		String type = "VoteResult";
//		String[] itemType = systemService.getVariables("vote_Result")
//				.getValue().split(";");
//		Integer[] itemIds = new Integer[itemType.length];
//		for (int i = 0; i < itemType.length; i++) {
//			itemIds[i] = Integer.parseInt(itemType[i]);
//
//		}
		Integer itemType = Integer.parseInt(systemService.getVariables("vote_Result").getValue());
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("itemId", itemType);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * 日统计PV
	 * 
	 * @author penglei
	 */
	private void doResourceDT() {
		String hql = "select rdt.resourceId,sum(rdt.dpv+rdt.rpv) from ResourceDT  rdt where (rdt.dpv > 0 or rdt.rpv > 0) and rdt.createTime between :startDate and :endDate and rdt.resourceId is not null  group by rdt.resourceId";
		String type = "ResourceDT";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", sdf.format(startDate));
		params.put("endDate", sdf.format(endDate));
		universalMethod(hql, type, params);
	}

	private void universalMethod(String hql, String type, Object obj) {

		int i = 0;
		int pageNo = 1;
		int pageSize = 100;
		boolean hasResult = true;

		while (hasResult) {
			logger.info("universalMethod:" + type + ":" + hql + ":"
					+ new Date());
			List<Object[]> clickResult = null;
			if (obj instanceof Object[]) {
				Object[] values = (Object[]) obj;
				if (type.equalsIgnoreCase("ResourceDT")) {
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE2); // 设置远程数据源
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // 点击
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE1); // 设置本地数据源
				} else {
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // 点击
				}

			} else {
				Map values = (Map) obj;
				if (type.equalsIgnoreCase("ResourceDT")) {
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE2); // 设置远程数据源
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // 点击
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE1); // 设置本地数据源
					System.out.println("Size ==================       "
							+ clickResult.size());
				} else {
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // 点击
				}
			}
			logger.info("universalMethod:" + type + ":" + clickResult.size()
					+ ":" + new Date());
			if (clickResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}



			for (Object[] objs : clickResult) {

				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					if (type.equals("Favorites")) {
						resource.setFavNumDate(((Long) objs[1]).intValue()); // 设置当天收藏总数
						// resource.setFavNum(resource.getFavNumDate()
						// + resource.getFavNum()); // 设置总收藏数
					} else if (type.equalsIgnoreCase("UserBuy")) {
						resource.setOrderNumDate(((Long) objs[1]).intValue()); // 设置当天订购总数
						 resource.setOrderNum(resource.getOrderNum()
						 + resource.getOrderNumDate()); // 设置总订购数
					} else if (type.equalsIgnoreCase("MsgRecord")) {
						resource.setMsgNumDate(((Long) objs[1]).intValue());
						// 设置当天留言总数
						 resource.setMsgNum(resource.getMsgNum()
						 + resource.getMsgNumDate()); // 设置总留言数

					} else if (type.equalsIgnoreCase("VoteResult")) {
//						resource.setVoteNumDate(((Long) objs[1]).intValue()); // 设置当天投票总数
//						resource.setVoteNum(resource.getVoteNum()
//								+ resource.getVoteNumDate()); // 设置总投票数
						System.out.println("VOTE:"+resource.getId()+":"+objs[1]);
						Map param = (Map) obj;
						Integer itemId = (Integer) param.get("itemId");
						logger.info("投票类型[日]:" + itemId);
						int total = ((Long) objs[1]).intValue();
						VoteSubItem vote = voteService.getVoteSubItemById(
								resource.getId(), itemId);
						if (vote != null) {
							// 做更新
							if (total > vote.getVoteValue().intValue()) {
								resource.setVoteNumDate(vote.getVoteValue());// 设置当天投票总数(鲜花)
							} else {
								resource.setVoteNumDate(total);
							}

						} else {
							resource.setVoteNumDate(0);
						}
						

					} else if (type.equalsIgnoreCase("ResourceDT")) {
						resource.setRankingNumDate(((Long) objs[1]).intValue()); // 设置当天PV总数
						resource.setRankingNum(resource.getRankingNum()
								+ resource.getRankingNumDate()); // 设置总PV数
						logger.info("DTDate" + resource.getId() + ":"
								+ resource.getRankingNum() + ":"
								+ resource.getRankingNumDate());
					}

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);

				} else {
					continue;
				}
				i++;
				
				

			}
	
		}
		logger.info("universalMethod end  commit.");
	}

	public static void main(String[] args) throws ParseException {
		Date date = new Date();
		// String strDate = ToolDateUtil.dateToString(date, "yyyyMMdd");
		// date = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		// System.out.println(date.toLocaleString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = sdf.format(date);
		date = sdf.parse(s);
		System.out.println(date.toLocaleString());
	}

}
