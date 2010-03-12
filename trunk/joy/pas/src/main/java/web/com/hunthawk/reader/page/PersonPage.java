package com.hunthawk.reader.page;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;


import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.callback.EditCallback;
import com.hunthawk.framework.tapestry.callback.SearchCallback;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.system.UserService;


public abstract class PersonPage extends EditPage implements
		PageBeginRenderListener {
	
	@InjectObject("spring:userService")
	public abstract UserService  getUserService();
	
	public abstract String getNewPassword();
	public abstract void setNewPassword(String newPassword);
	
	public abstract String getRenewPassword();
	public abstract void setRenewPassword(String renewPassword);
	
	public abstract String getOldpwd();
	public abstract void setOldpwd(String oldpwd);

	@Override
	public Class getModelClass() {
		return UserImpl.class;
	}

	public abstract String getChoose();
	public abstract void setChoose(String choose);
	
	@Override
	public void saveAndReturn(IRequestCycle cycle) {
		if (save()) {
			setChoose("true");
		}
	}
	@Override
	protected boolean persist(Object object) {
		try {
			UserImpl user = (UserImpl) object;
				//检查当前用户 同现在的输入的密码是否一致：
				String pwd = user.getPassword();
				if(!pwd.equals(getOldpwd()))
					throw new Exception("请输入正确的原始密码");
				if(!getNewPassword().equals(getRenewPassword()))
					throw new Exception("两次输入的新密码必须一致");
				user.setPassword(getNewPassword());
				getUserService().updateUser(user);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}
	
	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new UserImpl());
		}
	}
}