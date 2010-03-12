package com.hunthawk.reader.page.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.device.UAGroup;
import com.hunthawk.reader.domain.device.UAGroupRelation;
import com.hunthawk.reader.domain.device.UAInfo;
import com.hunthawk.reader.service.guest.GuestService;

@Restrict(roles = { "edituagrouprelationpage" }, mode = Restrict.Mode.ROLE)
public abstract class EditUAGroupRelationPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("device/EditUAGroupPage")
	public abstract EditUAGroupPage getEditPage();

	@InjectObject("spring:guestService")
	public abstract GuestService getGuestService();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract void setGroupId(Integer id);

	public abstract Integer getGroupId();

	public IPage onEdit(Object obj) {
		getEditPage().setModel(obj);
		return getEditPage();
	}

	@InjectPage("device/ShowUAGroupRelationPage")
	public abstract ShowUAGroupRelationPage getShowUAGroupRelationPage();

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

	public IPage submitUA() {
		String err = "";
		if (getSelectedObjects().size() == 0) {
			err = "您至少要选择一本资源";
		}
		if (StringUtils.isNotEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			return this;
		}
		for (UAInfo obj : (Set<UAInfo>) getSelectedObjects()) {
			String ua = obj.getUa();
			UAGroupRelation uaGroupRelation = new UAGroupRelation();
			uaGroupRelation.setGroupId(getGroupId());
			uaGroupRelation.setUa(ua);
			try {
				getGuestService().addOrUpdateUaGroupRelation(uaGroupRelation);
			} catch (Exception e) {
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
				return this;
			}

		}
		UAGroup ua = new UAGroup();
		ua.setId(getGroupId());
		getShowUAGroupRelationPage().setUaGroup(ua);
		return getShowUAGroupRelationPage();

	}


	@Override
	public IBasicTableModel getTableModel() {

		return new IBasicTableModel() {
			public int getRowCount() {
				return getGuestService().getUAInfoNotInGroupCount(getGroupId(),
						getName()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getGuestService().findUAInfosNotInGroup(pageNo,
						nPageSize, getGroupId(), getName()).iterator();
			}
		};
	}

	public void delete(Object object) {

	}
	
	public IPage back(Integer groupId){
		UAGroup ua = new UAGroup();
		ua.setId(groupId);
		getShowUAGroupRelationPage().setUaGroup(ua);
		return getShowUAGroupRelationPage();
	}

}
