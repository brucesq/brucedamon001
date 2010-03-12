/**
 * 
 */
package com.hunthawk.framework.tapestry.component;

import java.util.Set;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectState;

import com.hunthawk.framework.security.Visit;





/**
 * @author sunquanzhi
 *
 */
public abstract class Warning extends BaseComponent {

	@InjectState("visit")
    public abstract Visit getVisit();
	
	public Set<String> getErrors()
	{
		return this.getVisit().getErrors();
	}

	public abstract String getError();
	
	public abstract void setError(String error);
	
	@Asset("iconWarning.gif")
    public abstract IAsset getWarnicon();
}
