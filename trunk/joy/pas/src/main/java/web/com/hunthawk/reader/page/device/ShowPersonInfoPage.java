/**
 * 
 */
package com.hunthawk.reader.page.device;

import java.util.ArrayList;
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
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.service.guest.GuestService;

/**
 * @author BruceSun
 * 
 */
@Restrict(mode = Restrict.Mode.ROLE, roles = { "person" })
public abstract class ShowPersonInfoPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("device/EditPersonInfoPage")
	public abstract EditPersonInfoPage getEditPage();

	@InjectObject("spring:guestService")
	public abstract GuestService getGuestService();

	public abstract Integer getPersonGroupId();

	public abstract void setPersonGroupId(Integer group);

	public IPage onEdit(Object obj) {
		getEditPage().setModel(obj);
		getEditPage().setPersonGroupId(((PersonInfo) obj).getGroupId());
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
	 * 获得当前Privilege
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
			PersonInfo person = (PersonInfo) object;
			getGuestService().deletePersonInfo(person);
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
		return getGuestService().getPersonInfos(getPersonGroupId());
	}

	public IPage onAddPersonInfo(Integer groupId) {
		getEditPage().setPersonGroupId(groupId);
		return getEditPage();
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("personGroupId");
		nameC.setValue(getPersonGroupId());
		searchConditions.add(nameC);
		return searchConditions;
	}
}
