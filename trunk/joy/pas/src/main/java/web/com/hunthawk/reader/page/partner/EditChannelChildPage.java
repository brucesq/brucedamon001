package com.hunthawk.reader.page.partner;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.ChannelChild;
import com.hunthawk.reader.service.partner.PartnerService;

@Restrict(roles = { "channelchildchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditChannelChildPage extends EditPage implements PageBeginRenderListener{
	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass()
	{
		return ChannelChild.class;
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try{
			ChannelChild child = (ChannelChild)object;
			if(isModelNew())
			{
				child.setParent(getChannel());
				child.setCreateTime(new Date());
				child.setModifyTime(new Date());
				child.setCreateorId(getUser().getId());
				child.setMotifierId(getUser().getId());
				getPartnerService().addChannelChild(child);
			}else{
				child.setModifyTime(new Date());
				child.setMotifierId(getUser().getId());
				getPartnerService().updateChannelChild(child);
			}
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public abstract Channel getChannel();
	public abstract void setChannel(Channel channel);

	public void pageBeginRender(PageEvent event)
	{
		if(getModel() == null)
		{
			ChannelChild child  = new ChannelChild();
			child.setProportion("10%");
			setModel(child);
		}
		
	}
	
	public IPropertySelectionModel getBussinessTypeList(){
		return new MapPropertySelectModel(Constants.getBussinessTypes());
	}
	
	
	public IPropertySelectionModel getCreditList(){
		return new MapPropertySelectModel(Constants.getCredits());
	}
}

