/**
 * 
 */
package com.hunthawk.framework.security.simple;

import java.util.HashSet;
import java.util.Set;

import com.hunthawk.framework.security.SecurityContext;
import com.hunthawk.framework.security.User;
import com.hunthawk.framework.security.Visit;



/**
 * @author sunquanzhi
 *
 */
public class MockSecurityContext implements SecurityContext {

	HashSet<String> errors;
	Visit visit;
	public MockSecurityContext()
	{
		SimpleUser user = new SimpleUser();
		user.setName("mock");
		user.setPassword("mock");
		Role role = new Role();
		Privilege pri = new Privilege();
		pri.setName("p");
		role.addPrivilege(pri);
		role.setName("administrator1");
		user.addRole(role);
		
		visit = new SimpleVisit();
		visit.setUser(user);
		
		errors = new HashSet<String>();
	}
	
	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.SecurityContext#getErrors()
	 */
	public Set<String> getErrors() {
		
		return errors;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.SecurityContext#getUser()
	 */
	public User getUser() {
		
		return visit.getUser();
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.SecurityContext#getVisit()
	 */
	public Visit getVisit() {
		
		return visit;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.SecurityContext#setError(java.lang.String)
	 */
	public void setError(String errors) {
		this.errors.add(errors);

	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.framework.security.SecurityContext#setVisit(com.aspire.pams.framework.security.Visit)
	 */
	public void setVisit(Visit visit) {
		this.visit = visit;

	}

}
