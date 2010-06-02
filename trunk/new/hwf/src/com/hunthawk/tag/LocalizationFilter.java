package com.hunthawk.tag;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hunthawk.tag.util.LocalizationResponse;



public class LocalizationFilter implements Filter{
	@SuppressWarnings("unused")
	private FilterConfig config = null;
	
	private static Logger log = Logger.getLogger(LocalizationFilter.class);
	public void destroy() {
		config = null;
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {	
		long begin = System.currentTimeMillis();
		log.debug("awf 调用前...");
		((HttpServletRequest)arg0).setCharacterEncoding("UTF-8");	
		LocalizationResponse response = new LocalizationResponse((HttpServletResponse) arg1);
		response.setLocal(((HttpServletRequest)arg0).getParameter("lo"));
		arg2.doFilter(arg0, response);
		
		long end = System.currentTimeMillis();
		log.debug("awf 调用结束。耗时:" + (end - begin) + "ms");
	}

	public void init(FilterConfig arg0) throws ServletException {
		this.config = arg0;
	}

}
