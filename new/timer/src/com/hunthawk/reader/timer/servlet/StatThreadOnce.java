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
		System.out.println("ִ�ж�ʱ����ʼ..."+new Date());
		statDataRankDate.doJob();
		System.out.println("ִ�ж�ʱ�������..."+ new Date());
	}
}
