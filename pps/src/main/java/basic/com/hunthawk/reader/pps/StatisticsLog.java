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
	 * ͳ����־
	 * @param type 1�����2������4���ء� 11���������� 12���������� 13�ؼ�������  ��15���������� 
	 *            
	 * 			   --��ֽ		  21		  				23				25
	 * 			   --��־		  31		  				33				35
	 * 			   --����		  41		  42			43				45
	 *             --��Ƶ          61          62            63              65
	 *             --��Ѷ          71          72            73              74
	 * @param content
	 */
	public static void logStat(int type,String content){
		logger.info(type+"###"+content);
	}
	
	
}
