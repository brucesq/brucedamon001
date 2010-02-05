/**
 * 
 */
package com.hunthawk.reader.timer.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.domain.statistics.URLConfigGroup;
import com.hunthawk.reader.domain.statistics.URLDataReport;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;

/**
 * @author BruceSun
 * 
 */
public class MonthReportJob {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void doJob() {
		long startTime = System.currentTimeMillis();
		Date date = new Date();
		date = DateUtils.addMonths(date, -1);
		statUrl(date);
		long endTime = System.currentTimeMillis();
		System.out.println("End Month" + date.toString()
				+ " Report Process  Spend=" + (endTime - startTime) + "ms");
	}

	private void statUrl(Date date) {
		String strDate = ToolDateUtil.dateToString(date, "yyyy-MM");
		String deleteHql = "delete from URLDataReport where (statType = 11 or statType = 12) and dataTime like '"
				+ strDate + "%'  ";
		int count = controller.executeUpdate(deleteHql);
		System.out.println("Month UrlData Delete count=" + count);

		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions
				.add(new CompareExpression("startDate", date, CompareType.Le));
		expressions.add(new CompareExpression("endDate", date, CompareType.Ge));
		List<URLConfig> configs = controller.findBy(URLConfig.class, 1, 100000,
				expressions);
		String statHql = "select  count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '"
				+ strDate + "%' and l.requestUrl like ";

		for (URLConfig config : configs) {
			String hql = statHql + "'" + config.getUrl().replaceAll("\\*", "%")
					+ "'";
			System.out.println("Config SQL" + hql);
			// TODO:
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			List<Object[]> urlStat = controller.findBy(hql);
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
			for (Object[] objs : urlStat) {
				if (((Long) objs[0]).intValue() == 0) {
					continue;
				}
				URLDataReport urlData = new URLDataReport();
				urlData.setStatType(11);
				urlData.setConfigId(config.getId());
				urlData.setDataTime(strDate);
				urlData.setPageViews(((Long) objs[0]).intValue());
				urlData.setUserViews(((Long) objs[1]).intValue());
				urlData.setBytes(((Long) objs[2]).intValue());
				urlData.setIpViews(((Long) objs[3]).intValue());
				controller.save(urlData);
			}
		}

		List<URLConfigGroup> groups = controller.getAll(URLConfigGroup.class);

		statHql = "select  count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '"
				+ strDate + "%' and ( ";

		for (URLConfigGroup group : groups) {
			String hql = statHql;
			int i = 0;
			if (group.getConfigs().size() == 0) {
				continue;
			}
			for (URLConfig config : group.getConfigs()) {
				i++;
				hql += " l.requestUrl like '"
						+ config.getUrl().replaceAll("\\*", "%") + "'";
				if (i < group.getConfigs().size()) {
					hql += " or ";
				}
			}
			hql += " ) ";
			System.out.println("GROUP SQL" + hql);
			// TODO:
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			List<Object[]> urlStat = controller.findBy(hql);
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
			for (Object[] objs : urlStat) {
				if (((Long) objs[0]).intValue() == 0) {
					continue;
				}
				URLDataReport urlData = new URLDataReport();
				urlData.setStatType(12);
				urlData.setConfigId(group.getId());
				urlData.setDataTime(strDate);
				urlData.setPageViews(((Long) objs[0]).intValue());
				urlData.setUserViews(((Long) objs[1]).intValue());
				urlData.setBytes(((Long) objs[2]).intValue());
				urlData.setIpViews(((Long) objs[3]).intValue());
				controller.save(urlData);
			}
		}

	}
}
