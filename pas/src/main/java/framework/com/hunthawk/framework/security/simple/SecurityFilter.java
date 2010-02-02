/**
 * 
 */
package com.hunthawk.framework.security.simple;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hunthawk.framework.security.SecurityContextHolder;


/**
 * @author sunquanzhi
 *
 */
public class SecurityFilter implements Filter {

	/**
	 * 
	 */
	public SecurityFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
	
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		SecurityContextImpl context = new SecurityContextImpl();
		context.setResponse((HttpServletResponse)response);
		context.setReuqest((HttpServletRequest)request);
		SecurityContextHolder.setContext(context);
		chain.doFilter(request,response);
		SecurityContextHolder.clearContext();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		

	}

	

}
