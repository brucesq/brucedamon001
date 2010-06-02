/**
 * 
 */
package com.hunthawk.framework.security;

import java.util.Set;


/**
 * @author sunquanzhi
 *
 */
public interface Visit {

	
	public void setUser(User user);
	
	public User getUser();
	

	public void clearErrors();

	public Set<String> getErrors();

	public void addError(String err);
	
	public void setErrors(Set<String> errors);
	
	
}
