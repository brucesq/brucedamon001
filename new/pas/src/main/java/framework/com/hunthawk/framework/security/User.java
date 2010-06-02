/**
 * 
 */
package com.hunthawk.framework.security;

/**
 * @author sunquanzhi
 *
 */
public interface User {

	public static final String ADMIN = "administrator";
	
	public String getName();
	public void setName(String name);
	
	public Integer getId();
	
	public String getPassword();
	public void setPassword(String pwd);
	
	public boolean hasRole(String name);
	
	public boolean isAdmin();
	
}
