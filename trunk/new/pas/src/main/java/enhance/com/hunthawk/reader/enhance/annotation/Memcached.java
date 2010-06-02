/**
 * 
 */
package com.hunthawk.reader.enhance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author BruceSun
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Memcached {

	enum Position {
		ARG_1, ARG_2, ARG_3, ARG_4, ARG_5, ARG_6, ARG_7, ARG_8, ARG_9
	}
	
	enum Type{
		SET,DELETE
	}

	// 对象内容所在位置
	Position value() default Position.ARG_1;

	String[] properties();

	Class targetClass();
	
	Type type() default Type.DELETE;
}
