package com.hunthawk.reader.page.util;

import java.io.Serializable;

/**
 * 存储资源的搜索条件
 * @author x_dingjiangtao_a
 *
 */
public class SearchField implements Serializable
{
	/**
	 * 组件的名称
	 */
	private String name;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 组件的值
	 */
	private String value;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}

}
