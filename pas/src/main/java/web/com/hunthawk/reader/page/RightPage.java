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
	 * ��ñ�����Ⱥ��Ȩ�޵ļ���
	 * 
	 * @return
	 */
	public abstract Set getSelectedRights();

	public abstract void setSelectedRights(Set set);

	/**
	 * ��ñ������û�Ȩ�޵ļ���
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
	 * ��ô������ļ���
	 * 
	 * @return
	 */
	public abstract Set getSelectedObjects();

	/**
	 * ��ѡ��Ķ�����뼯����
	 * 
	 * @param set
	 */
	public abstract void setSelectedObjects(Set set);

	public abstract Object getCurrentObject();

	public void setCheckboxSelected(boolean bSelected) {
		Object object = getCurrentObject();

		// ��session�л�ȡ���϶��󣬱�֤���ݵ���ȷ
		Set selectedObject = getSelectedObjects();

		// �������ѡ��
		if (bSelected) {
			selectedObject.add(object);
		} else {
			selectedObject.remove(object);
		}

		// persist value
		setSelectedObjects(selectedObject);

	}

	/**
	 * �жϵ�ǰ�����Ƿ�ѡ��
	 * 
	 * @return
	 */
	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	/**
	 * ��ö���Ĳ���Ȩ���б�
	 * 
	 * @return
	 */
	public List getRightList() {
		// ����һ���鿴Ȩ��
		RightField seeRight = new RightField();
		seeRight.setId(PowerUtil.SEE);
		seeRight.setTitle("�鿴Ȩ��");

		// ����һ���޸�Ȩ��
		RightField eidtRight = new RightField();
		eidtRight.setId(PowerUtil.EDIT);
		eidtRight.setTitle("�޸�Ȩ��");

		// ����һ��ɾ��Ȩ��
		RightField deleteRight = new RightField();
		deleteRight.setId(PowerUtil.DELETE);
		deleteRight.setTitle("ɾ��Ȩ��");

		List rightList = new ArrayList();

		rightList.add(seeRight);
		rightList.add(eidtRight);
		rightList.add(deleteRight);
		return rightList;
	}

	/**
	 * ��������е�Ȩ���б�
	 * 
	 * @return
	 */
	public List getGroupRightList() {

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// ��ö����Ѿ������˵�Ȩ��
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		List<RightIterator> rightIteratorList = new ArrayList();
		for (int i = 0; i < groupPowerList.size(); i++) {
			GroupPower grouppower = groupPowerList.get(i);
			RightIterator rightIterator = new RightIterator(grouppower);

			rightIteratorList.add(rightIterator);

		}

		logger.info("��Ȩ���б�ĸ�����" + rightIteratorList.size());

		return rightIteratorList;
	}

	/**
	 * ɾ�����Ȩ��
	 * 
	 * @param cycle
	 */
	public void deleteGroupRight(IRequestCycle cycle) {
		logger.info("ɾ����Ȩ��");

		// ��Ҫɾ���ĸ���
		int selectedSize = getSelectedObjects().size();
		if (selectedSize == 0) {
			ValidationDelegate delegate = getDelegate();
			delegate.setFormComponent(null);
			delegate.record("���ٵ�ѡ��һ��Ⱥ�����ɾ��", null);
			return;
		}
		logger.info("ɾ���ĸ���:" + this.getSelectedObjects().size());

		Set selectedGroupPower = getSelectedObjects();

		Iterator groupIterator = selectedGroupPower.iterator();

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// ��ö����Ѿ������˵�Ȩ��
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		// int groupSize = groupPowerList.size();

		logger.info("��ʼ���Ȩ�޸���:" + groupPowerList.size());

		while (groupIterator.hasNext()) {
			RightIterator rightIterator = (RightIterator) groupIterator.next();

			for (int i = 0; i < groupPowerList.size(); i++) {
				// ��Ҫɾ����Ȩ��
				if (rightIterator.getId() == groupPowerList.get(i).getGroup()
						.getId()) {
					groupPowerList.remove(i);
				}
			}

		}

		Object updateObject = getObject();

		logger.info("Ҫɾ��Ȩ�޵Ķ���:" + updateObject);
		logger.info("���Ȩ�޸���:" + groupPowerList.size());

		Object[] objects = new Object[] { updateObject };

		// ��Ȩ����
		PowerUtil.updatePower(updateObject, userPowerList, groupPowerList);
		// setSucess(true);
		try {
			logger.info("���¶����Ȩ��");

			getHibernateGenericController().update(updateObject);
			logger.info("Ȩ�޸������");
		} catch (Exception e) {
			logger.info(e);
		} finally {

			this.setSelectedObjects(new HashSet());
		}
	}

	/**
	 * ����û����е�Ȩ���б�
	 * 
	 * @return
	 */
	public List getUserRightList() {
		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// ��ö����Ѿ������˵�Ȩ��
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		List<RightIterator> rightIteratorList = new ArrayList();
		for (int i = 0; i < userPowerList.size(); i++) {
			UserPower userPower = userPowerList.get(i);
			RightIterator rightIterator = new RightIterator(userPower);

			rightIteratorList.add(rightIterator);

		}

		logger.info("�û�Ȩ���б�ĸ�����" + rightIteratorList.size());
		for (int i = 0; i < rightIteratorList.size(); i++) {
			logger.info("�鿴Ȩ��:" + rightIteratorList.get(i).getSeeRight());
			logger.info("�༭Ȩ��:" + rightIteratorList.get(i).getEditRight());
			logger.info("�鿴Ȩ��:" + rightIteratorList.get(i).getDeleteRight());
		}

		return rightIteratorList;
	}

	/**
	 * ɾ���û���Ȩ��
	 * 
	 * @param cycle
	 */
	public void deleteUserRight(IRequestCycle cycle) {
		logger.info("ɾ���û�Ȩ��");

		// ��Ҫɾ���ĸ���
		int selectedSize = getSelectedObjects().size();

		if (selectedSize == 0) {
			ValidationDelegate delegate = getDelegate();
			delegate.setFormComponent(null);
			delegate.record("���ٵ�ѡ��һ���û�����ɾ��", null);
			return;
		}
		logger.info("ɾ���ĸ���:" + this.getSelectedObjects().size());

		Set selectedUserPower = getSelectedObjects();

		Iterator userIterator = selectedUserPower.iterator();

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// ��ö����Ѿ������˵�Ȩ��
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		// int groupSize = groupPowerList.size();

		logger.info("��ʼ���Ȩ�޸���:" + groupPowerList.size());

		while (userIterator.hasNext()) {
			RightIterator rightIterator = (RightIterator) userIterator.next();

			for (int i = 0; i < userPowerList.size(); i++) {
				// ��Ҫɾ����Ȩ��
				if (rightIterator.getId() == userPowerList.get(i).getUser()
						.getId()) {
					userPowerList.remove(i);
				}
			}

		}

		Object updateObject = getObject();

		logger.info("Ҫɾ��Ȩ�޵Ķ���:" + updateObject);
		logger.info("���Ȩ�޸���:" + groupPowerList.size());

		Object[] objects = new Object[] { updateObject };

		// ��Ȩ����
		PowerUtil.updatePower(updateObject, userPowerList, groupPowerList);
		// setSucess(true);
		try {
			logger.info("���¶����Ȩ��");
			getHibernateGenericController().update(updateObject);
			logger.info("Ȩ�޸������");
		} catch (Exception e) {
			logger.info(e);
		} finally {

			setSelectedObjects(new HashSet());
		}
	}

	/**
	 * ������б�
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
	 * ����û��б�
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
	 * ѡ�����
	 * 
	 * @return
	 */
	public abstract List getSelectedGroup();

	public abstract void setSelectedGroup(List group);

	/**
	 * ��ѡ���û�
	 * 
	 * @return
	 */
	public abstract List getSelectedUser();

	public abstract void setSelectedUser(List user);

	/**
	 * �����û�ѡ���Ⱥ��Ȩ��
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
	 * �����û���Ȩ��
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
	 * �õ��û���Ȩ��
	 * 
	 * @author dingjiangtao_a Jun 16, 2008 9:38:37 AM
	 * @version 1.0
	 * @return
	 */
	public boolean getUserRightCheckboxSelected() {
		return getUserSelectedRights().contains(getRight());
	}

	/**
	 * ���Ȩ��
	 * 
	 * @param cycle
	 */
	public void addRight(IRequestCycle cycle) {

		logger.info("��ʼ���Ⱥ��Ȩ�޵Ķ���:" + getObject());
		ValidationDelegate delegate = getDelegate();
		Set setSelectedRights = getSelectedRights();

		// Ȩ�޸����Ⱥ��
		List<Group> groupList = this.getSelectedGroup();

		int setSize = setSelectedRights.size();

		if (setSize == 0 && groupList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("��ѡ��Ҫ��Ȩ��Ⱥ���Ȩ��", null);
			return;
		}

		// ���û��ѡ��Ȩ��
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("��ѡ��Ȩ��", null);
			return;
		}
		if (groupList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("��ѡ��Ҫ��Ȩ��Ⱥ��", null);
			return;
		}

		logger.info("Ȩ�޸�����" + setSize);

		List<GroupPower> groupPowerList = new ArrayList();
		List<UserPower> userPowerList = new ArrayList();

		// �����ظ���Ȩ����
		List<Group> notGroupList = new ArrayList();

		Iterator selectedIterator = setSelectedRights.iterator();

		// ��ö����Ѿ������˵�Ȩ��
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
			delegate.record("��[" + groupName + "]���ܶ�θ�Ȩ", null);
		}

		groupList.removeAll(notGroupList);

		// ���Ѿ���Ȩ
		// if (groupExist)
		// {
		// delegate.setFormComponent(null);
		// delegate.record("һ���鲻�ܶ�θ�Ȩ", null);
		// return;
		// }

		logger.info("��������:" + groupPowerList.size());
		logger.info("�û��������:" + userPowerList.size());

		// for (int i = 0; i < userPowerList.size(); i++)
		// {
		// logger.info("����:" + userPowerList.get(i).getUser().getSysName());
		//
		// }

		// ����Ⱥ��Ȩ�޵�SET
		Set groupSet = new HashSet();

		// ѡ���Ⱥ��ĸ���
		int groupSize = groupList.size();

		// Ⱥ��Ȩ���б�Ĺ���
		for (int i = 0; i < groupSize; i++) {
			// ���ѡ���Ȩ�޵ĵ�����
			Iterator iterator = setSelectedRights.iterator();
			logger.info("Ȩ�޵ĵ������Ĵ�С:" + iterator.hasNext());
			GroupPower groupPower = new GroupPower();
			groupPower.setGroup(groupList.get(i));
			// �����û�ѡ���Ȩ��
			while (iterator.hasNext()) {
				RightField rightField = (RightField) iterator.next();
				groupPower.addPower(rightField.getId());

			}
			groupPowerList.add(groupPower);
		}

		// clear selection
		setSelectedRights(new HashSet());

		// logger.info("��:" + groupPowerList.get(0).getGroup().getName());
		// logger.info("���ӵ�Ȩ�޸���:" + groupPowerList.get(0).getPowers().size());

		Object updateObject = getObject();

		Object[] objects = new Object[] { updateObject };

		logger.info("����:" + updateObject);
		logger.info("�û�Ȩ�޶���ĸ���:" + userPowerList.size());
		logger.info("��Ȩ�޶���ĸ���:" + groupPowerList.size());

		// ��Ȩ����
		PowerUtil.updatePower(updateObject, userPowerList, groupPowerList);
		logger.info("�ı��Ķ���:" + updateObject);
		// setSucess(true);
		try {
			logger.info("���¶����Ȩ��");
			getHibernateGenericController().update(updateObject);
			logger.info("Ȩ�޸������");
		} catch (Exception e) {
			logger.info(e);
		}
	}

	/**
	 * ����û���Ȩ��
	 * 
	 * @param cycle
	 */
	public void addUserRight(IRequestCycle cycle) {
		Set setSelectedRights = getUserSelectedRights();
		ValidationDelegate delegate = getDelegate();

		// Ȩ�޸�����û�
		List<User> userList = this.getSelectedUser();

		int setSize = setSelectedRights.size();

		if (setSize == 0 && userList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("��ѡ��Ҫ��Ȩ���û���Ȩ��", null);
			return;
		}

		// ���û��ѡ��Ȩ��
		if (setSize == 0) {
			delegate.setFormComponent(null);
			delegate.record("��ѡ��Ȩ��", null);
			return;
		}
		if (userList.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("��ѡ��Ҫ��Ȩ���û�", null);
			return;
		}

		// Ȩ���Ѿ����ڵ���
		List<GroupPower> groupPowerList = new ArrayList();

		// Ȩ���Ѿ����ڵ��û�
		List<UserPower> userPowerList = new ArrayList();

		// �����ظ���Ȩ���û�
		List<User> notUserList = new ArrayList();

		// ��ö����Ѿ������˵�Ȩ��
		PowerUtil.getPowerUsers(getObject(), userPowerList, groupPowerList,
				getHibernateGenericController());

		logger.info("Ȩ�޸�����" + setSize);

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
			delegate.record("�û�[" + userName + "]���ܶ�θ�Ȩ", null);
		}

		userList.removeAll(notUserList);

		// �û��Ѿ���Ȩ
		// if (userExist)
		// {
		// delegate.setFormComponent(null);
		// delegate.record("һ���û����ܶ�θ�Ȩ", null);
		// return;
		// }

		// �����û�Ȩ�޵�SET
		Set userSet = new HashSet();

		// ѡ����û��ĸ���
		int userSize = userList.size();

		// �û�Ȩ���б�Ĺ���
		for (int i = 0; i < userSize; i++) {
			// ���ѡ���Ȩ�޵ĵ�����
			Iterator iterator = setSelectedRights.iterator();

			UserPower userPower = new UserPower();
			userPower.setUser(userList.get(i));
			// �����û�ѡ���Ȩ��
			while (iterator.hasNext()) {
				logger.info("�û���Ȩ�޵ĵ������Ĵ�С:");
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
				logger.info("Ȩ��:" + it.next());
			}
		}

		// ��Ȩ����
		PowerUtil.updatePower(getObject(), userPowerList, groupPowerList);

		try {
			logger.info("���¶����Ȩ��");
			getHibernateGenericController().update(getObject());
			logger.info("Ȩ�޸������");
		} catch (Exception e) {
			logger.info(e);
		}

	}

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {

		/**
		 * parameters�ĵ�һ������Ϊ��Ȩ�����id,�ڶ�������Ϊ����ҵ����󣬵���������Ϊ���¶���ķ��� ���ĸ�����Ϊ��id��ö���ķ���
		 */

		setObject(parameters[0]);

		// Ȩ���Ѿ����ڵ���
		List<GroupPower> groupPowerList = new ArrayList();

		// Ȩ���Ѿ����ڵ��û�
		List<UserPower> userPowerList = new ArrayList();

		// Ȩ�޸�����û�
		List<User> userList = this.getSelectedUser();

		// ��ö����Ѿ������˵�Ȩ��
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
