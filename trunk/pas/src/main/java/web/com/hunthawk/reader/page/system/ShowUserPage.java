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
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;


import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.security.User;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.UserService;
@Restrict(roles = { "user" }, mode = Restrict.Mode.ROLE)
public abstract class ShowUserPage  extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("system/EditUserPage")
	public abstract EditUserPage getEditUserPage();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	public abstract String getUsername();
	public abstract void setUsername(String name);
	

	public abstract Integer getUserid();
	public abstract void setUserid(Integer userid);

	
	public IPage onEdit(UserImpl user)
	{
		getEditUserPage().setModel(user);
		return getEditUserPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		User user = (User)getCurrentUser();
		Set selectedUser = getSelectedUsers();
		// 选择了用户
		if (bSelected)
		{
			selectedUser.add(user);
		}
		else
		{
			selectedUser.remove(user);
		}
		// persist value
		setSelectedUsers(selectedUser);
		
	}

	public boolean getCheckboxSelected()
	{
		return getSelectedUsers().contains(getCurrentUser());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedUsers();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedUsers(Set set);

	/**
	 * 获得当前用户
	 * 
	 * @return
	 */
	public abstract Object getCurrentUser();


	
	
	
	@Override
	protected  void delete(Object object)
	{
		try{
			
			getUserService().deleteUser((UserImpl)object);
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void onBatchDelete(IRequestCycle cycle){
		for(Object obj : getSelectedUsers())
		{
			delete(obj);
		}
		setSelectedUsers(new HashSet());
		ICallback callback = (ICallback) getCallbackStack().getCurrentCallback();
	    callback.performCallback(cycle);
		
	}

	public  Collection<HibernateExpression> getSearchExpressions()
	{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(!ParameterCheck.isNullOrEmpty(getUsername()))
		{
			HibernateExpression nameE = new CompareExpression("name","%"+getUsername()+"%",CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if(getUserid()!=null && getUserid()>0){
			HibernateExpression useridE = new CompareExpression("id",getUserid(),CompareType.Equal);
			hibernateExpressions.add(useridE);
		}
		
		return hibernateExpressions;
	}
	public void search(){
	}


	@Override
	public  List<SearchCondition> getSearchConditions()
	{
		
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("username");
		nameC.setValue(getUsername());
		searchConditions.add(nameC);
		
		SearchCondition useridC = new SearchCondition();
		useridC.setName("userid");
		useridC.setValue(getUserid());
		searchConditions.add(useridC);
		
		return searchConditions;
	}

	

	public   IBasicTableModel getTableModel()
	{
		return new IBasicTableModel()
		{
			public int getRowCount()
			{
				return getUserService().getResultCount(UserImpl.class, getSearchExpressions()).intValue();
			}
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder)
			{
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getUserService().findBy(UserImpl.class, pageNo, nPageSize, "id", false,  getSearchExpressions()).iterator();
			}
		};
	}


}
