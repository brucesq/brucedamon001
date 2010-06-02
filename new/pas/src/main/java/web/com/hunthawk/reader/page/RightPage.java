package com.hunthawk.reader.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.security.User;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.RightField;
import com.hunthawk.reader.page.util.RightIterator;
import com.hunthawk.reader.security.GroupPower;
import com.hunthawk.reader.security.PowerUtil;
import com.hunthawk.reader.security.UserPower;
import com.hunthawk.reader.service.system.UserService;

/**
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class RightPage extends SecurityPage implements IExternalPage {

	public abstract RightField getRight();

	@InjectObject("spring:hibernateGenericController")
	public abstract HibernateGenericController getHibernateGenericController();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	/**
	 * 获得保存了群组权限的集合
	 * 
	 * @return
	 */
	public abstract Set getSelectedRights();

	public abstract void setSelectedRights(Set set);

	/**
	 * 获得保存了用户权限的集合
	 * 
	 * @author dingjiangtao_a Jun 16, 2008 9:39:56 AM
	 * @version 1.0
	 * @return
	 */
	public abstract Set getUserSelectedRights();

	public abstract void setUserSelectedRights(Set set);

	public abstract Object getObject();

	public abstract void setObject(Object object);

	/**
	 * 获得存入对象的集合
	 * 
	 * @return
	 */
	public abstract Set getSelectedObjects();

	/**
	 * 将选择的对象放入集合中
	 * 
	 * @param set
	 */
	public abstract void setSelectedObjects(Set set);

	public abstract Object getCurrentObject();

	public void setCheckboxSelected(boolean bSelected) {
		Object object = getCurrentObject();

		// 从session中获取集合对象，保证数据的正确
		Set selectedObject = getSelectedObjects();

		// 如果对象被选择
		if (bSelected) {
			selectedObject.add(object);
		} else {
			selectedObject.remove(object);
		}

		// persist value
		setSelectedObjects(selectedObject);

	}

	/**
	 * 判断当前对象是否被选择
	 * 
	 * @return
	 */
	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	/**
	 * 获得对象的操作权限列表
	 * 
	 * @return
	 */
	public List getRightList() {
		// 构造一个查看权限
		RightField seeRight = new RightField();
		seeRight.setId(PowerUtil.SEE);
		seeRight.setTitle("查看权限");

		// 构造一个修改权限
		RightField eidtRight = new RightField();
		eidtRight.setId(PowerUtil.EDIT);
		eidtRight.setTitle("修改权限");

		// 构造一个删除权限
		RightField deleteRight = new RightField();
		deleteRight.setId(PowerUtil.DELETE);
		deleteRight.setTitle("删除权限");

		List rightList = new ArrayList();

		rightList.add(seeRight);
		rightList.add(eidtRight);
		rightList.add(deleteRight);
		return rightList;
	}

	/**
	 * 获得组已有的权限列表
	 * 
	 * @return
	 */
	public List getGroupRightList() {

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// 获得对象已经赋予了的权限
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		List<RightIterator> rightIteratorList = new ArrayList();
		for (int i = 0; i < groupPowerList.size(); i++) {
			GroupPower grouppower = groupPowerList.get(i);
			RightIterator rightIterator = new RightIterator(grouppower);

			rightIteratorList.add(rightIterator);

		}

		logger.info("组权限列表的个数：" + rightIteratorList.size());

		return rightIteratorList;
	}

	/**
	 * 删除组的权限
	 * 
	 * @param cycle
	 */
	public void deleteGroupRight(IRequestCycle cycle) {
		logger.info("删除组权限");

		// 需要删除的个数
		int selectedSize = getSelectedObjects().size();
		if (selectedSize == 0) {
			ValidationDelegate delegate = getDelegate();
			delegate.setFormComponent(null);
			delegate.record("至少得选择一个群组进行删除", null);
			return;
		}
		logger.info("删除的个数:" + this.getSelectedObjects().size());

		Set selectedGroupPower = getSelectedObjects();

		Iterator groupIterator = selectedGroupPower.iterator();

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// 获得对象已经赋予了的权限
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		// int groupSize = groupPowerList.size();

		logger.info("开始组的权限个数:" + groupPowerList.size());

		while (groupIterator.hasNext()) {
			RightIterator rightIterator = (RightIterator) groupIterator.next();

			for (int i = 0; i < groupPowerList.size(); i++) {
				// 是要删除的权限
				if (rightIterator.getId() == groupPowerList.get(i).getGroup()
						.getId()) {
					groupPowerList.remove(i);
				}
			}

		}

		Object updateObject = getObject();

		logger.info("要删除权限的对象:" + updateObject);
		logger.info("组的权限个数:" + groupPowerList.size());

		Object[] objects = new Object[] { updateObject };

		// 赋权操作
		PowerUtil.updatePower(updateObject, userPowerList, groupPowerList);
		// setSucess(true);
		try {
			logger.info("更新对象的权限");

			getHibernateGenericController().update(updateObject);
			logger.info("权限更新完成");
		} catch (Exception e) {
			logger.info(e);
		} finally {

			this.setSelectedObjects(new HashSet());
		}
	}

	/**
	 * 获得用户已有的权限列表
	 * 
	 * @return
	 */
	public List getUserRightList() {
		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// 获得对象已经赋予了的权限
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		List<RightIterator> rightIteratorList = new ArrayList();
		for (int i = 0; i < userPowerList.size(); i++) {
			UserPower userPower = userPowerList.get(i);
			RightIterator rightIterator = new RightIterator(userPower);

			rightIteratorList.add(rightIterator);

		}

		logger.info("用户权限列表的个数：" + rightIteratorList.size());
		for (int i = 0; i < rightIteratorList.size(); i++) {
			logger.info("查看权限:" + rightIteratorList.get(i).getSeeRight());
			logger.info("编辑权限:" + rightIteratorList.get(i).getEditRight());
			logger.info("查看权限:" + rightIteratorList.get(i).getDeleteRight());
		}

		return rightIteratorList;
	}

	/**
	 * 删除用户的权限
	 * 
	 * @param cycle
	 */
	public void deleteUserRight(IRequestCycle cycle) {
		logger.info("删除用户权限");

		// 需要删除的个数
		int selectedSize = getSelectedObjects().size();

		if (selectedSize == 0) {
			ValidationDelegate delegate = getDelegate();
			delegate.setFormComponent(null);
			delegate.record("至少得选择一个用户进行删除", null);
			return;
		}
		logger.info("删除的个数:" + this.getSelectedObjects().size());

		Set selectedUserPower = getSelectedObjects();

		Iterator userIterator = selectedUserPower.iterator();

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// 获得对象已经赋予了的权限
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		// int groupSize = groupPowerList.size();

		logger.info("开始组的权限个数:" + groupPowerList.size());

		while (userIterator.hasNext()) {
			RightIterator rightIterator = (RightIterator) userIterator.next();

			for (int i = 0; i < userPowerList.size(); i++) {
				// 是要删除的权限
				if (rightIterator.getId() == userPowerList.get(i).getUser()
						.getId()) {
					userPowerList.remove(i);
				}
			}

		}

		Object updateObject = getObject();

		logger.info("要删除权限的对象:" + updateObject);
		logger.info("组的权限个数:" + groupPowerList.size());

		Object[] objects = new Object[] { updateObject };

		// 赋权操作
		PowerUtil.updatePower(updateObject, userPowerList, groupPowerList);
		// setSucess(true);
		try {
			logger.info("更新对象的权限");
			getHibernateGenericController().update(updateObject);
			logger.info("权限更新完成");
		} catch (Exception e) {
			logger.info(e);
		} finally {

			setSelectedObjects(new HashSet());
		}
	}

	/**
	 * 获得组列表
	 * 
	 * @return
	 */
	public IPropertySelectionModel getGroups() {
		List<Group> groups = getUserService().findBy(Group.class, 1, Integer.MAX_VALUE,
				"id", false, new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				groups, Group.class, "getName", "getId", false, null);
		return model;
	}

	/**
	 * 获得用户列表
	 * 
	 * @return
	 */
	public IPropertySelectionModel getUsers() {
		List<UserImpl> users = getUserService().findBy(UserImpl.class, 1, Integer.MAX_VALUE,
				"id", false, new ArrayList<HibernateExpression>());

		ObjectPropertySelectionModel privilegeSelection = new ObjectPropertySelectionModel(
				users, UserImpl.class, "getName", "getId", false, null);
		return privilegeSelection;
	}

	@InjectComponent("objectUserList")
	public abstract Block getObjectUserList();

	@InjectComponent("objectuserExist")
	public abstract Block getObjectuserExist();

	@InjectComponent("groupList")
	public abstract Block getGroupList();

	@InjectComponent("groupExist")
	public abstract Block getGroupExist();

	/**
	 * 选择的组
	 * 
	 * @return
	 */
	public abstract List getSelectedGroup();

	public abstract void setSelectedGroup(List group);

	/**
	 * 所选的用户
	 * 
	 * @return
	 */
	public abstract List getSelectedUser();

	public abstract void setSelectedUser(List user);

	/**
	 * 保存用户选择的群组权限
	 * 
	 * @param bSelected
	 */
	public void setRightCheckboxSelected(boolean bSelected) {
		RightField right = getRight();

		Set selectedObject = getSelectedRights();

		if (bSelected) {
			selectedObject.add(right);
		}

		else {
			selectedObject.remove(right);
		}

		// persist value
		setSelectedRights(selectedObject);

	}

	public boolean getRightCheckboxSelected() {
		return getSelectedRights().contains(getRight());
	}

	/**
	 * 设置用户的权限
	 * 
	 * @author dingjiangtao_a Jun 16, 2008 9:38:18 AM
	 * @version 1.0
	 * @param bSelected
	 */
	public void setUserRightCheckboxSelected(boolean bSelected) {
		RightField right = getRight();

		Set selectedObject = getUserSelectedRights();

		if (bSelected) {
			selectedObject.add(right);
		}

		else {
			selectedObject.remove(right);
		}

		// persist value
		setUserSelectedRights(selectedObject);

	}

	/**
	 * 得到用户的权限
	 * 
	 * @author dingjiangtao_a Jun 16, 2008 9:38:37 AM
	 * @version 1.0
	 * @return
	 */
	public boolean getUserRightCheckboxSelected() {
		return getUserSelectedRights().contains(getRight());
	}

	/**
	 * 添加权限
	 * 
	 * @param cycle
	 */
	public void addRight(IRequestCycle cycle) {

		logger.info("开始添加群组权限的对象:" + getObject());
		ValidationDelegate delegate = getDelegate();
		Set setSelectedRights = getSelectedRights();

		// 权限赋予的群组
		List<Group> groupList = this.getSelectedGroup();

		int setSize = setSelectedRights.size();

		if (setSize == 0 && groupList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("请选择要赋权的群组和权限", null);
			return;
		}

		// 如果没有选择权限
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("请选择权限", null);
			return;
		}
		if (groupList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("请选择要赋权的群组", null);
			return;
		}

		logger.info("权限个数：" + setSize);

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// 不能重复赋权的组
		List<Group> notGroupList = new ArrayList();

		Iterator selectedIterator = setSelectedRights.iterator();

		// 获得对象已经赋予了的权限
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		boolean groupExist = false;
		for (int j = 0; j < groupList.size(); j++) {
			Group group = groupList.get(j);
			for (int i = 0; i < groupPowerList.size(); i++) {
				if (group.getId() == groupPowerList.get(i).getGroup().getId()) {
					notGroupList.add(group);
				}
			}
		}

		if (notGroupList.size() > 0) {

			String groupName = "";
			for (int i = 0; i < notGroupList.size(); i++) {
				groupName = groupName + ((Group) notGroupList.get(i)).getName()
						+ ",";
			}

			if (groupName.endsWith(",")) {
				groupName = groupName.substring(0, groupName.length() - 1);
			}

			delegate.setFormComponent(null);
			delegate.record("组[" + groupName + "]不能多次赋权", null);
		}

		groupList.removeAll(notGroupList);

		// 组已经赋权
		// if (groupExist)
		// {
		// delegate.setFormComponent(null);
		// delegate.record("一个组不能多次赋权", null);
		// return;
		// }

		logger.info("组对象个数:" + groupPowerList.size());
		logger.info("用户对象个数:" + userPowerList.size());

		// for (int i = 0; i < userPowerList.size(); i++)
		// {
		// logger.info("组名:" + userPowerList.get(i).getUser().getSysName());
		//
		// }

		// 保存群组权限的SET
		Set groupSet = new HashSet();

		// 选择的群组的个数
		int groupSize = groupList.size();

		// 群组权限列表的构造
		for (int i = 0; i < groupSize; i++) {
			// 获得选择的权限的迭代器
			Iterator iterator = setSelectedRights.iterator();
			logger.info("权限的迭代器的大小:" + iterator.hasNext());
			GroupPower groupPower = new GroupPower();
			groupPower.setGroup(groupList.get(i));
			// 迭代用户选择的权限
			while (iterator.hasNext()) {
				RightField rightField = (RightField) iterator.next();
				groupPower.addPower(rightField.getId());

			}
			groupPowerList.add(groupPower);
		}

		// clear selection
		setSelectedRights(new HashSet());

		// logger.info("组:" + groupPowerList.get(0).getGroup().getName());
		// logger.info("增加的权限个数:" + groupPowerList.get(0).getPowers().size());

		Object updateObject = getObject();

		Object[] objects = new Object[] { updateObject };

		logger.info("对象:" + updateObject);
		logger.info("用户权限对象的个数:" + userPowerList.size());
		logger.info("组权限对象的个数:" + groupPowerList.size());

		// 赋权操作
		PowerUtil.updatePower(updateObject, userPowerList, groupPowerList);
		logger.info("改变后的对象:" + updateObject);
		// setSucess(true);
		try {
			logger.info("更新对象的权限");
			getHibernateGenericController().update(updateObject);
			logger.info("权限更新完成");
		} catch (Exception e) {
			logger.info(e);
		}
	}

	/**
	 * 添加用户的权限
	 * 
	 * @param cycle
	 */
	public void addUserRight(IRequestCycle cycle) {
		Set setSelectedRights = getUserSelectedRights();
		ValidationDelegate delegate = getDelegate();

		// 权限赋予的用户
		List<User> userList = this.getSelectedUser();

		int setSize = setSelectedRights.size();

		if (setSize == 0 && userList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("请选择要赋权的用户和权限", null);
			return;
		}

		// 如果没有选择权限
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("请选择权限", null);
			return;
		}
		if (userList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("请选择要赋权的用户", null);
			return;
		}

		// 权限已经赋于的组
		List<GroupPower> groupPowerList = new ArrayList();

		// 权限已经赋于的用户
		List<UserPower> userPowerList = new ArrayList();

		// 不能重复赋权的用户
		List<User> notUserList = new ArrayList();

		// 获得对象已经赋予了的权限
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		logger.info("权限个数：" + setSize);

		boolean userExist = false;
		for (int j = 0; j < userList.size(); j++) {
			User user = userList.get(j);
			for (int i = 0; i < userPowerList.size(); i++) {
				if (user.getId() == userPowerList.get(i).getUser().getId()) {
					notUserList.add(user);

				}
			}
		}

		if (notUserList.size() > 0) {

			String userName = "";
			for (int i = 0; i < notUserList.size(); i++) {
				userName = userName + ((User) notUserList.get(i)).getName()
						+ ",";
			}

			if (userName.endsWith(",")) {
				userName = userName.substring(0, userName.length() - 1);
			}
			delegate.setFormComponent(null);
			delegate.record("用户[" + userName + "]不能多次赋权", null);
		}

		userList.removeAll(notUserList);

		// 用户已经赋权
		// if (userExist)
		// {
		// delegate.setFormComponent(null);
		// delegate.record("一个用户不能多次赋权", null);
		// return;
		// }

		// 保存用户权限的SET
		Set userSet = new HashSet();

		// 选择的用户的个数
		int userSize = userList.size();

		// 用户权限列表的构造
		for (int i = 0; i < userSize; i++) {
			// 获得选择的权限的迭代器
			Iterator iterator = setSelectedRights.iterator();

			UserPower userPower = new UserPower();
			userPower.setUser(userList.get(i));
			// 迭代用户选择的权限
			while (iterator.hasNext()) {
				logger.info("用户的权限的迭代器的大小:");
				RightField rightField = (RightField) iterator.next();
				userPower.addPower(rightField.getId());
				logger.info("Power:" + rightField.getId());
			}
			userPowerList.add(userPower);
		}

		setUserSelectedRights(new HashSet());

		Object updateObject = getObject();

		Object[] objects = new Object[] { updateObject };

		for (int i = 0; i < userPowerList.size(); i++) {
			Set set = userPowerList.get(i).getPowers();

			Iterator it = set.iterator();
			while (it.hasNext()) {
				logger.info("权限:" + it.next());
			}
		}

		// 赋权操作
		PowerUtil.updatePower(getObject(), userPowerList, groupPowerList);

		try {
			logger.info("更新对象的权限");
			getHibernateGenericController().update(getObject());
			logger.info("权限更新完成");
		} catch (Exception e) {
			logger.info(e);
		}

	}

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {

		/**
		 * parameters的第一个参数为赋权对象的id,第二个参数为那种业务对象，第三个参数为更新对象的方法 第四个参数为由id获得对象的方法
		 */

		setObject(parameters[0]);

		// 权限已经赋于的组
		List<GroupPower> groupPowerList = new ArrayList();

		// 权限已经赋于的用户
		List<UserPower> userPowerList = new ArrayList();

		// 权限赋予的用户
		List<User> userList = this.getSelectedUser();

		// 获得对象已经赋予了的权限
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		List<User> userListTmp = new ArrayList();

		int size = userPowerList.size();
		for (int i = 0; i < size; i++) {
			userListTmp.add(userPowerList.get(i).getUser());

		}

		this.setSelectedUser(userListTmp);

		List<Group> groupListTmp = new ArrayList();

		size = groupPowerList.size();

		for (int i = 0; i < size; i++) {
			groupListTmp.add(groupPowerList.get(i).getGroup());
		}

		this.setSelectedGroup(groupListTmp);
	}

	private boolean sucess;

	public boolean isSucess() {
		return sucess;
	}

	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}

}
