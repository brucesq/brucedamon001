/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.service.bussiness.BussinessService;

/**
 * @author BruceSun
 *
 */
public abstract class ShowProductPageGroupPage extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditProductPageGroupPage")
	public abstract EditProductPageGroupPage getEditProductPageGroupPage();
	
	

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();


	
	public IPage onEdit(PackGroupProvinceRelation rel)
	{
		getEditProductPageGroupPage().setModel(rel);
		getEditProductPageGroupPage().setProductId(rel.getPid());
		return getEditProductPageGroupPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		PackGroupProvinceRelation obj = (PackGroupProvinceRelation)getCurrentObject();
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
			
			getBussinessService().deletePackGroupProvinceRelation((PackGroupProvinceRelation)object);
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

	public abstract void setProductId(String id);
	
	public abstract String getProductId();

	@Override
	public  List<SearchCondition> getSearchConditions()
	{
		
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("productId");
		nameC.setValue(getProductId());
		searchConditions.add(nameC);
		return searchConditions;
	}

	public  Collection<HibernateExpression> getSearchExpressions()
	{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pid",getProductId(),CompareType.Equal);
		hibernateExpressions.add(ex);
		return hibernateExpressions;
	}
	

	public   List getTables()
	{
		return getBussinessService().findPackGroupProvinceRelations(1, Integer.MAX_VALUE, "aid", true, getSearchExpressions());
	}

	public IPage onAddRelation(String productId){
		getEditProductPageGroupPage().setProductId(productId);
		return getEditProductPageGroupPage();
	}
	
	public ITableColumn getAreaName(){
		return new SimpleTableColumn("areaname", "地区",
				new ITableColumnEvaluator(){

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow){
						PackGroupProvinceRelation p1 = (PackGroupProvinceRelation) objRow;
						return Constants.getAreaName(p1.getAid());

					}

				}, false);

	}
	
	public ITableColumn getPgname(){
		return new SimpleTableColumn("pgname", "页面组",
				new ITableColumnEvaluator(){

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow){
						PackGroupProvinceRelation p1 = (PackGroupProvinceRelation) objRow;
						PageGroup pg = getBussinessService().getPageGroup(p1.getPgid());
						return pg.getPkName();

					}

				}, false);

	}

}
