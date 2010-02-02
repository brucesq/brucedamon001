package com.hunthawk.reader.page.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
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

import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.system.Log;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

public abstract class ShowExamineResourcePage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract Date getBeginTime();

	public abstract void setBeginTime(Date beginTime);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date endTime);

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
		// 保留删除方法
		// try {
		// Adapter adapter = (Adapter) object;
		// getAdapterService().deleteAdapter(adapter);
		// setAdapterTypeId(adapter.getAdapterTypeId());
		// } catch (Exception e) {
		// getDelegate().setFormComponent(null);
		// getDelegate().record(e.getMessage(), null);
		// }

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

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("beginTime");
		nameC.setValue(getBeginTime());
		searchConditions.add(nameC);

		nameC.setName("endTime");
		nameC.setValue(getEndTime());
		searchConditions.add(nameC);

		return searchConditions;
	}

	@Override
	public IBasicTableModel getTableModel() {
		if (getBeginTime() == null && getEndTime() == null) {
			Date date = new Date();
			date = DateUtils.addDays(date, -1);
			String strDate = ToolDateUtil.dateToString(date, "yyyyMMdd");
			setBeginTime(ToolDateUtil.stringToDate(strDate, "yyyyMMdd"));
		}
		return new IBasicTableModel() {
			public int getRowCount() {
				return getSystemService().findExamineResourceCount(
						getBeginTime(), getEndTime()).intValue();

			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getSystemService().findExamineResourceList(
						getBeginTime(), getEndTime(), pageNo, nPageSize)
						.iterator();
			}
		};
	}

	public ITableColumn getAction() {
		return new SimpleTableColumn("action", "审核操作",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						String result = "未知";
						Log log = (Log) objRow;
						String action = log.getAction();
						if (action.equalsIgnoreCase("waitToPublish")) {
							result = "待审变商用";
						} else if (action.equalsIgnoreCase("waitToAgin")) {
							result = "待审变复审";
						} else if (action.equalsIgnoreCase("waitToRejected")) {
							result = "待审变否决";
						} else if (action.equalsIgnoreCase("aginToPublish")) {
							result = "复审变商用";
						} else if (action.equalsIgnoreCase("aginToRejected")) {
							result = "复审变否决";
						} else if (action.equalsIgnoreCase("pauseToPublish")) {
							result = "暂停变商用";
						} else if (action.equalsIgnoreCase("PublishTopause")) {
							result = "商用到暂停";
						} else if (action.equalsIgnoreCase("reCheck")) {
							result = "复审";
						}

						return result;
					}

				}, false);
	}

	public ITableColumn getCreatorId() {
		return new SimpleTableColumn("creatorId", "操作者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = -1618042560372356799L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						String result = "操作人已被删除";
						Log log = (Log) objRow;
						Integer userId = log.getUserId();

						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, userId);
						if (user != null) {
							result = user.getName();
						}
						return result;

					}

				}, false);
	}

	public ITableColumn getResourceName() {
		return new SimpleTableColumn("resourceName", "资源名称",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = -2035097547068377534L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						String result = "资源不存在";
						Log log = (Log) objRow;
						String key = log.getKey();

						String resourceId = key.substring(1, key.length() - 1);
						ResourceAll resourceAll = getResourceService()
								.getResource(resourceId);
						if (resourceAll != null) {
							result = resourceAll.getName();
						}

						return result;

					}

				}, false);
	}

}
