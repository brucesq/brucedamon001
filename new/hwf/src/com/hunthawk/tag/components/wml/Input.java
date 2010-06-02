/**
 * 
 */
package com.hunthawk.tag.components.wml;

import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.components.Component;
import com.hunthawk.tag.components.MarkupWriter;

/**
 * @author sunquanzhi
 *
 */
public class Input implements Component {

	private String name = "";
	private String value ;
	private String title ;
	private String type ;
	private String format ;
	private int maxlength;
	private boolean emptyok;
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
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
	public void setType(String type)
	{
		this.type = type;
	}
	public void setFormat(String format)
	{
		this.format = format;
	}
	public void setMaxlength(int maxlength)
	{
		this.maxlength = maxlength;
	}
	public void setEmptyok(boolean emptyok)
	{
		this.emptyok = emptyok;
	}
	public boolean isEmptyok()
	{
		return this.emptyok;
	}
	/* (non-Javadoc)
	 * @see com.aspire.tag.components.Component#renderComponent()
	 */
	public String renderComponent() {
		// TODO Auto-generated method stub
		if(TagUtil.isBlank(name))
		{
			return "";
		}
		MarkupWriter writer = new MarkupWriter();
		writer.begin("input");
		writer.attribute("name",name);
		
		if(TagUtil.isNonBlank(type))
		{
			writer.attribute("type",type);
		}
		if(TagUtil.isNonBlank(value))
		{
			writer.attribute("value",value);
		}
		if(TagUtil.isNonBlank(title))
		{
			writer.attribute("title",title);
		}
		if(TagUtil.isNonBlank(format))
		{
			writer.attribute("format",format);
		}
		if(maxlength > 0)
		{
			writer.attribute("maxlength",String.valueOf(maxlength));
		}
		if(emptyok)
		{
			writer.attribute("emptyok",String.valueOf(emptyok));
		}
		writer.close();
		return writer.flush();
	}

	

}
