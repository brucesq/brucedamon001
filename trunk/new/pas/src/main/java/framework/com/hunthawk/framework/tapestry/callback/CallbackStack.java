/**
 * 
 */
package com.hunthawk.framework.tapestry.callback;

import java.util.Stack;

/**
 * @author sunquanzhi
 *
 */
public class CallbackStack {

	private Stack<PamsCallback> stack = new Stack<PamsCallback>();

	public Stack<PamsCallback> getStack()
	{
		return stack;
	}

	public void setStack(Stack<PamsCallback> stack)
	{
		this.stack = stack;
	}
	
	/**
	 * 
	 * @param callback
	 */
	public void push(PamsCallback callback)
	{
		if (!getStack().empty() && (callback.shouldReplace(getStack().peek())))
		{
			getStack().pop();
		}
		getStack().push(callback);
	}
	
	

	/**
	 * 
	 * @return
	 */
	public PamsCallback popPreviousCallback()
	{
		if (getStack().size() > 1)
		{
			getStack().pop();
			return getStack().pop();
		}
		else return null;
	}

	public PamsCallback getPreviousCallback()
	{
		if (getStack().size() > 1)
		{
			return getStack().get(getStack().size() - 2);
		}
		else return null;
	}
	
	public PamsCallback getCurrentCallback()
	{
		if (getStack().size() >= 1)
		{
			return getStack().get(getStack().size() - 1);
		}
		else return null;
	}
}
