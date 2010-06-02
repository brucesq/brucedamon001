/**
 * 
 */
package com.hunthawk.reader.page.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.domain.system.Role;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "user" }, mode = Restrict.Mode.ROLE)
public abstract class EditUserPage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return UserImpl.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			UserImpl user = (UserImpl) object;
			if (isModelNew()) {
				user.setFlag(1);
				getUserService().addUser(user);
			} else {
				getUserService().updateUser(user);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new UserImpl());
		}

	}

	public IPropertySelectionModel getRoles() {
		List<Role> roles = getUserService().findBy(Role.class, 1, Integer.MAX_VALUE, "id",
				false, new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				roles, Role.class, "getCnName", "getId", false, null);
		return model;
	}

	public IPropertySelectionModel getGroups() {
		List<Group> groups = getUserService().findBy(Group.class, 1, Integer.MAX_VALUE,
				"id", false, new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				groups, Group.class, "getName", "getId", false, null);
		return model;
	}

	public IPropertySelectionModel getProviders() {
		List<Provider> providers = getPartnerService().findProvider(1,
				Integer.MAX_VALUE, "id", false,
				new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				providers, Provider.class, "getIntro", "getId", true, "");
		return model;
	}

	public IPropertySelectionModel getTypes() {
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		;
		types.put("内容合作方", 2);
		types.put("渠道合作方", 3);
		types.put("运营人员", 1);
		return new MapPropertySelectModel(types, false, "");
	}

	public IPropertySelectionModel getChannels() {
		List<Channel> channels = getPartnerService().findChannel(1,
				Integer.MAX_VALUE, "id", false,
				new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				channels, Channel.class, "getIntro", "getId", true, "");
		return model;
	}

	@InjectComponent("roleList")
	public abstract Block getRoleList();

	@InjectComponent("roleExist")
	public abstract Block getRoleExist();

	public void setSelectedRoles(List roles) {
		((UserImpl) getModel()).setRoles(new HashSet<Role>(roles));
	}

	public List getSelectedRoles() {
		List roles = new ArrayList(((UserImpl) getModel()).getRoles());
		return roles;
	}

	@InjectComponent("groupList")
	public abstract Block getGroupList();

	@InjectComponent("groupExist")
	public abstract Block getGroupExist();

	public void setSelectedGroups(List groups) {
		((UserImpl) getModel()).setGroups(new HashSet<Group>(groups));
	}

	public List getSelectedGroups() {
		List groups = new ArrayList(((UserImpl) getModel()).getGroups());
		return groups;
	}
}
