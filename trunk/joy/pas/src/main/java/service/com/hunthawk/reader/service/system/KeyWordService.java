package com.hunthawk.reader.service.system;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.enhance.annotation.Memcached;

public interface KeyWordService {
	
	/**
	 * �������ӹؼ���
	 */
	@Logable(name = "KeyWord", action = "add", property = { "id=ID,keyWord=���д�,type.type=����,creator=������,createTime=����ʱ��" })
	public String addKeyWord(KeyWord keyWord)throws Exception;
	
	/**
	 * ɾ���ؼ���
	 */
	@Logable(name = "KeyWord", action = "delete", property = { "id=ID,keyWord=���д�,type.type=����,creator=������,createTime=����ʱ��" })
	public void delKeyWord(KeyWord keyWord)throws Exception;
	
	/**
	 * ���¹ؼ���
	 */
	@Logable(name = "KeyWord", action = "update", property = { "id=ID,keyWord=���д�,type.type=����,creator=������,createTime=����ʱ��" })
	public String updateKeyWord(KeyWord keyWord)throws Exception;
	
	/**
	 * �ؼ����б�
	 */
	@SuppressWarnings("unchecked")
	public List findKeyWordBy( int pageNo, int pageSize, String orderBy, boolean isAsc,Collection<HibernateExpression> expressions);
	/**
	 * �ؼ��ָ���
	 */
	@SuppressWarnings("unchecked")
	public Long getKeyWordResultCount( Collection<HibernateExpression> expressions );
	
	/**
	 * ��ѯ�����ؼ���
	 */
	
	public Object getKeyWord(Integer id);
	
	/**
	 * �ؼ������͹���
	 */
	
	@Logable(name = "KeyWordType", action = "add", property = { "id=ID,type=���д���������,creator=������,createTime=����ʱ��" })
	public void addKeyWordType(KeyWordType type);
	
	@Logable(name = "KeyWordType", action = "update", property = { "id=ID,type=���д���������,creator=������,createTime=����ʱ��" })
	public void updateKeyWordType(KeyWordType type);
	
	@Logable(name = "KeyWordType", action = "delete", property = { "id=ID,type=���д���������,creator=������,createTime=����ʱ��" })
	public void delKeyWordType(KeyWordType type);
	
	public List<KeyWordType> getKeyWordTypeList(int pageNo, int pageSize, String orderBy, boolean isAsc,Collection<HibernateExpression> expressions);
	
	public KeyWordType getKeyWordType(Integer id);
	
	public Long getKeyWordTypeCount(Collection<HibernateExpression> expressions );
}
