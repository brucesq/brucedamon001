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
import com.hunthawk.framework.hibernate.LogicalExpression;
import com.hunthawk.framework.hibernate.LogicalType;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.domain.statistics.URLConfigGroup;
import com.hunthawk.reader.domain.statistics.URLDataReport;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.system.StatisticsService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "bi" }, mode = Restrict.Mode.ROLE)
public abstract class ShowURLStatPage extends SearchPage {

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

	public abstract void setURLConfigId(Integer id);

	public abstract Integer getURLConfigId();

	public abstract void setStatType(Integer id);

	public abstract Integer getStatType();

	public abstract void setDetailConfig(boolean id);

	public abstract boolean getDetailConfig();

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		hibernateExpressions.add(new CompareExpression("statType",
				getStatType(), CompareType.Equal));
		if (getDetailConfig()) {
			URLConfigGroup group = getStatisticsService().getURLConfigGroup(
					getURLConfigId());
			LogicalExpression logicalEx = null;
			CompareExpression firstExpression = null;
			for (URLConfig config : group.getConfigs()) {
				CompareExpression expression = new CompareExpression(
						"configId", config.getId(), CompareType.Equal);
				if (firstExpression == null) {
					firstExpression = expression;
				} else {
					if (logicalEx == null) {
						logicalEx = new LogicalExpression(firstExpression,
								expression, LogicalType.Or);
					}else{
						logicalEx = new LogicalExpression(logicalEx,
								expression, LogicalType.Or);
					}
				}
			}
			
			if( logicalEx != null){
				hibernateExpressions.add(logicalEx);
			}else{
				if(firstExpression != null){
					hibernateExpressions.add(firstExpression);
				}
			}

		} else {
			hibernateExpressions.add(new CompareExpression("configId",
					getURLConfigId(), CompareType.Equal));
		}

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
				return getStatisticsService().getURLDataReportResultCount(
						getSearchExpressions()).intValue();
			}

			@SuppressWarnings("unchecked")
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getStatisticsService().findURLDataReportBy(pageNo,
						nPageSize, "dataTime,pageViews", false,
						getSearchExpressions()).iterator();
			}
		};
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

	public ITableColumn getTitle() {
		return new SimpleTableColumn("title", "标题",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						URLDataReport o1 = (URLDataReport) objRow;
						if (getStatType() == 1 || getStatType() == 11
								|| getDetailConfig()) {
							URLConfig config = getStatisticsService()
									.getURLConfig(o1.getConfigId());
							if (config != null)
								return config.getTitle();
						} else {
							URLConfigGroup group = getStatisticsService()
									.getURLConfigGroup(o1.getConfigId());
							if (group != null)
								return group.getTitle();
						}
						return "URL统计项不存在";
					}

				}, false);

	}
}
