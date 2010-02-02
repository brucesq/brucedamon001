/**
 * 
 */
package com.hunthawk.reader.page.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.DetailPage;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "syslog" }, mode = Restrict.Mode.ROLE)
public abstract class ShowLogPage extends SecurityPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getAction();

	public abstract void setAction(String action);

	public abstract String getKey();

	public abstract void setKey(String key);

	public abstract String getDetail();

	public abstract void setDetail(String detail);

	public abstract Date getBeginTime();

	public abstract void setBeginTime(Date date);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date date);

	public abstract UserImpl getSearchUser();

	public abstract void setSearchUser(UserImpl user);

	public abstract Object getCurrentObject();

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

	public void search() {

	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (!ParameterCheck.isNullOrEmpty(getAction())) {
			HibernateExpression actionE = new CompareExpression("action",
					getAction(), CompareType.Equal);
			hibernateExpressions.add(actionE);
		}
		if (!ParameterCheck.isNullOrEmpty(getKey())) {
			HibernateExpression keyE = new CompareExpression("key", "%"
					+ getKey() + "%", CompareType.Like);
			hibernateExpressions.add(keyE);
		}
		if (!ParameterCheck.isNullOrEmpty(getDetail())) {
			HibernateExpression detailE = new CompareExpression("key", "%"
					+ getDetail() + "%", CompareType.Like);
			hibernateExpressions.add(detailE);
		}
		if (getBeginTime() != null) {
			HibernateExpression timeE = new CompareExpression("logTime",
					getBeginTime(), CompareType.Ge);
			hibernateExpressions.add(timeE);
		}
		if (getEndTime() != null) {
			HibernateExpression timeE = new CompareExpression("logTime",
					getEndTime(), CompareType.Le);
			hibernateExpressions.add(timeE);
		}
		if (getSearchUser() != null) {
			HibernateExpression userE = new CompareExpression("userId",
					getSearchUser().getId(), CompareType.Equal);
			hibernateExpressions.add(userE);
		}

		return hibernateExpressions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getSystemService().getLogResultCount(
						getSearchExpressions()).intValue();
			}

			@SuppressWarnings("unchecked")
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getSystemService().findLogBy(pageNo, nPageSize, "id",
						false, getSearchExpressions()).iterator();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getUserModel() {
		List<UserImpl> users = getUserService().findBy(UserImpl.class, 1, Integer.MAX_VALUE,
				"id", false, new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				users, UserImpl.class, "getName", "getId", true, "");
		return model;
	}

	public IPage onDetail(Object obj) {
		getDetailPage().setCurrentObject(obj);
		return getDetailPage();
	}

	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getLogNameList() {
		return new MapPropertySelectModel(showLogName(false));
	}
	
	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getLogActionList() {
		return new MapPropertySelectModel(showLogAction(false));
	}
	
	private Map<String,String> showLogAction(boolean showType){
		Map<String,String> map = new OrderedMap<String,String>();
		String logAction = getSystemService().getVariables("system_log_action").getValue();
		String[] actions = logAction.split(";");
		map.put("全部", "");
		if(showType){
			for(int i=0;i<actions.length;i++){
				String[] currentActions = actions[i].split("=");
				map.put(currentActions[0], currentActions[1]);
			}
		}else{
			for(int i=0;i<actions.length;i++){
				String[] currentActions = actions[i].split("=");
				map.put(currentActions[1], currentActions[0]);
			}
		}
		return map;
	}
	
	public Map<String,String> showLogName(boolean showType){
		Map<String,String> map = new OrderedMap<String,String>();
		String logAction = getSystemService().getVariables("system_log_name").getValue();
		String[] actions = logAction.split(";");
		map.put("全部", "");
		if(showType){
			for(int i=0;i<actions.length;i++){
				String[] currentActions = actions[i].split("=");
				map.put(currentActions[0], currentActions[1]);
			}
		}else{
			for(int i=0;i<actions.length;i++){
				String[] currentActions = actions[i].split("=");
				map.put(currentActions[1], currentActions[0]);
			}	
		}
		return map;
	}
	@InjectPage("DetailPage")
	public abstract DetailPage getDetailPage();

	public ITableColumn getDisplayOperator() {
		return new SimpleTableColumn("operator", "操作人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Log o1 = (Log) objRow;
						if( o1.getUserId()==null )
							return "";
						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, o1.getUserId());
						if (user == null) {
							return "";
						} else {
							return user.getName();
						}

					}

				}, false);

	}
	
	
	public ITableColumn getShowAction() {
		return new SimpleTableColumn("showAction", "动作",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Log o1 = (Log) objRow;
						//o1.getAction();
						Map<String,String> map = showLogAction(true);
						return map.get(o1.getAction());

					}

				}, false);

	}
	public ITableColumn getShowName() {
		return new SimpleTableColumn("showName", "名称",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Log o1 = (Log) objRow;
						//o1.getAction();
						Map<String,String> map = showLogName(true);
						return map.get(o1.getName());

					}

				}, false);

	}
	
}
