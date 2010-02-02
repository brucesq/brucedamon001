package com.hunthawk.reader.pps.service.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.util.Assert;

import com.hunthawk.framework.HibernateGenericController;

public class HibernateOverWriteController extends HibernateGenericController {

	/**
	 * <p>��ҳ��ѯ</p>
	 * @param hql ��̬HQL��� 
	 * @param startNum	���Կ������α���ʼλ�� ��1���Ļ�ֵΪ0 
	 * @param pageSize	���Կ�����ÿҳ��ʾ��������¼
	 * @param values	��ѯ�����Ķ�̬����
	 * @return LIST ����һ���ӵ�N����ʼ����M�������ļ�¼���� 
	 */
	public List findBy(String hql, int startNum, int pageSize, Object... values) 
	{
		Assert.hasText(hql);
		int startIndex = startNum;
		System.out.println("��дfindBy���� , startIndex="+startIndex);
		Query query = createQuery(hql, values);
		return query.setFirstResult(startIndex).setMaxResults(pageSize).list();
		
	}
}
