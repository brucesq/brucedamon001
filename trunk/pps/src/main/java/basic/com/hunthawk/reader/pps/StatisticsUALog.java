package com.hunthawk.reader.pps;

import org.apache.log4j.Logger;

public class StatisticsUALog {

private static Logger logger = Logger.getLogger(StatisticsUALog.class);
	
	public static void logUA(String shortUA,String longUA){
		logger.info(shortUA+"###"+longUA);
	}
}
