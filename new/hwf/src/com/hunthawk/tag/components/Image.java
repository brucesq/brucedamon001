/**
 * 
 */
package com.hunthawk.tag.components;

import com.hunthawk.tag.TagUtil;

/**
 * @author sunquanzhi
 *
 */
public class Image implements Component {

	private String url = "";
	private String alt = "";
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	public void setAlt(String alt)
	{
		this.alt = alt;
	}
	
	public String renderComponent() {
		if(TagUtil.isBlank(url))
		{
			return "";
		}
		MarkupWriter writer = new MarkupWriter();
		writer.begin("img");
		writer.attribute("src",url);
		writer.attribute("alt",alt);
		writer.close();
		return writer.flush();
	}

	

}
