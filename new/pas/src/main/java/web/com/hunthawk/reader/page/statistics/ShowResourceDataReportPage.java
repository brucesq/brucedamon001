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

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
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
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.stat.ResourceStatService;

/**
 * @author sunquanzhi
 *
 */
@Restrict(roles = { "bi" }, mode = Restrict.Mode.ROLE)
public abstract class ShowResourceDataReportPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:resourceStatService")
	public abstract ResourceStatService getResourceStatService();
	
	public abstract Date getBeginTime();

	public abstract void setBeginTime(Date date);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date date);
	
	public abstract String getResName();
	
	public abstract void setResName(String name);
	
	public abstract void setResourceId(String resourceID);
	
	public abstract String getResourceId();
	
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(StringUtils.isNotEmpty(getResourceId())){
			hibernateExpressions.add(new CompareExpression("resourceId",
					getResourceId(), CompareType.Equal));
		}
		if(StringUtils.isNotEmpty(getResName())){
			hibernateExpressions.add(new CompareExpression("resourceName",
					"%"+getResName()+"%", CompareType.Like));
		}
		
		if (getBeginTime() != null) {
			HibernateExpression timeE = new CompareExpression("requestTime",
					ToolDateUtil.dateToString(getBeginTime(), "yyyy-MM-dd"),
					CompareType.Ge);
			hibernateExpressions.add(timeE);
		}
		if (getEndTime() != null) {
			HibernateExpression timeE = new CompareExpression("requestTime",
					ToolDateUtil.dateToString(getEndTime(), "yyyy-MM-dd"),
					CompareType.Le);
			hibernateExpressions.add(timeE);
		}
		return hibernateExpressions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResourceStatService().getResourceReportResultCount(
						getSearchExpressions()).intValue();
			}

			@SuppressWarnings("unchecked")
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getResourceStatService().findResourceReportBy(pageNo,
						nPageSize, "requestTime,pageViews", false,
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


}
