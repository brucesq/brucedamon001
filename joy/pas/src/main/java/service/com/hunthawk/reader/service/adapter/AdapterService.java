package com.hunthawk.reader.service.adapter;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterRule;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.annotation.Memcached;

public interface AdapterService {

	/**
	 * ��ѯ�����������е�����
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 * @author penglei
	 */
	public List<AdapterType> findAdapterTypeList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * ��ѯ������
	 * 
	 * @param expressions
	 * @return
	 * @author penglei
	 */
	public Long findAdapterTypeCount(Collection<HibernateExpression> expressions);

	/**
	 * ��ȡ������ת���͵���������
	 * 
	 * @param expressions
	 * @return
	 * @author penglei
	 */
	public Long findAdapterCount(Collection<HibernateExpression> expressions);

	/**
	 * ��ѯ������
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param isAsc
	 * @param expressions
	 * @return
	 */
	public List<Adapter> findAdapterList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	public Long findAdapterRuleCount(Collection<HibernateExpression> expressions);

	public List<Adapter> findAdapterRuleList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions);

	/**
	 * ɾ��
	 * 
	 * @author penglei
	 * @param clazz
	 * @param entity
	 */
	@Logable(name = "Adapter", action = "delete", keyproperty = "id", property = { "id=ID,name=����������,adapterTypeId=����������ID" })
	@Memcached(targetClass = AdapterRule.class, properties = { "id#!list",
			"id#!adapter", "id" })
	public void deleteAdapter(Adapter entity);

	@Logable(name = "AdapterRule", action = "delete", keyproperty = "id", property = { "id=ID,name=��������������,adapterTypeId=����������ID,adapterId=������ID" })
	@Memcached(targetClass = AdapterRule.class, properties = {
			"adapterId#!list", "id" })
	public void deleteAdapterRule(AdapterRule entity);

	@Logable(name = "Adapter", action = "add", keyproperty = "id", property = { "id=ID,name=����������,adapterTypeId=����������ID" })
	public void saveAdapter(Adapter adapter, UserImpl user);

	@Logable(name = "Adapter", action = "update", keyproperty = "id", property = { "id=ID,name=����������,adapterTypeId=����������ID" })
	@Memcached(targetClass = AdapterRule.class, properties = { "id#!list",
			"id#!adapter", "id" })
	public void updateAdapter(Adapter adapter, UserImpl user);

	@Logable(name = "AdapterRule", action = "add", keyproperty = "id", property = { "id=ID,name=��������������,adapterTypeId=����������ID,adapterId=������ID" })
	public void saveAdapterRule(AdapterRule adapterRule);

	@Logable(name = "AdapterRule", action = "update", keyproperty = "id", property = { "id=ID,name=��������������,adapterTypeId=����������ID,adapterId=������ID" })
	@Memcached(targetClass = AdapterRule.class, properties = {
			"adapterId#!list", "id" })
	public void updateAdapterRule(AdapterRule adapterRule);

	@Logable(name = "Adapter", action = "update", keyproperty = "id", property = { "id=ID,name=����������,adapterTypeId=����������ID,status=״̬" })
	@Memcached(targetClass = AdapterRule.class, properties = { "id#!list",
			"id#!adapter", "id" })
	public void changeStatusAdapter(Adapter adapter, Integer status)
			throws Exception;

	@Logable(name = "AdapterRule", action = "update", keyproperty = "id", property = { "id=ID,name=��������������,adapterTypeId=����������ID,adapterId=������ID" })
	@Memcached(targetClass = AdapterRule.class, properties = {
			"adapterId#!list", "id" })
	public void changeStatusAdapterRule(AdapterRule adapterRule, Integer status)
			throws Exception;
	
	

	public AdapterType getAdapterTypeById(Integer id);

	public Adapter getAdapterById(Integer id);
}
