/**
 * 
 */
package com.hunthawk.tag.components;

/**
 * @author sunquanzhi
 *
 */
public class MarkupWriter {

	private StringBuffer buffer;
	/**
	 * 
	 */
	public MarkupWriter() {
		this.buffer = new StringBuffer();
	}

	public void begin(String name)
	{
		buffer.append("<"+name+" ");
	}
	public void attribute(String name, String value)
	{
		
		buffer.append(name);
		buffer.append("=\"");
		buffer.append(value+"\" ");
	}
	public void close(String name)
	{
		buffer.append("</"+name+">");
	}
	
	public void end()
	{
		buffer.append(">");
	}
	public void close()
	{
		buffer.append("/>");
	}
	
	public String flush()
	{
		return buffer.toString();
	}
	
	public void print(String str)
	{
		buffer.append(str);
	}

}
