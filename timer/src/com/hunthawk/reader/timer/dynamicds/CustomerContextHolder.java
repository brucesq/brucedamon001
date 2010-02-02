package com.hunthawk.reader.timer.dynamicds;

/**
 * 获取和设置上下文环境 <br>
 * 主要负责设置上下文环境和获得上下文环境。
 * 
 * @author penglei 2009.11.06
 * 
 */
public class CustomerContextHolder {

	private static final ThreadLocal contextHolder = new ThreadLocal();

	public static void setCustomerType(String customerType) {
		contextHolder.set(customerType);
	}

	public static String getCustomerType() {
		return (String) contextHolder.get();
	}

	public static void clearCustomerType() {
		contextHolder.remove();
	}

}
