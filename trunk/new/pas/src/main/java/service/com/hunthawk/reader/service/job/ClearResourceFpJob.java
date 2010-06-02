/**
 * 
 */
package com.hunthawk.reader.service.job;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.hunthawk.framework.HibernateGenericController;

/**
 * @author sunquanzhi
 * 
 */
public class ClearResourceFpJob {
	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void doJob() {
		Date today = new Date();
		Date clearDate = DateUtils.addDays(today, -30);
		String hql = "delete UserFootprint where createTime < ? ";
		System.out.println("CLEAR RESOURCE FP :"+hql+":"+clearDate.toLocaleString());
		controller.executeUpdate(hql, clearDate);
	}
}
