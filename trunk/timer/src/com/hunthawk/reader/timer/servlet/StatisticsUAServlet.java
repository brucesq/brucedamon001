package com.hunthawk.reader.timer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.framework.util.Page;
import com.hunthawk.reader.timer.service.StatisticsUAService;

/**
 * 查询统计UA列表
 * @author Administrator
 *
 */
public class StatisticsUAServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8007989016420379858L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html;charset=utf-8");
		int pageNo = 0;
		int pageSize = 0;
		try {
			pageNo = request.getParameter("pageNo") == null ? 1 : Integer
					.parseInt(request.getParameter("pageNo"));
			pageSize = request.getParameter("pageSize") == null ? 50 : Integer
					.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
			pageNo = 1;
			pageSize = 50;
		}
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(this.getServletContext());
		StatisticsUAService statisticsUAService = (StatisticsUAService) context
				.getBean("statisticsUAService");
		Page result = statisticsUAService.findAllByPage(pageNo, pageSize);
		request.setAttribute("result", result.getData());
		request.setAttribute("total", result.getTotalDataCount());
		request.setAttribute("pageSize", result.getDataCountInOnePage());
		request.setAttribute("pageNo", result.getCurrentPageNo());

		request.getRequestDispatcher("./statisticsUA.jsp").forward(request,
				response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
