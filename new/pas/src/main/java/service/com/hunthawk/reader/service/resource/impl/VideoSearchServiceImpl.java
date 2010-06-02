/**
 * 
 */
package com.hunthawk.reader.service.resource.impl;

import java.util.ArrayList;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.LogicalExpression;
import com.hunthawk.framework.hibernate.LogicalType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.service.resource.VideoSearchService;

/**
 * @author sunquanzhi
 * 
 */
public class VideoSearchServiceImpl implements VideoSearchService {

	private HibernateGenericController controller;
	
	public void setHibernateGenericController(HibernateGenericController controller){
		this.controller = controller;
	}

	public List<Video> search(String partnerId, String q, int pageNo,
			int pageSize, String sort, boolean isAsc) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression statusE = new CompareExpression("status", 0,
				CompareType.Equal);
		expressions.add(statusE);
		expressions.add(new CompareExpression("cpId", Integer.parseInt(partnerId),
				CompareType.Equal));
		HibernateExpression searchName = new CompareExpression("name", "%"+q+"%",
				CompareType.Like);
		HibernateExpression searchKey = new CompareExpression("RKeyword", "%"+q+"%",
				CompareType.Like);
		expressions.add(new LogicalExpression(searchName,searchKey,LogicalType.Or));

		return controller.findBy(Video.class, pageNo, pageSize, sort, isAsc,
				expressions);
	}

	public long searchCount(String partnerId, String q) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression statusE = new CompareExpression("status", 0,
				CompareType.Equal);
		expressions.add(statusE);
		expressions.add(new CompareExpression("cpId", Integer.parseInt(partnerId),
				CompareType.Equal));
		HibernateExpression searchName = new CompareExpression("name", "%"+q+"%",
				CompareType.Like);
		HibernateExpression searchKey = new CompareExpression("RKeyword", "%"+q+"%",
				CompareType.Like);
		expressions.add(new LogicalExpression(searchName,searchKey,LogicalType.Or));
		return controller.getResultCount(Video.class, expressions);
	}
}
