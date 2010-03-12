/**
 * 
 */
package com.hunthawk.reader.page.inter;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.inter.MsgBoard;
import com.hunthawk.reader.domain.inter.VoteAct;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.inter.InteractiveService;

/**
 * @author yuzs
 *
 */
@Restrict(roles = { "voteAct" }, mode = Restrict.Mode.ROLE)
public abstract class EditVoteActPage extends EditPage implements
		PageBeginRenderListener {
	
	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();
		
		@SuppressWarnings("unchecked")
		@Override
		public Class getModelClass() {
			return VoteAct.class;
		}
		
		@Override
		protected boolean persist(Object object) {
			try {
				VoteAct voteAct = (VoteAct) object;
				UserImpl user = (UserImpl) getUser();
				if (isModelNew()) {
					voteAct.setCreateTime(new Date());
					voteAct.setCreator(user.getId());
					getInteractiveService().addVoteAct(voteAct);	
				} else {
					getInteractiveService().updateVoteAct(voteAct);
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
				setModel(new VoteAct());
			}

		}
}
