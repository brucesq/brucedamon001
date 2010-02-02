/**
 * 
 */
package com.hunthawk.framework.tapestry.form;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * @author BruceSun
 *
 */
public class ExStringPropertySelectionModel implements IPropertySelectionModel
{
	  private String[] _options;
	  
	  private boolean _allowNoSelection ;
	  
	  private int _offset;
	  
	  private String _noSelectionMessage;

	  public ExStringPropertySelectionModel(String[] options)
	  {
	        this(options,false,null);
	  }

	  public ExStringPropertySelectionModel(String[] options,boolean allowNoSelection,String noSelectionMessage)
	  {
	        _options = options;
	        _allowNoSelection = allowNoSelection;
	        _noSelectionMessage = noSelectionMessage;
	        if(_allowNoSelection)
	        {
	        	_offset = 1;
	        }else{
	        	_offset = 0;
	        }
	  }
	  
	    public int getOptionCount()
	    {
	        return _options.length+_offset;
	    }

	    public Object getOption(int index)
	    {
	    	return new Integer(index-_offset);
	    }

	    /**
	     * Labels match options.
	     */

	    public String getLabel(int index)
	    {
	    	if(_allowNoSelection)
	    	{
	    		if(index < _offset)
	    			return _noSelectionMessage;
	    	}
	        return _options[index-_offset];
	    }

	    

	    public String getValue(int index)
	    {
	        return Integer.toString(index-_offset);
	    }

	    public Object translateValue(String value)
	    {
	    	 return new Integer(value);
	    }
}
