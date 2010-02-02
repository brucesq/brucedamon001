/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.service.bussiness.TemplateService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "templatecatalog" }, mode = Restrict.Mode.ROLE)
public abstract class EditTemplateCatalogPage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return TemplateCatalog.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			TemplateCatalog type = (TemplateCatalog) object;
			if (isModelNew()) {
				type.setCreateTime(new Date());
				type.setCreatorId(getUser().getId());
				getTemplateService().addTemplateCatalog(type);
			} else {
				getTemplateService().updateTemplateCatalog(type);
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
			setModel(new TemplateCatalog());
		}

	}

}
