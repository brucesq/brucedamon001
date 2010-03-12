/**
 * 
 */
package com.hunthawk.reader.security;

import java.util.HashSet;
import java.util.Set;

import com.hunthawk.reader.domain.system.Group;

/**
 * 
 * @author BruceSun
 *
 */
public class GroupPower implements Power{

	 private Group group;
	 private Set<String> powers;
	  
	   
	   public GroupPower() 
	   {
	     powers = new HashSet<String>();
	   }
	   public void setGroup(Group group)
	   {
		   this.group = group;
	   }
	   public Group getGroup()
	   {
		   return this.group;
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

}
