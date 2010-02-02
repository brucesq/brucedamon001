/**
 * 
 */
package com.hunthawk.framework.tapestry;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;

import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.security.PowerUtil;

/**
 * @author BruceSun
 *
 */
public abstract class RichSearchPage extends SearchPage{

	public abstract Object getCurrentObject();
	
	public boolean canRight(){
		return PowerUtil.hasPower(getCurrentObject(),PowerUtil.OWNER,(UserImpl)getUser());
	}
	
	public boolean hasCommonPermission(String name,String action){
		if(hasRole(name)){
			return hasPermission("common", action, getCurrentObject());
		}
		return false;
	}
	
	public boolean hasPermission(String name,String action){
		return hasPermission(name, action, getCurrentObject());
		
	}
	
	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();
	
	public String getRightURL(){
		
		if(canRight()){
			Object[] params = new Object[]{getCurrentObject()};
			return PageHelper.getExternalFunction(getExternalService(),
					"RightPage", params);
		}else{
			return "javascript:window.alert(\"你没有赋权权限\")";
		}
	
	}
}
