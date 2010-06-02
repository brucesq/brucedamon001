/**
 * 
 */
package com.hunthawk.reader.page.system;

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
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.system.Privilege;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "system" }, mode = Restrict.Mode.ROLE)
public abstract class ShowPrivilegePage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("system/EditPrivilegePage")
	public abstract EditPrivilegePage getEditPrivilegePage();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();
	
	public abstract String getName();
	public abstract void setName(String name);
	

	public abstract Integer getPrivilegeid();
	public abstract void setPrivilegeid(Integer userid);
	
	
	public IPage onEdit(Privilege pri)
	{
		getEditPrivilegePage().setModel(pri);
		return getEditPrivilegePage();
	}
	
	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		Privilege privilege = getCurrentPrivilege();
		
		if(privilege == null){
			return;
		}
		Set selectedPrivilege = getSelectedPrivileges();
		// 选择了用户
		if (bSelected)
		{
			selectedPrivilege.add(privilege);
		}
		else
		{
			selectedPrivilege.remove(privilege);
		}
		// persist value
		setSelectedPrivileges(selectedPrivilege);
		
	}

	public boolean getCheckboxSelected()
	{
		return getSelectedPrivileges().contains(getCurrentPrivilege());
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedPrivileges();

	public abstract void setSelectedPrivileges(Set set);
	
	/**
	 * 获得当前Privilege
	 * 
	 * @return
	 */
	
	public abstract  Privilege getCurrentPrivilege();
	
	public void search()
	{
		
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.SearchPage#delete(java.lang.Object)
	 */
	@Override
	protected void delete(Object object) {
		try{
			
			getUserService().deletePrivilege((Privilege)object);
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}

	}

	public void onBatchDelete(IRequestCycle cycle){
		
		for(Object obj : getSelectedPrivileges())
		{
			
			if(obj != null){
				
				delete(obj);
			}
			
		}
		setSelectedPrivileges(new HashSet());
		getCallbackStack().popPreviousCallback();

		
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.SearchPage#getSearchConditions()
	 */
	
	public  Collection<HibernateExpression> getSearchExpressions()
	{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(!ParameterCheck.isNullOrEmpty(getName()))
		{
			HibernateExpression nameE = new CompareExpression("name","%"+getName()+"%",CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		HibernateExpression useridE = new CompareExpression("id",getPrivilegeid(),CompareType.Equal);
		hibernateExpressions.add(useridE);
		return hibernateExpressions;
	}

	@Override
	public  List<SearchCondition> getSearchConditions()
	{
		
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);
		
		SearchCondition useridC = new SearchCondition();
		useridC.setName("privilegeid");
		useridC.setValue(getPrivilegeid());
		searchConditions.add(useridC);
		
		return searchConditions;
	}
	
	@Override
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel()
		{
			public int getRowCount()
			{
				return getUserService().getResultCount(Privilege.class, getSearchExpressions()).intValue();
			}
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder)
			{
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getUserService().findBy(Privilege.class, pageNo, nPageSize, "id", false,  getSearchExpressions()).iterator();
			}
		};
	}

	


	
}