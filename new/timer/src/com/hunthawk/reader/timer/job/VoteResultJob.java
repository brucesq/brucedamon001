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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.domain.vote.VoteResult;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.system.SystemService;

/*******************************************************************************
 * 定时统计投票任务
 * 
 * @author penglei
 * 
 */
public class VoteResultJob {

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
			String filename = "voteResult.log_" + strDate;
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
		System.out.println("Vote Process File:" + file);
		try {
			fr = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(fr);
			String line = "";
			int count = 0;
			while ((line = br.readLine()) != null) {
				// System.out.println(line+":"+count);
				++count;
				String[] fileds = line.split("###");

				if (fileds.length == 4) {
					Map<String, String> record = new HashMap<String, String>();
					// System.out.println("put:"+count);
					record.put("type", fileds[0]);
					record.put("content", fileds[1]);
					record.put("mobile", fileds[2]);
					record.put("itemId", fileds[3]);
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
		System.out.println("voteResult Record Size:" + map.size());
		for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
			Map<String, String> record = entry.getValue();

			try {
				Integer type = Integer.parseInt(record.get("type"));
				VoteResult result = new VoteResult();
				if (type.intValue() != 9) {
					if (type.intValue() == VoteSubItem.TYPE_PRODUCT) {
						result.setProductId(record.get("content"));
					} else if (type.intValue() == VoteSubItem.TYPE_COLUMN) {
						result.setColumnId(Integer.parseInt(record
								.get("content")));
					} else if (type.intValue() == VoteSubItem.TYPE_CONTENT) {
						result.setContentId(record.get("content"));
					} else if (type.intValue() == VoteSubItem.TYPE_CUSTOM) {
						result.setCustomId(record.get("content"));
					}
					result.setCreateTime(date);
					result.setItemId(Integer.parseInt(record.get("itemId")));
					result.setMobile(record.get("mobile"));
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
		try {
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			e.printStackTrace();
		}
		SessionFactoryUtils.closeSession(session);
	}

}
