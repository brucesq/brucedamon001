/**
 * 
 */
package com.hunthawk.reader.timer.servlet;

import org.apache.log4j.Logger;

import com.hunthawk.reader.timer.job.StatDataRankSummary;

/**
 * @author BruceSun
 *
 */
public class StatThread extends Thread {
	
	protected static Logger logger = Logger.getLogger(StatThread.class);

	StatDataRankSummary statDataRankSummary  ;
	
	private int type ;
	public StatThread(StatDataRankSummary statDataRankSummary ,int type ){
		this.statDataRankSummary = statDataRankSummary;
		this.type = type;
	}
	public void run(){
		logger.info("汇总任务执行开始...");
		if(type == 1){
			statDataRankSummary.doJob();
		}else if(type ==2){
			statDataRankSummary.doResourceDTJob();
		}else if(type == 3){
			statDataRankSummary.doResourceStatisticsJob();
		}
		
		logger.info("汇总任务任务完毕...");
	}
}