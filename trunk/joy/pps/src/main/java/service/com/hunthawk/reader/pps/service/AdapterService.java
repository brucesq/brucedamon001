package com.hunthawk.reader.pps.service;

import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterRule;

public interface AdapterService {

	/**
	 * 获得地区适配规则
	 * 
	 * @param mobile
	 *            手机号
	 * @return
	 * @author penglei
	 */
	public AdapterRule getAdapterRuleWithAreas(String mobile,
			Integer adapterRuleId, Integer adapterId);

	/**
	 * 获得时间适配规则
	 * 
	 * @return
	 * @author penglei
	 */
	public AdapterRule getAdapterRuleWithTime(Integer adapterRuleId,
			Integer adapterId);

	/**
	 * 获得UA适配规则
	 * 
	 * @return
	 */
	public AdapterRule getAdapterRuleWithUA(String ua, Integer adapterRuleId,
			Integer adapterId);

	/**
	 * 获得周适配规则
	 * 
	 * @param ua
	 * @param adapterRuleId
	 * @param adapterId
	 * @return
	 */
	public AdapterRule getAdapterRuleWithWeek(Integer adapterRuleId,
			Integer adapterId);

	/**
	 * 获得天适配
	 * 
	 * @param adapterRuleId
	 * @param adapterId
	 * @return
	 */
	public AdapterRule getAdapterRuleWithDay(Integer adapterRuleId,
			Integer adapterId);
	
	public Adapter getAdapterById(Integer id);
}
