/**
 * 
 */
package com.hunthawk.framework.tapestry;

import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.event.PageEndRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.tapestry.callback.CallbackStack;
import com.hunthawk.framework.tapestry.callback.PamsCallback;


/**
 * @author sunquanzhi
 *
 */
public abstract class CallbackPage extends SecurityPage implements PageEndRenderListener{
	 @InjectState("callbackStack")
	 public abstract CallbackStack getCallbackStack();
	 
	 public abstract boolean getCancelPullback();
	 public abstract void setCancelPullback(boolean bNeed);
		
	 
	 public void pushCallback()
	 {
			getCallbackStack().push(new PamsCallback(getPageName()));
	 }
	 
	 public void pageEndRender(PageEvent event)
	 {
			if(!getCancelPullback())
				pushCallback();
	 }
}
