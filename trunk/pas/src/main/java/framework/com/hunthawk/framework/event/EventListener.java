/**
 * 
 */
package com.hunthawk.framework.event;


/**
 * @author sunquanzhi
 *
 */
public interface EventListener {

	enum Mode {Before,After};
	
	enum Position{ RETURN_OBJ,  ARG_1, ARG_2, ARG_3, ARG_4, ARG_5, ARG_6, ARG_7, ARG_8, ARG_9 };
	
	public void setEventComponent(EventComponent eventComponent);
	
	public void register() ;
}
