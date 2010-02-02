package com.hunthawk.reader.page.resource;

import java.util.Date;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.resource.ReCheck;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.resource.ResourceService;

public abstract class SourceToReCheck extends SecurityPage {

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectPage("resource/SourceToReCheck")
	public abstract SourceToReCheck getSourceToReCheck();

	public abstract String getResourceId();

	public abstract void setResourceId(String resourceId);

	public abstract String getMsg();

	public abstract void setMsg(String msg);
	
	public abstract String getChooseFlag();
	
	public abstract void setChooseFlag(String chooseFlag);

	
	

	public IPage savePage() {
		ValidationDelegate delegate = getDelegate();
		String msg = getMsg();
		UserImpl user = (UserImpl) getUser();
		ReCheck reCheck = new ReCheck();
		reCheck.setComment(msg);
		reCheck.setCreatorId(user.getId());
		reCheck.setCreateTime(new Date());
		reCheck.setResourceId(getResourceId());
		ResourceAll resource = getResourceService().getResource(getResourceId());
		try {
			resource.setStatus(4);
			getResourceService().updateResourceAll(resource);
			getResourceService().saveReCheck(reCheck);
			
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return this;
		}
		getSourceToReCheck().setChooseFlag("Strue");
		return getSourceToReCheck();
	}
}
