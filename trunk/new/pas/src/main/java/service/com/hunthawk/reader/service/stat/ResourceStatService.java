/**
 * 
 */
package com.hunthawk.reader.service.stat;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.statistics.ResourceDataReport;
import com.hunthawk.reader.domain.statistics.UADataReport;

/**
 * @author sunquanzhi
 *
 */
public interface ResourceStatService {
	
	public Long getResourceReportResultCount(
			Collection<HibernateExpression> expressions);

	public List<ResourceDataReport> findResourceReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);
	
	public Long getUaReportResultCount(
			Collection<HibernateExpression> expressions);

	public List<UADataReport> findUaReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

}
