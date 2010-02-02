package com.hunthawk.reader.timer.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.timer.job.MonthReportJob;
import com.hunthawk.reader.timer.job.StatDataRankDate;
import com.hunthawk.reader.timer.job.StatDataRankSummary;
import com.hunthawk.reader.timer.job.StatisticsAccessLogJob;
import com.hunthawk.reader.timer.job.StatisticsJob;
import com.hunthawk.reader.timer.job.VoteResultJob;

/**
 * 启动定时任务
 * 
 * @author penglei 2009.11.09
 * 
 */
public class StartJobs extends HttpServlet {

	private static final long serialVersionUID = 968876744485319286L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		String param = request.getParameter("param");
		PrintWriter out = response.getWriter();
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(this.getServletContext());
		if (param != null && param.equalsIgnoreCase("hunthawk")) {
			StatDataRankDate statDataRankDate = (StatDataRankDate) context
					.getBean("statDataRankDateObj");

			StatThreadOnce thread = new StatThreadOnce(statDataRankDate);
			// thread.setDaemon(true);
			thread.start();
			out.println("执行完毕...");
			out.close();
		} else if (param != null && param.equalsIgnoreCase("summary")) {
			int type = Integer.parseInt(request.getParameter("type"));
			StatDataRankSummary statDataRankSummary = (StatDataRankSummary) context
					.getBean("statDataRankSummaryObj");
			StatThread statThread = new StatThread(statDataRankSummary, type);
			// statThread.setDaemon(true);
			statThread.start();
			out.println("汇总执行完毕...");
			out.close();
		} else if (param != null && param.equalsIgnoreCase("etl")) {
			StatisticsJob statJob = (StatisticsJob) context
					.getBean("statisticsJobObj");
			statJob.doJob();
			out.println("ETL入库执行完毕...");
			out.close();
		} else if (param != null && param.equalsIgnoreCase("vote")) {
			VoteResultJob statJob = (VoteResultJob) context
					.getBean("voteResultJobObj");
			statJob.doJob();
			out.println("Vote入库执行完毕...");
			out.close();
		} else if (param != null && param.equalsIgnoreCase("access")) {
			StatisticsAccessLogJob statJob = (StatisticsAccessLogJob) context
					.getBean("statisticsAccessLogJobObj");
			statJob.doJob();
			out.println("Access Log 入库执行完毕...");
			out.close();
		} else if (param != null && param.equalsIgnoreCase("month")) {
			MonthReportJob monthReportJob = (MonthReportJob) context
					.getBean("statisticsMonthLogJobObj");
			monthReportJob.doJob();
			out.println("Month Log 入库执行完毕...");
			out.close();
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);

	}

}
