/**
 * 
 */
package com.hunthawk.framework.enhance;

import java.lang.reflect.Method;

/**
 * @author sunquanzhi
 *
 */
public interface AfterEnhance {

	public void after(Method method,Object[] args,Object owner,Object return_obj)throws Throwable ;
}
