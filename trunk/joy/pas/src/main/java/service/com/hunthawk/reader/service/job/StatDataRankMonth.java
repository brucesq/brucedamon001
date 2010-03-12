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

public class StatDataRankMonth {

	private HibernateGenericController controller;

	private SystemService systemService;

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
		doFavorites();// 月收藏统计
		doUserBuy();// 月订购统计
		doMsgRecord();// 月留言统计
		doVoteSubItem();// 月投票统计
		System.out.println("StatDataRankMonth End:[月统计]"
				+ (System.currentTimeMillis() - start));
	}

	@SuppressWarnings("unchecked")
	public void doStatisticsJob() {

		String hql = "select s.content,sum(views) from StatData s where s.type = ? and createTime between ? and ? group by s.content";

		Date[] returnDate = getDate();

		int i = 0;
		int pageNo = 1;
		int pageSize = 1000;
		boolean hasResult = true;

		while (hasResult) {
			List<Object[]> clickResult = controller.findBy(hql, pageNo,
					pageSize, 1, returnDate[0], returnDate[1]);

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
					resource.setDownNumMonth(((Long) objs[1]).intValue());
				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}

			}
			tx.commit();
			SessionFactoryUtils.closeSession(session);
		}

		// 搜索
		i = 0;
		pageNo = 1;
		hasResult = true;

		while (hasResult) {
			List<Object[]> searchResult = controller.findBy(hql, pageNo,
					pageSize, 2, returnDate[0], returnDate[1]);

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
					resource.setSearchNumMonth(((Long) objs[1]).intValue());
				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}

			}
			tx.commit();
			SessionFactoryUtils.closeSession(session);
		}

	}

	public Date[] getDate() {
		// Calendar today = Calendar.getInstance();
		// Date[] returnDate = new Date[2];
		//
		// int month = today.get(Calendar.MONTH); // 得到当前月
		// int currentYear = today.get(Calendar.YEAR); // 得到当前年
		// String[] formate = new String[] { "yyyy-MM-dd hh:mm:ss" };
		//
		// try {
		// if (month == 0) { // 当前月 1 月
		// returnDate[0] = DateUtils.parseDate((currentYear - 1)
		// + "-12-1 00:00:00", formate);
		// returnDate[1] = DateUtils.parseDate((currentYear - 1)
		// + "-12-31 24:00:00", formate);
		// } else {
		// returnDate[0] = DateUtils.parseDate(currentYear + "-" + month
		// + "-1 00:00:00", formate);
		// returnDate[1] = DateUtils.parseDate(currentYear + "-"
		// + (month + 1) + "-1 00:00:00", formate);
		// }
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
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
		String hql = "select f.contentId,count(f.mobile) from Favorites f where f.createTime between ? and ? group by f.contentId";
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
		String hql = "select u.contentId,count(u.mobile) from UserBuy u where u.createTime between ? and ? group by u.contentId";
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
		String hql = "select m.contentId,count(m.mobile) from MsgRecord m where m.status in(:values) and m.msgType= :msgType and m.createTime between :startDate and :endDate group by m.contentId";
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
		Date[] returnDate = getDate();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", returnDate[0]);
		params.put("endDate", returnDate[1]);
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
				System.out.println(objs[0].toString() + ":"
						+ objs[1].toString());
				if (resource != null) {
					if (type.equalsIgnoreCase("Favorites")) {
						resource.setFavNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("UserBuy")) {
						resource.setOrderNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("MsgRecord")) {
						resource.setMsgNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("VoteResult")) {
						resource.setVoteNumMonth(((Long) objs[1]).intValue());
					}

				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}

			}
			tx.commit();
			SessionFactoryUtils.closeSession(session);
		}
	}
}
