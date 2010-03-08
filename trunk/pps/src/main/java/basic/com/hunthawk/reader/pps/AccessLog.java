/**
 * 
 */
package com.hunthawk.reader.pps;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * @author BruceSun
 * 
 */
public class AccessLog {

	private static Logger logger = Logger.getLogger(AccessLog.class);

	private static final String SPLIT = "|+|";
		
	public static void log(HttpServletRequest request, Integer bytes,
			Integer verson) {
		String date = ToolDateUtil.dateToString(new Date());
		String url = request.getRequestURL() +"?"+ request.getQueryString();
		String uaStr = RequestUtil.getUa();
		String userAgent = request.getHeader("user-agent");
		String mobile = RequestUtil.getMobile();
		String ip = getIpAddr(request);
		String refer = request.getHeader("Referer");
		logger.info(date+SPLIT+mobile+SPLIT+bytes+SPLIT+verson+SPLIT+uaStr+SPLIT+ip+SPLIT+url+SPLIT+userAgent+SPLIT+refer);
	}

	public static void log(String mobile,Integer bytes,Integer verson,String uaStr,String ip,String url,String userAgent,String refer){
		String date = ToolDateUtil.dateToString(new Date());
		logger.info(date+SPLIT+mobile+SPLIT+bytes+SPLIT+verson+SPLIT+uaStr+SPLIT+ip+SPLIT+url+SPLIT+userAgent+SPLIT+refer);
	}
	
	
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
