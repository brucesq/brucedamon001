package com.hunthawk.reader.service.system;

import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.reader.domain.statistics.StatData;

public interface HotWordService {
	
	/**	 * 热词列表
	 */
	@SuppressWarnings("unchecked")
	public List findHotWordByHQL(String content,Integer type,Integer date,int pageNum, int pageSize);
	
	/**
	 * 热词个数
	 */
	public Long getHotWordResultCountByHQL(String content,Integer type,Integer date);
	
	/**
	 * 查询单个热词
	 */
	public Object getHotWord(Integer id);
	
	@Logable(name = "StatData", action = "add", property = { "id=ID,content=热词内容,type=热词类型,views=点击数" })	
	public void addHoteWord(StatData sd);
}
