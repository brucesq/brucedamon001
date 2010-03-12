/**
 * 
 */
package com.hunthawk.reader.page.inter;

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

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.inter.VoteAct;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "voteItem" }, mode = Restrict.Mode.ROLE)
public abstract class ShowVoteItemPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("inter/EditVoteItemPage")
	public abstract EditVoteItemPage getEditVoteItemPage();

	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();
	
	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	public abstract String getName();
	public abstract void setName(String name);

	public abstract Integer getVoteActId();
	public abstract void setVoteActId(Integer voteActId);
	
	public IPage onEdit(Object obj) {
		getEditVoteItemPage().setModel(obj);
		return getEditVoteItemPage();
	}

	public IPage addItem(Integer voteActId){
		getEditVoteItemPage().setVoteActId(voteActId);
		return getEditVoteItemPage();
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
	 * 
	 * 
	 * @return
	 */
	public abstract Object getCurrentObject();

	public void search() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.SearchPage#delete(java.lang.Object)
	 */
	@Override
	protected void delete(Object object) {
		try {
			VoteItem voteItem = (VoteItem) object;
			getInteractiveService().deleteVoteItem(voteItem);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}

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

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		
		if (getVoteActId()!=null && getVoteActId()>0) {
			HibernateExpression nameE = new CompareExpression("voteId",
					getVoteActId(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}
		return hibernateExpressions;
	}
	
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getInteractiveService().getVoteItemList(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getInteractiveService().findVoteItems(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}
		};
	}
	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("voteActId");
		nameC.setValue(getVoteActId());
		searchConditions.add(nameC);
		
		return searchConditions;
	}
	
	public String getPreAddress() {
		String url = getSystemService().getVariables("media_url").getValue();
		VoteItem item = (VoteItem)getCurrentObject();	
		if(item.getImgId()!=null && !"".equals(item.getImgId())){
			Material mater = getMaterialService().getMaterial(item.getImgId());
			return url + mater.getFilename() + "." + mater.getExtName();
		}else{
			return "无图片";
		}
	}
	
	@InjectObject("spring:userService")
	public abstract UserService getUserService();
	
	public ITableColumn getUsername() {
		return new SimpleTableColumn("username", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						VoteItem rp = (VoteItem) objRow;
						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, rp.getCreator());
						if(user==null)
							return "";
						else
							return user.getName();
					}

				}, false);

	}

}
