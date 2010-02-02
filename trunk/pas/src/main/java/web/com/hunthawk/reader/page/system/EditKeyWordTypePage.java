package com.hunthawk.reader.page.system;

import java.util.Date;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.KeyWordService;


@Restrict(roles = { "keyword" }, mode = Restrict.Mode.ROLE)
public abstract class EditKeyWordTypePage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:keywordService")
	public abstract KeyWordService getKeyWordService();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return KeyWordType.class;
	}

	@InjectPage("system/ShowKeyWordPage")
	public abstract ShowKeyWordPage getShowKeyWordPage();
	
	public IPage savePage(IRequestCycle cycle) {
		if(save())
			return getShowKeyWordPage();
		else
			return this;
	}
	@Override
	protected boolean persist(Object object) {
		try {
			UserImpl user = (UserImpl) getUser();
			KeyWordType type= (KeyWordType)object;
			if(isModelNew()){
				type.setCreator(user.getId());
				type.setCreateTime(new Date());
				getKeyWordService().addKeyWordType(type);
			}else{
				getKeyWordService().updateKeyWordType(type);
			}		
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new KeyWordType());
		}
	}
}
