/**
 * 
 */
package com.hunthawk.reader.page.guide;

/**
 * @author BruceSun
 *
 */
public interface Type {
	public Object getValue();
	public void setValue(Object obj);
	
	public void setName(String name);
	public String getName();
	
	public void setTitle(String title);
	
	public String getTitle();
}
