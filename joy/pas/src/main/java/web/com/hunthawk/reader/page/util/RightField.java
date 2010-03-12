package com.hunthawk.reader.page.util;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 
 * @author BruceSun
 *
 */

@SuppressWarnings("serial")
public class RightField implements Serializable
{
	private String Id;
	private String title;
	public String getId()
	{
		return Id;
	}
	public void setId(String Id)
	{
		this.Id = Id;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
    
    public boolean equals(Object obj)
    {
        if (obj instanceof RightField == false)
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        RightField rf = (RightField) obj;

        return new EqualsBuilder().append(Id, rf.getId()).isEquals();
    }

    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
