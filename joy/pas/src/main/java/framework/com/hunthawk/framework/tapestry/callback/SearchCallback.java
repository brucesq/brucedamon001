/**
 * 
 */
package com.hunthawk.framework.tapestry.callback;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.ICallback;

import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;

/**
 * @author sunquanzhi
 *
 */
@SuppressWarnings("serial")
public class SearchCallback extends PamsCallback {

	 private List<SearchCondition> conditions = new ArrayList<SearchCondition>();
	 private boolean bFirstPage = false;
	 public void setFirstPage(boolean bFirstPage)
	 {
		 this.bFirstPage = bFirstPage;
	 }
	 public void setSearchConditionList(List<SearchCondition> conditions)
	 {
		 this.conditions = conditions;
	 }
	 public List<SearchCondition> getSearchConditionList()
	 {
		 return conditions;
	 }
	 public void addSearchCondition(SearchCondition condition)
	 {
		 conditions.add(condition);
	 }
	
	 public SearchCallback(String name,List<SearchCondition> conditions)
	 {
	        super(name);
	        setSearchConditionList(conditions);
	 }
	 public void performCallback(IRequestCycle cycle)
	 {
	        SearchPage searchPage = (SearchPage)cycle.getPage(getPageName());
	        searchPage.invokeSearchCondition(conditions);
	        if(bFirstPage)
	        {
	        	searchPage.setInitialPage(0);
	        }
	        cycle.activate(searchPage);
	 }
	@Override
	public boolean shouldReplace(ICallback callback)
	{
			if (callback instanceof SearchCallback)
			{
				if(getPageName().equals(((SearchCallback)callback).getPageName()))
				{
					return true;
				}
			}
			return false;
	}
}
