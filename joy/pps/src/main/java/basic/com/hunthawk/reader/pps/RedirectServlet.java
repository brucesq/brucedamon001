/**
 * 
 */
package com.hunthawk.reader.pps;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sunquanzhi
 * 
 */
public class RedirectServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response);
	}
	
	
	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// long startTime = System.currentTimeMillis();

		request.setCharacterEncoding("UTF-8");
		RequestUtil.setRequest(request,response);
		AccessLog.log(request, 0, RequestUtil.getNeedWapType());
		String url = request.getParameter("gourl");
		response.sendRedirect(url);
		
	}

}
