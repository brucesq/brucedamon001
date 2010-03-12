package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ReferenToResource extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@Bean
	public abstract ValidationDelegate getDelegate();

	public abstract ResourceReferen getResourceReferen();

	public abstract void setResourceReferen(ResourceReferen resourceReferen);

	public abstract String getResourceReferenName();

	public abstract void setResourceReferenName(String resourceReferenName);

	@InitialValue("-1")
	public abstract Integer getSearchId();

	public abstract void setSearchId(Integer id);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getResourceReferenName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getResourceReferenName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		if (getSearchId() != null && getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		HibernateExpression status = new CompareExpression("status", 0,
				CompareType.Equal);
		hibernateExpressions.add(status);
		return hibernateExpressions;
	}

	/**
	 * 获得批价包
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseResourceReferens() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResourceService().getResourceReferenResultCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getResourceService().findResourceReferenBy(pageNo,
						nPageSize, "id", false, getSearchExpressions())
						.iterator();
			}

		};

	}

	/**
	 * 搜索版权信息
	 * 
	 * @param cycle
	 */
	public void searchResourceReferen(IRequestCycle cycle) {
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

		if (getResourceReferen() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个资源(或资源已被删除)", null);

		} else {
			setRadioValue(String.valueOf((getResourceReferen().getId())));
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

	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	public void pageBeginRender(PageEvent arg0) {

	}

}
