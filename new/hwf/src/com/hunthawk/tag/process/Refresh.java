/**
 * 
 */
package com.hunthawk.tag.process;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.tag.util.ParamUtil;

/**
 * @author sunquanzhi
 *
 */
public class Refresh {

	private int timer;
	
	private String targetUrl;
	
	public int getTimer() {
		return timer;
	}
	public void setTimer(int timer) {
		this.timer = timer;
	}
	
	

	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	@SuppressWarnings("unchecked")
	public static void pageRefresh(int timer,String randomParameterName,HttpServletRequest request)
	{
		Refresh refresh = new Refresh();	
		refresh.setTimer(timer);
		String tempTime = "" + System.currentTimeMillis();
		Map para = new HashMap();
        para.put(randomParameterName, tempTime.substring(tempTime.length() - 4));
		String targetUrl = ParamUtil.urlChange(request,para,null,null,0);
		refresh.setTargetUrl(targetUrl);
		RefreshUtil.pageRefresh(refresh);
	}
	
	@SuppressWarnings("unchecked")
	public static void pageRefresh(int timer,String randomParameterName,Map para,HttpServletRequest request)
	{
		Refresh refresh = new Refresh();	
		refresh.setTimer(timer);
		String tempTime = "" + System.currentTimeMillis();
        para.put(randomParameterName, tempTime.substring(tempTime.length() - 4));
		String targetUrl = ParamUtil.urlChange(request,para,null,null,0);
		refresh.setTargetUrl(targetUrl);
		RefreshUtil.pageRefresh(refresh);
	}
	
	
	public static void pageRefresh(int timer,String targetUrl)
	{
		Refresh refresh = new Refresh();	
		refresh.setTimer(timer);
		refresh.setTargetUrl(targetUrl);
		RefreshUtil.pageRefresh(refresh);
	}
}
