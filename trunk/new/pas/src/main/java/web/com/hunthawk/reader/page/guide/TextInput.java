/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.io.Serializable;

/**
 * @author sunquanzhi
 *
 */
public class TextInput implements Type,Serializable {

	private String title = "";
	private String name = "";
	private Object value ;
	private String validators = "";
	private String translator = "";

	
	public void setValidators(String validators)
	{
		this.validators = validators;
	}
	public String getValidators()
	{
		return this.validators;
	}
	public void setTranslator(String translator)
	{
		this.translator = translator;
	}
	public String getTranslator()
	{
		return this.translator;
	}
	/* (non-Javadoc)
	 * @see com.aspire.pams.page.type.Type#getValue()
	 */
	public Object getValue() {
		
		return this.value;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.page.type.Type#setValue(java.lang.Object)
	 */
	public void setValue(Object obj) {
		this.value = obj;

	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.page.type.Type#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;

	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.page.type.Type#getName()
	 */
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.page.type.Type#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;

	}

	/* (non-Javadoc)
	 * @see com.aspire.pams.page.type.Type#getTitle()
	 */
	public String getTitle() {
		return this.title;
	}

	

}
