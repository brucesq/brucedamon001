/**
 * 
 */
package com.hunthawk.reader.pps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author BruceSun
 * 
 */
public class FeeRedirectServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1589137653198362417L;

	private  Properties props = new Properties();

	private static final String DEFAULT = "default";

	public void init() {
		try {
			InputStream in = FeeRedirectServlet.class
			.getResourceAsStream("/feeurl.properties");
			props.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		props = null;
		super.destroy();
	}

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
		String uri = request.getRequestURI(); 
		uri = uri.substring(request.getContextPath().length() + "/".length());
		
		int index = uri.indexOf(".");
		String url = null;
		if(index > 0){
			String feeUrl = uri.substring(0, index);
			if(feeUrl.equalsIgnoreCase("reseturl")){
				String ua = request.getHeader("user-agent");
				String mobile = request.getHeader("x-up-calling-line-id");
				response.getOutputStream().print(mobile+"###"+ua);
				return;
			}
			url = props.getProperty(feeUrl);
		}
		if(url == null){
			url = props.getProperty(DEFAULT);
		}
		response.sendRedirect(url);
	}
}
