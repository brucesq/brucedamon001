package com.hunthawk.reader.page.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.device.MobileInfo;
import com.hunthawk.reader.service.guest.GuestService;

/**
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "mobileinfo" }, mode = Restrict.Mode.ROLE)
public abstract class ShowMobileInfoPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("device/EditMobileInfoPage")
	public abstract EditMobileInfoPage getEditPage();

	@InjectObject("spring:guestService")
	public abstract GuestService getGuestService();

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

			getGuestService().deleteMobileInfo((MobileInfo) object);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.SearchPage#getSearchConditions()
	 */

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			String name = getName();
			if (name.length() > 8) {
				name = name.substring(0, 8);
			}
			HibernateExpression nameE = new CompareExpression("prefix", name,
					CompareType.Equal);
			hibernateExpressions.add(nameE);
		}

		return hibernateExpressions;
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		return searchConditions;
	}

	@Override
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getGuestService().getMobileInfoCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getGuestService().findMobileInfos(pageNo, nPageSize,
						"prefix", false, getSearchExpressions()).iterator();
			}
		};
	}

	public ITableColumn getAreaName() {
		return new SimpleTableColumn("areaname", "地区",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						MobileInfo p1 = (MobileInfo) objRow;
						return Constants.getAreaName(p1.getArea());

					}

				}, false);

	}
}
