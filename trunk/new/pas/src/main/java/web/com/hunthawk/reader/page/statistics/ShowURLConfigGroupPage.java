/**
 * 
 */
package com.hunthawk.reader.page.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.domain.statistics.URLConfigGroup;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.system.StatisticsService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "bi" }, mode = Restrict.Mode.ROLE)
public abstract class ShowURLConfigGroupPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:statisticsService")
	public abstract StatisticsService getStatisticsService();

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	@InjectPage("statistics/EditURLConfigGroupPage")
	public abstract EditURLConfigGroupPage getEditPage();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getObjectid();

	public abstract void setObjecteid(Integer userid);

	public abstract Date getBeginTime();

	public abstract void setBeginTime(Date date);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date date);

	public IPage onEdit(Object obj) {
		getEditPage().setModel(obj);
		return getEditPage();
	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("title", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		HibernateExpression useridE = new CompareExpression("id",
				getObjectid(), CompareType.Equal);
		hibernateExpressions.add(useridE);

		return hibernateExpressions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getStatisticsService().getURLConfigGroupResultCount(
						getSearchExpressions()).intValue();
			}

			@SuppressWarnings("unchecked")
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getStatisticsService().findURLConfigGroupBy(pageNo,
						nPageSize, "id", false, getSearchExpressions())
						.iterator();
			}
		};
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

	@Override
	protected void delete(Object object) {
		try {
			getStatisticsService()
					.deleteURLConfigGroup((URLConfigGroup) object);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	@Override
	public List<SearchCondition> getSearchConditions() {
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		SearchCondition useridC = new SearchCondition();
		useridC.setName("objectid");
		useridC.setValue(getObjectid());
		searchConditions.add(useridC);

		return searchConditions;
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

	public IPage onStat(URLConfigGroup obj){
		getShowURLStatPage().setURLConfigId(obj.getId());
		getShowURLStatPage().setStatType(2);
		return getShowURLStatPage();
	}
	
	public IPage onStatMonth(URLConfigGroup obj){
		getShowURLStatPage().setURLConfigId(obj.getId());
		getShowURLStatPage().setStatType(12);
		return getShowURLStatPage();
	}
	
	public IPage onStatDetail(URLConfigGroup obj){
		getShowURLStatPage().setURLConfigId(obj.getId());
		getShowURLStatPage().setStatType(1);
		getShowURLStatPage().setDetailConfig(true);
		return getShowURLStatPage();
	}
	
	public IPage onStatMonthDetail(URLConfigGroup obj){
		getShowURLStatPage().setURLConfigId(obj.getId());
		getShowURLStatPage().setStatType(11);
		getShowURLStatPage().setDetailConfig(true);
		return getShowURLStatPage();
	}
	
	
	public IPage onStatHour(URLConfigGroup obj){
		getShowURLHourStatPage().setURLConfigId(obj.getId());
		getShowURLHourStatPage().setStatType(2);
		return getShowURLHourStatPage();
	}

	@InjectPage("statistics/ShowURLStatPage")
	public abstract ShowURLStatPage getShowURLStatPage();
	
	@InjectPage("statistics/ShowURLHourStatPage")
	public abstract ShowURLHourStatPage getShowURLHourStatPage();

}
