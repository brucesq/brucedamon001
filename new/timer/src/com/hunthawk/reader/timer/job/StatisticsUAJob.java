package com.hunthawk.reader.timer.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.statistics.StatisticsUA;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;

public class StatisticsUAJob {
	private SystemService systemService;
	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void doJob() {
		Date date = new Date();
		date = DateUtils.addDays(date, -1);
		String strDate = ToolDateUtil.dateToString(date, "yyyyMMdd");
		date = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");
		String[] ips = systemService.getVariables("ppsips").getValue().split(
				" ");
		String module = systemService.getVariables("rsync_statis").getValue();
		String localRoot = systemService.getVariables("rsync_statis_log")
				.getValue();
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		for (String ip : ips) {
			String filename = "statisticsUA.log_" + strDate;
			String cmd = "rsync -av " + ip + "::" + module + "/" + filename
					+ " " + localRoot;
			System.out.println(cmd);
			try {
				Runtime.getRuntime().exec(cmd);
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			processFile(localRoot + filename, map);
		}
		processResult(map, date);
	}

	private void processFile(String file, Map<String, Map<String, String>> map) {
		InputStreamReader fr = null;
		BufferedReader br = null;
		System.out.println("UA Process File:" + file);
		try {
			fr = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(fr);
			String line = "";
			int count = 0;
			while ((line = br.readLine()) != null) {
				++count;
				String[] fileds = line.split("###");

				if (fileds.length == 2) {
					Map<String, String> record = new HashMap<String, String>();

					record.put("shortUA", fileds[0]);
					record.put("longUA", fileds[1]);

					map.put(count + "", record);
				}
			}
			FileUtils.forceDelete(new File(file));
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

	private void processResult(Map<String, Map<String, String>> map, Date date) {
		int count = 0;
		Session session = controller.getHibernateTemplate().getSessionFactory()
				.openSession();
		Transaction tx = session.beginTransaction();
		System.out.println("UA Record Size:" + map.size());
		for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
			Map<String, String> record = entry.getValue();
			try {

				String shortUA = record.get("shortUA");
				String longUA = record.get("longUA");
				if (!shortUA.equalsIgnoreCase("test")
						&& !longUA.equalsIgnoreCase("test")) {
					StatisticsUA result = new StatisticsUA();
					result.setCreateTime(date);
					result.setShortUA(shortUA);
					result.setLongUA(longUA);
					count++;
					session.save(result);

					if (count % 20 == 0) {
						session.flush();
						session.clear();
					}
					if (count % 1000 == 0) {
						tx.commit();
						tx = session.beginTransaction();
					}
				}

			} catch (Exception e) {
				tx.rollback();
				tx = session.beginTransaction();
				e.printStackTrace();
			}

		}
		try{
			tx.commit();
		}catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		
		SessionFactoryUtils.closeSession(session);
	}

}
