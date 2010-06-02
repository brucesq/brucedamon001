/**
 * 
 */
package com.hunthawk.tag.components.wml;

import java.util.Map;

/**
 * @author sunquanzhi
 *
 */
public class SimpleSelectionModel implements ISelectionModel {

	private Map map;
	private boolean onpick;
	/**
	 * 
	 */
	public SimpleSelectionModel(Map map , boolean onpick) {
		this.map = map;
		this.onpick = onpick;
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.wml.ISelectionModel#getOptionCount()
	 */
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return map.size();
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.wml.ISelectionModel#isOnpick()
	 */
	public boolean isOnpick() {
		// TODO Auto-generated method stub
		return onpick;
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.wml.ISelectionModel#getLabel(int)
	 */
	public String getLabel(int index) {
		// TODO Auto-generated method stub
		Object[] obj = map.keySet().toArray();
		return obj[index].toString();
	}

	/* (non-Javadoc)
	 * @see com.aspire.tag.components.wml.ISelectionModel#getValue(int)
	 */
	public String getValue(int index) {
		// TODO Auto-generated method stub
		Object[] obj = map.keySet().toArray();
		Object label = map.get(obj[index]);
		return label.toString();
	}

	

}
