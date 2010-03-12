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
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class TagTemplateChoicePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	@InjectComponent("table")
	public abstract TableView getTableView();

	@InitialValue("-1")
	public abstract int getSearchId();
	public abstract void setSearchId(int id);
	
	public abstract String getName();
	public abstract void setName(String name);

	public abstract String getTagName();
	public abstract void setTagName(String tagName);
	
	public abstract TagTemplate getTagTemplate();
	public abstract void setTagTemplate(TagTemplate tagTemplate);
	
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (!ParameterCheck.isNullOrEmpty(getTagName())) {
			HibernateExpression nameE = new CompareExpression("tagName", "%"
					+ getTagName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}

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

				count = getTemplateService().getTagTemplateCount(getSearchExpressions()).intValue();

				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getTemplateService().getTagTemplateList(pageNo, nPageSize,
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

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);
	
	public abstract String getRadioValue1();

	public abstract void setRadioValue1(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getTagTemplate() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个素材", null);

		} else {
			setRadioValue(String.valueOf((getTagTemplate().getId())));
			
		/*	String url = getSystemService().getVariables("media_url").getValue();
			
			setRadioValue1( url + getMaterial().getFilename() + "." + getMaterial().getExtName());*/
		}

		logger.info("提交的值:" + getRadioValue());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);
		
		if(parameters.length==1){
			setReturnElement((String) parameters[0]);
		}else if(parameters.length==2){
			setReturnElement((String) parameters[0]);
			String tagName = (String)parameters[1];
			if(!"all".equals(tagName))
				setTagName(tagName);
		}
			
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();
	

	
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

	

	

	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {

	}

	public abstract Material getCurrentObject();
	public abstract void setCurrentObject(Material m);
	
}
