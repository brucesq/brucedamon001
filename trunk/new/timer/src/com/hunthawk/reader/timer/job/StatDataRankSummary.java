package com.hunthawk.reader.timer.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;
import com.hunthawk.reader.timer.service.VoteService;

/**
 * 统计汇总
 * 
 * @author penglei
 * 
 */
public class StatDataRankSummary {

	protected static Logger logger = Logger
			.getLogger(StatDataRankSummary.class);

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

	private VoteService voteService;

	public void setVoteService(VoteService voteService) {
		this.voteService = voteService;
	}

	public void doResourceDTJob() {
		// long start = System.currentTimeMillis();
		// logger.info("StatDataRankSummary Start:[汇总DT统计]");
		// try{
		// logger.info("StatDataRankSummary DO doResourceDT"+new Date());
		// doResourceDT();// 统计远程服务器的PV汇总
		// logger.info("StatDataRankSummary END doResourceDT"+new Date());
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// logger.info("StatDataRankSummary End:[汇总DT统计]"
		// + (System.currentTimeMillis() - start));
	}

	public void doResourceStatisticsJob() {
		long start = System.currentTimeMillis();
		logger.info("StatDataRankSummary Start:[汇总doStatisticsJob统计]");
		try {
			logger.info("StatDataRankSummary DO doStatisticsJob" + new Date());
			doStatisticsJob(); // 点击和搜索汇总
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("StatDataRankSummary End:[汇总doStatisticsJob统计]"
				+ (System.currentTimeMillis() - start));
	}

	/**
	 * 执行任务
	 */
	public void doJob() {
		long start = System.currentTimeMillis();
		logger.info("StatDataRankSummary Start:[汇总统计]");

		try {
			logger.info("StatDataRankSummary DO doFavorites" + new Date());
			doFavorites(); // 收藏汇总
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			logger.info("StatDataRankSummary DO doUserBuy" + new Date());
			doUserBuy(); // 订购汇总
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			logger.info("StatDataRankSummary DO doMsgRecord" + new Date());
			doMsgRecord();// 留言汇总
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			logger.info("StatDataRankSummary DO doVoteSubItem" + new Date());
			doVoteSubItem();// 投票统计汇总
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("StatDataRankSummary End:[汇总统计]"
				+ (System.currentTimeMillis() - start));
	}

	@SuppressWarnings("unchecked")
	private void doStatisticsJob() {
		int i = 0;
		int pageNo = 1;
		int pageSize = 100;
		boolean hasResult = true;

		String hql = "select s.content,sum(views) from StatData s where s.type = ? and s.content is not null group by s.content";
		int num = 0;
		while (hasResult) {
			List<Object[]> clickResult = controller.findBy(hql, pageNo,
					pageSize, 4); // 下载

			logger.info("doStatisticsJob DOWN SIZE=" + clickResult.size()
					+ ":NUM=" + (++num));

			if (clickResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			//			

			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setDownnum(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);
				} else {
					continue;
				}

			}

		}

		logger.info("doStatisticsJob DOWN END SIZE=" + i + " :NUM=" + (num));

		num = 0;
		pageNo = 1;
		i = 0;
		hasResult = true;

		// 搜索
		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 2); // 搜索

			logger.info("doStatisticsJob Search SIZE=" + searchResult.size()
					+ ":NUM=" + (++num));

			if (searchResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			//		
			for (Object[] objs : searchResult) {

				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setSearchNum(((Long) objs[1]).intValue());

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
		logger.info("doStatisticsJob Search END SIZE=" + i + " :NUM=" + (num));

		num = 0;
		pageNo = 1;
		i = 0;
		hasResult = true;

		// 点击
		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 1); // 点击

			logger.info("doStatisticsJob view SIZE=" + searchResult.size()
					+ ":NUM=" + (++num));

			if (searchResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			//			
			for (Object[] objs : searchResult) {

				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setRankingNum(((Long) objs[1]).intValue());

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
		logger.info("doStatisticsJob view END SIZE=" + i + " :NUM=" + (num));
	}

	/***************************************************************************
	 * 收藏统计汇总
	 * 
	 * @author penglei
	 */
	public void doFavorites() {
		String hql = "select f.contentId,count(f.mobile) from Favorites f where f.contentId is not null group by f.contentId";
		String type = "Favorites";
		universalMethod(hql, type, null);

	}

	/***************************************************************************
	 * 订购统计汇总
	 * 
	 * @author penglei
	 */
	private void doUserBuy() {
		String hql = "select u.contentId,count(u.mobile) from UserBuy u where u.contentId is not null group by u.contentId";
		String type = "UserBuy";
		universalMethod(hql, type, null);

	}

	/***************************************************************************
	 * 留言统计汇总
	 * 
	 * @author penglei
	 */
	public void doMsgRecord() {
		String hql = "select m.contentId,count(m.mobile) from MsgRecord m where m.status in(:values) and m.msgType= :msgType and m.contentId is not null group by m.contentId";
		String type = "MsgRecord";
		Object[] status = { 1, 2 };// 1：发布未审；2：发布已审
		Integer msgType = 1; // 对内容留言
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("values", status);
		params.put("msgType", msgType);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * 日投票统计
	 * 
	 * @author penglei
	 */
	public void doVoteSubItem() {
		String hql = "select v.contentId,count(v.id) from VoteResult v where v.contentId is not null and v.itemId = :itemId and  v.contentId is not null group by v.contentId";
		String type = "VoteResult";
		// String[] itemType = systemService.getVariables("vote_Result")
		// .getValue().split(";");
		// Integer[] itemIds = new Integer[itemType.length];
		// for (int i = 0; i < itemType.length; i++) {
		// itemIds[i] = Integer.parseInt(itemType[i]);
		//
		// }
		Integer itemType = Integer.parseInt(systemService.getVariables(
				"vote_Result").getValue());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemId", itemType);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * 日统计PV
	 * 
	 * @author penglei
	 */
	private void doResourceDT() {
		String hql = "select rdt.resourceId,sum(rdt.dpv+rdt.rpv) from ResourceDT  rdt where (rdt.dpv > 0 or rdt.rpv > 0) and rdt.resourceId is not null  group by rdt.resourceId";
		String type = "ResourceDT";
		universalMethod(hql, type, null);
	}

	private void universalMethod(String hql, String type, Object obj) {
		int i = 0;
		int pageNo = 1;
		int pageSize = 100;
		boolean hasResult = true;
		while (hasResult) {
			logger.info("summart:" + type + ":" + hql + ":" + new Date());
			List<Object[]> clickResult = null;
			if (obj != null) {
				if (obj instanceof Object[]) {
					Object[] values = (Object[]) obj;
					if (type.equalsIgnoreCase("ResourceDT")) {
						CustomerContextHolder
								.setCustomerType(DataSourceMap.DATASOURCE2); // 设置远程数据源
						clickResult = controller.findBy(hql, pageNo, pageSize,
								values);
						CustomerContextHolder
								.setCustomerType(DataSourceMap.DATASOURCE1); // 设置本地数据源
					} else {
						clickResult = controller.findBy(hql, pageNo, pageSize,
								values);
					}

				} else {
					Map values = (Map) obj;
					if (type.equalsIgnoreCase("ResourceDT")) {
						CustomerContextHolder
								.setCustomerType(DataSourceMap.DATASOURCE2); // 设置远程数据源
						clickResult = controller.findBy(hql, pageNo, pageSize,
								values);
						CustomerContextHolder
								.setCustomerType(DataSourceMap.DATASOURCE1); // 设置本地数据源
					} else {
						clickResult = controller.findBy(hql, pageNo, pageSize,
								values);
					}
				}
			} else {
				if (type.equalsIgnoreCase("ResourceDT")) {
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE2); // 设置远程数据源

					clickResult = controller.findBy(hql, pageNo, pageSize,
							new Object[] {});

					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE1); // 设置本地数据源
				} else {
					clickResult = controller.findBy(hql, pageNo, pageSize,
							new Object[] {});
				}
			}

			logger.info("summart:" + type + ":" + clickResult.size() + ":"
					+ new Date());
			if (clickResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}

			//			

			for (Object[] objs : clickResult) {
				logger.info("ID:" + objs[0].toString());
				ResourceAll resource = controller.get(ResourceAll.class,
						objs[0].toString());
				// ResourceAll resource = (ResourceAll) session.get(
				// ResourceAll.class, objs[0].toString());
				if (resource != null) {
					if (type.equals("Favorites")) {

						resource.setFavNum(((Long) objs[1]).intValue()); // 设置收藏汇总

					} else if (type.equalsIgnoreCase("UserBuy")) {

						resource.setOrderNum(((Long) objs[1]).intValue()); // 设置订购汇总

					} else if (type.equalsIgnoreCase("MsgRecord")) {

						resource.setMsgNum(((Long) objs[1]).intValue()); // 设置留言汇总

					} else if (type.equalsIgnoreCase("VoteResult")) {

						// resource.setVoteNum(((Long) objs[1]).intValue()); //
						// 设置投票汇总
						Map param = (Map) obj;
						Integer itemId = (Integer) param.get("itemId");
						int total = ((Long) objs[1]).intValue();

						logger.info("投票类型[总]:" + itemId);

						VoteSubItem vote = voteService.getVoteSubItemById(
								resource.getId(), itemId);
						if (vote != null) {

							if (total != vote.getVoteValue().intValue()) {// 做更新
								resource.setVoteNum(vote.getVoteValue());
							} else {
								resource.setVoteNum(total);
							}

						} else {
							resource.setVoteNum(0);
						}

					} else if (type.equalsIgnoreCase("ResourceDT")) {

						resource.setRankingNum(((Long) objs[1]).intValue()); // 设置当天PV总数
						logger.info("DT" + resource.getId() + ":"
								+ resource.getRankingNum());

					}
					// logger.info("bID:"+objs[0].toString());
					controller.update(resource);
					// logger.info("eID:"+objs[0].toString());
					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存

				} else {
					continue;
				}
				//			
				logger.info("universalMethod end once commit.");
			}
			logger.info("universalMethod end  commit.");
		}
	}
}