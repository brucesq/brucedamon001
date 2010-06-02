/**
 * 
 */
package com.hunthawk.reader.pps;

import org.apache.log4j.Logger;

/**
 * @author BruceSun
 *
 */
public class StatisticsLog {
	private static Logger logger = Logger.getLogger(StatisticsLog.class);
	/**
	 * 统计日志
	 * @param type 1点击、2搜索、4下载、 11书名搜索、 12作者搜索、 13关键字搜索  、15出版社搜索 
	 *            
	 * 			   --报纸		  21		  				23				25
	 * 			   --杂志		  31		  				33				35
	 * 			   --漫画		  41		  42			43				45
	 *             --视频          61          62            63              65
	 *             --资讯          71          72            73              74
	 * @param content
	 */
	public static void logStat(int type,String content){
		logger.info(type+"###"+content);
	}
	
	
}
