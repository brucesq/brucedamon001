/**
 * 
 */
package com.hunthawk.reader.page.partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "fee" }, mode = Restrict.Mode.ROLE)
public abstract class ShowFeePage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("partner/EditFeePage")
	public abstract EditFeePage getEditFeePage();

	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	public abstract Provider getProvider();

	public abstract void setProvider(Provider provider);

	public abstract String getFeeId();

	public abstract void setFeeId(String feeId);

	public abstract String getFeeName();

	public abstract void setFeeName(String feeName);

	public abstract String getUrlName();

	public abstract void setUrlName(String url);

	public abstract Integer getFeeType();

	public abstract void setFeeType(Integer feeType);

	public abstract Integer getFeeStatus();

	public abstract void setFeeStatus(Integer feeStatus);

	public IPage onEdit(Fee fee) {
		getEditFeePage().setModel(fee);
		return getEditFeePage();
	}

	public IPage onEdit(Fee fee, Provider provider) {
		getEditFeePage().setModel(fee);
		getEditFeePage().setProvider(provider);
		return getEditFeePage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		Fee co = (Fee) getCurrentObject();
		Set selectedObjects = getSelectedObjects();
		// 选择了用户
		if (bSelected) {
			selectedObjects.add(co);
		} else {
			selectedObjects.remove(co);
		}
		// persist value
		setSelectedObjects(selectedObjects);

	}

	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedObjects();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedObjects(Set set);

	/**
	 * 获得当前用户
	 * 
	 * @return
	 */
	public abstract Object getCurrentObject();

	@Override
	protected void delete(Object object) {
		try {

			getFeeService().deleteFee((Fee) object);
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

	public void search() {
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("provider");
		nameC.setValue(getProvider());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("urlName");
		nameC.setValue(getUrlName());
		searchConditions.add(nameC);

		return searchConditions;
	}

	public boolean isShowSearch() {
		if (getProvider() != null)
			return false;
		else
			return true;
	}

	public IPropertySelectionModel getFeeTypeList() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("", -1);
		map.put("免费", 1);
		map.put("包月", 2);
		map.put("计次", 3);
		return new MapPropertySelectModel(map);
	}

	public IPropertySelectionModel getFeeStatusList() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("", -1);
		map.put("商用", 1);
		map.put("暂停", 0);
		return new MapPropertySelectModel(map);
	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("provider",
				getProvider(), CompareType.Equal);
		hibernateExpressions.add(ex);

		if (!ParameterCheck.isNullOrEmpty(getFeeId())) {
			HibernateExpression name = new CompareExpression("id", getFeeId(),
					CompareType.Equal);
			hibernateExpressions.add(name);
		}

		if (!ParameterCheck.isNullOrEmpty(getFeeName())) {
			HibernateExpression name = new CompareExpression("name", "%"
					+ getFeeName() + "%", CompareType.Like);
			hibernateExpressions.add(name);
		}

		if (getFeeType() != null && getFeeType() >= 0) {
			HibernateExpression name = new CompareExpression("type",
					getFeeType(), CompareType.Equal);
			hibernateExpressions.add(name);
		}
		if (getFeeStatus() != null && getFeeStatus() >= 0) {
			HibernateExpression name = new CompareExpression("status",
					getFeeStatus(), CompareType.Equal);
			hibernateExpressions.add(name);
		}
		String url = getUrl();
		if (StringUtils.isNotEmpty(url)) {
			HibernateExpression name = new CompareExpression("url", url,
					CompareType.Equal);
			hibernateExpressions.add(name);
		}
		return hibernateExpressions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return new Long(getFeeService().findFeeResultCount(
						getSearchExpressions())).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getFeeService().findFee(pageNo, nPageSize, "id", true,
						getSearchExpressions()).iterator();
			}
		};
	}

	public IPage onAddProvider(Provider provider) {
		EditFeePage page = getEditFeePage();
		page.setProvider(provider);
		return page;
	}

	public IPage onAddProvider() { // 没有合作方时的添加计费
		return getEditFeePage();
	}

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedObjects();
		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "您至少得选择一个";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					getFeeService().auditFee((Fee) obj, getStatusValue());
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

	public String getFeeURL() {
		String url = getSystemService().getVariables("fee_url").getValue();

		return "copy('" + url + ((Fee) getCurrentObject()).getActionUrl()
				+ "');";
	}

	public String getActionURL() {
		String url = getSystemService().getVariables("fee_url").getValue();

		return "copy('" + url + ((Fee) getCurrentObject()).getUrl() + "');";
	}

	public String getComeInURL() {

		String url = getSystemService().getVariables("fee_url").getValue();
		url += ((Fee) getCurrentObject()).getUrl() + ".index";
		return "copy('" + url + "');";
	}

	public ITableColumn getProviderName() {
		return new SimpleTableColumn("providerName", "SP/CP名称",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Fee fee = (Fee) objRow;
						if (fee.getProvider() != null)
							return fee.getProvider().getIntro();
						else
							return "";
					}

				}, false);

	}

	public String getUrl() {
		String url = null;
		String urlName = getUrlName();
		if (StringUtils.isNotEmpty(urlName)) {
			String[] urlTemps = urlName.split("/");
			if (urlTemps != null && urlTemps.length > 1) {
				url = urlTemps[urlTemps.length - 1];
			}

		}
		return url;
	}
}
