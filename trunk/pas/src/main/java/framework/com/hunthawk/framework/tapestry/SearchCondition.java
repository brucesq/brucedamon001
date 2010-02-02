/**
 * 
 */
package com.hunthawk.framework.tapestry;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.hunthawk.framework.util.BeanUtils;

/**
 * @author sunquanzhi
 *
 */
@SuppressWarnings("serial")
public class SearchCondition implements Serializable{

	private String name;
	private Object value;
	public void setName(String name)
	{
		this.name = name;
	}
	public void setValue(Object value)
	{
		this.value = value;
	}
	public String getName()
	{
		return name;
	}
	public Object getValue()
	{
		return value;
	}
	public void invoke(Object object)
	{
		try{
			BeanUtils.forceSetProperty(object,name,value);
		}catch(Exception e)
		{
			
		}
	}
	@Override
	public boolean equals(Object obj)
	{
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
