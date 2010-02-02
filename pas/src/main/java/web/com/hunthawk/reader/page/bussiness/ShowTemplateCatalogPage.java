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
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.service.bussiness.TemplateService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "templatecatalog" }, mode = Restrict.Mode.ROLE)
public abstract class ShowTemplateCatalogPage extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditTemplateCatalogPage")
	public abstract EditTemplateCatalogPage getEditTemplateCatalogPage();
	
	

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	


	
	public IPage onEdit(TemplateCatalog type)
	{
		getEditTemplateCatalogPage().setModel(type);
		return getEditTemplateCatalogPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		TemplateCatalog type = (TemplateCatalog)getCurrentObject();
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
			
			getTemplateService().deleteTemplateCatalog((TemplateCatalog)object);
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
		return getTemplateService().getAllTemplateCatalog();
	}


}
