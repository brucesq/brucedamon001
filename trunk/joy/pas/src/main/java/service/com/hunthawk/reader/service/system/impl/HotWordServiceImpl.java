package com.hunthawk.reader.service.system.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.statistics.StatData;
import com.hunthawk.reader.service.system.HotWordService;

@SuppressWarnings("unchecked")
public class HotWordServiceImpl implements HotWordService {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public Object getHotWord(Integer id) {
		return controller.get(StatData.class, id);
	}

	public Long getHotWordResultCountByHQL(String content, Integer type,
			Integer date) {

		String hql = "select count(distinct s.content) from StatData s where 1=1 ";

		List param = new ArrayList();

		if (type != null && type != 0) {
			hql += " and s.type=? ";
			param.add(type);
		}
		if (StringUtils.isNotEmpty(content)) {
			hql += " and s.content like ? ";
			param.add(content);
		}

		if (null != date) {
			if (date != 0) {
				hql += " and s.createTime between ? and ?";
				/*
				 * param.add(getDate(date)[0]); param.add(getDate(date)[1]);
				 */
				param.add(getDate(date).get(0));
				param.add(getDate(date).get(1));
			}
		}

		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);
		List<Long> counts = controller.findBy(hql, arr);

		return counts.get(0);
	}

	public List<StatData> findHotWordByHQL(String content, Integer type,
			Integer date, int pageNum, int pageSize) {

		String hql = "select s.content,sum(s.views) from StatData s where 1=1 ";

		List param = new ArrayList();

		if (type != null && type != 0) {
			hql += " and s.type=? ";
			param.add(type);
		} else {
			hql += " and s.type=? ";
			param.add(11);
		}
		if (StringUtils.isNotEmpty(content)) {
			hql += " and s.content like ? ";
			param.add(content);
		}

		if (null != date) {
			if (date != 0) {
				hql += " and s.createTime between ? and ?";
				/*
				 * param.add(getDate(date)[0]); param.add(getDate(date)[1]);
				 */

				param.add(getDate(date).get(0));
				param.add(getDate(date).get(1));
			}
		}

		hql += " group by s.type,s.content order by sum(s.views) desc";

		final int size = param.size();
		Object[] arr = (Object[]) param.toArray(new Object[size]);

		List<Object[]> list = controller.findBy(hql, pageNum, pageSize, arr); // 查询对应的几个字段，组合的为一个对象数组
		List<StatData> statDataList = new ArrayList<StatData>();

		for (Object[] obj : list) {
			StatData statData = new StatData();
			statData.setContent(obj[0].toString());
			statData.setViews(Integer.valueOf(obj[1].toString()));
			statDataList.add(statData);
		}

		return statDataList;
	}

	protected List<Date> getDate(int date) {
		List<Date> list = new ArrayList<Date>();
		Calendar today = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate = null;
		try {
			nowDate = sdf.parse(sdf.format(today.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (date == 1) { // 日排行
			list.add(DateUtils.addDays(nowDate, -1));// 当前时间减去1天--昨天
			list.add(nowDate);
		} else if (date == 2) { // 周排行
			list.add(DateUtils.addDays(nowDate, -7));// 当前时间减去1周--
			list.add(nowDate);

		} else if (date == 3) {
			list.add(DateUtils.addDays(nowDate, -30));// 当前时间减去1月
			list.add(nowDate);
		} else {
			list.add(DateUtils.addDays(nowDate, -1));// 当前时间减去1天--昨天
			list.add(nowDate);
		}
		return list;
	}

	/*
	 * public Date[] getDate(int date){ Calendar today = Calendar.getInstance();
	 * Date[] returnDate = new Date[2];
	 * 
	 * int month = today.get(Calendar.MONTH); // 得到当前月 int currentYear =
	 * today.get(Calendar.YEAR); // 得到当前年 String[] formate = new
	 * String[]{"yyyy-MM-dd hh:mm:ss"};
	 * 
	 * if (date == 1){ // 日排行 returnDate[1] =
	 * DateUtils.truncate(today.getTime(), Calendar.DATE); returnDate[0] =
	 * DateUtils.addMilliseconds(returnDate[1], (int) ((-1)
	 * (DateUtils.MILLIS_PER_DAY))); } else if (date == 2){ // 周排行 Date endDate
	 * = DateUtils.truncate(DateUtils.addDays(today.getTime(), (-1)
	 * (today.get(Calendar.DAY_OF_WEEK) - 2)), Calendar.DATE); Date startDate =
	 * DateUtils.addDays(endDate, -7);
	 * 
	 * returnDate[0] = startDate; returnDate[1] = endDate; } else if (date ==
	 * 3){ // 月排行 try{ if (month == 0){ // 当前月 1 月 returnDate[0] =
	 * DateUtils.parseDate((currentYear - 1) + "-12-1 00:00:00", formate);
	 * returnDate[1] = DateUtils.parseDate((currentYear - 1) +
	 * "-12-31 24:00:00", formate); } else { returnDate[0] =
	 * DateUtils.parseDate(currentYear + "-" + month + "-1 00:00:00", formate);
	 * returnDate[1] = DateUtils.parseDate(currentYear + "-" + (month + 1) +
	 * "-1 00:00:00", formate); } } catch (ParseException e){
	 * e.printStackTrace(); } }
	 * 
	 * return returnDate; }
	 */

	public void addHoteWord(StatData sd) {
		controller.save(sd);
	}

}
