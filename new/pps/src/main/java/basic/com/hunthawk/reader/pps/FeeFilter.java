/**
 * 
 */
package com.hunthawk.reader.pps;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.enhance.util.Des;
import com.hunthawk.reader.page.util.PageUtil;

/**
 * @author BruceSun
 * 
 */
public class FeeFilter implements Filter {

	private String sessionId = "sid";

	private String cookieDomain = "";

	private String cookiePath = "/";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		request.setCharacterEncoding("UTF-8");
		String mobileSec = request.getParameter("mobileno");
		if (StringUtils.isNotEmpty(mobileSec)) {
			Des des = new Des();
			String mobileNo = des.decrypt(mobileSec);
			Cookie mycookies = new Cookie(sessionId, mobileNo);
			mycookies.setMaxAge(315360000);
			if (this.cookieDomain != null && this.cookieDomain.length() > 0) {
				mycookies.setDomain(this.cookieDomain);
			}
			mycookies.setPath(this.cookiePath);
			response.addCookie(mycookies);
			request.getSession().setAttribute("x-up-calling-line-id", mobileNo);
		}
		String PT = request.getParameter(ParameterConstants.UNICOM_PT);
		if(StringUtils.isNotEmpty(PT)){
			request.getSession().setAttribute("UNICOM_PT", PT);
		}
		RequestUtil.setRequest(request, response);
		String uri = request.getRequestURI();
		uri = uri.substring(request.getContextPath().length() + "/".length());
		int index = uri.indexOf("/");
		if (index > 0 && uri.indexOf("do")>0) {
			String feeUrl = uri.substring(0, index);
			if (feeUrl.length() == 4) {
				// 判断是否是正常用户
				if (!FeeProcess.isNormalUser(request)) {
					return;
				}
				String local = "http://local.iuni.com.cn?spurl="
						+ "http://"
						+ PageUtil.getDomainName(request.getRequestURL()
								.toString());
				String url = local + request.getContextPath() + "/a/" + uri;
				if (StringUtils.isNotEmpty(request.getQueryString())) {
					url += "&" + request.getQueryString();
				}
				System.out.println("FEE=" + url);
				response.sendRedirect(url);
				return;
			} else if (feeUrl.length() == 1 && feeUrl.equals("a")) {
				uri = uri.substring(index + 1);
				index = uri.indexOf("/");
				feeUrl = uri.substring(0, index);
				if (feeUrl.length() == 4) {
					FeeProcess.processFee(feeUrl, request);
					String startQuery = uri.substring(uri.indexOf("s.do") + 4);
					startQuery = startQuery.replaceAll("\\?", "&");
					startQuery = startQuery.replaceAll("%26", "&");
					if (startQuery.startsWith("&")) {
						startQuery = startQuery.substring(1);
					}
					String queryString = request.getQueryString();
					String url = request.getContextPath()
							+ ParameterConstants.PORTAL_PATH + "?" + startQuery
							+ "&" + queryString;
					System.out.println("go:" + url);
					response.sendRedirect(url);
//					res.setContentLength(resultBuf.size( ));
//		            res.setContentType("text/html");
//		            res.getOutputStream().write(resultBuf.toByteArray( ));
//		            res.flushBuffer( );

					return;
				}
			}

		}
		arg2.doFilter(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
