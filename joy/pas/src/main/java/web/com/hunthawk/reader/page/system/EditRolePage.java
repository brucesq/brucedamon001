/**
 * 
 */
package com.hunthawk.reader.page.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.system.Privilege;
import com.hunthawk.reader.domain.system.Role;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "system" }, mode = Restrict.Mode.ROLE)
public abstract class EditRolePage extends EditPage  implements PageBeginRenderListener{

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Role.class;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			if(isModelNew())
			{
				getUserService().addRole((Role)object);
			}else{
				getUserService().updateRole((Role)object);
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
			setModel(new Role());
		}
		
	}
	
	
	public IPropertySelectionModel getPrivileges()
	{
		List<Privilege> privileges = getUserService().findBy(Privilege.class, 1, Integer.MAX_VALUE, "id", false, new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model =  new  ObjectPropertySelectionModel(privileges, Privilege.class, "getTitle", "getId", false, null);
		return model;
	}
	
	@InjectComponent("roleList")
	public abstract Block getRoleList();

	@InjectComponent("roleExist")
	public abstract Block getRoleExist();
	
	public void setSelectedPrivileges(List privileges)
	{
		((Role)getModel()).setPrivileges(new HashSet<Privilege>(privileges));
	}
	public List getSelectedPrivileges()
	{
		return new ArrayList(((Role)getModel()).getPrivileges());
	}
}
