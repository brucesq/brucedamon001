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
import com.hunthawk.framework.hibernate.InExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author yuzs
 * 
 */
public abstract class PackGroupSetPage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();


	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InitialValue("-1")
	public abstract Integer getSearchId();

	public abstract void setSearchId(Integer id);

	public abstract String getPageGroupName();

	public abstract void setPageGroupName(String pageGroupName);
	
	public abstract Integer getSearchType();
	
	public abstract void setSearchType(Integer type);

	
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getPageGroupName())) {
			HibernateExpression nameE = new CompareExpression("pkName", "%"
					+ getPageGroupName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id", getSearchId(),
					CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		
		HibernateExpression typeF = new CompareExpression("showType", getSearchType(),
				CompareType.Equal);
		hibernateExpressions.add(typeF);
		
		//过滤 只搜索上线产品
		HibernateExpression statusE = new CompareExpression("pkStatus", 0,
				CompareType.Equal);
		hibernateExpressions.add(statusE);
	
			return hibernateExpressions;
	}

	/**
	 * 获得模板
	 * 
	 * @return
	 */
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {

			// 模板的总数
			int count = 0;

			public int getRowCount() {
				if (count > 0) {

					return count;
				}

				count = getBussinessService().getPageGroupCount(getSearchExpressions()).intValue();

				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getBussinessService().findPageGroups(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();

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

	public abstract PageGroup getPageGroup();

	public abstract void setPageGroup(PageGroup pageGroup);

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);
	
	public abstract String getRadioValue1();

	public abstract void setRadioValue1(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getPageGroup() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个素材", null);

		} else {
			setRadioValue(String.valueOf((getPageGroup().getId())));
		}

		logger.info("提交的值:" + getRadioValue());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);
		if(parameters.length==1)
			setReturnElement((String) parameters[0]);
		else if(parameters.length==2){
			setReturnElement((String) parameters[0]);
			int showType = Integer.parseInt(parameters[1].toString());
			if(showType==0)
				setSearchType(1);
			else if(showType==1)
				setSearchType(3);
			else if(showType==2)
				setSearchType(2);
		}
		
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();
	
	public abstract void setReturnElement1(String name);

	public abstract String getReturnElement1();

	

	// 是否为搜索请求
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	

	

	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {

	}

	public abstract PageGroup getCurrentObject();
	public abstract void setCurrentObject(PageGroup m);
	
}
