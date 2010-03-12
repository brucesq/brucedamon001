/**
 * 
 */
package com.hunthawk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hunthawk.framework.event.EventListener;

/**
 * @author sunquanzhi
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Observer {

	String value() ;
	EventListener.Mode mode() default EventListener.Mode.After;
	EventListener.Position[] positions();
}
