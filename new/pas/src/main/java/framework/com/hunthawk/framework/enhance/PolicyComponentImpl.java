/**
 * 
 */
package com.hunthawk.framework.enhance;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.security.SecurityComponent;
import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.util.ClassUtilities;

/**
 * @author sunquanzhi
 * 
 */
public class PolicyComponentImpl implements PolicyComponent {

	private SecurityComponent securityComponent;

	private List<BeforeEnhance> beforeEnhances = new ArrayList<BeforeEnhance>();

	private List<AfterEnhance> afterEnhances = new ArrayList<AfterEnhance>();

	public void registerBefore(BeforeEnhance enhance) {
		beforeEnhances.add(enhance);
	}

	private boolean securityDisabled = true;

	public void setSecurityDisabled(boolean disabled) {
		this.securityDisabled = disabled;
	}

	public void registerAfter(AfterEnhance enhance) {
		afterEnhances.add(enhance);
	}

	public void setSecurityComponent(SecurityComponent securityComponent) {

		this.securityComponent = securityComponent;

	}

	public Object process(MethodInvocation mi) throws Throwable {

		Method method = mi.getMethod();// ClassUtilities.getNestMethod(mi.getThis().getClass(),
										// mi.getMethod());

		if (securityDisabled
				&& securityCheck(method, mi.getArguments(), mi.getThis())) {
			long startTime = System.currentTimeMillis();
			for (BeforeEnhance beforeEnhance : beforeEnhances) {
				beforeEnhance.before(method, mi.getArguments(), mi.getThis());
			}
			long endTime = System.currentTimeMillis();
			if(endTime-startTime>1000){
				System.out.println("Enhance before spend more time "+(endTime-startTime)+"ms "+method.getName()+" "+method.getDeclaringClass().getName());
			}
			Object obj = mi.proceed();
			startTime = System.currentTimeMillis();
			for (AfterEnhance afterEnhance : afterEnhances) {
				afterEnhance
						.after(method, mi.getArguments(), mi.getThis(), obj);
			}
			endTime = System.currentTimeMillis();
			if(endTime-startTime>1000){
				System.out.println("Enhance after spend more time "+(endTime-startTime)+"ms "+method.getName()+" "+method.getDeclaringClass().getName());
			}
			return obj;
		} else if (securityDisabled) {

			SecurityContextHolder.getContext().setError("你没有相关的操作权限！");

			return null;

		} else {

			return mi.proceed();

		}

	}

	protected boolean securityCheck(Method method, Object[] args, Object owner) {
		if (ClassUtilities.isAnnotationPresent(method, Restrict.class)) {
			Restrict restrict = ClassUtilities.getAnnotation(method,
					Restrict.class);
			return securityComponent.hasPermission(restrict, method, args,
					owner);
		} else {
			return true;
		}

	}
}
