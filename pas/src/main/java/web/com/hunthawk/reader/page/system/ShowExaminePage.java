package com.hunthawk.reader.page.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
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

import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.hunthawk.reader.page.adapter.EditAdapterPage;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * 日志统计
 * 
 * @author penglei
 * 
 */
public abstract class ShowExaminePage extends SearchPage {
	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("adapter/EditAdapterPage")
	public abstract EditAdapterPage getEditPage();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	public abstract Date getBeginTime();

	public abstract void setBeginTime(Date beginTime);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date endTime);

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
				return getUserService().findUserImplExamineCount(
						getBeginTime(), getEndTime()).intValue();

			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getUserService().findUserImplExamineList(getBeginTime(),
						getEndTime(), pageNo, nPageSize).iterator();
			}
		};
	}

	public ITableColumn getWaitToPublish() {
		return new SimpleTableColumn("waitToPublish", "待审变商用",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "waitToPublish");
					}

				}, false);
	}

	public ITableColumn getWaitToAgin() {
		return new SimpleTableColumn("waitToAgin", "待审变复审",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = -4110513672948368696L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "waitToAgin");
					}

				}, false);
	}

	public ITableColumn getWaitToRejected() {
		return new SimpleTableColumn("waitToRejected", "待审变否决",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 3147401038908417413L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "waitToRejected");
					}

				}, false);
	}

	public ITableColumn getAginToPublish() {
		return new SimpleTableColumn("aginToPublish", "复审变商用",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 3927015754948848304L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "aginToPublish");
					}

				}, false);
	}

	public ITableColumn getAginToRejected() {
		return new SimpleTableColumn("aginToRejected", "复审变否决",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 5748632049642731409L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "aginToRejected");
					}

				}, false);
	}

	public ITableColumn getPauseToPublish() {
		return new SimpleTableColumn("pauseToPublish", "暂停变商用",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 4336967280465736641L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "pauseToPublish");
					}

				}, false);
	}

	public ITableColumn getPublishTopause() {
		return new SimpleTableColumn("PublishTopause", "商用到暂停",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = -3909351381138734831L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "PublishTopause");
					}

				}, false);
	}

	public ITableColumn getReCheck() {
		return new SimpleTableColumn("reCheck", "复审",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						UserImpl user = (UserImpl) objRow;
						return getSystemService().findExamineActionByCount(
								user.getId(), "reCheck");
					}

				}, false);
	}

}
