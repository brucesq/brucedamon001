package com.hunthawk.reader.page.bussiness;

import java.util.Map;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.system.SystemService;
@Restrict(roles={"system"},mode=Restrict.Mode.ROLE)
public abstract class EditSysTagPage extends EditPage implements PageBeginRenderListener{
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass()
	{
		return SysTag.class;
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			SysTag tag = (SysTag)object;
			if(isModelNew())
			{
				getTemplateService().addSysTag(tag);
			}else{
				getTemplateService().updateSysTag(tag);
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
			setModel(new SysTag());
		}
		
	}
	
	public IPropertySelectionModel getContentsources()
	{

		Variables variables = getSystemService().getVariables("tag_of_type");
		Map map = PageUtil.getMapFormString(variables.getValue());
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}
	
	public IPropertySelectionModel getTagTypes()
	{
		return new StringPropertySelectionModel(SysTag.TAGGUIDETYPE);
	}
}
