package com.hunthawk.reader.page.resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author xianlaichen
 * 
 */
@Restrict(roles = { "copyright" }, mode = Restrict.Mode.ROLE)
public abstract class ShowReferenPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditReferenPage")
	public abstract EditReferenPage getEditReferenPage();

	// @InjectPage("resource/ReferenDownloadPage")
	// public abstract ReferenDownloadPage getReferenDownloadPage();
	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@Override
	protected void delete(Object object) {
		try {
			getResourceService().auditResourceReferen((ResourceReferen) object,
					2);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public IPage onEdit(ResourceReferen resourcereferen) {
		getEditReferenPage().setModel(resourcereferen);
		return getEditReferenPage();
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedResourceReferen();

	public abstract void setSelectedResourceReferen(Set set);

	public abstract Object getCurrentObject();

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		ResourceReferen pro = (ResourceReferen) getCurrentProduct();
		Set selectedPros = getSelectedResourceReferen();
		// 选择了用户
		if (bSelected) {
			selectedPros.add(pro);
		} else {
			selectedPros.remove(pro);
		}
		// persist value
		setSelectedResourceReferen(selectedPros);

	}

	public boolean getCheckboxSelected() {
		return getSelectedResourceReferen().contains(getCurrentProduct());
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedResourceReferen()) {
			delete(obj);
		}
		setSelectedResourceReferen(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);
	}

	// 产品名称
	public abstract String getName();

	public abstract void setName(String name);

	public abstract Date getBeginTime();

	public abstract void setBeginTime(Date date);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date date);

	public abstract UserImpl getSearchUser();

	public abstract void setSearchUser(UserImpl user);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getBeginTime() != null) {
			HibernateExpression timeE = new CompareExpression("createTime",
					getBeginTime(), CompareType.Ge);
			hibernateExpressions.add(timeE);
		}
		if (getEndTime() != null) {
			HibernateExpression timeE = new CompareExpression("createTime",
					getEndTime(), CompareType.Le);
			hibernateExpressions.add(timeE);
		}
		if (getSearchUser() != null) {
			HibernateExpression userE = new CompareExpression("creatorId",
					getSearchUser().getId(), CompareType.Equal);
			hibernateExpressions.add(userE);
		}
		UserImpl user = (UserImpl) getUser();
		if (user.isRoleProvider()) {// SPCP只能看到自己的内容
			Integer cpid = user.getProvider() != null ? user.getProvider()
					.getId() : null;
			HibernateExpression cpE = new CompareExpression("cpId", cpid,
					CompareType.Equal);
			hibernateExpressions.add(cpE);

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

		nameC = new SearchCondition();
		nameC.setName("beginTime");
		nameC.setValue(getBeginTime());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("endTime");
		nameC.setValue(getEndTime());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("searchUser");
		nameC.setValue(getSearchUser());
		searchConditions.add(nameC);

		return searchConditions;
	}

	public IPropertySelectionModel getAuthorStatusList() {
		return new MapPropertySelectModel(Constants.getAuthorStatus());
	}

	public IBasicTableModel getTableModel() {
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
	 * 获得当前产品
	 * 
	 * @return
	 */
	public abstract Object getCurrentProduct();

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedResourceReferen();
		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "您至少得选择一个";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					getResourceService().auditResourceReferen(
							(ResourceReferen) obj, getStatusValue());
				} catch (Exception e) {
					err += e.getMessage();
				}
			}

		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
		}

		// clear selection
		setSelectedResourceReferen(new HashSet());
		getCallbackStack().popPreviousCallback();
	}

	@InjectPage("resource/DownloadReferenPage")
	public abstract DownloadReferenPage getDownloadReferenPage();

	public IPage onDownload(Object obj) {
		getDownloadReferenPage().setCurrentObject(obj);
		return getDownloadReferenPage();
	}

	public ITableColumn getUsername() {
		return new SimpleTableColumn("username", "修改人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceReferen rp = (ResourceReferen) objRow;
						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, rp.getModifierId());
						if(user==null)
							return "";
						else
							return user.getName();
					}

				}, false);

	}

	public ITableColumn getProvidername() {
		return new SimpleTableColumn("providername", "CPID",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceReferen rp = (ResourceReferen) objRow;
						if (rp.getCpId() != null) {
							Provider provider = getPartnerService()
									.getProvider(rp.getCpId());

							return provider.getProviderId();
						} else {
							return "";
						}

					}

				}, false);

	}

	public ITableColumn getBegin(){
		return new SimpleTableColumn("beginTime", "生效日期",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceReferen o1 = (ResourceReferen) objRow;
						SimpleDateFormat dateFormate = new SimpleDateFormat(
								"yyyy-MM-dd");
						return dateFormate.format(o1.getBeginTime());
					}

				}, false);
	}
	public ITableColumn getEnd(){
		return new SimpleTableColumn("endTime", "失效日期",
				new ITableColumnEvaluator() {
					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceReferen o1 = (ResourceReferen) objRow;
						SimpleDateFormat dateFormate = new SimpleDateFormat(
								"yyyy-MM-dd");
						return dateFormate.format(o1.getEndTime());
					}

				}, false);
	}

	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getUserModel() {
		List<UserImpl> users = getUserService().findBy(UserImpl.class, 1, Integer.MAX_VALUE,
				"id", false, new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				users, UserImpl.class, "getName", "getId", true, "");
		return model;
	}
}
