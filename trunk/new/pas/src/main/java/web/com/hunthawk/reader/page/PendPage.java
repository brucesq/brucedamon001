/**
 * 
 */
package com.hunthawk.reader.page;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;

/**
 * @author BruceSun
 *
 */
@Restrict(roles={"basic"},mode=Restrict.Mode.ROLE)
public abstract class PendPage extends SecurityPage{

	public boolean hasRole(String role){
		return super.hasRole(role);
	}
}
