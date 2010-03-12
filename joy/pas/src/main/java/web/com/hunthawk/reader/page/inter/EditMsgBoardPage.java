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
import com.hunthawk.reader.service.inter.InteractiveService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "msgaudit" }, mode = Restrict.Mode.ROLE)
public abstract class EditMsgBoardPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return MsgBoard.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			MsgBoard board = (MsgBoard) object;
			if (isModelNew()) {
				board.setCreateTime(new Date());
				board.setCreator(getUser().getId());
				board.setUpdateTime(new Date());
				board.setModifier(getUser().getId());
				getInteractiveService().addMsgBoard(board);
			} else {
				board.setUpdateTime(new Date());
				board.setModifier(getUser().getId());
				getInteractiveService().updateMsgBoard(board);
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
			setModel(new MsgBoard());
		}

	}
	
	public IPropertySelectionModel getStatusTypeList() {
		return new MapPropertySelectModel(MsgBoard.getBoardStatus());
	}

	public IPropertySelectionModel getAuditTypeList() {
		return new MapPropertySelectModel(MsgBoard.getBoardAudit());
	}

}
