/**
 * 
 */
package com.hunthawk.framework.enhance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author sunquanzhi
 * 
 */
public class EnhanceMethodInterceptor implements MethodInterceptor {

	private boolean disabled = false;
	private PolicyComponent policyComponent;

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public Object invoke(MethodInvocation mi) throws Throwable {
		if (disabled) {
			return mi.proceed();
		} else {
			return policyComponent.process(mi);
		}
	}

}
