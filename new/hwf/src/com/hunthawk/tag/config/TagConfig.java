/**
 * 
 */
package com.hunthawk.tag.config;

/**
 * @author sunquanzhi
 *
 */
public class TagConfig {

	String name;
	String classname;
	String desc;
	/**
	 * 
	 */
	public TagConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setName(String name)
	{
		this.name = name;
	}
	public void setClassname(String classname)
	{
		this.classname = classname;
	}
	public void setDesc(String desc)
	{
		this.desc = desc;
	}
	public String getName()
	{
		return this.name;
	}
	public String getClassname()
	{
		return this.classname;
	}
	public String getDesc()
	{
		return this.desc;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
