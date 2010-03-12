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

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "channel" }, mode = Restrict.Mode.ROLE)
public abstract class ShowChannelPage extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("partner/EditChannelPage")
	public abstract EditChannelPage getEditChannelPage();
	
	@InjectPage("partner/ShowChannelChildPage")
	public abstract ShowChannelChildPage getShowChannelChildPage();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	public abstract String getChannelId();
	public abstract void setChannelId(String channelId);
	



	
	public IPage onEdit(Channel channel)
	{
		getEditChannelPage().setModel(channel);
		return getEditChannelPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		Channel channel = (Channel)getCurrentObject();
		Set selectedChannel = getSelectedObjects();
		// 选择了用户
		if (bSelected)
		{
			selectedChannel.add(channel);
		}
		else
		{
			selectedChannel.remove(channel);
		}
		// persist value
		setSelectedObjects(selectedChannel);
		
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
			
			getPartnerService().deleteChannel((Channel)object);
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
		nameC.setName("channelId");
		nameC.setValue(getChannelId());
		searchConditions.add(nameC);
		return searchConditions;
	}

	public  Collection<HibernateExpression> getSearchExpressions()
	{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(!ParameterCheck.isNullOrEmpty(getChannelId())){
			HibernateExpression ex = new CompareExpression("id","%"+getChannelId()+"%",CompareType.Like);
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
				return new Long(getPartnerService().findChannelResultCount(getSearchExpressions())).intValue();
			}
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder)
			{
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getPartnerService().findChannel( pageNo, nPageSize,"id",true,getSearchExpressions() ).iterator();
			}
		};
	}

	
	public IPage showChannelChild(Channel channel){
		ShowChannelChildPage showChannelChildPage = getShowChannelChildPage();
		showChannelChildPage.setChannel(channel);
		return showChannelChildPage;
	}

}