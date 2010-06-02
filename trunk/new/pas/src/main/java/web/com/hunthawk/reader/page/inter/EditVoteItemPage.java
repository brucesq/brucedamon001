/**
 * 
 */
package com.hunthawk.reader.page.inter;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.inter.MsgBoard;
import com.hunthawk.reader.domain.inter.VoteAct;
import com.hunthawk.reader.domain.inter.VoteItem;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.inter.InteractiveService;

/**
 * @author yuzs
 *
 */
@Restrict(roles = { "voteItem" }, mode = Restrict.Mode.ROLE)
public abstract class EditVoteItemPage extends EditPage implements
		PageBeginRenderListener {
		
	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();
		
	public abstract Integer getVoteActId();
	public abstract void setVoteActId(Integer voteActId);
	
	public abstract String getImgUrl();
	public abstract void setImgUrl(String imgUrl);
	
		@SuppressWarnings("unchecked")
		@Override
		public Class getModelClass() {
			return VoteItem.class;
		}
		
		@Override
		protected boolean persist(Object object) {
			try {
				VoteItem voteItem = (VoteItem) object;
				UserImpl user = (UserImpl) getUser();
				if (isModelNew()) {
					voteItem.setCreateTime(new Date());
					voteItem.setCreator(user.getId());
					voteItem.setVoteId(getVoteActId());
					getInteractiveService().addVoteItem(voteItem);	
				} else {
					getInteractiveService().updateVoteItem(voteItem);
				}
			} catch (Exception e) {
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
				return false;
			}
			return true;
		}

		public void pageBeginRender(PageEvent event) {
			if (getModel() == null) {
				setModel(new VoteItem());
			}

		}
		
		@InjectObject("engine-service:external")
		public abstract IEngineService getExternalService();
		
		
		public String getChooseImageURL(){
			IEngineService service = getExternalService();
			Object[] params = new Object[3];
			params[0] = "imgId";
			params[1] = "imgUrl";
			params[2] = String.valueOf(MaterialCatalog.TYPE_IMAGE);
			String resURL = PageHelper.getExternalFunction(service,
					"resource/MaterialChoosePage", params);

			return resURL;
		}
}
