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
public class Select implements Component {

	private String name = "";
	private String value = "";
	private boolean isMultiple = false;
	private ISelectionModel model  ;
	private String ivalue = "";
	private String iname = "";
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
	public void setMultipe(boolean multiple)
	{
		this.isMultiple = multiple;
	}
	public boolean isMultiple()
	{
		return isMultiple;
	}
	public void setIName(String iname)
	{
		this.iname = iname;
	}
	public void setIValue(String ivalue)
	{
		this.ivalue = ivalue;
	}
	public void setSelectionModel(ISelectionModel model)
	{
		this.model = model;
	}
	public ISelectionModel getSelectionModel()
	{
		return this.model;
	}
	/**
	 * 
	 */
	public Select() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.Component#renderComponent()
	 */
	public String renderComponent() {
		// TODO Auto-generated method stub
		if(model == null || TagUtil.isBlank(name))
		{
			return "";
		}
		MarkupWriter writer = new MarkupWriter();
		writer.begin("select");
		writer.attribute("name",name);
		if(TagUtil.isNonBlank(value))
		{
			writer.attribute("value",value);
		}
		if(TagUtil.isNonBlank(iname))
		{
			writer.attribute("iname",iname);
		}
		if(TagUtil.isNonBlank(ivalue))
		{
			writer.attribute("ivalue",ivalue);
		}
		if(isMultiple())
		{
			writer.attribute("multiple","true");
		}
		writer.end();
		
		for(int i=0;i<model.getOptionCount();i++)
		{
			buildOption(writer,model.getLabel(i),model.getValue(i),model.isOnpick());
		}
		writer.close("select");
		return writer.flush();
	}

	
	private void buildOption(MarkupWriter writer,String label,String value,boolean isOnpick)
	{
		writer.begin("option");
		if(isOnpick)
		{
			writer.attribute("onpick",value);
		}else{
			writer.attribute("value",value);
		}
		writer.end();
		writer.print(label);
		writer.close("option");
	}
	

}
