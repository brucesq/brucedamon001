package com.hunthawk.reader.timer.service;

import com.hunthawk.framework.util.Page;

public interface StatisticsUAService {
	
	/****
	 * 获取统计UA列表
	 * @param pageNo 当前页
	 * @param pageSize 每页显示的数量
	 * @return Page 封装了一些页面的数据
	 * @author Administrator
	 * 
	 */
	public Page findAllByPage(Integer pageNo,Integer pageSize);
	
	/**
	 * 根据短UA获取所有短UA的详细列表
	 * @param shortUa
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page findUaDetailByShortUa(String shortUa,int pageNo,int pageSize);
}
