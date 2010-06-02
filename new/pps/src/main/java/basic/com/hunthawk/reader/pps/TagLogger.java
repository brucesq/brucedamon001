/**
 * 
 */
package com.hunthawk.reader.pps;

import org.apache.log4j.Logger;

/**
 * @author BruceSun
 *
 */
public class TagLogger {

	private static Logger logger = Logger.getLogger(TagLogger.class);
	
	public static void debug(String tagName,String info,String queryString,Throwable t){
		if(logger.isDebugEnabled()){
			StringBuilder builder = new StringBuilder();
			builder.append("{[tag=");
			builder.append(tagName);
			builder.append("],[info=");
			builder.append(info);
			builder.append("],[request=");
			builder.append(queryString);
			builder.append("]}");
			if(t!=null){
				logger.debug(builder.toString(),t);
			}else{
				logger.debug(builder.toString());
			}
			
		}
	}
	
	public static void info(String tagName,String info,String queryString,Throwable t){
		if(logger.isInfoEnabled()){
			StringBuilder builder = new StringBuilder();
			builder.append("{[tag=");
			builder.append(tagName);
			builder.append("],[info=");
			builder.append(info);
			builder.append("],[request=");
			builder.append(queryString);
			builder.append("]}");
			if(t!=null){
				logger.info(builder.toString(),t);
			}else{
				logger.info(builder.toString());
			}
		}
	}
	
	public static void error(String tagName,String info,String queryString,Throwable t){
		
		StringBuilder builder = new StringBuilder();
		builder.append("{[tag=");
		builder.append(tagName);
		builder.append("],[info=");
		builder.append(info);
		builder.append("],[request=");
		builder.append(queryString);
		builder.append("]}");
		if(t != null){
			logger.info(builder.toString(),t);
		}else{
			logger.info(builder.toString());
		}	
	}
	
	
	
}
