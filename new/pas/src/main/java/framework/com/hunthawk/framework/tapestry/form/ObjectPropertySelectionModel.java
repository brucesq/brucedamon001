/**
 * 
 */
package com.hunthawk.framework.tapestry.form;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * @author sunquanzhi
 *
 */
public class ObjectPropertySelectionModel implements IPropertySelectionModel {

	private List _list;
	private Method _methodToGetLabel = null;
	protected Method _methodToGetOption = null;
	private boolean _allowNoSelection = false;
	private String _noSelectionMessage = null;
	private int _offset = 0;

	/**
	 * Do not use this, the default constructor.
	 */
	public ObjectPropertySelectionModel() {
		_list = new ArrayList();
		throw new UnsupportedOperationException("Do not use the default constructor.");
	}

	/**
	 * 
	 * @param list
	 *            the list of objects to represent in a PropertySelection
	 *            component. WARNING: The objects in the list MUST implement
	 *            equals(Object obj) and hashCode(), and if they are EJB3
	 *            Entities those methods MUST return the same thing for
	 *            different instances of the same detached entity (eg. by
	 *            matching on Id only).
	 * @param clazz
	 *            the class of objects in the list.
	 * @param nameOfMethodToGetLabel
	 *            the name of the method that PropertySelection must invoke on
	 *            each object in the list to get its label to display in the
	 *            list on the page. For example, the method name might be
	 *            "getShortName".
	 * @param nameOfMethodToGetOption
	 *            the name of the method that PropertySelection must invoke on
	 *            an object when it has been selected. The result is put into
	 *            the property named in the "value" parameter of the
	 *            PropertySelection. For example, the method name might be
	 *            "getKey". If you want the result to be the the whole object
	 *            then set this argument to null.
	 * 
	 */
	public ObjectPropertySelectionModel(List<? extends Object> list, Class clazz, String nameOfMethodToGetLabel,
			String nameOfMethodToGetOption) {

		this(list, clazz, nameOfMethodToGetLabel, nameOfMethodToGetOption, false, null);
	}

	/**
	 * 
	 * @param list
	 *            the list of objects to represent in a PropertySelection
	 *            component. WARNING: The objects in the list MUST implement
	 *            equals(Object obj) and hashCode(), and if they are EJB3
	 *            Entities those methods MUST return the same thing for
	 *            different instances of the same detached entity (eg. by
	 *            matching on Id only).
	 * @param clazz
	 *            the class of objects in the list.
	 * @param nameOfMethodToGetLabel
	 *            the name of the method that PropertySelection must invoke on
	 *            each object in the list to get its label to display in the
	 *            list on the page. For example, the method name might be
	 *            "getShortName".
	 * @param nameOfMethodToGetOption
	 *            the name of the method that PropertySelection must invoke on
	 *            an object when it has been selected. The result is put into
	 *            the property named in the "value" parameter of the
	 *            PropertySelection. For example, the method name might be
	 *            "getKey". If you want the result to be the the whole object
	 *            then set this argument to null.
	 * @param allowNoSelection
	 *            If true then adds an element to the start of the list. Its
	 *            option is null.
	 * @param noSelectionLabel
	 *            If null then no label. A typical label might be "Select...".
	 * 
	 */
	public ObjectPropertySelectionModel(List<? extends Object> list, Class clazz, String nameOfMethodToGetLabel,
			String nameOfMethodToGetOption, boolean allowNoSelection, String noSelectionLabel) {

		try {
			_list = list;
			_methodToGetLabel = clazz.getMethod(nameOfMethodToGetLabel, (Class[]) null);
			if (nameOfMethodToGetOption != null) {
				_methodToGetOption = clazz.getMethod(nameOfMethodToGetOption, (Class[]) null);
			}
			_allowNoSelection = allowNoSelection;
			if (_allowNoSelection) {
				_noSelectionMessage = noSelectionLabel;
				_offset = 1;
			}
		}
		catch (SecurityException e) {
			throw new IllegalStateException(e);
		}
		catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Given nameOfMethodToGetLabel=\"" + nameOfMethodToGetLabel + "\", nameOfMethodToGetOption=\"" 
				+ nameOfMethodToGetOption + "\", class=\"" + clazz + "\".", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getOptionCount()
	 */
	public int getOptionCount() {
		
		return _list.size() + _offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getOption(int)
	 */
	public Object getOption(int index) {
		if (_allowNoSelection) {
			if (index < _offset) {
				return null;
			}
		}
		Object o = _list.get(index - _offset);
		//if (_methodToGetOption == null) {
			return o;
//		}
//		else {
//			try {
//				Object option = _methodToGetOption.invoke(o, (Object[]) null);
//				return option;
//			}
//			catch (IllegalArgumentException e) {
//				throw new IllegalStateException("Problem with the given methodToGetOption \"" + _methodToGetOption + "\": ", e);
//			}
//			catch (IllegalAccessException e) {
//				throw new IllegalStateException("Problem with the given methodToGetOption \"" + _methodToGetOption + "\": ", e);
//			}
//			catch (InvocationTargetException e) {
//				throw new IllegalStateException("Problem with the given methodToGetOption \"" + _methodToGetOption + "\": ", e);
//			}
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getLabel(int)
	 */
	public String getLabel(int index) {
		if (_allowNoSelection) {
			if (index < _offset) {
				return _noSelectionMessage;
			}
		}
		Object o = _list.get(index - _offset);
		try {
			String label = _methodToGetLabel.invoke(o, (Object[]) null).toString();
			return label;
		}
		catch (IllegalArgumentException e) {
			throw new IllegalStateException("Problem with the given methodToGetLabel \"" + _methodToGetOption + "\": ", e);
		}
		catch (IllegalAccessException e) {
			throw new IllegalStateException("Problem with the given methodToGetLabel \"" + _methodToGetOption + "\": ", e);
		}
		catch (InvocationTargetException e) {
			throw new IllegalStateException	("Problem with the given methodToGetLabel \"" + _methodToGetOption + "\": ", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#getValue(int)
	 */
	public String getValue(int index) {
		
		return Integer.toString(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry.form.IPropertySelectionModel#translateValue(String)
	 */
	public Object translateValue(String value) {
		try{
		if (Integer.parseInt(value) < _offset)
		{
			return null;
		}}catch(Exception e)
		{
			return null;
		}
		Object obj = null;
		if(value != null)
		{
			int index = Integer.parseInt(value) -_offset;
			if(index >=0 && index < _list.size())
			{
				obj = _list.get(index);
			}
		}
		return obj;
	}

	public boolean isDisabled(int index)
	{
		return false;
	}
}
