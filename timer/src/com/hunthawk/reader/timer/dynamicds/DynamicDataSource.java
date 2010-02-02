package com.hunthawk.reader.timer.dynamicds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 建立动态数据源类<br>
 * 这个类实现了determineCurrentLookupKey方法，该方法返回一个Object，一般是返回字符串，也可以是枚举类型。<br>
 * 该方法中直接使用了CustomerContextHolder.getCustomerType()方法获得上下文环境并直接返回。
 * 
 * @author penglei 2009.11.06
 * 
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	protected Object determineCurrentLookupKey() {

		return CustomerContextHolder.getCustomerType();
	}

}
