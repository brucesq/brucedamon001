/**
 * 
 */
package com.hunthawk.reader.page;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author sunquanzhi
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class CheckPage extends SecurityPage {

	@InjectObject("spring:uploadService")
	public abstract UploadService getUploadService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	public void check(IRequestCycle cycle){
		UserImpl user = (UserImpl)getUser();
		Thread thread1 = new CheckThread(user,getResourceService(),getUploadService(),
				getSystemService());
		thread1.start();
	}
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
	
	
}
