package com.hunthawk.framework.security;

import java.util.Set;
public interface SecurityContext {
	
	public User getUser();
	public void setError(String errors);
	public Set<String> getErrors();
	public Visit getVisit();
	public void setVisit(Visit visit);
	
	
}
