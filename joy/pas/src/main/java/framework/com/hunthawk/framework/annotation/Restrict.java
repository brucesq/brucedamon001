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
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Restrict {
	enum Position 
	 {
	         ARG_1, ARG_2, ARG_3, ARG_4, ARG_5, ARG_6, ARG_7, ARG_8, ARG_9 
	 }
	enum Mode
	{
		ROLE,PERMISSION,ALL
	}
	//权限名称
	String name() default "";
	//动作名称
	String action() default "";
	//参数
	Restrict.Position[] args() default {};
	//属性
	String[] properties() default {};
	//角色
	String[] roles() default {};
	
	Restrict.Mode mode() default Mode.ALL;
}
