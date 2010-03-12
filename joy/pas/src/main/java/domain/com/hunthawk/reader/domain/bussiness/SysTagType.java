package com.hunthawk.reader.domain.bussiness;

import java.io.Serializable;
import java.util.List;
@SuppressWarnings("unchecked")
public class SysTagType implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String type;

	
	private List tagGuide;

	public List getTagGuide()
	{
		return tagGuide;
	}

	public void setTagGuide(List tagGuide)
	{
		this.tagGuide = tagGuide;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
