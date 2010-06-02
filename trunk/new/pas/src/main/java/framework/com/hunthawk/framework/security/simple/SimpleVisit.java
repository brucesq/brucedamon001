/**
 * 
 */
package com.hunthawk.framework.security.simple;

import java.util.HashSet;
import java.util.Set;

import com.hunthawk.framework.security.User;


/**
 * @author sunquanzhi
 *
 */
public class SimpleVisit  implements com.hunthawk.framework.security.Visit {

	Set<String> errs = new HashSet<String>();
	private User user;
	
	public void addError(String err) {
		errs.add(err);

	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.Visit#clearErrors()
	 */
	public void clearErrors() {
		errs.clear();
		
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.Visit#getErrors()
	 */
	public Set<String> getErrors() {
		
		return errs;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.Visit#getUser()
	 */
	public User getUser() {
		
		return user;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.Visit#setErrors(java.util.Set)
	 */
	public void setErrors(Set<String> errors) {
		errs = errors;

	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.Visit#setUser(com.aspire.pams.framework.security.User)
	 */
	public void setUser(User user) {
		
		this.user = user;

	}
}
