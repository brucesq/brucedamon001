/**
 * 
 */
package com.hunthawk.reader.page;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.InjectObject;

import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.system.Menu;
import com.hunthawk.reader.service.system.MenuService;

/**
 * @author BruceSun
 * 
 */
public abstract class RedirectPage extends SecurityPage implements
		IExternalPage {

	@InjectObject("spring:menuService")
	public abstract MenuService getMenuService();

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		int menuID = (Integer) parameters[0];
		Menu menu = getMenuService().getMenu(menuID);
		String guid = "hunthawk";//UserContainer.getGUID(getUser());

		throw new RedirectException(menu.getParameter() + "&guid=" + guid);
	}
}
