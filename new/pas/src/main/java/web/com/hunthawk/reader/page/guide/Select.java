/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.io.Serializable;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * @author sunquanzhi
 *
 */
public class Select implements Type,Serializable{

	private IPropertySelectionModel model;
	
	private Object obj;
	
	private String name;
	
	private String title;
	/**
	 * 
	 */
	public Select() {
		super();
		
	}
	public void setName(String name)
	{
		this.name = name;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getTitle()
	{
		return this.title;
	}
	public String getName()
	{
		return this.name;
	}
	public void setPropertySelectionModel(IPropertySelectionModel model)
	{
		this.model = model;
	}
	public IPropertySelectionModel getPropertySelectionModel()
	{
		return this.model;
	}
//	public void setSelectObject(Object o)
//	{
//		this.obj = o;
//	}
//	public Object getSelectObject()
//	{
//		return this.obj;
//	}

	public Object getValue()
	{
		return this.obj;
	}
	public void setValue(Object o)
	{
		this.obj = o;
	}
}
