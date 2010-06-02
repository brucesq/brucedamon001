package com.hunthawk.reader.timer.dynamicds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * ������̬����Դ��<br>
 * �����ʵ����determineCurrentLookupKey�������÷�������һ��Object��һ���Ƿ����ַ�����Ҳ������ö�����͡�<br>
 * �÷�����ֱ��ʹ����CustomerContextHolder.getCustomerType()������������Ļ�����ֱ�ӷ��ء�
 * 
 * @author penglei 2009.11.06
 * 
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	protected Object determineCurrentLookupKey() {

		return CustomerContextHolder.getCustomerType();
	}

}
