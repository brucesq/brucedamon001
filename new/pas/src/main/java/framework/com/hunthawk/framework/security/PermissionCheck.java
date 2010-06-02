/**
 * 
 */
package com.hunthawk.framework.security;

/**
 * @author sunquanzhi
 *
 */
public class PermissionCheck {
	
	  private String name;
	  private String action;
	  private boolean granted;

	  public PermissionCheck(String name, String action)
	  {
	    this.name = name;
	    this.action = action;
	    this.granted = false;
	  }

	
	  public String getName()
	  {
	    return name;
	  }

	  public String getAction()
	  {
	    return action;
	  }

	  public void grant()
	  {
	    this.granted = true;
	  }

	  public boolean isGranted()
	  {
	    return granted;
	  }
}
