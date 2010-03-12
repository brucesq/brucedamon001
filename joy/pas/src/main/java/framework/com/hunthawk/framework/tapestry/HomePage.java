/**
 * 
 */
package com.hunthawk.framework.tapestry;

import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.tapestry.callback.CallbackStack;

/**
 * @author sunquanzhi
 *
 */
public abstract class HomePage extends SecurityPage implements PageBeginRenderListener{

	 @InjectState("callbackStack")
	 public abstract CallbackStack getCallbackStack();
	 
	 public void pageBeginRender(PageEvent event)
	 {
		 getCallbackStack().getStack().clear();
	 }

}
