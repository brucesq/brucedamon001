/**
 * 
 */
package com.hunthawk.reader.page.system;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.system.Group;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "usergroup" }, mode = Restrict.Mode.ROLE)
public abstract class EditGroupPage extends EditPage  implements PageBeginRenderListener{

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Group.class;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			if(isModelNew())
			{
				getUserService().addGroup((Group)object);
			}else{
				getUserService().updateGroup((Group)object);
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
			setModel(new Group());
		}
		
	}
	
	
	
	
}
