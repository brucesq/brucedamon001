/**
 * 
 */
package com.hunthawk.framework.event;

import com.hunthawk.framework.enhance.AfterEnhance;
import com.hunthawk.framework.enhance.BeforeEnhance;

/**
 * @author sunquanzhi
 *
 */
public interface EventComponent extends BeforeEnhance, AfterEnhance {

	public void register(JavaBehaviour listener,EventListener.Mode mode,String eventName);
}
