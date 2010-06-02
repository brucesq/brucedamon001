package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.resource.ReCheck;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.UserService;

public abstract class ShowReCheckPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

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

	public abstract String getResourceId();

	public abstract void setResourceId(String resourceId);

	/**
	 * 获得当前Privilege
	 * 
	 * @return
	 */
	public abstract Object getCurrentObject();

	public void search() {

	}

	/**
	 * 删除（预留）
	 */
	@Override
	protected void delete(Object object) {
		try {

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
		if (!ParameterCheck.isNullOrEmpty(getResourceId())) {
			HibernateExpression nameE = new CompareExpression("resourceId",
					getResourceId() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		return hibernateExpressions;
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		// SearchCondition nameC = new SearchCondition();
		// nameC.setName("name");
		// nameC.setValue(getName());
		// searchConditions.add(nameC);

		return searchConditions;
	}

	@Override
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResourceService().findReCheckCount(
						getSearchExpressions()).intValue();

			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getResourceService().findReCheckAll(pageNo, nPageSize,
						"id", true, getSearchExpressions()).iterator();
			}
		};
	}

	public ITableColumn getCreatorId() {
		return new SimpleTableColumn("creatorId", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ReCheck reCheck = (ReCheck) objRow;
						if (reCheck.getCreatorId() != null) {

							UserImpl user = (UserImpl) getUserService()
									.getObject(UserImpl.class,
											reCheck.getCreatorId());
							if (user == null)
								return "";
							return user.getName();
						} else {
							return "";
						}
					}

				}, false);
	}
}
