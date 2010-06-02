/**
 * 
 */
package com.hunthawk.reader.page.device;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.device.PersonGroup;
import com.hunthawk.reader.service.guest.GuestService;

/**
 * @author BruceSun
 * 
 */
@Restrict(mode = Restrict.Mode.ROLE, roles = { "personchange" })
public abstract class EditPersonGroupPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:guestService")
	public abstract GuestService getGuestService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return PersonGroup.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			if (isModelNew()) {
				getGuestService().addPersonGroup((PersonGroup) object);
			} else {
				getGuestService().updatePersonGroup((PersonGroup) object);
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
			setModel(new PersonGroup());
		}

	}

}