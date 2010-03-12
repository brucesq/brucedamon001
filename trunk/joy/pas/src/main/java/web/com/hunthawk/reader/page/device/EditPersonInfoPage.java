/**
 * 
 */
package com.hunthawk.reader.page.device;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.service.guest.GuestService;

/**
 * @author BruceSun
 * 
 */
@Restrict(mode = Restrict.Mode.ROLE, roles = { "personchange" })
public abstract class EditPersonInfoPage extends EditPage implements
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
		return PersonInfo.class;
	}

	public abstract Integer getPersonGroupId();

	public abstract void setPersonGroupId(Integer group);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			PersonInfo info = (PersonInfo) object;
			info.setGroupId(getPersonGroupId());
			info.setCreateTime(new Date());
			info.setCreator(getUser().getId());
			getGuestService().addOrUpdatePersonInfo(info);

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new PersonInfo());
		}

	}

}
