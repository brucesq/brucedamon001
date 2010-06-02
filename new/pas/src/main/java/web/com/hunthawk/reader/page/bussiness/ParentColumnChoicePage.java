/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author sunquanzhi
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ParentColumnChoicePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract PageGroup getPageGroup();

	public abstract void setPageGroup(PageGroup pg);

	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InitialValue("-1")
	public abstract int getSearchId();

	public abstract void setSearchId(int id);

	public abstract Columns getColumn();

	public abstract void setColumn(Columns col);

	/**
	 * 获得模板类型下拉列表的数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getPagegroupList() {

		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getBussinessService().findPageGroups(1, Integer.MAX_VALUE,
						"id", false, new ArrayList<HibernateExpression>()),
				PageGroup.class, "getPkName", "getId", true, "");
		return parentProper;

	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}

		HibernateExpression typeF = new CompareExpression("pagegroup",
				getPageGroup(), CompareType.Equal);
		hibernateExpressions.add(typeF);

		// HibernateExpression statusE = new CompareExpression("status",
		// Constants.STATUS_PUBLISH, CompareType.Equal);
		// hibernateExpressions.add(statusE);

		return hibernateExpressions;
	}

	/**
	 * 获得模板
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseTemplates() {
		return new IBasicTableModel() {

			// 模板的总数
			int count = 0;

			public int getRowCount() {
				if (count > 0) {

					return count;
				}
				//
				// count = getBussinessService().getColumnCount(
				// getSearchExpressions()).intValue();
				List<Columns> columns = getBussinessService().findColumns(1,
						100000, "id", false, getSearchExpressions());

				if (getColumn() != null) {
					List<HibernateExpression> ex = new ArrayList<HibernateExpression>();
					ex.add(new CompareExpression("parent", getColumn(),
							CompareType.Equal));
					List<Columns> childs = getBussinessService().findColumns(1,
							10000, "id", true, ex);
					columns.removeAll(childs);
					columns.remove(getColumn());
				}
				count = columns.size();
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				List<Columns> columns = getBussinessService().findColumns(
						pageNo, nPageSize, "id", false, getSearchExpressions());
				if (getColumn() != null) {
					List<HibernateExpression> ex = new ArrayList<HibernateExpression>();
					ex.add(new CompareExpression("parent", getColumn(),
							CompareType.Equal));
					List<Columns> childs = getBussinessService().findColumns(1,
							10000, "id", true, ex);
					columns.removeAll(childs);
					columns.remove(getColumn());
				}
				return columns.iterator();
			}

		};

	}

	/**
	 * 搜索模板
	 * 
	 * @param cycle
	 */
	public void searchTemplate(IRequestCycle cycle) {
		setChoose("false");
		setSearch(true);
		getTableView().reset();

	}

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getColumn() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个栏目", null);

		} else {
			setRadioValue(String.valueOf((getColumn().getId())));
		}

		logger.info("提交的值:" + getRadioValue());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板类型

		setReturnElement((String) parameters[0]);
		setPageGroup((PageGroup) parameters[1]);
		setColumn((Columns) parameters[2]);

	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	/**
	 * 获得资源的状态列表
	 * 
	 * @return
	 */
	// public IPropertySelectionModel getTemplateStatus() {
	//
	// ExStringPropertySelectionModel pamsPro = new
	// ExStringPropertySelectionModel(
	// Constants.STATUS, true, "");
	// return pamsPro;
	//
	// }
	/**
	 * 搜索时的状态条件
	 * 
	 * @return
	 */
	@InitialValue("-1")
	public abstract int getSearchStatus();

	public abstract void setSearchStatus(int status);

	// 是否为搜索请求
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	public void pageBeginRender(PageEvent arg0) {

	}

	public abstract Object getCurrentObject();

	public abstract void setCurrentObject(Object m);

}
