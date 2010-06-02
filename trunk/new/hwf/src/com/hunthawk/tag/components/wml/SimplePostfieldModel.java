/**
 * 
 */
package com.hunthawk.tag.components.wml;

import java.util.Map;

/**
 * @author sunquanzhi
 *
 */
public class SimplePostfieldModel implements IPostfieldModel {

	private Map map ;
	/**
	 * 
	 */
	public SimplePostfieldModel(Map map) {
		this.map = map;
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.wml.IPostfieldModel#getPostfieldCount()
	 */
	public int getPostfieldCount() {
		// TODO Auto-generated method stub
		return map.size();
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.wml.IPostfieldModel#getName(int)
	 */
	public String getName(int index) {
		// TODO Auto-generated method stub
		Object[] obj = map.keySet().toArray();
		return obj[index].toString();
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.wml.IPostfieldModel#getValue(int)
	 */
	public String getValue(int index) {
		// TODO Auto-generated method stub
		Object[] obj = map.keySet().toArray();
		Object label = map.get(obj[index]);
		return label.toString();
	}

	

}
