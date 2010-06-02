/**
 * 
 */
package com.hunthawk.framework.enhance;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author sunquanzhi
 *
 */
public interface PolicyComponent {
	
	public void  registerBefore(BeforeEnhance enhance);
	
	public void registerAfter(AfterEnhance enhance);
	
	public Object process(MethodInvocation mi)throws Throwable;

}
