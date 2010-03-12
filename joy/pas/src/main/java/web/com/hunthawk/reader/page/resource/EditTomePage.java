package com.hunthawk.reader.page.resource;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.security.SecurityComponent;
import com.hunthawk.framework.security.Visit;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.callback.CallbackStack;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;


public abstract class EditTomePage extends EditPage implements PageBeginRenderListener{

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	public abstract ResourceAll getResourceAll();
	public abstract void setResourceAll(ResourceAll resourceAll);
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return EbookTome.class;
	}
	
	@Override
	protected boolean persist(Object object) {
		try{
			EbookTome tome = (EbookTome)object;	
			
			if(isModelNew())
			{	
				tome.setResourceId(getResourceAll().getId());
				getResourceService().addEbookTome(tome);
			}else{
				getResourceService().updateEbookTome(tome);
			}
			ResourceAll resource = getResourceAll();
			if(resource!=null){
				resource.setStatus(1);
				getResourceService().updateResource(resource, resource.RESOURCE_TYPE_BOOK);
			}
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent arg0) {
		if(getModel() == null){
			setModel(new EbookTome());
		}
		
	}
	
}
