/**
 * 
 */
package com.hunthawk.framework.hibernate;

/**
 * @author sunquanzhi
 *
 */
public enum CompareType {
	Equal("="),NotEqual("<>"),Gt(">"),Lt("<"),Ge(">="),Le("<="),Like(" like "),NotLike(" not like ");
	
	private String value;
	private CompareType(String value)
	{
		this.value = value;
	}
	public String getValue()
	{
		return value;
	}
}
