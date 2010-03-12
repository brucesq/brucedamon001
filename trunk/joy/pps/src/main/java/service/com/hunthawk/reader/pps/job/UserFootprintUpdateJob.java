/**
 * 
 */
package com.hunthawk.reader.pps.job;

import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.custom.UserBookPk;
import com.hunthawk.reader.domain.custom.UserFootprint;
import com.hunthawk.reader.pps.service.CustomService;

/**
 * 定时更新用户访问地址
 * @author BruceSun
 *
 */
public class UserFootprintUpdateJob {

	private static Logger logger = Logger.getLogger(UserFootprintUpdateJob.class);
	
	private HibernateGenericController controller;
	
	private CustomService customService;
	
	private MemCachedClientWrapper memcached;
	
	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}
	
	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}
	
	public void setCustomService(CustomService customService) {
		this.customService = customService;
	}

	public void doJob(){
		Set<String> allKeys = customService.getAllUserFootprintKey();
		logger.info("UserFootprintUpdateJob start keys("+allKeys.size()+") startTime= "+new Date());
		long startTime = System.currentTimeMillis();
		int i = 0;
		for(String key : allKeys){
			i++;
			if(i % 100 == 0){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			String memKey = Utility.getMemcachedKey(UserFootprint.class,key);
			try{
				UserFootprint footprint = (UserFootprint)memcached.get(memKey);
				if (footprint != null) {
					UserBookPk pk = new UserBookPk();
					pk.setContentId(footprint.getContentId());
					pk.setMobile(footprint.getMobile());
					UserFootprint footprintDb = controller.get(UserFootprint.class, pk);
					if(footprintDb == null){
						controller.save(footprint);
					}else if(!footprintDb.getUrl().equals(footprint.getUrl())){
						controller.update(footprint);
					}
				}
			}catch(Exception e){
				
			}
		}
		logger.info("UserFootprintUpdateJob end  count("+i+") spend("+(System.currentTimeMillis()-startTime)+"ms) endTime= "+new Date());
		
	}
}
