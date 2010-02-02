/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.components.TableView;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.service.resource.MaterialService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "material" }, mode = Restrict.Mode.ROLE)
public abstract class ShowMaterialCatalogPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditMaterialCatalogPage")
	public abstract EditMaterialCatalogPage getEditPage();

	@InjectPage("resource/ShowMaterialPage")
	public abstract ShowMaterialPage getShowMaterialPage();

	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();

	public abstract String getName();

	public abstract void setName(String name);

	public IPage onEdit(Object obj) {
		getEditPage().setModel(obj);
		return getEditPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		Object obj = getCurrentObject();
		Set selectedObjs = getSelectedObjects();
		// 选择了用户
		if (bSelected) {
			selectedObjs.add(obj);
		} else {
			selectedObjs.remove(obj);
		}
		// persist value
		setSelectedObjects(selectedObjs);

	}

	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedObjects();

	public abstract void setSelectedObjects(Set set);

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract Object getCurrentObject();

	public void search() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.SearchPage#delete(java.lang.Object)
	 */
	@Override
	protected void delete(Object object) {
		try {
			getMaterialService()
					.deleteMaterialCatalog((MaterialCatalog) object);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}

	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedObjects()) {
			delete(obj);
		}
		setSelectedObjects(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);
	}

	public List getModels() {
		return getMaterialService().getMaterialCatalogs();
	}

	public IPage showPersonInfo(Integer groupId) {
		getShowMaterialPage().setCatalogId(groupId);
		return getShowMaterialPage();
	}
}