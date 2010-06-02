/**
 * 
 */
package com.hunthawk.reader.page.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
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
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.statistics.DataReport;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.system.StatisticsService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "bi" }, mode = Restrict.Mode.ROLE)
public abstract class ShowPageGroupStatPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:statisticsService")
	public abstract StatisticsService getStatisticsService();

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	public abstract Date getBeginTime();

	public abstract void setBeginTime(Date date);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date date);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		hibernateExpressions.add(new CompareExpression("statType",12,CompareType.Equal));
		if (getBeginTime() != null) {
			HibernateExpression timeE = new CompareExpression("dataTime",
					ToolDateUtil.dateToString(getBeginTime(), "yyyy-MM-dd"),
					CompareType.Ge);
			hibernateExpressions.add(timeE);
		}
		if (getEndTime() != null) {
			HibernateExpression timeE = new CompareExpression("dataTime",
					ToolDateUtil.dateToString(getEndTime(), "yyyy-MM-dd"),
					CompareType.Le);
			hibernateExpressions.add(timeE);
		}
		return hibernateExpressions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getStatisticsService().getDataReportResultCount(
						getSearchExpressions()).intValue();
			}

			@SuppressWarnings("unchecked")
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getStatisticsService().findDataReportBy(pageNo,
						nPageSize, "id", false, getSearchExpressions())
						.iterator();
			}
		};
	}

	public ITableColumn getProduct() {
		return new SimpleTableColumn("product", "产品名称",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						DataReport obj = (DataReport) objRow;
						try {
							Product product = getBussinessService().getProduct(
									obj.getParaPd());
							return product.getName();
						} catch (Exception e) {
							return "产品不存在";
						}

					}

				}, false);
	}
	
	public ITableColumn getPagegroupname() {
		return new SimpleTableColumn("pagegroupname", "页面组名称",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						DataReport obj = (DataReport) objRow;
						try {
							PageGroup pg = getBussinessService().getPageGroup(Integer.parseInt(obj.getParaGd()));
							return pg.getPkName();
						} catch (Exception e) {
							return "页面组不存在";
						}

					}

				}, false);
	}

	@Override
	protected void delete(Object object) {
	}

	@Override
	public List<SearchCondition> getSearchConditions() {
		return new ArrayList();
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

}

