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
	 * 查询适配类型所有的数据
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
	 * 查询总行数
	 * 
	 * @param expressions
	 * @return
	 * @author penglei
	 */
	public Long findAdapterTypeCount(Collection<HibernateExpression> expressions);

	/**
	 * 获取适配跳转类型的所有数据
	 * 
	 * @param expressions
	 * @return
	 * @author penglei
	 */
	public Long findAdapterCount(Collection<HibernateExpression> expressions);

	/**
	 * 查询总行数
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
	 * 删除
	 * 
	 * @author penglei
	 * @param clazz
	 * @param entity
	 */
	@Logable(name = "Adapter", action = "delete", keyproperty = "id", property = { "id=ID,name=适配器名称,adapterTypeId=适配器类型ID" })
	@Memcached(targetClass = AdapterRule.class, properties = { "id#!list",
			"id#!adapter", "id" })
	public void deleteAdapter(Adapter entity);

	@Logable(name = "AdapterRule", action = "delete", keyproperty = "id", property = { "id=ID,name=适配器规则名称,adapterTypeId=适配器类型ID,adapterId=适配器ID" })
	@Memcached(targetClass = AdapterRule.class, properties = {
			"adapterId#!list", "id" })
	public void deleteAdapterRule(AdapterRule entity);

	@Logable(name = "Adapter", action = "add", keyproperty = "id", property = { "id=ID,name=适配器名称,adapterTypeId=适配器类型ID" })
	public void saveAdapter(Adapter adapter, UserImpl user);

	@Logable(name = "Adapter", action = "update", keyproperty = "id", property = { "id=ID,name=适配器名称,adapterTypeId=适配器类型ID" })
	@Memcached(targetClass = AdapterRule.class, properties = { "id#!list",
			"id#!adapter", "id" })
	public void updateAdapter(Adapter adapter, UserImpl user);

	@Logable(name = "AdapterRule", action = "add", keyproperty = "id", property = { "id=ID,name=适配器规则名称,adapterTypeId=适配器类型ID,adapterId=适配器ID" })
	public void saveAdapterRule(AdapterRule adapterRule);

	@Logable(name = "AdapterRule", action = "update", keyproperty = "id", property = { "id=ID,name=适配器规则名称,adapterTypeId=适配器类型ID,adapterId=适配器ID" })
	@Memcached(targetClass = AdapterRule.class, properties = {
			"adapterId#!list", "id" })
	public void updateAdapterRule(AdapterRule adapterRule);

	@Logable(name = "Adapter", action = "update", keyproperty = "id", property = { "id=ID,name=适配器名称,adapterTypeId=适配器类型ID,status=状态" })
	@Memcached(targetClass = AdapterRule.class, properties = { "id#!list",
			"id#!adapter", "id" })
	public void changeStatusAdapter(Adapter adapter, Integer status)
			throws Exception;

	@Logable(name = "AdapterRule", action = "update", keyproperty = "id", property = { "id=ID,name=适配器规则名称,adapterTypeId=适配器类型ID,adapterId=适配器ID" })
	@Memcached(targetClass = AdapterRule.class, properties = {
			"adapterId#!list", "id" })
	public void changeStatusAdapterRule(AdapterRule adapterRule, Integer status)
			throws Exception;
	
	

	public AdapterType getAdapterTypeById(Integer id);

	public Adapter getAdapterById(Integer id);
}
