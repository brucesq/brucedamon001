package com.hunthawk.reader.service.system.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.service.system.SystemService;

public class SystemServiceImpl implements SystemService {

	private HibernateGenericController controller;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hunthawk.reader.service.system.UserService#findBy(java.lang.Class,
	 * int, int, java.lang.String, boolean, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List findVariablesBy(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		return controller.findBy(Variables.class, pageNo, pageSize, orderBy,
				isAsc, expressions);
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	@SuppressWarnings("unchecked")
	public Long getVariablesResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(Variables.class, expressions);

	}

	public Variables getVariables(String name) {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("name", name,
				CompareType.Equal);
		hibernateExpressions.add(ex);
		List<Variables> variables = controller.findBy(Variables.class, 1, 1,
				hibernateExpressions);
		if (variables.size() > 0) {
			return variables.get(0);
		}
		return null;
	}

	public Object getVariables(Integer id) {
		return controller.get(Variables.class, id);
	}

	public void updateVariables(Variables object) {
		controller.update(object);
	}

	public void addVariables(Variables object) {
		controller.save(object);
	}

	public void deleteVariables(Variables object) {
		controller.delete(object);
	}

	public List<Log> findLogBy(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		return controller.findBy(Log.class, pageNo, pageSize, orderBy, isAsc,
				expressions);
	}

	public Long getLogResultCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(Log.class, expressions);
	}

	public void addLog(Log log) {
		controller.save(log);
	}

	public Long findExamineActionByCount(Integer userId, String action) {
		String hql = "select count(l.action) from Log l where l.userId = ? and l.action =?";

		return controller.getResultCount(hql, userId, action);
	}

	public Long findExamineResourceCount(Date beginTime, Date endTime) {

		String hql = "select count(*) from Log l where exists (select r.id from ResourceAll r "
				+ "where r.id=substr(l.key,2,length(l.key)-2)) and l.name = 'ResourceAll' and"
				+ " l.action in('waitToPublish','waitToAgin','waitToRejected','aginToPublish','aginToRejected','pauseToPublish','PublishTopause','reCheck') ";
		Object[] params = null;
		if (beginTime != null && endTime != null) {
			hql += " and l.logTime between ? and ?";
			params = new Object[] { beginTime,
					new Date(endTime.getTime() + (1000 * 60 * 60 * 24)) };
		} else if (beginTime != null) {
			hql += " and l.logTime >= ?";
			params = new Object[] { beginTime };
		} else if (endTime != null) {
			hql += " and l.logTime <= ?";
			params = new Object[] { new Date(endTime.getTime()
					+ (1000 * 60 * 60 * 24)) };
		}
		return controller.getResultCount(hql, params != null
				&& params.length > 0 ? params : new Object[] {});
	}

	public List<Log> findExamineResourceList(Date beginTime, Date endTime,
			int pageNum, int pageSize) {
		String hql = "select l from Log l where exists (select r.id from ResourceAll r "
				+ "where r.id=substr(l.key,2,length(l.key)-2)) and l.name = 'ResourceAll' and"
				+ " l.action in('waitToPublish','waitToAgin','waitToRejected','aginToPublish','aginToRejected','pauseToPublish','PublishTopause','reCheck') ";
		Object[] params = null;
		if (beginTime != null && endTime != null) {
			hql += " and l.logTime between ? and ?";
			params = new Object[] { beginTime,
					new Date(endTime.getTime() + (1000 * 60 * 60 * 24)) };
		} else if (beginTime != null) {
			hql += " and l.logTime >= ?";
			params = new Object[] { beginTime };
		} else if (endTime != null) {
			hql += " and l.logTime <= ?";
			params = new Object[] { new Date(endTime.getTime()
					+ (1000 * 60 * 60 * 24)) };
		}
		return controller.findBy(hql, pageNum, pageSize, params != null
				&& params.length > 0 ? params : new Object[] {});
	}

}
