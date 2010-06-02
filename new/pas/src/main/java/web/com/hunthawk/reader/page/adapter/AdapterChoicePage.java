package com.hunthawk.reader.page.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.service.adapter.AdapterService;


/***
 * 选择适配器
 * @author penglei
 *
 */
public abstract class AdapterChoicePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InjectObject("spring:adapterService")
	public abstract AdapterService getAdapterService();

	public abstract String getSearchId();

	public abstract void setSearchId(String id);

	public abstract Adapter getAdapter();

	public abstract void setAdapter(Adapter adapter);

	public abstract String getName();

	public abstract void setName(String boardName);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression status = new CompareExpression("status", 1,
				CompareType.Equal);
		hibernateExpressions.add(status);

		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (!ParameterCheck.isNullOrEmpty(getSearchId())) {
			HibernateExpression idE = new CompareExpression("id", Integer
					.parseInt(getSearchId()), CompareType.Equal);
			hibernateExpressions.add(idE);
		}

		return hibernateExpressions;
	}

	/**
	 * 获得板块列表
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseMsgBoard() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return new Long(getAdapterService().findAdapterCount(
						getSearchExpressions())).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getAdapterService().findAdapterList(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}

		};

	}

	/**
	 * 搜索列表
	 * 
	 * @param cycle
	 */
	public void search(IRequestCycle cycle) {
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

		if (getAdapter() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个资源(或资源已被删除)", null);

		} else {
			setRadioValue(String.valueOf((getAdapter().getId())));
		}
	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板类型
		if (parameters.length == 2) {
			setReturnElement((String) parameters[0]);
		}
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	public ITableColumn getType() {
		return new SimpleTableColumn("type", "适配类型",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Adapter adapter = (Adapter) objRow;
						if (adapter.getAdapterTypeId() != null) {

							AdapterType adapterType = getAdapterService()
									.getAdapterTypeById(
											adapter.getAdapterTypeId());
							if (adapterType == null)
								return "";
							return adapterType.getName();
						} else {
							return "";
						}
					}

				}, false);

	}

	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {

	}
}
