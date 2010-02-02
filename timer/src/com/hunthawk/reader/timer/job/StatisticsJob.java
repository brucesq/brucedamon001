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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.memcached.MemCachedClientWrapper;
import com.hunthawk.framework.util.Utility;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.statistics.StatData;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 小时统计任务
 * 
 * @author BruceSun
 * 
 */
public class StatisticsJob {

	private SystemService systemService;
	private HibernateGenericController controller;
	private MemCachedClientWrapper memcached;

	public void setMemcached(MemCachedClientWrapper memcached) {
		this.memcached = memcached;
	}

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
		Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
		for (String ip : ips) {
			String filename = "statistics.log_" + strDate;
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
		// 启动投票定时任务 
//		VoteResultJob vote = new VoteResultJob();
//		vote.setHibernateGenericController(controller);
//		vote.setSystemService(systemService);
//		vote.doJob();
	}

	

	private void processResult(Map<String, Map<String, Integer>> map, Date date) {
		date = DateUtils.addHours(date, 12);
		int count = 0;
		Session session = controller.getHibernateTemplate().getSessionFactory()
				.openSession();
		Transaction tx = session.beginTransaction();
		System.out.println("StatisticsJob Record Size:" + map.size());
		List<String> resources = new ArrayList<String>();
		for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
			Map<String, Integer> record = entry.getValue();
			String content = entry.getKey();
			ResourceAll resource = null;

			for (Map.Entry<String, Integer> recordEntry : record.entrySet()) {
				try {
					Integer type = Integer.parseInt(recordEntry.getKey());
					Integer views = recordEntry.getValue();
					StatData data = new StatData();
					data.setContent(content);
					data.setCreateTime(date);
					data.setType(type);
					data.setViews(views);

					count++;
					session.save(data);

//					if (type < 10) {
//						if (resource == null) {
//							resource = (ResourceAll) session.get(
//									ResourceAll.class, content);
//						}
//						if (resource != null) {
//							if (type == 1) {
//								int downnum = resource.getDownnum() == null ? views
//										: resource.getDownnum() + views;
//
//								String key = Utility.getMemcachedKey(
//										ResourceAll.class, "hits", resource
//												.getId());
//								Long counter = memcached.getCounter(key);
//								System.out.println("Resource ID:"
//										+ resource.getId() + ":" + downnum
//										+ ":" + counter);
//								if (counter.intValue() > downnum) {
//									resource.setDownnum(counter.intValue());
//								} else {
//									if (counter > 0
//											&& counter.intValue() < downnum) {
//										memcached.storeCounter(key, downnum);
//									}
//									resource.setDownnum(downnum);
//								}
//							}
//							if (type == 2) {
//								int searchnum = resource.getSearchNum() == null ? views
//										: resource.getSearchNum() + views;
//								resource.setSearchNum(searchnum);
//							}
//						}
//
//						count++;
//					}
					if (count % 20 == 0) {
						session.flush();
						session.clear();
					}
					if (count % 1000 == 0) {
						tx.commit();
						tx = session.beginTransaction();
					}

				} catch (Exception e) {
					tx.rollback();
					tx = session.beginTransaction();
					e.printStackTrace();
				}
			}
			if (resource != null) {
				resources.add(content);
			}
		}
		try {
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			e.printStackTrace();
		}
		SessionFactoryUtils.closeSession(session);
		for (String resourceId : resources) {
			String resKey = Utility.getMemcachedKey(ResourceAll.class,
					resourceId);
			memcached.delete(resKey);
		}
	}

	private void processFile(String file, Map<String, Map<String, Integer>> map) {
		InputStreamReader fr = null;
		BufferedReader br = null;
		System.out.println("StatisticsJob Process File:" + file);
		try {
			fr = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {

				String[] fileds = line.split("###");

				if (fileds.length >= 2) {
					Map<String, Integer> record = map.get(fileds[1]);
					if (record == null) {
						record = new HashMap<String, Integer>();
					}
					Integer views = record.get(fileds[0]);
					if (views == null) {
						views = 0;
					}
					views++;
					record.put(fileds[0], views);
					map.put(fileds[1], record);
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
}
