/**
 * 
 */
package com.hunthawk.framework.tapestry.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * @author sunquanzhi
 * 
 */
public class MapPropertySelectModel implements IPropertySelectionModel,
		Serializable
{

	protected Map _map;

	private boolean _allowNoSelection;

	private int _offset;

	private String _noSelectionMessage;

	public MapPropertySelectModel(Map map)
	{
		this(map, false, null);
	}

	public MapPropertySelectModel(Map map, boolean allowNoSelection,
			String noSelectionMessage)
	{
		

		_allowNoSelection = allowNoSelection;
		_noSelectionMessage = noSelectionMessage;
		if (_allowNoSelection)
		{
			_offset = 1;
//			Map newMap = new LinkedHashMap();
//			newMap.put("", null);
//
//			newMap.putAll(_map);
//			_map.clear();
//			
//			_map.putAll(newMap);

			// _map.put("","");
			_map = new LinkedHashMap();
			_map.put("", null);
			_map.putAll(map);
			
		}
		else
		{
			_map = map;
			_offset = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getOptionCount()
	 */
	public int getOptionCount()
	{

		return _map.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getOption(int)
	 */
	public Object getOption(int option)
	{

		Object obj = _map.get(getKey(option));

		return obj;
	}

	protected Object getKey(int option)
	{
		Iterator iterator = _map.keySet().iterator();
		int index = 0;
		Object obj = null;
		while (index++ <= option && iterator.hasNext())
		{
			obj = iterator.next();
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getLabel(int)
	 */
	public String getLabel(int option)
	{
		
		Object obj = getKey(option);
//		System.out.println("option:"+option+":"+obj);
		if(obj == null){
			return "null";
		}
		return obj.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getValue(int)
	 */
	public String getValue(int index)
	{

		return Integer.toString(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#translateValue(java.lang.String)
	 */
	public Object translateValue(String value)
	{

		int index = Integer.parseInt(value);
		return getOption(index);

	}

}
