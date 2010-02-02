/**
 * 
 */
package com.hunthawk.reader.pps.job;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.pps.service.InteractiveService;

/**
 * 投票更新任务
 * 
 * @author BruceSun
 * 
 */
public class VoteUpdateJob {
	private static Logger logger = Logger
			.getLogger(ResourceVisitsUpdateJob.class);

	private HibernateGenericController controller;

	private InteractiveService interactiveService;

	private MemCachedClientWrapper memcached;

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setInteractiveService(InteractiveService interactiveService) {
		this.interactiveService = interactiveService;
	}

	public void doJob() {
		Map<String, Object[]> allKeys = interactiveService.getAllVoteKey();
		logger.info("VoteUpdateJob start keys(" + allKeys.size()
				+ ") startTime= " + new Date());
		long startTime = System.currentTimeMillis();
		int i = 0;
		for (Map.Entry<String, Object[]> entry : allKeys.entrySet()) {
			i++;
			if (i % 100 == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			String memKey = Utility.getMemcachedKey(VoteSubItem.class, entry
					.getKey());
			try {
				Long count = memcached.getCounter(memKey);
				if (count > 0) {
					VoteSubItem item = controller.get(VoteSubItem.class, entry
							.getKey());
					if (item == null) {
						item = new VoteSubItem();
						item.setId(entry.getKey());
						item.setColumnId((Integer) entry.getValue()[3]);
						item.setContentId((String) entry.getValue()[2]);
						item.setCustomId((String) entry.getValue()[5]);
						item.setItemId((Integer) entry.getValue()[1]);
						item.setProductId((String) entry.getValue()[4]);
						item.setVoteType((Integer) entry.getValue()[0]);
						item.setVoteValue(count.intValue());
						controller.save(item);
					} else if (item.getVoteValue() < count.intValue()) {
						item.setVoteValue(count.intValue());
						controller.update(item);
					}

				}
			} catch (Exception e) {

			}
		}
		logger.info("VoteUpdateJob end spend("
				+ (System.currentTimeMillis() - startTime) + "ms) endTime= "
				+ new Date());

	}

}
