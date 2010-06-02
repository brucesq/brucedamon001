/**
 * 
 */
package com.hunthawk.reader.service.system.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.domain.system.Privilege;
import com.hunthawk.reader.domain.system.Role;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 * 
 */
public class UserServiceImpl implements UserService {

	private static final String FIND_USER_BY_ROLE = " from UserImpl user where ? in elements(user.roles) ";
	private static final String FIND_ROLE_BY_PRIVILEGE = " from Role role where ? in elements(role.privileges) ";
	private static final String FIND_USER_BY_GROUP = " from UserImpl user where ? in elements(user.groups) ";

	private HibernateGenericController controller;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hunthawk.reader.service.system.UserService#findBy(java.lang.Class,
	 * int, int, java.lang.String, boolean, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List findBy(Class clasz, int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		return controller.findBy(clasz, pageNo, pageSize, orderBy, isAsc,
				expressions);
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	@SuppressWarnings("unchecked")
	public Long getResultCount(Class clasz,
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(clasz, expressions);

	}

	public void deleteUser(UserImpl user) {

		controller.deleteById(UserImpl.class, user.getId());

	}

	public List getAll(Class clasz) {
		return controller.getAll(clasz);
	}

	public Object getObject(Class clasz, Integer id) {
		return controller.get(clasz, id);
	}

	public void updateUser(UserImpl user) throws Exception {
		if (controller.isUnique(UserImpl.class, user, "name")) {
			controller.update(user);
		} else {
			throw new Exception("该用户名已经存在!");
		}

	}

	public void updateRole(Role role) throws Exception {
		if (controller.isUnique(Role.class, role, "name")) {
			controller.update(role);
		} else {
			throw new Exception("角色名已经存在!");
		}
	}

	public void updateGroup(Group group) throws Exception {
		if (controller.isUnique(Group.class, group, "name")) {
			controller.update(group);
		} else {
			throw new Exception("组名已经存在!");
		}

	}

	public void updatePrivilege(Privilege privilege) throws Exception {
		if (controller.isUnique(Privilege.class, privilege, "name")) {
			controller.update(privilege);
		} else {
			throw new Exception("权限名已经存在!");
		}
	}

	public void addUser(UserImpl user) throws Exception {
		if (controller.isUnique(UserImpl.class, user, "name")) {
			controller.save(user);
		} else {
			throw new Exception("用户名已经存在!");
		}
	}

	public void addGroup(Group group) throws Exception {
		if (controller.isUnique(Group.class, group, "name")) {
			controller.save(group);
		} else {
			throw new Exception("组名已经存在!");
		}

	}

	public void addRole(Role role) throws Exception {
		if (controller.isUnique(Role.class, role, "name")) {
			controller.save(role);
		} else {
			throw new Exception("角色名已经存在!");
		}

	}

	public void addPrivilege(Privilege privilege) throws Exception {
		if (controller.isUnique(Privilege.class, privilege, "name")) {
			controller.save(privilege);
		} else {
			throw new Exception("权限名已经存在!");
		}

	}

	public void deleteGroup(Group group) throws Exception {
		long count = controller.getResultCount(FIND_USER_BY_GROUP,
				new Object[] { group });
		if (count > 0L) {
			throw new Exception("组" + group.getName() + "已经被" + count
					+ "个用户使用!");
		}
		controller.delete(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aspire.pams.sims.service.UserService#deletePrivilege(com.aspire.pams
	 * .sims.model.Privilege)
	 */
	public void deletePrivilege(Privilege privilege) throws Exception {
		long count = controller.getResultCount(FIND_ROLE_BY_PRIVILEGE,
				new Object[] { privilege });
		if (count > 0L) {
			throw new Exception("权限" + privilege.getName() + "已经被" + count
					+ "个角色使用!");
		}
		controller.delete(privilege);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aspire.pams.sims.service.UserService#deleteRole(com.aspire.pams.sims
	 * .model.Role)
	 */
	public void deleteRole(Role role) throws Exception {
		long count = controller.getResultCount(FIND_USER_BY_ROLE,
				new Object[] { role });
		if (count > 0L) {
			throw new Exception("角色" + role.getName() + "已经被" + count
					+ "个用户使用!");
		}
		controller.delete(role);

	}

	public Long findUserImplExamineCount(Date beginTime, Date endTime) {
		String hql = "select count(u.id) from UserImpl u where exists"
				+ " (select l.userId from Log l where"
				+ " l.action in( 'waitToPublish','waitToAgin','waitToRejected','aginToPublish','aginToRejected','pauseToPublish','PublishTopause','reCheck')"
				+ " and u.id =l.userId ";
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
		hql += " group by l.userId)";

		return controller.getResultCount(hql, params != null
				&& params.length > 0 ? params : new Object[] {});
	}

	public List<UserImpl> findUserImplExamineList(Date beginTime, Date endTime,
			int pageNum, int pageSize) {
		String hql = "select u from UserImpl u where exists"
				+ " (select l.userId from Log l where"
				+ " l.action in( 'waitToPublish','waitToAgin','waitToRejected','aginToPublish','aginToRejected','pauseToPublish','PublishTopause','reCheck')"
				+ " and u.id =l.userId ";
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
		hql += " group by l.userId)";

		return controller.findBy(hql, pageNum, pageSize, params != null
				&& params.length > 0 ? params : new Object[] {});
	}

}
