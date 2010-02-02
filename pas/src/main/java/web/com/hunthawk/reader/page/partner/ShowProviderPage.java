/**
 * 
 */
package com.hunthawk.reader.page.partner;

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
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "provider" }, mode = Restrict.Mode.ROLE)
public abstract class ShowProviderPage extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("partner/EditProviderPage")
	public abstract EditProviderPage getEditProviderPage();
	
	

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	public abstract String getProviderId();
	public abstract void setProviderId(String channelId);
	

	@InjectPage("partner/ShowFeePage")
	public abstract ShowFeePage getShowFeePage();

	
	public IPage onEdit(Provider provider)
	{
		getEditProviderPage().setModel(provider);
		return getEditProviderPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		Provider obj = (Provider)getCurrentObject();
		Set selectedObjs = getSelectedObjects();
		// 选择了用户
		if (bSelected)
		{
			selectedObjs.add(obj);
		}
		else
		{
			selectedObjs.remove(obj);
		}
		// persist value
		setSelectedObjects(selectedObjs);
		
	}

	public boolean getCheckboxSelected()
	{
		return getSelectedObjects().contains(getCurrentObject());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedObjects();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedObjects(Set set);

	/**
	 * 获得当前用户
	 * 
	 * @return
	 */
	public abstract Object getCurrentObject();


	
	
	
	@Override
	protected  void delete(Object object)
	{
		try{
			
			getPartnerService().deleteProvider((Provider)object);
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void onBatchDelete(IRequestCycle cycle){
		for(Object obj : getSelectedObjects())
		{
			delete(obj);
		}
		setSelectedObjects(new HashSet());
		ICallback callback = (ICallback) getCallbackStack().getCurrentCallback();
	    callback.performCallback(cycle);
		
	}

	
	public void search(){
	}


	@Override
	public  List<SearchCondition> getSearchConditions()
	{
		
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("providerId");
		nameC.setValue(getProviderId());
		searchConditions.add(nameC);
		return searchConditions;
	}

	public  Collection<HibernateExpression> getSearchExpressions()
	{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(!ParameterCheck.isNullOrEmpty(getProviderId())){
			HibernateExpression ex = new CompareExpression("id","%"+getProviderId()+"%",CompareType.Like);
			hibernateExpressions.add(ex);
		}
		
		return hibernateExpressions;
	}
	

	public   IBasicTableModel getTableModel()
	{
		return new IBasicTableModel()
		{
			public int getRowCount()
			{
				return new Long(getPartnerService().findProviderResultCount(getSearchExpressions())).intValue();
			}
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder)
			{
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getPartnerService().findProvider( pageNo, nPageSize,"id",true,getSearchExpressions() ).iterator();
			}
		};
	}

	
	public abstract int getStatusValue();
	public abstract void setStatusValue(int statusValue);
	
	public void onChangeStatus(IRequestCycle cycle){
		Set setSelectedObjects = getSelectedObjects();

		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0){
			err = "您至少得选择一个";
		} else{
			for(Object obj:setSelectedObjects){
				try {
					getPartnerService().auditProvider((Provider)obj, getStatusValue());
				} catch (Exception e) {
					err += e.getMessage();
				}
			}
			
		}
		if(!ParameterCheck.isNullOrEmpty(err)){
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
		}
	
		// clear selection
		setSelectedObjects(new HashSet());
		getCallbackStack().popPreviousCallback();
	}
	
	public IPage showFee(Provider provider){
		ShowFeePage page = getShowFeePage();
		page.setProvider(provider);
		return page;
	}

}
