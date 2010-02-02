/**
 * 
 */
package com.hunthawk.framework.hibernate;

/**
 * @author sunquanzhi
 * 
 */
public enum LogicalType {

	And("and"), Or("or");

	private String value;

	private LogicalType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
