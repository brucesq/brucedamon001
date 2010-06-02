/**
 * 
 */
package com.hunthawk.reader.service.system;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.statistics.DataReport;
import com.hunthawk.reader.domain.statistics.IPDataReport;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.domain.statistics.URLConfigGroup;
import com.hunthawk.reader.domain.statistics.URLDataReport;
import com.hunthawk.reader.domain.statistics.URLHourDataReport;

/**
 * @author BruceSun
 * 
 */
public interface StatisticsService {

	public Long getDataReportResultCount(
			Collection<HibernateExpression> expressions);

	public List<DataReport> findDataReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long getURLConfigResultCount(
			Collection<HibernateExpression> expressions);

	public List<URLConfig> findURLConfigBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	@Logable(name = "URLConfig", action = "add", property = { "id=ID,title=名称,startDate=开始时间,endDate=结束时间,creator=创建人,createTime=创建时间" })
	public void addURLConfig(URLConfig config);

	@Logable(name = "URLConfig", action = "update", property = { "id=ID,title=名称,startDate=开始时间,endDate=结束时间,creator=创建人,createTime=创建时间" })
	public void updateURLConfig(URLConfig config);
	
	@Logable(name = "URLConfig", action = "delete", property = { "id=ID,title=名称,startDate=开始时间,endDate=结束时间,creator=创建人,createTime=创建时间" })
	public void deleteURLConfig(URLConfig config)throws Exception;

	public URLConfig getURLConfig(int id);

	public Long getURLConfigGroupResultCount(
			Collection<HibernateExpression> expressions);

	public List<URLConfigGroup> findURLConfigGroupBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	@Logable(name = "URLConfigGroup", action = "add", property = { "id=ID,title=名称,creator=创建人,createTime=创建时间" })
	public void addURLConfigGroup(URLConfigGroup configgroup);

	@Logable(name = "URLConfigGroup", action = "update", property = { "id=ID,title=名称,creator=创建人,createTime=创建时间" })
	public void updateURLConfigGroup(URLConfigGroup configgroup);

	@Logable(name = "URLConfigGroup", action = "delete", property = { "id=ID,title=名称,creator=创建人,createTime=创建时间" })
	public void deleteURLConfigGroup(URLConfigGroup configgroup);

	public URLConfigGroup getURLConfigGroup(int id);

	public Long getURLDataReportResultCount(
			Collection<HibernateExpression> expressions);

	public List<URLDataReport> findURLDataReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);
	
	public Long getURLHourDataReportResultCount(
			Collection<HibernateExpression> expressions);

	public List<URLHourDataReport> findURLHourDataReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);
	
	public Long getIPDataReportResultCount(
			Collection<HibernateExpression> expressions);

	public List<IPDataReport> findIPDataReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

}
