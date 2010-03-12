package com.hunthawk.reader.page.adapter;

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
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.adapter.AdapterService;
import com.hunthawk.reader.service.system.UserService;

/**
 * 适配器列表
 * 
 * @author penglei
 * 
 */
public abstract class ShowAdapterPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("adapter/EditAdapterPage")
	public abstract EditAdapterPage getEditPage();

	@InjectObject("spring:adapterService")
	public abstract AdapterService getAdapterService();

	@InjectPage("adapter/ShowAdapterPage")
	public abstract ShowAdapterPage getShowAdapterPage();

	@InjectPage("adapter/ShowAdapterRulePage")
	public abstract ShowAdapterRulePage getShowAdapterRulePage();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getAdapterTypeId();

	public abstract void setAdapterTypeId(Integer AdapterId);

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
	 * @see
	 * com.aspire.pams.framework.tapestry.SearchPage#delete(java.lang.Object)
	 */
	@Override
	protected void delete(Object object) {
		try {
			Adapter adapter = (Adapter) object;
			getAdapterService().deleteAdapter(adapter);
			setAdapterTypeId(adapter.getAdapterTypeId());
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
			HibernateExpression nameE = new CompareExpression("name", getName()
					+ "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getAdapterTypeId() != null) {
			HibernateExpression nameE = new CompareExpression("adapterTypeId",
					getAdapterTypeId(), CompareType.Equal);
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

		nameC.setName("adapterTypeId");
		nameC.setValue(getAdapterTypeId());
		searchConditions.add(nameC);

		return searchConditions;
	}

	@Override
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getAdapterService().findAdapterCount(
						getSearchExpressions()).intValue();

			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getAdapterService().findAdapterList(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}
		};
	}

	public ITableColumn getStatus() {
		return new SimpleTableColumn("status", "状态",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Adapter adapter = (Adapter) objRow;
						return adapter.getStatus() == 0 ? "待审" : "上线";
					}

				}, false);
	}

	public ITableColumn getAdapterType() {
		return new SimpleTableColumn("adapterType", "适配类型",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Adapter adapter = (Adapter) objRow;
						AdapterType adapterType = getAdapterService()
								.getAdapterTypeById(adapter.getAdapterTypeId());
						return adapterType.getName();
					}

				}, false);
	}

	public IPage showAdapterRuleList(Adapter adapter) {
		getShowAdapterRulePage().setAdapterId(adapter.getId());
		getShowAdapterRulePage().setAdapterTypeId(adapter.getAdapterTypeId());
		return getShowAdapterRulePage();

	}

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedObjects();

		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "您至少得选择一条数据";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					getAdapterService().changeStatusAdapter((Adapter) obj,
							getStatusValue());
				} catch (Exception e) {
					err += e.getMessage();
				}
			}

		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
		}

		// clear selection
		setSelectedObjects(new HashSet());
		getCallbackStack().popPreviousCallback();
	}

	public ITableColumn getModifierId() {
		return new SimpleTableColumn("modifierId", "修改人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Adapter adapter = (Adapter) objRow;
						if (adapter.getModifierId() != null) {

							UserImpl user = (UserImpl) getUserService()
									.getObject(UserImpl.class,
											adapter.getModifierId());
							if (user == null)
								return "";

							return user.getName();
						} else {
							return "";
						}
					}

				}, false);

	}

	public ITableColumn getCreatorId() {
		return new SimpleTableColumn("creatorId", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Adapter adapter = (Adapter) objRow;
						if (adapter.getCreatorId() != null) {

							UserImpl user = (UserImpl) getUserService()
									.getObject(UserImpl.class,
											adapter.getCreatorId());
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
