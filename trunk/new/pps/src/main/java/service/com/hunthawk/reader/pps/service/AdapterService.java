package com.hunthawk.reader.pps.service;

import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterRule;

public interface AdapterService {

	/**
	 * ��õ����������
	 * 
	 * @param mobile
	 *            �ֻ���
	 * @return
	 * @author penglei
	 */
	public AdapterRule getAdapterRuleWithAreas(String mobile,
			Integer adapterRuleId, Integer adapterId);

	/**
	 * ���ʱ���������
	 * 
	 * @return
	 * @author penglei
	 */
	public AdapterRule getAdapterRuleWithTime(Integer adapterRuleId,
			Integer adapterId);

	/**
	 * ���UA�������
	 * 
	 * @return
	 */
	public AdapterRule getAdapterRuleWithUA(String ua, Integer adapterRuleId,
			Integer adapterId);

	/**
	 * ������������
	 * 
	 * @param ua
	 * @param adapterRuleId
	 * @param adapterId
	 * @return
	 */
	public AdapterRule getAdapterRuleWithWeek(Integer adapterRuleId,
			Integer adapterId);

	/**
	 * ���������
	 * 
	 * @param adapterRuleId
	 * @param adapterId
	 * @return
	 */
	public AdapterRule getAdapterRuleWithDay(Integer adapterRuleId,
			Integer adapterId);
	
	public Adapter getAdapterById(Integer id);
}
