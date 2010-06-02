/**
 * 
 */
package com.hunthawk.reader.page.guide;

/**
 * @author sunquanzhi
 *
 */
public class GuideParameter {

	private String key;
	private String type;
	private String value;
	private String title;
	
	public void setKey(String key)
	{
		this.key = key;
	}
	public String getKey()
	{
		return this.key;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getType()
	{
		return this.type;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
	public String getValue()
	{
		return this.value;
	}
	public String getTitle()
	{
		return this.title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}

}
