/**
 * 
 */
package com.hunthawk.reader.page.util;

import java.io.Serializable;

/**
 * @author BruceSun
 * 
 */
public class DetailPageField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7923017203629034638L;

	private String title;
	private String value;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
