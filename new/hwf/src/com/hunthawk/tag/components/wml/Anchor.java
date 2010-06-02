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
public class Anchor implements Component {

	private String title = "";
	private String text ; 
	private Go go;
	public void setTitle(String title)
	{
		this.title = title;
	}
	public void setGo(Go go)
	{
		this.go = go;
	}

	public void setText(String text)
	{
		this.text = text;
	}
	/* (non-Javadoc)
	 * @see com.aspire.tag.components.Component#renderComponent()
	 */
	public String renderComponent() {
		// TODO Auto-generated method stub
		if(go == null)
		{
			return "";
		}
		MarkupWriter writer = new MarkupWriter();
		writer.begin("anchor");
		writer.attribute("title",title);
		writer.end();
		if(TagUtil.isNonBlank(text))
		{
			writer.print(text);
		}
		writer.print(go.renderComponent());
		writer.close("anchor");
		return writer.flush();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
