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
import com.hunthawk.reader.domain.adapter.AdapterRule;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.service.adapter.AdapterService;

/**
 * 适配规则列表
 * 
 * @author penglei
 * 
 */
public abstract class ShowAdapterRulePage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("adapter/EditAdapterRulePage")
	public abstract EditAdapterRulePage getEditPage();

	@InjectObject("spring:adapterService")
	public abstract AdapterService getAdapterService();

	@InjectPage("adapter/ShowAdapterPage")
	public abstract ShowAdapterPage getShowAdapterPage();

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getAdapterTypeId();

	public abstract Integer getAdapterId();

	public abstract void setAdapterId(Integer adapterId);

	public abstract void setAdapterTypeId(Integer adapteTyperId);

	public IPage onEdit(Object obj) {
		getEditPage().setModel(obj);
		AdapterRule adapterRule = (AdapterRule) obj;
		getEditPage().setAdapterId(adapterRule.getAdapterId());
		getEditPage().setAdapterTypeId(adapterRule.getAdapterTypeId());
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

			getAdapterService().deleteAdapterRule((AdapterRule) object);
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
		if (getAdapterId() != null) {
			HibernateExpression nameE = new CompareExpression("adapterId",
					getAdapterId(), CompareType.Equal);
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

		nameC = new SearchCondition();
		nameC.setName("adapterTypeId");
		nameC.setValue(getAdapterTypeId());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("adapterId");
		nameC.setValue(getAdapterId());
		searchConditions.add(nameC);

		return searchConditions;
	}

	@Override
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getAdapterService().findAdapterRuleCount(
						getSearchExpressions()).intValue();

			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getAdapterService().findAdapterRuleList(pageNo,
						nPageSize, "id", false, getSearchExpressions())
						.iterator();
			}
		};
	}

	public ITableColumn getStatus() {
		return new SimpleTableColumn("status", "状态",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						AdapterRule adapter = (AdapterRule) objRow;
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
						AdapterRule adapter = (AdapterRule) objRow;
						AdapterType adapterType = getAdapterService()
								.getAdapterTypeById(adapter.getAdapterTypeId());
						return adapterType.getName();
					}

				}, false);
	}

	public IPage addAdapterRule(Integer adapterTypeId, Integer adapterId) {
		getEditPage().setAdapterTypeId(adapterTypeId);
		getEditPage().setAdapterId(adapterId);
		return getEditPage();
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
					getAdapterService().changeStatusAdapterRule(
							(AdapterRule) obj, getStatusValue());
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

}
