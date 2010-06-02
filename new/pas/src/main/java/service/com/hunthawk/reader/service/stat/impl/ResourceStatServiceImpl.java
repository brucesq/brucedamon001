/**
 * 
 */
package com.hunthawk.reader.service.stat.impl;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.statistics.ResourceDataReport;
import com.hunthawk.reader.domain.statistics.UADataReport;
import com.hunthawk.reader.service.stat.ResourceStatService;

/**
 * @author sunquanzhi
 * 
 */
public class ResourceStatServiceImpl implements ResourceStatService {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.stat.ResourceStatService#findResourceReportBy(int,
	 *      int, java.lang.String, boolean, java.util.Collection)
	 */
	@Override
	public List<ResourceDataReport> findResourceReportBy(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(ResourceDataReport.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.stat.ResourceStatService#getResourceReportResultCount(java.util.Collection)
	 */
	@Override
	public Long getResourceReportResultCount(
			Collection<HibernateExpression> expressions) {
		// TODO Auto-generated method stub
		return controller.getResultCount(ResourceDataReport.class, expressions);
	}

	public Long getUaReportResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(UADataReport.class, expressions);
	}

	public List<UADataReport> findUaReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(UADataReport.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}
}
