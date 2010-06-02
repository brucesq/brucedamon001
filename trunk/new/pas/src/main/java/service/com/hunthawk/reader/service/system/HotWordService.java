package com.hunthawk.reader.service.system;

import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.reader.domain.statistics.StatData;

public interface HotWordService {
	
	/**	 * �ȴ��б�
	 */
	@SuppressWarnings("unchecked")
	public List findHotWordByHQL(String content,Integer type,Integer date,int pageNum, int pageSize);
	
	/**
	 * �ȴʸ���
	 */
	public Long getHotWordResultCountByHQL(String content,Integer type,Integer date);
	
	/**
	 * ��ѯ�����ȴ�
	 */
	public Object getHotWord(Integer id);
	
	@Logable(name = "StatData", action = "add", property = { "id=ID,content=�ȴ�����,type=�ȴ�����,views=�����" })	
	public void addHoteWord(StatData sd);
}
