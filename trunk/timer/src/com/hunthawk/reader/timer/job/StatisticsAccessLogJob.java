/**
 * 
 */
package com.hunthawk.reader.timer.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.statistics.DataReport;
import com.hunthawk.reader.domain.statistics.LogData;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.domain.statistics.URLConfigGroup;
import com.hunthawk.reader.domain.statistics.URLDataReport;
import com.hunthawk.reader.domain.statistics.URLHourDataReport;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.timer.dynamicds.CustomerContextHolder;
import com.hunthawk.reader.timer.dynamicds.DataSourceMap;

/**
 * @author BruceSun
 * 
 */
public class StatisticsAccessLogJob {

	private SystemService systemService;
	private HibernateGenericController controller;

	private List<LogData> objs = new ArrayList<LogData>();

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void doJob() {
		long startTime = System.currentTimeMillis();
		Date date = new Date();
		date = DateUtils.addDays(date, -1);
		String strDate = ToolDateUtil.dateToString(date, "yyyyMMdd");
		date = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		String[] ips = systemService.getVariables("ppsips").getValue().split(
				" ");
		String module = systemService.getVariables("rsync_statis").getValue();
		String localRoot = systemService.getVariables("rsync_statis_log")
				.getValue();
		for (String ip : ips) {
			String filename = "access_log.log_" + strDate;
			String cmd = "rsync -av " + ip + "::" + module + "/" + filename
					+ " " + localRoot;
			System.out.println(cmd);
			try {
				Runtime.getRuntime().exec(cmd);
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			processFile(localRoot + filename);
		}

		// stat(ToolDateUtil.dateToString(date, "yyyy-MM-dd"));
		statUrl(date);
		statHourUrl(date);
		long endTime = System.currentTimeMillis();
		System.out.println("End Process Access_Log Spend="
				+ (endTime - startTime) + "ms");
	}

	public static void main(String[] args) {
		// String s = "asasd|+|asd|+|asd";
		// ;
		// String[] d = s.split("\\|\\+\\|");
		// for (String c : d) {
		// System.out.println(c);
		// }
		String url = "asdas*pd=12&*s";
		System.out.println(url.replaceAll("\\*", "%"));

	}

	private void processFile(String filename) {
		InputStreamReader fr = null;
		BufferedReader br = null;
		System.out.println("StatisticsAccessLogJob Process File:" + filename);
		try {
			fr = new InputStreamReader(new FileInputStream(filename));
			br = new BufferedReader(fr);
			String line = "";
			int i = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\\|\\+\\|");
				if (values.length < 9) {
					continue;
				}
				try {
					i++;
					LogData data = new LogData();
					data.setRequestTime(values[0]);
					data.setMsisdn(values[1]);
					data.setBytes(Integer.parseInt(values[2]));
					data.setWapType(Integer.parseInt(values[3]));
					data.setUaStart(values[4]);
					data.setIp(values[5]);
					data.setRequestUrl(values[6]);
					data.setUserAgent(values[7]);
					data.setRefer(values[8]);
					parserLog(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			closeParser();
			System.out.println("Insert " + i + " records!");
			FileUtils.forceDeleteOnExit((new File(filename)));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private void statHourUrl(Date date) {
		String strDate = ToolDateUtil.dateToString(date, "yyyy-MM-dd");
		String deleteHql = "delete from URLHourDataReport where dataTime like '"
				+ strDate + "%'";
		int count = controller.executeUpdate(deleteHql);
		System.out.println("UrlHourData Delete count=" + count);

		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions
				.add(new CompareExpression("startDate", date, CompareType.Le));
		expressions.add(new CompareExpression("endDate", date, CompareType.Ge));
		List<URLConfig> configs = controller.findBy(URLConfig.class, 1, 100000,
				expressions);
		String statHql = "select  count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '";

		for (int h = 0; h < 24; h++) {
			String hour = strDate;
			if (h < 10) {
				hour += " 0" + h;
			} else {
				hour += " " + h;
			}

			for (URLConfig config : configs) {
				String hql = statHql + hour + "%' and l.requestUrl like '"
						+ config.getUrl().replaceAll("\\*", "%") + "'";
				System.out.println("Config SQL" + hql);
				// TODO:
				CustomerContextHolder
						.setCustomerType(DataSourceMap.DATASOURCE2);
				List<Object[]> urlStat = controller.findBy(hql);
				CustomerContextHolder
						.setCustomerType(DataSourceMap.DATASOURCE1);

				for (Object[] objs : urlStat) {
					if (((Long) objs[0]).intValue() == 0) {
						continue;
					}
					URLHourDataReport urlData = new URLHourDataReport();
					urlData.setStatType(1);
					urlData.setConfigId(config.getId());
					urlData.setDataTime(hour);
					urlData.setPageViews(((Long) objs[0]).intValue());
					urlData.setUserViews(((Long) objs[1]).intValue());
					urlData.setBytes(((Long) objs[2]).intValue());
					urlData.setIpViews(((Long) objs[3]).intValue());
					controller.save(urlData);
				}
			}
		}

		List<URLConfigGroup> groups = controller.getAll(URLConfigGroup.class);

		statHql = "select  count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '";

		for (int h = 0; h < 24; h++) {
			String hour = strDate;
			if (h < 10) {
				hour += " 0" + h;
			} else {
				hour += " " + h;
			}

			for (URLConfigGroup group : groups) {
				String hql = statHql + hour + "%' and ( ";
				int i = 0;
				if (group.getConfigs().size() == 0) {
					continue;
				}
				for (URLConfig config : group.getConfigs()) {
					i++;
					hql += " l.requestUrl like '"
							+ config.getUrl().replaceAll("\\*", "%") + "'";
					if (i < group.getConfigs().size()) {
						hql += " or ";
					}
				}
				hql += " ) ";
				System.out.println("GROUP SQL" + hql);
				// TODO:
				CustomerContextHolder
						.setCustomerType(DataSourceMap.DATASOURCE2);
				List<Object[]> urlStat = controller.findBy(hql);
				CustomerContextHolder
						.setCustomerType(DataSourceMap.DATASOURCE1);

				for (Object[] objs : urlStat) {
					if (((Long) objs[0]).intValue() == 0) {
						continue;
					}
					URLHourDataReport urlData = new URLHourDataReport();
					urlData.setStatType(2);
					urlData.setConfigId(group.getId());
					urlData.setDataTime(hour);
					urlData.setPageViews(((Long) objs[0]).intValue());
					urlData.setUserViews(((Long) objs[1]).intValue());
					urlData.setBytes(((Long) objs[2]).intValue());
					urlData.setIpViews(((Long) objs[3]).intValue());
					controller.save(urlData);
				}
			}
		}
	}

	private void statUrl(Date date) {
		String strDate = ToolDateUtil.dateToString(date, "yyyy-MM-dd");
		String deleteHql = "delete from URLDataReport where dataTime like '"
				+ strDate + "%'";
		int count = controller.executeUpdate(deleteHql);
		System.out.println("UrlData Delete count=" + count);

		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions
				.add(new CompareExpression("startDate", date, CompareType.Le));
		expressions.add(new CompareExpression("endDate", date, CompareType.Ge));
		List<URLConfig> configs = controller.findBy(URLConfig.class, 1, 100000,
				expressions);
		String statHql = "select  count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '"
				+ strDate + "%' and l.requestUrl like ";

		for (URLConfig config : configs) {
			String hql = statHql + "'" + config.getUrl().replaceAll("\\*", "%")
					+ "'";
			System.out.println("Config SQL" + hql);

			// TODO:
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			List<Object[]> urlStat = controller.findBy(hql);
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);

			for (Object[] objs : urlStat) {
				if (((Long) objs[0]).intValue() == 0) {
					continue;
				}
				URLDataReport urlData = new URLDataReport();
				urlData.setStatType(1);
				urlData.setConfigId(config.getId());
				urlData.setDataTime(strDate);
				urlData.setPageViews(((Long) objs[0]).intValue());
				urlData.setUserViews(((Long) objs[1]).intValue());
				urlData.setBytes(((Long) objs[2]).intValue());
				urlData.setIpViews(((Long) objs[3]).intValue());
				controller.save(urlData);
			}
		}

		List<URLConfigGroup> groups = controller.getAll(URLConfigGroup.class);

		statHql = "select  count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '"
				+ strDate + "%' and ( ";

		for (URLConfigGroup group : groups) {
			String hql = statHql;
			int i = 0;
			if (group.getConfigs().size() == 0) {
				continue;
			}
			for (URLConfig config : group.getConfigs()) {
				i++;
				hql += " l.requestUrl like '"
						+ config.getUrl().replaceAll("\\*", "%") + "'";
				if (i < group.getConfigs().size()) {
					hql += " or ";
				}
			}
			hql += " ) ";
			System.out.println("GROUP SQL" + hql);
			// TODO:
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			List<Object[]> urlStat = controller.findBy(hql);
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);

			for (Object[] objs : urlStat) {
				if (((Long) objs[0]).intValue() == 0) {
					continue;
				}
				URLDataReport urlData = new URLDataReport();
				urlData.setStatType(2);
				urlData.setConfigId(group.getId());
				urlData.setDataTime(strDate);
				urlData.setPageViews(((Long) objs[0]).intValue());
				urlData.setUserViews(((Long) objs[1]).intValue());
				urlData.setBytes(((Long) objs[2]).intValue());
				urlData.setIpViews(((Long) objs[3]).intValue());
				controller.save(urlData);
			}
		}

	}

	private void stat(String date) {
		// 清空原有数据
		// String deleteSql = "delete from reader_statistics_data_report where
		// date_time like '"
		// + date + "'";
		String deleteHql = "delete from DataReport where dataTime like '"
				+ date + "%'";
		int count = controller.executeUpdate(deleteHql);
		// controller.getHibernateTemplate().getSessionFactory()
		// .openSession().createSQLQuery(deleteSql).executeUpdate();
		System.out.println("Delete count=" + count);
		String statHql = "select l.paraPd, count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '"
				+ date + "%' and l.paraPd is not null group by l.paraPd";

		// TODO:
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
		List<Object[]> productStat = controller.findBy(statHql);
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);

		for (Object[] objs : productStat) {
			if (((Long) objs[0]).intValue() == 0) {
				continue;
			}
			DataReport report = new DataReport();
			report.setParaPd((String) objs[0]);
			report.setPageViews(((Long) objs[1]).intValue());
			report.setUserViews(((Long) objs[2]).intValue());
			report.setBytes(((Long) objs[3]).intValue());
			report.setIpViews(((Long) objs[4]).intValue());
			report.setStatType(11);
			report.setWapType(1);
			report.setDataTime(date);
			controller.save(report);
		}
		// 页面组
		statHql = "select l.paraPd,l.paraGd, count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '"
				+ date
				+ "%' and l.paraPd is not null and l.paraGd is not null group by l.paraPd,l.paraGd";

		// TODO:
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
		productStat = controller.findBy(statHql);
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);

		for (Object[] objs : productStat) {
			DataReport report = new DataReport();
			report.setParaPd((String) objs[0]);
			report.setParaGd((String) objs[1]);
			report.setPageViews(((Long) objs[2]).intValue());
			report.setUserViews(((Long) objs[3]).intValue());
			report.setBytes(((Long) objs[4]).intValue());
			report.setIpViews(((Long) objs[5]).intValue());
			report.setStatType(12);
			report.setWapType(1);
			report.setDataTime(date);
			controller.save(report);
		}

		// 栏目
		statHql = "select l.paraPd,l.paraGd,l.paraCd, count(l.id),count(distinct l.msisdn),sum(l.bytes),count(distinct l.ip) from LogData l where l.requestTime like '"
				+ date
				+ "%' and l.paraPd is not null and l.paraGd is not null and l.paraCd is not null group by l.paraPd,l.paraGd,l.paraCd";
		// TODO:
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
		productStat = controller.findBy(statHql);
		CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
		for (Object[] objs : productStat) {
			DataReport report = new DataReport();
			report.setParaPd((String) objs[0]);
			report.setParaGd((String) objs[1]);
			report.setParaCd((String) objs[2]);
			report.setPageViews(((Long) objs[3]).intValue());
			report.setUserViews(((Long) objs[4]).intValue());
			report.setBytes(((Long) objs[5]).intValue());
			report.setIpViews(((Long) objs[6]).intValue());
			report.setStatType(13);
			report.setWapType(1);
			report.setDataTime(date);
			controller.save(report);
		}
	}

	private void closeParser() {
		try {
			// TODO:
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE2);
			Session session = controller.getHibernateTemplate()
					.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for (LogData obj : objs) {
				session.save(obj);
			}
			tx.commit();
			session.close();
			objs.clear();
			CustomerContextHolder.setCustomerType(DataSourceMap.DATASOURCE1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parserLog(LogData data) {

		try {
			String url = data.getRequestUrl();
			int index = url.indexOf("?");
			if (index > 0) {
				String queryString = url.substring(index + 1);
				String[] params = queryString.split("&");
				for (String param : params) {
					int i = param.indexOf("=");
					if (i > 0) {
						String key = param.substring(0, i);
						String value = param.substring(i + 1);
						if (key.equals("pd")) {
							data.setParaPd(value);
						} else if (key.equals("cd")) {
							data.setParaCd(value);
						} else if (key.equals("gd")) {
							data.setParaGd(value);
						} else if (key.equals("ed")) {
							data.setParaEd(value);
						} else if (key.equals("fc")) {
							data.setParaFc(value);
						} else if (key.equals("pg")) {
							data.setParaPg(value);
						} else if (key.equals("rd")) {
							data.setParaRd(value);
						} else if (key.equals("fd")) {
							data.setParaFd(value);
						} else if (key.equals("fn")) {
							data.setParaFn(value);
						} else if (key.equals("nd")) {
							data.setParaNd(value);
						} else if (key.equals("td")) {
							data.setParaTd(value);
						} else if (key.equals("zd")) {
							data.setParaZd(value);
							data.setParaRd(value.substring(0,
									value.length() - 3));
						} else if (key.equals("ad")) {
							data.setParaAd(value);
						} else if (key.equals("au")) {
							data.setParaAu(value);
						}

					}

				}
			}
			objs.add(data);
			if (objs.size() > 1000) {
				// TODO:
				CustomerContextHolder
						.setCustomerType(DataSourceMap.DATASOURCE2);
				Session session = controller.getHibernateTemplate()
						.getSessionFactory().openSession();
				Transaction tx = session.beginTransaction();
				for (LogData obj : objs) {
					session.save(obj);
				}
				tx.commit();
				session.close();
				objs.clear();
				CustomerContextHolder
						.setCustomerType(DataSourceMap.DATASOURCE1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
