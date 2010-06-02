/**
 * 
 */
package com.hunthawk.framework.enhance;

import java.lang.reflect.Method;

/**
 * @author sunquanzhi
 *
 */
public interface BeforeEnhance {

	public void before(Method method,Object[] args,Object owner)throws Throwable ;
}
