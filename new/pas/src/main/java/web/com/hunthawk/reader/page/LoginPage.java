package com.hunthawk.reader.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.services.CookieSource;
import org.apache.tapestry.services.ResponseBuilder;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.security.Visit;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.system.UserService;




public abstract class LoginPage extends BasePage{
  
    @Bean
    public abstract UserImpl getUser();

    @Bean
    public abstract ValidationDelegate getDelegate();


    @InjectState("visit")
    public abstract Visit getVisit();

    @InjectObject("spring:userService")
	public abstract UserService getUserService();

    

    @InjectObject("service:tapestry.globals.HttpServletRequest")
    public abstract HttpServletRequest getServletRequest();

    @InjectObject("service:tapestry.globals.HttpServletResponse")
    public abstract HttpServletResponse getServletResponse();

    @InjectObject("infrastructure:cookieSource")
    public abstract CookieSource getCookieSource();

    public abstract String getValidCode();

    public abstract void setValidCode(String code);
    
    

   
    public abstract String getPassword();
	 
	public abstract String getName();
    

    public abstract ResponseBuilder getBuilder();

    public String getCnbrnContextPath()
    {
        String contextPath = getServletRequest().getContextPath();

        return getServletResponse().encodeURL(
                contextPath + "/app?service=validatorImage");
    }

    public String getOnclickPath()
    {
        String contextPath = getServletRequest().getContextPath();

        String aa = getServletResponse().encodeURL(
                contextPath + "/app?service=validatorImage&ts="+System.currentTimeMillis()+"'");

        String path = "document.getElementById('validImg').src='" + aa;

        // String path = "document.getElementById('validImg').src='" +
        // contextPath
        // + "/app?service=validatorImage'";

        return path;
    }

    /**
     * 验证用户的登录
     * 
     * @param cycle
     * @return
     */
    @SuppressWarnings("unchecked")
	public void login(IRequestCycle cycle)
    {
        String validCode = (String) cycle.getInfrastructure().getRequest().getSession(true).getAttribute("img_code");
        if(!getValidCode().equalsIgnoreCase(validCode)){
        	getDelegate().setFormComponent(null);
        	getDelegate().record("验证码不正确", null);
            setValidCode(null);
            return;
        }
        
        
        Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression nameE = new CompareExpression("name",getName(),CompareType.Equal);
		hibernateExpressions.add(nameE);
			
		HibernateExpression pwdE = new CompareExpression("password",getPassword(),CompareType.Equal);
		hibernateExpressions.add(pwdE);
			
	    List list = getUserService().findBy(UserImpl.class, 1, 1, "id", false,  hibernateExpressions);
		  if(list.size() > 0)
		  {
			  getVisit().setUser((UserImpl)list.get(0));
			  
//			  cycle.activate("HomePage");
			  String contextPath = getServletRequest().getContextPath();
			  HttpSession session = getServletRequest().getSession();
			  session.setAttribute("state:reader:userImpl", (UserImpl)list.get(0));
			  session.setAttribute("BindingNotify",new HttpSessionBinding(session.getServletContext(),getServletRequest()));   
//			  String port = "";
//			  if(getServletRequest().getRemotePort() != 80){
//				  port = ":"+getServletRequest().getRemotePort();
//			  }
	            throw new RedirectException("http://" + PageUtil.getDomainName(getServletRequest().getRequestURL().toString())
	                    + contextPath + "/Home.page;jsessionid="+getServletRequest().getSession().getId());
//			  throw new RedirectException("HomePage.page");
		  }else{
			  getVisit().addError("用户名密码不正确！");
		  }
		  
		 



    }

   
      
   
  

}
