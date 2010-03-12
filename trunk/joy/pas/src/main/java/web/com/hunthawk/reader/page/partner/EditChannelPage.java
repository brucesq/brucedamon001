/**
 * 
 */
package com.hunthawk.reader.page.partner;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.ChannelType;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "channelchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditChannelPage  extends EditPage implements PageBeginRenderListener{
	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass()
	{
		return Channel.class;
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			Channel channel = (Channel)object;
			if(isModelNew())
			{
				channel.setCreateTime(new Date());
				channel.setModifyTime(new Date());
				channel.setCreateorId(getUser().getId());
				channel.setMotifierId(getUser().getId());
				getPartnerService().addChannel(channel);
			}else{
				channel.setModifyTime(new Date());
				channel.setMotifierId(getUser().getId());
				getPartnerService().updateChannel(channel);
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
			Channel channel = new Channel();
			channel.setProportion("40%");
			setModel(channel);
		}
		
	}
	
	public IPropertySelectionModel getChannelTypeList(){
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				ChannelType.getAllChannelTypes(), ChannelType.class, "getName", "getId", false, "");
		return parentProper;
	}
	
	public IPropertySelectionModel getCreditList(){
		return new MapPropertySelectModel(Constants.getCredits());
	}
}
