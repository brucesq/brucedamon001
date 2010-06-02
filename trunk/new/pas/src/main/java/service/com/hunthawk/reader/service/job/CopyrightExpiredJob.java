/**
 * 
 */
package com.hunthawk.reader.service.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.BetweenExpression;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.Log;

/**
 * 版权过期检查任务
 * 
 * @author BruceSun
 * 
 */
public class CopyrightExpiredJob {

	private HibernateGenericController controller;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CopyrightExpiredJob.class);

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void doJob() {
		List<ResourceReferen> copyrights = getExpiredCopyright();
		
		logger.info("Do CopyrightExpiredJob.....");
		for (ResourceReferen copyright : copyrights) {
			Log log = new Log();
			log.setAction("expired");
			log.setName("Copyright");
			log.setKey("K"+String.valueOf(copyright.getId())+",");
			log.setUserId(1);
			log.setLogTime(new Date());
			log.setDetail(resourceOffline(copyright.getId()));
			controller.save(log);
		}
		logger.info("End CopyrightExpiredJob.....");
	}

	/**
	 * 获取过期的版权
	 * 
	 * @return
	 */
	private List<ResourceReferen> getExpiredCopyright() {
		Date today = new Date();
		Date beginDay = DateUtils.addDays(today, -2);
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		BetweenExpression be = new BetweenExpression("endTime", beginDay, today);
		expressions.add(be);
		return controller.findBy(ResourceReferen.class, 1, Integer.MAX_VALUE,
				expressions);
	}

	private String resourceOffline(Integer copyrightId) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		CompareExpression copyrightE = new CompareExpression("copyrightId",
				copyrightId, CompareType.Equal);
		expressions.add(copyrightE);
		List<ResourceAll> resources = controller.findBy(ResourceAll.class, 1,
				Integer.MAX_VALUE, expressions);
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (ResourceAll resource : resources) {
			resource.setStatus(1);
			controller.update(resource);
			resourceRelOffline(resource.getId());
			builder.append("[");
			builder.append(resource.getId());
			builder.append("]");
		}
		builder.append("}");
		return builder.toString();
	}

	private void resourceRelOffline(String resourceId) {
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		CompareExpression resourceE = new CompareExpression("resourceId",
				resourceId, CompareType.Equal);
		expressions.add(resourceE);
		List<ResourcePackReleation> rels = controller.findBy(
				ResourcePackReleation.class, 1, Integer.MAX_VALUE, expressions);
		for (ResourcePackReleation rel : rels) {
			rel.setStatus(1);
			controller.update(rel);
		}
	}

	public static void main(String[] args) {
		Date today = new Date();
		Date beginDay = DateUtils.addDays(today, -2);
		System.out.println(today.toString());
		System.out.println(beginDay.toString());
	}
}
