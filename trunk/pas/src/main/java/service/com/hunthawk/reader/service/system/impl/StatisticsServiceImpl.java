/**
 * 
 */
package com.hunthawk.reader.service.system.impl;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.statistics.DataReport;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.domain.statistics.URLConfigGroup;
import com.hunthawk.reader.domain.statistics.URLDataReport;
import com.hunthawk.reader.service.system.StatisticsService;

/**
 * @author BruceSun
 * 
 */
public class StatisticsServiceImpl implements StatisticsService {
	
	private static final String FIND_GROUP_BY_CONFIG = " from URLConfigGroup group where ? in elements(group.configs) ";
	

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.system.StatisticsService#findDataReportBy(int,
	 *      int, java.lang.String, boolean, java.util.Collection)
	 */
	@Override
	public List<DataReport> findDataReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(DataReport.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.system.StatisticsService#getDataReportResultCount(java.util.Collection)
	 */
	@Override
	public Long getDataReportResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(DataReport.class, expressions);
	}
	
	
	public Long getURLConfigResultCount(
			Collection<HibernateExpression> expressions){
		return controller.getResultCount(URLConfig.class, expressions);
	}

	public List<URLConfig> findURLConfigBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions){
		return controller.findBy(URLConfig.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}
			

	public void addURLConfig(URLConfig config){
		controller.save(config);
	}

	public void updateURLConfig(URLConfig config){
		controller.update(config);
	}
	public void deleteURLConfig(URLConfig config)throws Exception{
		long count = controller.getResultCount(FIND_GROUP_BY_CONFIG,
				new Object[] { config });
		if (count > 0L) {
			throw new Exception("URL配置【" + config.getTitle() + "】已经被" + count
					+ "个组使用!");
		}
		controller.delete(config);
	}

	public URLConfig getURLConfig(int id){
		return controller.get(URLConfig.class, id);
	}

	public Long getURLConfigGroupResultCount(
			Collection<HibernateExpression> expressions){
		return controller.getResultCount(URLConfigGroup.class, expressions);
	}

	public List<URLConfigGroup> findURLConfigGroupBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions){
		return controller.findBy(URLConfigGroup.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public void addURLConfigGroup(URLConfigGroup configgroup){
		controller.save(configgroup);
	}

	public void updateURLConfigGroup(URLConfigGroup configgroup){
		controller.update(configgroup);
	}
	public void deleteURLConfigGroup(URLConfigGroup configgroup){
		controller.delete(configgroup);
	}

	public URLConfigGroup getURLConfigGroup(int id){
		return controller.get(URLConfigGroup.class, id);
	}

	public Long getURLDataReportResultCount(
			Collection<HibernateExpression> expressions){
		return controller.getResultCount(URLDataReport.class, expressions);
	}

	public List<URLDataReport> findURLDataReportBy(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions){
		return controller.findBy(URLDataReport.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

}
