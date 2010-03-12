/**
 * 
 */
package com.hunthawk.reader.service.inter.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.inter.Remind;
import com.hunthawk.reader.domain.inter.RemindMobile;
import com.hunthawk.reader.service.inter.RemindService;

/**
 * @author BruceSun
 * 
 */
public class RemindServiceImpl implements RemindService {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void addRemind(Remind remind, List<String> mobiles) throws Exception {
		remind.setStatus(0);
		if (controller.isUnique(Remind.class, remind, "name")) {
			controller.save(remind);
		} else {
			throw new Exception("名称重复!");
		}
		for (String mobile : mobiles) {
			RemindMobile rm = new RemindMobile();
			rm.setMobile(mobile);
			rm.setRemindId(remind.getId());
			rm.setStatus(0);
			try {
				controller.save(rm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void deleteRemind(Remind remind) throws Exception {
		remind.setStatus(3);
		controller.update(remind);
	}

	public List<Remind> findReminds(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		expressions
				.add(new CompareExpression("status", 3, CompareType.NotEqual));
		return controller.findBy(Remind.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public Long getRemindCount(Collection<HibernateExpression> expressions) {
		expressions
				.add(new CompareExpression("status", 3, CompareType.NotEqual));
		return controller.getResultCount(Remind.class, expressions);
	}

	public int getReservationCount(String contentId){
		String counthql = "select count(distinct  buy.mobile) from Reservation buy where buy.contentId = ? ";
		List<Long> result = controller.findBy(counthql, contentId);
		return result.get(0).intValue();
	}
	
	public int getAllFeeUserCount(){
		String counthql = "select count(distinct  buy.mobile) from UserBuy buy ";
		List<Long> result = controller.findBy(counthql);
		return result.get(0).intValue();
	}
	
	public int getAllReservationCount(){
		String counthql = "select count(distinct  buy.mobile) from Reservation buy ";
		List<Long> result = controller.findBy(counthql);
		return result.get(0).intValue();
	}
	public void sendRemind(Remind remind) {
		// 增加号码
		if(remind.getStatus() != 0)
			return;
		remind.setStatus(1);
		controller.update(remind);
		if (remind.getAllFeeuser() == 1) {
			String counthql = "select count(distinct  buy.mobile) from UserBuy buy ";
			String findhql = "select distinct  buy.mobile from UserBuy buy order by buy.mobile";
			List<Long> result = controller.findBy(counthql);
			int pageCount = (result.get(0).intValue() / 1000) + 1;
			for (int i = 1; i <= pageCount; i++) {
				List<String> mobiles = controller.findBy(findhql, i, 1000);
				for (String mobile : mobiles) {
					RemindMobile rm = new RemindMobile();
					rm.setMobile(mobile);
					rm.setRemindId(remind.getId());
					rm.setStatus(0);
					try {
						controller.save(rm);
					} catch (Exception e) {
					}
				}

			}
		}
		if (remind.getAllReservation() == 1) {
			String counthql = "select count(distinct  buy.mobile) from Reservation buy ";
			String findhql = "select distinct  buy.mobile from Reservation buy order by buy.mobile";
			List<Long> result = controller.findBy(counthql);
			int pageCount = result.get(0).intValue() / 1000 + 1;
			for (int i = 1; i <= pageCount; i++) {
				List<String> mobiles = controller.findBy(findhql, i, 1000);
				for (String mobile : mobiles) {
					RemindMobile rm = new RemindMobile();
					rm.setMobile(mobile);
					rm.setRemindId(remind.getId());
					rm.setStatus(0);
					try {
						controller.save(rm);
					} catch (Exception e) {
					}
				}

			}
		}
		if (StringUtils.isNotEmpty(remind.getContentId())) {
			String counthql = "select count(distinct  buy.mobile) from Reservation buy where buy.contentId = ? ";
			String findhql = "select distinct  buy.mobile from Reservation buy where buy.contentId = ? order by buy.mobile";
			List<Long> result = controller.findBy(counthql, remind
					.getContentId());
			int pageCount = result.get(0).intValue() / 1000 + 1;
			for (int i = 1; i <= pageCount; i++) {
				List<String> mobiles = controller.findBy(findhql, i, 1000,
						remind.getContentId());
				for (String mobile : mobiles) {
					RemindMobile rm = new RemindMobile();
					rm.setMobile(mobile);
					rm.setRemindId(remind.getId());
					rm.setStatus(0);
					try {
						controller.save(rm);
					} catch (Exception e) {
					}
				}

			}
		}
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("status", 0, CompareType.Equal));
		expressions.add(new CompareExpression("remindId", remind.getId(),
				CompareType.Equal));
		List<RemindMobile> rms = controller.findBy(RemindMobile.class, 1, 1000,
				expressions);
		int count = 0;
		while (rms.size() > 0) {
			updateStatus(rms);
			for(RemindMobile rm : rms){
				sendSMS(rm.getMobile(),remind.getPushWord(),remind.getPushUrl());
				rm.setSendTime(new Date());
				rm.setStatus(2);
				controller.update(rm);
				count ++;
			}
			rms =  controller.findBy(RemindMobile.class, 1, 1000,
					expressions);
		}
		remind.setSendNum(count);
		remind.setStatus(2);
		controller.update(remind);
	}
	
	

	public void updateRemind(Remind remind, int type, List<String> mobiles)
			throws Exception {
		controller.update(remind);
		if (type == 0) {
			for (String mobile : mobiles) {
				RemindMobile rm = new RemindMobile();
				rm.setMobile(mobile);
				rm.setRemindId(remind.getId());
				rm.setStatus(0);
				try {
					controller.save(rm);
				} catch (Exception e) {

				}
			}

		} else if (type == 1) {
			String hql = "delete RemindMobile where remindId = ? ";
			controller.executeUpdate(hql, remind.getId());
			for (String mobile : mobiles) {
				RemindMobile rm = new RemindMobile();
				rm.setMobile(mobile);
				rm.setRemindId(remind.getId());
				rm.setStatus(0);
				try {
					controller.save(rm);
				} catch (Exception e) {

				}
			}
		}

	}

	private void sendSMS(String mobile, String pushword, String pushurl) {
		// TODO:实现接口
		System.out.println(mobile+":"+pushword+":"+pushurl);
	}

	private void updateStatus(List<RemindMobile> rms){
		Session session = controller.getHibernateTemplate().getSessionFactory().openSession();
		Transaction tran = session.beginTransaction();
        session.setCacheMode(CacheMode.IGNORE); 
        PreparedStatement stmt;
        try {
            stmt = session.connection().prepareStatement("UPDATE READER_INTER_REMIND_MOBILE T SET T.STATUS = ? WHERE T.MOBILE = ? AND T.REMIND_ID = ? ");
            for (RemindMobile rm : rms) {
               
                stmt.setInt(1, 1);
                stmt.setString(2, rm.getMobile());
                stmt.setInt(3, rm.getRemindId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            tran.rollback();
        }
        tran.commit();
        SessionFactoryUtils.closeSession(session);
	}
}
