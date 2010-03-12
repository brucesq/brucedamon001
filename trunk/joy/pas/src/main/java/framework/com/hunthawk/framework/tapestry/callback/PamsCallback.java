/**
 * 
 */
package com.hunthawk.framework.tapestry.callback;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.ICallback;

/**
 * @author sunquanzhi
 *
 */
@SuppressWarnings("serial")
public class PamsCallback implements ICallback {

	private String pageName;
	
	public PamsCallback(String pageName)
	{
		this.pageName = pageName;
	}
	public String getPageName()
	{
		return pageName;
	}

	public void setPageName(String pageName)
	{
		this.pageName = pageName;
	}
	/* (non-Javadoc)
	 * @see org.apache.tapestry.callback.ICallback#performCallback(org.apache.tapestry.IRequestCycle)
	 */
	public void performCallback(IRequestCycle cycle) {
		cycle.activate(cycle.getPage(getPageName()));

	}
	public boolean shouldReplace(ICallback callback)
	{
		return this.equals(callback);
	}

	@Override
	public boolean equals(Object obj)
	{
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
