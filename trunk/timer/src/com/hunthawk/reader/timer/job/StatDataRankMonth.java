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
 * ��ͳ������
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
		System.out.println("StatDataRankMonth Start:[��ͳ��]");
		doStatisticsJob();
		doFavorites(); // ���ղ�����
		doUserBuy(); // �¶���ͳ��
		doMsgRecord();// ������ͳ��
		doVoteSubItem();// ��ͶƱͳ��
//		doResourceDT();// ͳ��Զ�̷�������PV
		System.out.println("StatDataRankMonth End:[��ͳ��]"
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

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (Object[] objs : clickResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setDownNumMonth(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // �������
				} else {
					continue;
				}

				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
				if (i % 1000 == 0) {
					try {
						tx.commit();
					} catch (HibernateException e) {
						tx.rollback();
						e.printStackTrace();
					}		
					tx = session.beginTransaction();
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

		// ����
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

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setSearchNumMonth(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // �������
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

			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (Object[] objs : searchResult) {
				i++;
				ResourceAll resource = (ResourceAll) session.get(
						ResourceAll.class, objs[0].toString());
				if (resource != null) {
					resource.setRankingNumMonth(((Long) objs[1]).intValue());

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // �������
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

	public Date[] getDate() {
		Date endDate = new Date();
		Date startDate = DateUtils.addDays(endDate, -30);

		return new Date[] { startDate, endDate };
	}

	/**
	 * ���ղ�ͳ��
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
	 * �¶���ͳ��
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
	 * ������ͳ��
	 * 
	 * @author penglei
	 */
	private void doMsgRecord() {
		String hql = "select m.contentId,count(m.mobile) from MsgRecord m where m.status in(:values) and m.msgType= :msgType and m.createTime between :startDate and :endDate and m.contentId is not null group by m.contentId";
		String type = "MsgRecord";
		Object[] status = { 1, 2 };// 1������δ��2����������
		Integer msgType = 1; // ����������
		Date[] returnDate = getDate();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("values", status);
		params.put("msgType", msgType);
		params.put("startDate", returnDate[0]);
		params.put("endDate", returnDate[1]);
		universalMethod(hql, type, params);
	}

	/***************************************************************************
	 * ��ͶƱͳ��
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

	/***
	 * ��ͳ��PV
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
							.setCustomerType(DataSourceMap.DATASOURCE2); // ����Զ������Դ
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // ���
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE1); // ���ñ�������Դ
					System.out.println("------------------------ ��size ==="
							+ clickResult.size());
				} else {
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // ���
				}
			} else {
				Map values = (Map) obj;
				if (type.equalsIgnoreCase("ResourceDT")) {
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE2); // ����Զ������Դ
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // ���
					CustomerContextHolder
							.setCustomerType(DataSourceMap.DATASOURCE1); // ���ñ�������Դ
					System.out.println("------------------------ ��size ==="
							+ clickResult.size());
				} else {
					clickResult = controller.findBy(hql, pageNo, pageSize,
							values); // ���
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
						resource.setFavNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("UserBuy")) {
						resource.setOrderNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("MsgRecord")) {
						resource.setMsgNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("VoteResult")) {
						resource.setVoteNumMonth(((Long) objs[1]).intValue());
					} else if (type.equalsIgnoreCase("ResourceDT")) {
						resource
								.setRankingNumMonth(((Long) objs[1]).intValue()); // ������PV����

					}

					String resKey = Utility.getMemcachedKey(ResourceAll.class,
							resource.getId());
					memcached.delete(resKey); // �������

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
