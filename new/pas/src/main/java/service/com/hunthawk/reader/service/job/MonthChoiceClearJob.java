/**
 * 
 */
package com.hunthawk.reader.service.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.HibernateGenericController;

/**
 * 月初清空用户包月图书选择信息
 * 
 * @author BruceSun
 * 
 */
public class MonthChoiceClearJob {

	private HibernateGenericController controller;

	private static final Logger logger = LoggerFactory
			.getLogger(MonthChoiceClearJob.class);

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void doJob() {
		
		Date today = new Date();
		logger.info("Do MonthChoiceClearJob....."+today.toLocaleString());
		String hql = " delete UserBuyMonthChoice where createTime < ? ";
		controller.executeUpdate(hql, today);
	}
}
