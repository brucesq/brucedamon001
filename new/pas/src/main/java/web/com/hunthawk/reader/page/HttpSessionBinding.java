package com.hunthawk.reader.page;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.framework.security.Visit;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.SystemService;

public  class HttpSessionBinding implements javax.servlet.http.HttpSessionBindingListener{
	
	
	ServletContext application = null; 
    ApplicationContext ac = null;
    UserImpl user = null;
    HttpServletRequest request = null; 
	 public HttpSessionBinding(ServletContext application,HttpServletRequest request)   
	 {   
	  super();   
	  if (application ==null)   
	   throw new IllegalArgumentException("Null application is not accept.");   
	     
	  this.application = application;   
	  this.request = request;
	  this.ac= WebApplicationContextUtils.getWebApplicationContext(application);
	 }   
	  
	 public void valueBound(javax.servlet.http.HttpSessionBindingEvent e)    
	 { 
		 
	  Vector activeSessions = (Vector) application.getAttribute("activeSessions");   
	  if (activeSessions == null)   
	  {   
	   activeSessions = new Vector();   
	  }   
	  
	   user = (UserImpl)e.getSession().getAttribute("state:reader:userImpl");
	  if (user != null)   
	  {  
		  if(checkUser(activeSessions,user))
			  activeSessions.add(e.getSession());   
	  }   
	  checkUser(activeSessions,user);
		SystemService  systemService = 
			(SystemService) ac.getBean("systemService");
	   Log log = new Log();
	   log.setAction("login");
	   log.setDetail("{ID:"+user.getId()+",NAME:"+user.getChName()+",登录时间:"+new Date()+",登录IP:"+request.getRemoteAddr()+"}");
	   log.setName("user");
	   log.setUserId(user.getId());
	   log.setLogTime(new Date());
	   systemService.addLog(log);
	   
	   application.setAttribute("activeSessions",activeSessions);   
	 }   
	  
	 public void valueUnbound(javax.servlet.http.HttpSessionBindingEvent e)    
	 {   
		  
	   Vector activeSessions = (Vector) application.getAttribute("activeSessions"); 
	   
	   if (activeSessions != null)   
	   {   
	    activeSessions.remove(e.getSession());   
	    application.setAttribute("activeSessions",activeSessions);   
	   }    
	   SystemService  systemService = 
			(SystemService) ac.getBean("systemService");
	   Log log = new Log();
	   log.setAction("loginout");
	   log.setDetail("{ID:"+user.getId()+",NAME:"+user.getChName()+",退出时间:"+new Date()+",登录IP:"+request.getRemoteAddr()+"}");
	   log.setName("user");
	   log.setUserId(user.getId());
	   log.setLogTime(new Date());
	   systemService.addLog(log);
	 }   
	 
	 protected boolean checkUser(Vector vectors,UserImpl user){
		 Set<Integer> userSet = new HashSet<Integer>();
		 for(int i=0;i<vectors.size();i++){
			HttpSession session = (HttpSession) vectors.get(i);
			UserImpl users = (UserImpl)session.getAttribute("state:reader:userImpl");
			userSet.add(users.getId());
		 }
		Integer currentUserId = user.getId();
		if(userSet.contains(currentUserId))
			return false;
		else
			return true;
	 }
}
