package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
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
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.service.resource.ResourceService;

/**
 * 作者列表
 * 
 * @author penglei
 * 
 */

@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class AuthorListPage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@Bean
	public abstract ValidationDelegate getDelegate();

	public abstract String getAuthorNames();

	public abstract void setAuthorNames(String authorNames);

	public abstract String getAuthorName();

	public abstract void setAuthorName(String authorName);

	public abstract Object getCurrentObject();

	public abstract void setCurrentObject(Object currentObject);

	public abstract String getInitialLetter();

	public abstract void setInitialLetter(String initialLetter);

	public abstract String getPenName();

	public abstract void setPenName(String penName);

	public IPropertySelectionModel getInitialLetterList() {
		return new MapPropertySelectModel(Constants.getInitialLetter(), true,
				"");
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedObjects();

	public abstract void setSelectedObjects(Set set);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getAuthorName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getAuthorName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		if (!ParameterCheck.isNullOrEmpty(getPenName())) {
			HibernateExpression nameE = new CompareExpression("penName", "%"
					+ getPenName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		if (!ParameterCheck.isNullOrEmpty(getInitialLetter())
				&& !"0".equals(getInitialLetter())) {
			HibernateExpression nameE = new CompareExpression("initialLetter",
					"%" + getInitialLetter() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
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
	public IBasicTableModel getChooseAuthorList() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResourceService().getResourceAuthorResultCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getResourceService().findResourceAuthorBy(pageNo,
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

	public abstract String getRadioValueName();

	public abstract void setRadioValue(String radioValue);

	public abstract void setRadioValueName(String radioValue);

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

	public void chooseSubmit(IRequestCycle cycle) {
		ValidationDelegate delegate = getDelegate();
		Set selectObjs = getSelectedObjects();
		if (selectObjs.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个作者", null);
			return;
		}
		int type = -1;
		String value = "|";
		String name = "";
		for (Object obj : selectObjs) {
			ResourceAuthor author = (ResourceAuthor) obj;

			value += author.getId() + "|";
			name += author.getName() + ",";
		}

		setChoose("true");
		setRadioValue(value);
		setRadioValueName(name);
		setSelectedObjects(new HashSet());
		logger.info("提交的值:" + getRadioValue());
	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板类型
		setReturnElement((String) parameters[0]);
		setReturnElementID((String) parameters[1]);

	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	public abstract void setReturnElementID(String name);

	public abstract String getReturnElementID();

	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	public void pageBeginRender(PageEvent arg0) {

	}

}
