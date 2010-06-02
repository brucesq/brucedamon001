/**
 * 
 */
package com.hunthawk.reader.page.system;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.system.Menu;
import com.hunthawk.reader.service.system.MenuService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "system" }, mode = Restrict.Mode.ROLE)
public abstract class EditMenuPage extends EditPage implements PageBeginRenderListener {

	@InjectObject("spring:menuService")
	public abstract MenuService getMenuService();

	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass()
	{
		return Menu.class;
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			Menu menu = (Menu)object;
			if(isModelNew())
			{
				
				getMenuService().add(menu);
			}else{
				getMenuService().update(menu);
			}
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}


	public void pageBeginRender(PageEvent event)
	{
		if(getModel() == null)
		{
			setModel(new Menu());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getMenuList(){
		
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getMenuService().getMenu(getUser()), Menu.class, "getTitle", "getId", true, "¸ù²Ëµ¥");
		return parentProper;
	}
	
//	public IPropertySelectionModel getRoles()
//	{
//		List<Role> roles = getMenuService().findBy(Role.class, 1, 1000, "id", false, new ArrayList<HibernateExpression>());
//		ObjectPropertySelectionModel model =  new  ObjectPropertySelectionModel(roles, Role.class, "getName", "getId", false, null);
//		return model; 
//	}
	
//	@InjectComponent("roleList")
//	public abstract Block getRoleList();
//
//	@InjectComponent("roleExist")
//	public abstract Block getRoleExist();
	
//	public void setSelectedRoles(List roles)
//	{
//		((UserImpl)getModel()).setRoles(new HashSet<Role>(roles));
//	}
//	public List getSelectedRoles()
//	{
//		return new ArrayList(((UserImpl)getModel()).getRoles());
//	}
}
