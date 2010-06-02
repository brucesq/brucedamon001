/**
 * 
 */
package com.hunthawk.framework.annotation;

/**
 * @author sunquanzhi
 *
 */
public @interface PublishService {
	enum Position 
	 {
	         ARG_1, ARG_2, ARG_3, ARG_4, ARG_5, ARG_6, ARG_7, ARG_8, ARG_9 
	 }
	enum Type
	{
		Object,Collection,Int
	}
	//发布服务名称
	String serviceName();
	//发布控制状态所在未知
	PublishService.Position value() default Position.ARG_1;
	//发布控制状态类型
	PublishService.Type type() default Type.Object;
}
