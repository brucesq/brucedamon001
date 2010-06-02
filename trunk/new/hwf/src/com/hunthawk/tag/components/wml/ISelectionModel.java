/**
 * 
 */
package com.hunthawk.tag.components.wml;

/**
 * @author sunquanzhi
 *
 */
public interface ISelectionModel {
	public int getOptionCount();
	public boolean isOnpick();
	public String getLabel(int index);
	public String getValue(int index);
}
