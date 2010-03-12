package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author xianlaichen
 * 
 */
@Restrict(roles = { "author" }, mode = Restrict.Mode.ROLE)
public abstract class ShowAuthorPage extends SearchPage {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditAuthorPage")
	public abstract EditAuthorPage getEditAuthorPage();

	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage  getShowEbookPage();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@Override
	protected void delete(Object object) {
		try {
			getResourceService().deleteResourceAuthor((ResourceAuthor) object);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public IPage onEdit(ResourceAuthor resourceauthor) {
		getEditAuthorPage().setModel(resourceauthor);
		return getEditAuthorPage();
	}

	public IPage onBookLink(ResourceAuthor resourceauthor){
		getShowEbookPage().setAuthorId(resourceauthor.getId());
		return getShowEbookPage();
	}
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedResourceAuthor();

	public abstract void setSelectedResourceAuthor(Set set);

	public abstract Object getCurrentObject();

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		ResourceAuthor pro = (ResourceAuthor) getCurrentObject();
		Set selectedPros = getSelectedResourceAuthor();
		// 选择了用户
		if (bSelected) {
			selectedPros.add(pro);
		} else {
			selectedPros.remove(pro);
		}
		// persist value
		setSelectedResourceAuthor(selectedPros);

	}

	public boolean getCheckboxSelected() {
		return getSelectedResourceAuthor().contains(getCurrentObject());
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedResourceAuthor()) {
			delete(obj);
		}
		setSelectedResourceAuthor(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);
	}

	public abstract String getName();
	public abstract void setName(String name);

	public abstract String getPenName();
	public abstract void setPenName(String name);
	
	public abstract String getInitialLetter();
	public abstract void setInitialLetter(String initialLetter);
	
	public IPropertySelectionModel getInitialLetterList() {
		return new MapPropertySelectModel(Constants.getInitialLetter(),true,"");
	}
	
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		if (!ParameterCheck.isNullOrEmpty(getPenName())) {
			HibernateExpression nameE = new CompareExpression("penName", "%"
					+ getPenName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		if (!ParameterCheck.isNullOrEmpty(getInitialLetter()) && !"0".equals(getInitialLetter())) {
			HibernateExpression nameE = new CompareExpression("initialLetter", "%"
					+ getInitialLetter() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		return hibernateExpressions;
	}

	public void search() {
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);
		
		SearchCondition named = new SearchCondition();
		named.setName("penName");
		named.setValue(getPenName());
		searchConditions.add(named);
		
		return searchConditions;
	}

	public IPropertySelectionModel getAuthorStatusList() {
		return new MapPropertySelectModel(Constants.getAuthorStatus());
	}

	public IBasicTableModel getTableModel() {
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

	public String getPreAddress() {
		String url = getSystemService().getVariables("media_url").getValue();
		ResourceAuthor mater = (ResourceAuthor) getCurrentObject();
		return url + mater.getAuthorPic();
	}

	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceAuthor o1 = (ResourceAuthor) objRow;

						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, o1.getCreatorId());
						if (user == null) {
							return "";
						} else {
							return user.getName();
						}

					}

				}, false);

	}

}
