/**
 * 
 */
package com.hunthawk.reader.page.util;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author BruceSun
 * 
 */
public class SelectKeyValuePO {
	int id;
	String label;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		else if (!(o instanceof SelectKeyValuePO))
			return false;
		else if (o == this)
			return true;
		else {
			SelectKeyValuePO another = (SelectKeyValuePO) o;
			if (another.getId() == this.getId()) {
				if (another.getLabel() == null) {
					if (this.getLabel() != null)
						return false;
					else
						return true;
				} else {
					if (this.getLabel() == null)
						return false;
					else if (another.getLabel().equals(this.getLabel())) {

						return true;
					} else
						return false;
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * 调用ToStringBuilder进行反射属性。
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
