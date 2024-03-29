package com.hunthawk.reader.timer.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;

/**
 * 月统计任务
 * 
 * @author penglei 2009.11.06
 * 
 */
public class StatDataRankMonth {

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
		long start = System.currentTimeMillis();
		System.out.println("StatDataRankMonth Start:[月统计]");
		doStatisticsJob();
		doFavorites(); // 月收藏总数
		doUserBuy(); // 月订购统计
		doMsgRecord();// 月留言统计
		doVoteSubItem();// 月投票统计
		// doResourceDT();// 统计远程服务器的PV
		System.out.println("StatDataRankMonth End:[月统计]"
				+ (System.currentTimeMillis() - start));
	}

	@SuppressWarnings("unchecked")
	public void doStatisticsJob() {

		String hql = "select s.content,sum(views) from StatData s where s.type = ? and createTime between ? and ? and s.content is not null group by s.content";

		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -30);
		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");

		int i = 0;
		int pageNo = 1;
		int pageSize = 100;
		boolean hasResult = true;

		while (hasResult) {
			List<Object[]> clickResult = controller.findBy(hql, pageNo,
					pageSize, 4, startDate, endDate);

			if (clickResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			
			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setDownNumMonth(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);
				} else {
					continue;
				}

			}
		}

		// 搜索
		i = 0;
		pageNo = 1;
		hasResult = true;

		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 2, startDate, endDate);

			if (searchResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			
			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setSearchNumMonth(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);
				} else {
					continue;
				}

			}

		}

		i = 0;
		pageNo = 1;
		hasResult = true;

		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 1, startDate, endDate);

			if (searchResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			
			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setRankingNumMonth(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);
				} else {
					continue;
				}

			}
		}

	}

	public Date[] getDate() {
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -30);

		return new Date[] { startDate, endDate };
	}

	/**
	 * 月收藏统计
	 * 
	 * @author penglei
	 */
	private void doFavorites() {
		String hql = "select f.contentId,count(f.mobile) from Favorites f where f.createTime between ? and ? and f.contentId is not null group by f.contentId";
		String type = "Favorites";
		Date[] returnDate = getDate();
		universalMethod(hql, type,
				new Object[] { returnDate[0], returnDate[1] });

	}

	/***************************************************************************
	 * 月订购统计
	 * 
	 * @author penglei
	 */
	private void doUserBuy() {
		String hql = "select u.contentId,count(u.mobile) from UserBuy u where u.createTime between ? and ? and u.contentId is not null group by u.contentId";
		String type = "UserBuy";
		Date[] returnDate = getDate();
		universalMethod(hql, type,
				new Object[] { returnDate[0], returnDate[1] });

	}

	/***************************************************************************
	 * 月留言统计
	 * 
	 * @author penglei
	 */
	private void doMsgRecord() {
		String hql = "select m.contentId,count(m.mobile) from MsgRecord m where m.status in(:values) and m.msgType= :msgType and m.createTime between :startDate and :endDate and m.contentId is not null group by m.contentId";
		String type = "MsgRecord";
		Object[] status = { 1, 2 };// 1：发布未审；2：发布已审
		Integer msgType = 1; // 对内容留言
		Date[] returnDate = getDate();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("values", status);
		params.put("msgType", msgType);
		params.put("startDate", returnDate[0]);
		params.put("endDate", returnDate[1]);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * 月投票统计
	 * 
	 * @author penglei
	 */
	private void doVoteSubItem() {
		String hql = "select v.contentId,count(v.id) from VoteResult v where v.contentId is not null and v.itemId in(:itemId) and v.createTime between :startDate and :endDate and v.contentId is not null group by v.contentId";
		String type = "VoteResult";
		String[] itemType = systemService.getVariables("vote_Result")
				.getValue().split(";");
		Integer[] itemIds = new Integer[itemType.length];
		for (int i = 0; i < itemType.length; i++) {
			itemIds[i] = Integer.parseInt(itemType[i]);

		}
		Date[] returnDate = getDate();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", returnDate[0]);
		params.put("endDate", returnDate[1]);
		params.put("itemId", itemIds);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * 月统计PV
	 * 
	 * @author penglei
	 */
	private void doResourceDT() {
		String hql = "select rdt.resourceId,sum(rdt.dpv+rdt.rpv) from ResourceDT  rdt where (rdt.dpv > 0 or rdt.rpv > 0) and rdt.createTime between :startDate and :endDate and rdt.resourceId is not null group by rdt.resourceId";
		String type = "ResourceDT";
		Date[] returnDate = getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", sdf.format(returnDate[0]));
		params.put("endDate", sdf.format(returnDate[1]));
		universalMethod(hql, type, params);

	}

	private void universalMethod(String hql, String type, Object obj) {

		int i = 0;
		int pageNo = 1;
		int pageSize = 100;
		boolean hasResult = true;

		while (hasResult) {
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
					System.out.println("------------------------ 月size ==="
							+ clickResult.size());
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
					System.out.println("------------------------ 月size ==="
							+ clickResult.size());
				} else {
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // 点击
				}
			}

			if (clickResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			
			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) controller.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					if (type.equalsIgnoreCase("Favorites")) {
						resource.setFavNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("UserBuy")) {
						resource.setOrderNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("MsgRecord")) {
						resource.setMsgNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("VoteResult")) {
						resource.setVoteNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("ResourceDT")) {
						resource
								.setRankingNumMonth(((Long) objs[1]).intValue()); // 设置月PV总数

					}

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
					controller.update(resource);

				} else {
					continue;
				}

			}

		}
	}
}
