package com.hunthawk.reader.service.adapter.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterRule;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.adapter.AdapterService;
import com.hunthawk.reader.service.bussiness.impl.BussinessServiceImpl;

public class AdapterServiceImpl implements AdapterService {

	private static final Logger logger = LoggerFactory
			.getLogger(BussinessServiceImpl.class);

	private HibernateGenericController controller;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public Long findAdapterTypeCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(AdapterType.class, expressions);
	}

	public List<AdapterType> findAdapterTypeList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		List list = controller.findBy(AdapterType.class, pageNo, pageSize,
				orderBy, isAsc, expressions);
		return list;
	}

	public Long findAdapterCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(Adapter.class, expressions);
	}

	public List<Adapter> findAdapterList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(Adapter.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public void deleteAdapter(Adapter entity) {
		Adapter adapter = (Adapter) entity;
		List<AdapterRule> adapterRuleList = controller.findBy(
				AdapterRule.class, "adapterId", adapter.getId());
		for (AdapterRule adapterRule : adapterRuleList) {
			controller.delete(adapterRule);
		}
		controller.delete(entity);

	}

	public void deleteAdapterRule(AdapterRule entity) {
		controller.delete(entity);

	}

	public Long findAdapterRuleCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(AdapterRule.class, expressions);
	}

	public List<Adapter> findAdapterRuleList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(AdapterRule.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public void changeStatusAdapter(Adapter adapter, Integer status)
			throws Exception {
		adapter.setStatus(status);
		try {
			controller.update(adapter);

		} catch (Exception e) {
			throw new Exception();
		}

	}

	public void changeStatusAdapterRule(AdapterRule adapterRule, Integer status)
			throws Exception {
		adapterRule.setStatus(status);
		try {
			controller.update(adapterRule);
		} catch (Exception e) {
			throw new Exception();
		}

	}

	public void saveAdapter(Adapter adapter, UserImpl user) {

		adapter.setStatus(0);
		adapter.setCreateTime(new Date());
		adapter.setModifyTime(new Date());
		// 最后修改人和创造人
		adapter.setCreatorId(user.getId());
		adapter.setModifierId(user.getId());

		controller.save(adapter);

	}

	public void updateAdapter(Adapter adapter, UserImpl user) {

		adapter.setStatus(0);

		adapter.setModifyTime(new Date());
		// 最后修改人
		adapter.setModifierId(user.getId());
		controller.update(adapter);
	}

	public void saveAdapterRule(AdapterRule adapterRule) {
		adapterRule.setStatus(0);
		controller.save(adapterRule);

	}

	public void updateAdapterRule(AdapterRule adapterRule) {
		adapterRule.setStatus(0);
		controller.update(adapterRule);

	}


	public AdapterType getAdapterTypeById(Integer id) {
		AdapterType adapterType = null;
		adapterType = controller.get(AdapterType.class, id);
		return adapterType;
	}

	public Adapter getAdapterById(Integer id) {
		Adapter adapter = null;
		adapter = controller.get(Adapter.class, id);
		return adapter;
	}

}
