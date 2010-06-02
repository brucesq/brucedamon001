/**
 * 
 */
package com.hunthawk.tag.components;

/**
 * @author sunquanzhi
 *
 */
public class Link implements Component {

	private String title = "";
	private String url = "";
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.Component#renderComponent()
	 */
	public String renderComponent() {
		// TODO Auto-generated method stub
		MarkupWriter writer = new MarkupWriter();
		writer.begin("a");
		writer.attribute("href",url);
		writer.end();
		writer.print(title);
		writer.close("a");
		return writer.flush();
	}

	
}
