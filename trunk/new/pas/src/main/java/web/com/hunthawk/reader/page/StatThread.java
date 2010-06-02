/**
 * 
 */
package com.hunthawk.reader.page;

import com.hunthawk.reader.service.job.StatDataRankDate;
import com.hunthawk.reader.service.job.StatDataRankMonth;
import com.hunthawk.reader.service.job.StatDataRankWeek;

/**
 * @author BruceSun
 *
 */
public class StatThread extends Thread {

	private int type;
	
	private StatDataRankDate statDate;
	
	
	public StatThread(int type, 
			StatDataRankDate statDate) {
		super();
		this.type = type;
		
		this.statDate = statDate;
		
	}

	public void run(){
		
			statDate.doJob();
		
	}
}
