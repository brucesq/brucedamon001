/**
 * 
 */
package com.hunthawk.framework.security;

/**
 * @author sunquanzhi
 *
 */
public interface Identity {

	public  boolean hasRole(String role);
	
	public  boolean hasPermission(PermissionCheck permissionCheck,Object... args);
}
