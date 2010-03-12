/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

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

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.service.bussiness.TemplateService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles={"system"},mode=Restrict.Mode.ROLE)
public abstract class ShowTemplateTypePage extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditTemplateTypePage")
	public abstract EditTemplateTypePage getEditTemplateTypePage();
	
	

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	


	
	public IPage onEdit(TemplateType type)
	{
		getEditTemplateTypePage().setModel(type);
		return getEditTemplateTypePage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		TemplateType type = (TemplateType)getCurrentObject();
		Set selecteds = getSelectedObjects();
		// 选择了用户
		if (bSelected)
		{
			selecteds.add(type);
		}
		else
		{
			selecteds.remove(type);
		}
		// persist value
		setSelectedObjects(selecteds);
		
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
			
			getTemplateService().deleteTemplateType((TemplateType)object);
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


	

	
	

	public   List getModels(){
		return getTemplateService().getAllTemplateType();
	}

	
	
}