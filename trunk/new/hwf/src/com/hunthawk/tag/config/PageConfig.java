/**
 * 
 */
package com.hunthawk.tag.config;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author sunquanzhi
 *
 */
public class PageConfig {

	private String name ;
	private List tagList;
	/**
	 * 
	 */
	public PageConfig() {
		name = "";
		tagList = new ArrayList();
	}
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	public List getTagList()
	{
		return tagList;
	}
	
	public void setTagset(String tagset)
	{
		StringTokenizer tokenizer = new StringTokenizer(tagset,",");
		while(tokenizer.hasMoreTokens())
		{
			
			tagList.add(tokenizer.nextToken());
		}
		
	}
	
}
