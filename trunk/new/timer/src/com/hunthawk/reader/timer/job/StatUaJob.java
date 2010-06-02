/**
 * 
 */
package com.hunthawk.reader.timer.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.statistics.UADataReport;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;

/**
 * @author sunquanzhi
 * 
 */
public class StatUaJob {

	private HibernateGenericController controller;

	private Date statDate;

	public void setStatDate(Date statDate) {
		this.statDate = statDate;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void doJob() {
		Date date = new Date();
		if (statDate != null) {
			date = statDate;
		}
		date = DateUtils.addDays(date, -1);
		String strDate = ToolDateUtil.dateToString(date, "yyyy-MM-dd");

		String deleteHql = "delete from UADataReport where requestTime = '"
				+ strDate + "'";
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
		controller.executeUpdate(deleteHql);

		String hql = "select l.uaStart, count(l.id),count(distinct l.msisdn),count(distinct l.ip) from LogData l where l.requestDateTime = '"
				+ strDate + "' and l.uaStart is not null  group by l.uaStart";

		int pageNo = 1;
		System.out.println(hql);

		while (true) {

			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			List<Object[]> uaStat = controller.findBy(hql, pageNo++, 1000);
			for (Object[] objs : uaStat) {
				doUa(objs, strDate);
			}
			if (uaStat.size() < 1000) {
				break;
			}
		}
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
	}

	private void doUa(Object[] objs, String date) {
		try {
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			UADataReport r = new UADataReport();
			r.setUa((String) objs[0]);
			r.setRequestTime((String) date);
			r.setPageViews(((Long) objs[1]).intValue());
			r.setUserViews(((Long) objs[2]).intValue());
			r.setIpViews(((Long) objs[3]).intValue());
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			controller.save(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
