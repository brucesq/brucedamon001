package com.hunthawk.reader.timer.service;

import com.hunthawk.framework.util.Page;

public interface StatisticsUAService {
	
	/****
	 * ��ȡͳ��UA�б�
	 * @param pageNo ��ǰҳ
	 * @param pageSize ÿҳ��ʾ������
	 * @return Page ��װ��һЩҳ�������
	 * @author Administrator
	 * 
	 */
	public Page findAllByPage(Integer pageNo,Integer pageSize);
	
	/**
	 * ���ݶ�UA��ȡ���ж�UA����ϸ�б�
	 * @param shortUa
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page findUaDetailByShortUa(String shortUa,int pageNo,int pageSize);
}
