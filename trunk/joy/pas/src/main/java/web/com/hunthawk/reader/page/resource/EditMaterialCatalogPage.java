/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.service.resource.MaterialService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "material" }, mode = Restrict.Mode.ROLE)
public abstract class EditMaterialCatalogPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return MaterialCatalog.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			MaterialCatalog catalog = (MaterialCatalog) object;
			if (isModelNew()) {
				catalog.setCreateTime(new Date());
				catalog.setCreator(getUser().getId());
				catalog.setModifyTime(new Date());
				catalog.setModifier(getUser().getId());
				getMaterialService().addMaterialCatalog(catalog);
			} else {
				catalog.setModifyTime(new Date());
				catalog.setModifier(getUser().getId());
				getMaterialService().updateMaterialCatalog(catalog);
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
			setModel(new MaterialCatalog());
		}

	}

	public IPropertySelectionModel getMaterialTypeList() {
		return new MapPropertySelectModel(MaterialCatalog.getMaterialType());
	}

	
}