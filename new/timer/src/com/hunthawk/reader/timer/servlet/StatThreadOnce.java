/**
 * 
 */
package com.hunthawk.reader.timer.servlet;

import java.util.Date;

import com.hunthawk.reader.timer.job.StatDataRankDate;

/**
 * @author BruceSun
 *
 */
public class StatThreadOnce extends Thread {
	StatDataRankDate statDataRankDate ;
	public StatThreadOnce(StatDataRankDate statDataRankDate){
		this.statDataRankDate = statDataRankDate;
	}
	public void run(){
		System.out.println("执行定时任务开始..."+new Date());
		statDataRankDate.doJob();
		System.out.println("执行定时任务完毕..."+ new Date());
	}
}
