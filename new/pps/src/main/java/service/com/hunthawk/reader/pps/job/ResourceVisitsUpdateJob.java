/**
 * 
 */
package com.hunthawk.reader.pps.job;

import org.apache.log4j.Logger;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.reader.pps.StatisticsLog;
import com.hunthawk.reader.pps.StatisticsUALog;
import com.hunthawk.reader.pps.VoteResultLog;
import com.hunthawk.reader.pps.AccessLog;
import com.hunthawk.reader.pps.service.ResourceService;

/**
 * @author BruceSun
 * 
 */
public class ResourceVisitsUpdateJob {

	private static Logger logger = Logger
			.getLogger(ResourceVisitsUpdateJob.class);

	private HibernateGenericController controller;

	private ResourceService resourceService;

	private MemCachedClientWrapper memcached;

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
	public void doJob(){
		StatisticsLog.logStat(99,"noop");
		VoteResultLog.logVote(9, "10000001", "10000000000", 1);
		StatisticsUALog.logUA("test", "test"); //添加一些数据让日志文件正常生成

		AccessLog.log("10000000000",0,1,"Test","127.0.0.1","http://127.0.0.1","Test","http://127.0.0.1");

//		Set<String> allKeys = resourceService.getAllResourceVisitKey();
//		logger.info("ResourceVisitsUpdateJob start keys("+allKeys.size()+") startTime= "+new Date());
//		long startTime = System.currentTimeMillis();
//		int i = 0;
//		for(String key : allKeys){
//			i++;
//			if(i % 100 == 0){
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//				}
//			}
//			String memKey = Utility.getMemcachedKey(ResourceAll.class,
//					"hits",key);
//			try{
//				long count = memcached.getCounter(memKey);
//				if (count > 0) {
//					ResourceAll resource = resourceService.getResource(key);
//					if(resource != null && (resource.getDownnum() == null || resource.getDownnum() < count)){
//						resource.setDownnum(((Long)count).intValue());
//						controller.update(resource);
//						String resKey = Utility.getMemcachedKey(ResourceAll.class, key);
//						memcached.deleteForLocalMedium(resKey);
//					}
//					
//				}
//			}catch(Exception e){
//				
//			}
//		}
//		logger.info("ResourceVisitsUpdateJob end spend("+(System.currentTimeMillis()-startTime)+"ms) endTime= "+new Date());
//		
	}

}
