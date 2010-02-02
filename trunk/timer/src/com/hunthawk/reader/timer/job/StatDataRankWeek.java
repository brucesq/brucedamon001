package com.hunthawk.reader.timer.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;

/**
 * 周统计任务
 * 
 * @author penglei
 * 
 */
public class StatDataRankWeek {

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
		System.out.println("StatDataRankWeek Start:[周统计]");
		doStatisticsJob();
		doFavorites(); // 周收藏总数
		doUserBuy(); // 周订购统计
		doMsgRecord();// 周留言统计
		doVoteSubItem();// 周投票统计
//		doResourceDT();// 统计远程服务器的PV
		System.out.println("StatDataRankWeek End:[周统计]"
				+ (System.currentTimeMillis() - start));
	}

	@SuppressWarnings("unchecked")
	private void doStatisticsJob() {
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -7);
		String strDate = ToolDateUtil.dateToString(startDate, "yyyyMMdd");
		startDate = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");


		int i = 0;
		int pageNo = 1;
		int pageSize = 100;
		boolean hasResult = true;

		String hql = "select s.content,sum(views) from StatData s where s.type = ? and createTime between ? and ? and s.content is not null group by s.content";

		while (hasResult) {
			List<Object[]> clickResult = controller.findBy(hql, pageNo,
					pageSize, 4, startDate, endDate); // 下载

			if (clickResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();

			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setDownNumWeek(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
				} else {
					continue;
				}
				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
//				if (i % 1000 == 0) {
//					try {
//						tx.commit();
//					} catch (HibernateException e) {
//						tx.rollback();
//						e.printStackTrace();
//					}		
//					tx = session.beginTransaction();
//				}

			}
			try {
				tx.commit();
			} catch (HibernateException e) {
				tx.rollback();
				e.printStackTrace();
			}
			SessionFactoryUtils.closeSession(session);
		}

		pageNo = 1;
		i = 0;
		hasResult = true;

		// 搜索
		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 2, startDate, endDate); // 搜索

			if (searchResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setSearchNumWeek(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
//				if (i % 1000 == 0) {
//					try {
//						tx.commit();
//					} catch (HibernateException e) {
//						tx.rollback();
//						e.printStackTrace();
//					}		
//					tx = session.beginTransaction();
//				}

			}
			try {
				tx.commit();
			} catch (HibernateException e) {
				tx.rollback();
				e.printStackTrace();
			}
			SessionFactoryUtils.closeSession(session);

		}
		
		pageNo = 1;
		i = 0;
		hasResult = true;

		// 搜索
		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 1, startDate, endDate); // 点击

			if (searchResult.size() < pageSize)
				hasResult = false;
			else
				pageNo++;

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setRankingNumWeek(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存
				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}


			}
			try {
				tx.commit();
			} catch (HibernateException e) {
				tx.rollback();
				e.printStackTrace();
			}
			SessionFactoryUtils.closeSession(session);

		}


	}

	/***************************************************************************
	 * 周收藏数
	 * 
	 * @author penglei
	 */
	private void doFavorites() {
		String hql = "select f.contentId,count(f.mobile) from Favorites f where f.createTime between ? and ? and f.contentId is not null group by f.contentId";
		String type = "Favorites";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -7);
		universalMethod(hql, type, new Object[] { startDate, endDate });

	}

	/***************************************************************************
	 * 周订购统计
	 * 
	 * @author penglei
	 */
	private void doUserBuy() {
		String hql = "select u.contentId,count(u.mobile) from UserBuy u where u.createTime between ? and ? and u.contentId is not null group by u.contentId";
		String type = "UserBuy";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -7);
		universalMethod(hql, type, new Object[] { startDate, endDate });

	}

	/***************************************************************************
	 * 周留言统计
	 * 
	 * @author penglei
	 */
	private void doMsgRecord() {
		String hql = "select m.contentId,count(m.mobile) from MsgRecord m where m.status in(:values) and m.msgType= :msgType and m.createTime between :startDate and :endDate and m.contentId is not null group by m.contentId";
		String type = "MsgRecord";
		Object[] status = { 1, 2 };// 1：发布未审；2：发布已审
		Integer msgType = 1; // 对内容留言
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -7);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("values", status);
		params.put("msgType", msgType);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * 周投票统计
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

		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -7);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("itemId", itemIds);
		universalMethod(hql, type, params);
	}

	/***
	 * 周统计PV
	 * 
	 * @author penglei
	 */
	private void doResourceDT() {
		String hql = "select rdt.resourceId,sum(rdt.dpv+rdt.rpv) from ResourceDT  rdt where (rdt.dpv > 0 or rdt.rpv > 0) and rdt.createTime between :startDate and :endDate and rdt.resourceId is not null group by rdt.resourceId";
		String type = "ResourceDT";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -7);
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
					System.out.println("------------------------ 周size ==="
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
					System.out.println("------------------------ 周size ==="
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

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();

			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					if (type.equalsIgnoreCase("Favorites")) {
						resource.setFavNumWeek(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("UserBuy")) {
						resource.setOrderNumWeek(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("MsgRecord")) {
						resource.setMsgNumWeek(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("VoteResult")) {
						resource.setVoteNumWeek(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("ResourceDT")) {
						resource.setRankingNumWeek(((Long) objs[1]).intValue()); // 设置周PV总数

					}

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // 清除缓存

				} else {
					continue;
				}
				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
//				if (i % 1000 == 0) {
//					try {
//						tx.commit();
//					} catch (HibernateException e) {
//						tx.rollback();
//						e.printStackTrace();
//					}		
//					tx = session.beginTransaction();
//				}

			}
			try {
				tx.commit();
			} catch (HibernateException e) {
				tx.rollback();
				e.printStackTrace();
			}
			SessionFactoryUtils.closeSession(session);
		}
	}
}
