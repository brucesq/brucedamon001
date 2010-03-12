package com.hunthawk.reader.page.system;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.service.system.SystemService;

@Restrict(roles = { "system" }, mode = Restrict.Mode.ROLE)
public abstract class EditVariablesPage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return Variables.class;
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
				getSystemService().addVariables((Variables) object);
			} else {
				getSystemService().updateVariables((Variables) object);
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
			setModel(new Variables());
		}

	}

}
