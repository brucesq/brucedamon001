/**
 * 
 */
package com.hunthawk.reader.page.system;

import java.util.List;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.components.TableView;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.system.Menu;
import com.hunthawk.reader.service.system.MenuService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "system" }, mode = Restrict.Mode.ROLE)
public abstract class ShowMenuPage extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("system/EditMenuPage")
	public abstract EditMenuPage getEditMenuPage();

	@InjectObject("spring:menuService")
	public abstract MenuService getMenuService();

	

	
	public IPage onEdit(Menu menu)
	{
		getEditMenuPage().setModel(menu);
		return getEditMenuPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		Menu user = (Menu)getCurrentMenu();
		Set selectedUser = getSelectedMenus();
		// 选择了用户
		if (bSelected)
		{
			selectedUser.add(user);
		}
		else
		{
			selectedUser.remove(user);
		}
		// persist value
		setSelectedMenus(selectedUser);
		
	}

	public boolean getCheckboxSelected()
	{
		return getSelectedMenus().contains(getCurrentMenu());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedMenus();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedMenus(Set set);

	/**
	 * 获得当前用户
	 * 
	 * @return
	 */
	public abstract Object getCurrentMenu();


	
	
	
	@Override
	protected  void delete(Object object)
	{
		try{
			
			getMenuService().delete((Menu)object);
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}


	
	

	

	public   List getModels()
	{
		return getMenuService().getAllMenu();
	}


}
