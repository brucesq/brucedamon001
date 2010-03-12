/**
 * 
 */
package com.hunthawk.reader.enhance;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hunthawk.framework.enhance.BeforeEnhance;
import com.hunthawk.framework.enhance.PolicyComponent;
import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.util.ClassUtilities;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.annotation.Usersable;
import com.hunthawk.reader.security.PowerUtil;

/**
 * @author BruceSun
 *
 */
public class UserableEnhance implements BeforeEnhance {
	
	private static final Logger logger = LoggerFactory.getLogger(UserableEnhance.class);

	PolicyComponent policyComponent;
	public void setPolicyComponent(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}
	public void init()
	{
		policyComponent.registerBefore(this);
	}
	/* (non-Javadoc)
	 * @see com.hunthawk.framework.enhance.BeforeEnhance#before(java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	public void before(Method method, Object[] args, Object owner)
			throws Throwable {
		if(ClassUtilities.isAnnotationPresent(method,Usersable.class) && SecurityContextHolder.getContext().getUser() != null){
			Usersable userable = ClassUtilities.getAnnotation(method,Usersable.class);
			Object  obj = getArg(userable.value(),args);
			if(obj != null){
				String users = PowerUtil.makePowers((UserImpl)SecurityContextHolder.getContext().getUser());
				try{
					BeanUtils.setProperty(obj,"users",users);
				}catch(Exception e){
					logger.error("Userable Enhance error!",e);
				}
			}
		}
	}

	private Object getArg(Usersable.Position position,Object[] args)
	{
		Object arg = null;
		switch(position)
		{
		case ARG_1:
			 arg = getArg(0,args);
			 break;
		case ARG_2:
			 arg = getArg(1,args);
			 break;
		case ARG_3:
			 arg = getArg(2,args);
			 break;
		case ARG_4:
			 arg = getArg(3,args);
			 break;
		case ARG_5:
			 arg = getArg(4,args);
			 break;
		case ARG_6:
			 arg = getArg(5,args);
			 break;
		case ARG_7:
			 arg = getArg(6,args);
			 break;
		case ARG_8:
			 arg = getArg(7,args);
			 break;
		case ARG_9:
			 arg = getArg(8,args);
			 break;
		default:
				 break;
		}
		return arg;
	}
	private Object getArg(int pos,Object[] args)
	{
		if(args.length > pos)
		{
			return args[pos];
		}
		return null;
	}
}
