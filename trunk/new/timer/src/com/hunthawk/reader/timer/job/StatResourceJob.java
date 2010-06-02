/**
 * 
 */
package com.hunthawk.reader.timer.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.statistics.ResourceDataReport;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;

/**
 * @author sunquanzhi
 * 
 */
public class StatResourceJob {

	private HibernateGenericController controller;
	
	private Date statDate;
	
	public void setStatDate(Date statDate){
		this.statDate = statDate;
	}

	public Date getStatDate(){
		return this.statDate;
	}
	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void doJob() {
		Date date = new Date();
		if(statDate != null){
			date = statDate;
		}
		date = DateUtils.addDays(date, -1);
		String strDate = ToolDateUtil.dateToString(date, "yyyy-MM-dd");

		String deleteHql = "delete from ResourceDataReport where requestTime = '"
				+ strDate + "'";
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
		controller.executeUpdate(deleteHql);

		String hql = "select l.paraRd, count(l.id),count(distinct l.msisdn),count(distinct l.ip) from LogData l where l.requestDateTime = '"
				+ strDate
				+ "' and l.paraRd is not null and l.paraPg <> 'x' group by l.paraRd";
		int pageNo = 1;
		System.out.println(hql);
		while (true) {
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			List<Object[]> resourceStat = controller
					.findBy(hql, pageNo++, 1000);
			for (Object[] objs : resourceStat) {
				doResource(objs, strDate);
			}
			if (resourceStat.size() < 1000) {
				break;
			}
		}
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
	}

	private void doResource(Object[] objs, String date) {
		try {
			ResourceDataReport r = new ResourceDataReport();
			r.setResourceId((String) objs[0]);
			r.setRequestTime((String) date);
			r.setPageViews(((Long) objs[1]).intValue());
			r.setUserViews(((Long) objs[2]).intValue());
			r.setIpViews(((Long) objs[3]).intValue());
			r.setResourceName(getResourceName(r.getResourceId()));
			r.setResourceType(getResourceType(r.getResourceId()));
			r.setPlayViews(getResourcePlayTimes(r.getResourceId(), date));
			r.setDownViews(getResourceDownViews(r.getResourceId(), date));
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			controller.save(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Integer getResourcePlayTimes(String resourceId, String strDate) {
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
		String hql = " select count(*) from LogData l where l.paraRd='"
				+ resourceId + "'   and l.requestDateTime = '"
				+ strDate + "' and l.paraPg = 'x' and l.requestUrl like '%ucs'";
		List<Long> objs = controller.findBy(hql);
		if (objs.size() > 0) {
			return objs.get(0).intValue();
		}
		return 0;
	}

	private Integer getResourceDownViews(String resourceId, String strDate) {
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
		String hql = " select count(*) from LogData l where l.paraRd='"
				+ resourceId + "'  and l.requestDateTime = '"
				+ strDate + "' and l.paraPg = 'x' and l.requestUrl not like '%ucs'";
		List<Long> objs = controller.findBy(hql);
		if (objs.size() > 0) {
			return objs.get(0).intValue();
		}
		return 0;
	}

	private String getResourceName(String resourceId) {
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
		ResourceAll res = controller.get(ResourceAll.class, resourceId);
		if (res != null) {
			return res.getName();
		}
		return "";
	}

	private String getResourceType(String resourceId) {
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
		List<ResourceResType> types = controller.findBy(ResourceResType.class,
				"rid", resourceId);
		String s = "|";
		for (ResourceResType type : types) {
			s += type.getResTypeId() + "|";
		}
		return s;
	}
}
