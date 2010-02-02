package com.hunthawk.reader.pps;

import org.apache.log4j.Logger;

public class VoteResultLog {
	private static Logger logger = Logger.getLogger(VoteResultLog.class);
	
	public static void logVote(int type,String content,String mobile,Integer itemId){
		logger.info(type+"###"+content+"###"+mobile+"###"+itemId);
	}
}
