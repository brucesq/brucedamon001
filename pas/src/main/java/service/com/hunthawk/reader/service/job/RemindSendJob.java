/**
 * 
 */
package com.hunthawk.reader.service.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.inter.Remind;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.service.inter.RemindService;

/**
 * @author BruceSun
 *
 */
public class RemindSendJob {
	private RemindService remindService;
	private HibernateGenericController controller;
	public void setRemindService(RemindService remindService) {
		this.remindService = remindService;
	}
	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}
	public void doJob(){
		
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("status", 0, CompareType.Equal));
		expressions.add(new CompareExpression("sendTime", new Date(),
				CompareType.Le));
		List<Remind> reminds = remindService.findReminds(1,100,"sendTime",true,expressions);
		for(Remind remind : reminds){
			remindService.sendRemind(remind);
			Log log = new Log();
			log.setAction("send");
			log.setName("Remind");
			log.setKey("K"+String.valueOf(remind.getId())+",");
			log.setUserId(1);
			log.setLogTime(new Date());
			log.setDetail(remind.getName());
			controller.save(log);
		}
	}
}
