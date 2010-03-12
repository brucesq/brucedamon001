package com.hunthawk.reader.page.device;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.device.UAGroup;
import com.hunthawk.reader.domain.device.UAInfo;
import com.hunthawk.reader.service.guest.GuestService;

/**
 * 
 * @author penglei
 * 
 */
@Restrict(mode = Restrict.Mode.ROLE, roles = { "uagroupchange" })
public abstract class EditUAGroupPage extends EditPage implements
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
		return UAGroup.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			getGuestService().addOrUpdateUaGroup((UAGroup) object);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new UAGroup());
		}

	}

	public IPropertySelectionModel getScreenTypeList() {
		return new MapPropertySelectModel(UAInfo.getScreenTypes());
	}

	public IPropertySelectionModel getWapTypeList() {
		return new MapPropertySelectModel(Constants.getVersionTypes());
	}
}
