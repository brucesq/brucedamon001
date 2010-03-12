/**
 * 
 */
package com.hunthawk.framework.tapestry.callback;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.ICallback;

import com.hunthawk.framework.tapestry.EditPage;

/**
 * @author sunquanzhi
 *
 */
@SuppressWarnings("serial")
public class EditCallback extends PamsCallback {

	protected Object model;
	private boolean modelNew;
	
	public EditCallback(String pageName, Object model)
    {
        super(pageName);
        this.model = model;
    }
    
    public EditCallback(String pageName, Object model, boolean modelNew)
    {
    	this(pageName, model);
    	this.modelNew = modelNew;
    }
    public void performCallback(IRequestCycle cycle)
    {
        EditPage editPage = (EditPage)cycle.getPage(getPageName());
        editPage.setModel(model);
        cycle.activate(editPage);
    }

	public Object getModel()
	{
		return model;
	}

	public void setModel(Object model)
	{
		this.model = model;
	}

	public boolean isModelNew()
	{
		return modelNew;
	}

	public void setModelNew(boolean modelNew)
	{
		this.modelNew = modelNew;
	}
	@Override
	public boolean shouldReplace(ICallback callback)
	{
		if (callback instanceof EditCallback)
		{
			EditCallback editCallback = (EditCallback)callback;
			if (editCallback.isModelNew())
			{
				return true;
			}
			else
			{
				return this.equals(editCallback);
			}
		}
		else
		{
			return false;
		}
	}
}
