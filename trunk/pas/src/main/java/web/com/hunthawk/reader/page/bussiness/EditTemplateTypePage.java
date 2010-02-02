/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.service.bussiness.TemplateService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles={"system"},mode=Restrict.Mode.ROLE)
public abstract class EditTemplateTypePage extends EditPage implements PageBeginRenderListener{
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass()
	{
		return TemplateType.class;
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			TemplateType type = (TemplateType)object;
			if(isModelNew())
			{
				type.setCreateTime(new Date());
				type.setCreatorId(getUser().getId());
				getTemplateService().addTemplateType(type);
			}else{
				getTemplateService().updateTemplateType(type);
			}
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}


	public void pageBeginRender(PageEvent event)
	{
		if(getModel() == null)
		{
			setModel(new TemplateType());
		}
		
	}
	
	
}
