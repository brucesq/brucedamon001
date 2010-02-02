package com.hunthawk.reader.timer.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.util.Page;
import com.hunthawk.reader.timer.service.StatisticsUAService;
/**
 * USservice
 * @author penglei
 *
 */
public class StatisticsUAServiceImpl implements StatisticsUAService {

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController hibernateGenericController) {
		this.controller = hibernateGenericController;
	}

	
	
	/****
	 * 获取统计UA列表
	 * @param pageNo 当前页
	 * @param pageSize 每页显示的数量
	 * @return Page 封装了一些页面的数据
	 * @author Administrator
	 * 
	 */
	public Page findAllByPage(Integer pageNo, Integer pageSize) {
		String hql = "select s.shortUA,count(s.shortUA) from StatisticsUA s where not exists( select u from UAInfo u where s.shortUA=u.ua) group by s.shortUA order by count(s.shortUA) desc ";
		Page page = controller.findByPage(hql, null,
				pageNo, pageSize);
		return page;
	}

	

	public Page findUaDetailByShortUa(String shortUa,int pageNo,int pageSize) {
		String hql = "select distinct(s.longUA),s.shortUA from StatisticsUA s where not exists( select u from UAInfo u where s.shortUA=u.ua) and s.shortUA = ?";
		List list=new ArrayList();
		list.add(shortUa);
		Page page=controller.findByPage(hql, list, pageNo, pageSize);
		return page;
	}

}
