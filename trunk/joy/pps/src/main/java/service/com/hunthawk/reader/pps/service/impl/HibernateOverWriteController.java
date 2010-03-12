package com.hunthawk.reader.pps.service.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.util.Assert;

import com.hunthawk.framework.HibernateGenericController;

public class HibernateOverWriteController extends HibernateGenericController {

	/**
	 * <p>分页查询</p>
	 * @param hql 动态HQL语句 
	 * @param startNum	可以看成是游标起始位置 第1条的话值为0 
	 * @param pageSize	可以看成是每页显示多少条记录
	 * @param values	查询条件的动态参数
	 * @return LIST 返回一个从第N条开始到第M条结束的记录集合 
	 */
	public List findBy(String hql, int startNum, int pageSize, Object... values) 
	{
		Assert.hasText(hql);
		int startIndex = startNum;
		System.out.println("重写findBy方法 , startIndex="+startIndex);
		Query query = createQuery(hql, values);
		return query.setFirstResult(startIndex).setMaxResults(pageSize).list();
		
	}
}
