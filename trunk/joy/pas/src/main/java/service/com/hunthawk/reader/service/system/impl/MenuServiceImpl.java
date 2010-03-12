/**
 * 
 */
package com.hunthawk.reader.service.system.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.framework.security.User;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.system.Menu;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.MenuService;

/**
 * @author BruceSun
 * 
 */
public class MenuServiceImpl implements MenuService {

	private HibernateGenericController controller;

	private static final String ROLE_SP = "SP";
	private static final String ROLE_CHANNEL = "CHANNEL";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.service.system.MenuService#getMenu(com.hunthawk.framework.security.User)
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> getMenu(User user) {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new NullExpression("parent");
		hibernateExpressions.add(ex);

		List<Menu> ms = controller.findBy(Menu.class, 1, Integer.MAX_VALUE,
				"id", true, hibernateExpressions);
		if (user.isAdmin())
			return ms;
		Iterator<Menu> iter = ms.iterator();
		while (iter.hasNext()) {
			Menu menu = iter.next();
			if (hasPower(menu, user)) {
				Iterator<Menu> childsIter = menu.getChilds().iterator();
				while (childsIter.hasNext()) {
					Menu child = childsIter.next();
					if (!hasPower(child, user)) {
						childsIter.remove();
					}
				}
			} else {
				iter.remove();
			}
		}
		return ms;
	}

	private boolean hasPower(Menu menu, User user) {
		if (ParameterCheck.isNullOrEmpty(menu.getPowers())) {
			return true;
		}
		UserImpl userImpl = (UserImpl)user;
		if(userImpl.isRoleChannel()){
			if(menu.getPowers().indexOf(ROLE_CHANNEL) < 0)
				return false;
		}
		if(userImpl.isRoleProvider()){
			if(menu.getPowers().indexOf(ROLE_SP) < 0)
				return false;
		}
		String[] powers = menu.getPowers().split(",");
		for (String power : powers) {
			if (user.hasRole(power)) {
				return true;
			}
		}
		return false;
	}

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void add(Menu menu) {

		controller.save(menu);
	}

	public void update(Menu menu) {
		controller.update(menu);
	}

	public List<Menu> getAllMenu() {
		return controller.getAll(Menu.class);
	}

	public void delete(Menu menu) {
		controller.delete(menu);
	}
	
	public Menu getMenu(int menuId){
		return controller.get(Menu.class, menuId);
	}
}
