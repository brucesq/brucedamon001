/**
 * 
 */
package com.hunthawk.framework.security.simple;

import java.util.Enumeration;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hunthawk.framework.security.SecurityContext;
import com.hunthawk.framework.security.User;
import com.hunthawk.framework.security.Visit;



/**
 * @author sunquanzhi
 *
 */
public class SecurityContextImpl implements SecurityContext {

	private static  String VISIT = "state:pams:visit";
	
	protected static Logger logger  = Logger.getLogger("com.aspire.pams.framework.security");
	
	
	public static void setVisitName(String name)
	{
		logger.info("Register visit name : " + name);
		VISIT = name;
	}
	
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	public SecurityContextImpl()
	{
	}
	private void init()
	{
		Visit visit = ((Visit)request.getSession().getAttribute(VISIT));
		if(visit != null)
		{
			visit.clearErrors();
		}
		
	}
	public Visit getVisit()
	{
		Visit visit = null;
		if(request != null)
		{
			visit = (Visit)request.getSession().getAttribute(VISIT);
		}
		return visit;
	}
	public void setReuqest(HttpServletRequest request)
	{
		this.request = request;
		init();
	}
	public void setResponse(HttpServletResponse response)
	{
		this.response = response;
	}
	public HttpServletRequest getRequest()
	{
		return this.request;
	}
	public HttpServletResponse getResponse()
	{
		return this.response;
	}
	
	public User getUser()
	{
		if(request != null)
		{
			Visit visit = (Visit)request.getSession().getAttribute(VISIT);
			if(visit != null)
			{
				return visit.getUser();
			}else{
				System.out.println("visit is null");
			}
		}
		return null;
	}

	public void setError(String error)
	{
		
		Visit visit = 	(Visit)request.getSession().getAttribute(VISIT);
		if(visit != null)
		{
			visit.addError(error);
		}
	}
	public Set<String> getErrors()
	{
		Visit visit = 	(Visit)request.getSession().getAttribute(VISIT);
		if(visit != null)
		{
			return visit.getErrors();
		}
		return null;
	}

	public void setVisit(Visit visit)
	{
		if(request != null)
		{
			request.getSession().setAttribute(VISIT, visit);
		}
	}
}
