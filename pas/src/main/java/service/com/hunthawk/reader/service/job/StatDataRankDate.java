package com.hunthawk.reader.service.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 天统计任务
 * 
 * @author liuyan
 * 
 */
public class StatDataRankDate {

	private HibernateGenericController controller;

	private SystemService systemService;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void doJob() {
		long start = System.currentTimeMillis();
		System.out.println("StatDataRankDate Start:[日统计]");
		doStatisticsJob();
		doFavorites(); // 日收藏总数
		doUserBuy(); // 日订购统计
		doMsgRecord();// 日留言统计
		doVoteSubItem();// 日投票统计
		System.out.println("StatDataRankDate End:[日统计]"
				+ (System.currentTimeMillis() - start));
		// 统计周
		StatDataRankWeek sdrw = new StatDataRankWeek();
		sdrw.setHibernateGenericController(controller);
		sdrw.setSystemService(systemService);
		sdrw.doJob();

		// 统计月
		StatDataRankMonth sdrm = new StatDataRankMonth();
		sdrm.setHibernateGenericController(controller);
		sdrm.setSystemService(systemService);
		sdrm.doJob();
	}

	private void doStatisticsJob() {
		String hql = "select s.content,sum(views) from StatData s where s.type = ? and createTime between ? and ? group by s.content";

		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);

		int i = 0;
		int pageNo = 1;
		int pageSize = 1000;
		boolean hasResult = true;

		while (hasResult) {
			List<Object[]> clickResult = controller.findBy(hql, pageNo,
					pageSize, 1, startDate, endDate); // 点击

			if (clickResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();

			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());

				if (resource != null) {
					resource.setDownNumDate(((Long) objs[1]).intValue());
				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}

			}
			tx.commit(); // 防止不是整数的情况下，数据既没提交也没释放
			SessionFactoryUtils.closeSession(session);
		}

		pageNo = 1;
		i = 0;
		hasResult = true;

		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 2, startDate, endDate); // 搜索

			if (searchResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}
			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setSearchNumDate(((Long) objs[1]).intValue());
				} else {
					continue;
				}
				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}

			}
			tx.commit(); // 防止不是整数的情况下，数据既没提交也没释放
			SessionFactoryUtils.closeSession(session);
		}

	}

	/***************************************************************************
	 * 日收藏统计
	 * 
	 * @author penglei
	 */
	private void doFavorites() {
		String hql = "select f.contentId,count(f.mobile) from Favorites f where f.createTime between ? and ? group by f.contentId";
		String type = "Favorites";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		universalMethod(hql, type, new Object[] { startDate, endDate });

	}

	/***************************************************************************
	 * 日订购统计
	 * 
	 * @author penglei
	 */
	private void doUserBuy() {
		String hql = "select u.contentId,count(u.mobile) from UserBuy u where u.createTime between ? and ? group by u.contentId";
		String type = "UserBuy";
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		universalMethod(hql, type, new Object[] { startDate, endDate });

	}

	/***************************************************************************
	 * 日留言统计
	 * 
	 * @author penglei
	 */
	private void doMsgRecord() {
		String hql = "select m.contentId,count(m.mobile) from MsgRecord m where m.status in(:values) and m.msgType= :msgType and m.createTime between :startDate and :endDate group by m.contentId";
		String type = "MsgRecord";
		Object[] status = { 1, 2 };// 1：发布未审；2：发布已审
		Integer msgType = 1; // 对内容留言
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
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
		String hql = "select v.contentId,count(v.id) from VoteResult v where v.contentId is not null and v.itemId in(:itemId) and v.createTime between :startDate and :endDate group by v.contentId";
		String type = "VoteResult";
		String[] itemType = systemService.getVariables("vote_Result").getValue()
				.split(";");
		Integer[] itemIds = new Integer[itemType.length];
		for (int i = 0; i < itemType.length; i++) {
			itemIds[i] = Integer.parseInt(itemType[i]);

		}
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -1);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("itemId", itemIds);
		universalMethod(hql, type, params);
	}

	private void universalMethod(String hql, String type, Object obj) {

		int i = 0;
		int pageNo = 1;
		int pageSize = 1000;
		boolean hasResult = true;

		while (hasResult) {
			List<Object[]> clickResult = null;
			if (obj instanceof Object[]) {
				Object[] values = (Object[]) obj;
				clickResult = controller.findBy(hql, pageNo, pageSize, values); // 点击
			} else {
				Map values = (Map) obj;
				clickResult = controller.findBy(hql, pageNo, pageSize, values); // 点击
			}

			if (clickResult.size() < pageSize) {
				hasResult = false;
			} else {
				pageNo++;
			}

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();

			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				System.out.println(objs[0].toString() + ":"
						+ objs[1].toString());
				if (resource != null) {
					if (type.equals("Favorites")) {
						resource.setFavNumDate(((Long) objs[1]).intValue()); // 设置当天收藏总数
						resource.setFavNum(resource.getFavNumDate()
								+ resource.getFavNum()); // 设置总收藏数
					} else if (type.equalsIgnoreCase("UserBuy")) {
						resource.setOrderNumDate(((Long) objs[1]).intValue()); // 设置当天订购总数
						resource.setOrderNum(resource.getOrderNum()
								+ resource.getOrderNumDate()); // 设置总订购数
					} else if (type.equalsIgnoreCase("MsgRecord")) {
						resource.setMsgNumDate(((Long) objs[1]).intValue()); // 设置当天留言总数
						resource.setMsgNum(resource.getMsgNum()
								+ resource.getMsgNumDate()); // 设置总留言数
					} else if (type.equalsIgnoreCase("VoteResult")) {
						resource.setVoteNumDate(((Long) objs[1]).intValue()); // 设置当天投票总数
						resource.setVoteNum(resource.getVoteNum()
								+ resource.getVoteNumDate()); // 设置总投票数
					}

				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}

			}
			tx.commit(); // 防止不是整数的情况下，数据既没提交也没释放
			SessionFactoryUtils.closeSession(session);
		}
	}

}
