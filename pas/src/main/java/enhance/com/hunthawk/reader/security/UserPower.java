/**
 * 
 */
package com.hunthawk.reader.security;

import java.util.HashSet;
import java.util.Set;

import com.hunthawk.framework.security.User;

/**
 * @author BruceSun
 *
 */
public class UserPower implements Power{
	   private User user;
	   private Set<String> powers;
	  
	   
	   public UserPower() 
	   {
	     powers = new HashSet<String>();
	   }
	   public void setUser(User user)
	   {
		   this.user = user;
	   }
	   public User getUser()
	   {
		   return this.user;
	   }
	   public void addPower(String power)
	   {
		   powers.add(power);
	   }
	   public Set getPowers()
	   {
		   return this.powers;
	   }
	  
	   public boolean hasPower(String power)
	   {
		   return powers.contains(power);
	   }
	   
	   public boolean equals(Object obj)
	   {
		   if(obj instanceof UserPower)
		   {
			   UserPower up = (UserPower)obj;
			   if(up.getUser().equals(getUser()))
				   return true;
		   }
		   return false;
	   }
}
