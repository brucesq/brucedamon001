/**
 * 
 */
package com.hunthawk.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author sunquanzhi
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logable {
	
	String name();
	String action();
	Position[] position() default {Position.ARG_1};
	String keyproperty() default "id";
	Position keyposition() default Position.ARG_1;
	String[] property() ;

}
