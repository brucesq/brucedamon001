/**
 * 
 */
package com.hunthawk.framework.security;

import java.lang.reflect.Method;

import com.hunthawk.framework.annotation.Restrict;



/**
 * @author sunquanzhi
 *
 */
public interface SecurityComponent {

	public boolean hasPermission(Restrict restrict,Method method,Object[] args,Object owner);
	
	public boolean hasPermission(Restrict restrict,Object owner);
	
	public boolean hasRole(String[] roles);
	
	public boolean hasPermission(String name,String action,Object... args);
}
