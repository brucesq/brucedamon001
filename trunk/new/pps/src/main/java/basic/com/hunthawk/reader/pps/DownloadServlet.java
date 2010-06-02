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
 * @author BruceSun
 * 
 */
public class DownloadServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8332718339504196460L;

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
		RequestUtil.setRequest(request,response);
		String resourceId = URLUtil.getResourceId(request);
		StatisticsLog.logStat(4, resourceId);
		AccessLog.log(request, 0, 1);
		response.sendRedirect(request.getParameter("downurl"));
		RequestUtil.clear();
	}

}
