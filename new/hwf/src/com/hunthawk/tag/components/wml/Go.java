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
public class Go implements Component {

	private String url = "";
	private String method ;
	private String charset;
	private IPostfieldModel model;
	public void setUrl(String url)
	{
		this.url = url;
	}
	public void setMethod(String method)
	{
		this.method = method;
	}
	public void setCharset(String charset)
	{
		this.charset = charset;
	}
	public void setPostfieldModel(IPostfieldModel model)
	{
		this.model = model;
	}
	
	public String renderComponent() {
		// TODO Auto-generated method stub
		if(model == null || TagUtil.isBlank(url))
		{
			return "";
		}
		MarkupWriter writer = new MarkupWriter();
		writer.begin("go");
		writer.attribute("href",url);
		if(TagUtil.isNonBlank(method))
		{
			writer.attribute("method",method);
		}
		if(TagUtil.isNonBlank(charset))
		{
			writer.attribute("accept-charset",charset);
		}
		writer.end();
		for(int i=0;i<model.getPostfieldCount();i++)
		{
			buildPostfield(writer,model.getName(i),model.getValue(i));
		}
		writer.close("go");
		return writer.flush();
	}

	private void buildPostfield(MarkupWriter writer,String name,String value)
	{
		writer.begin("postfield");
		writer.attribute("name",name);
		writer.attribute("value",value);
		writer.close();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
