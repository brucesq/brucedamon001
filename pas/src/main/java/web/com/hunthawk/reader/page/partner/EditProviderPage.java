/**
 * 
 */
package com.hunthawk.reader.page.partner;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "providerchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditProviderPage  extends EditPage implements PageBeginRenderListener{
	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass()
	{
		return Provider.class;
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			Provider provide = (Provider)object;
			if(provide.getProviderId()!=null && !"".equals(provide.getProviderId())){  //表明添加了产品代码
				if(!StringUtils.isNumeric(provide.getProviderId()) || provide.getProviderId().length()!=8){
					throw new Exception("产品代码只能是数字并且只能是8位!");
				}
			}
			if(isModelNew())
			{
				provide.setCreateTime(new Date());
				provide.setModifyTime(new Date());
				provide.setCreateorId(getUser().getId());
				provide.setMotifierId(getUser().getId());
				provide.setStatus(Provider.STATUS_AUDIT_ELIGIBLE);
				getPartnerService().addProvider(provide);
			}else{
				provide.setModifyTime(new Date());
				provide.setMotifierId(getUser().getId());
				getPartnerService().updateProvider(provide);
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
			Provider provider = new Provider();
			provider.setProportion("40%");
			setModel(provider);
		}
		
	}
	
	public IPropertySelectionModel getProviderTypeList(){
		return new MapPropertySelectModel(Provider.getProviderTypes());
	}
	
	public IPropertySelectionModel getCreditList(){
		return new MapPropertySelectModel(Constants.getCredits());
	}
}
