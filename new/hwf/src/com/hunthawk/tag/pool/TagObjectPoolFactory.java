/**
 * 
 */
package com.hunthawk.tag.pool;

import org.apache.commons.pool.BasePoolableObjectFactory;

import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.ICallback;
import com.hunthawk.tag.util.PropertyUtil;

/**
 * @author sunquanzhi
 *
 */
public class TagObjectPoolFactory extends BasePoolableObjectFactory {

	private Class clasz;
	
	public TagObjectPoolFactory(Class clasz)
	{
		this.clasz = clasz;
	}
	/* (non-Javadoc)
	 * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
	 */
	@Override
	public Object makeObject() throws Exception {
		return clasz.newInstance();
		
	}
	@Override
	public void passivateObject(Object obj) throws Exception {
		if(obj instanceof ICallback)
		{
			((ICallback)obj).destroy();
		}
		if(obj instanceof BaseTag){
			BaseTag bTag = (BaseTag)obj;
			bTag.setParameter("");
		}else{
			PropertyUtil.setProperty(obj,"parameter","");
		}
		
		
	}
	@Override
	public void activateObject(Object obj)
	{
		if(obj instanceof ICallback)
		{
			((ICallback)obj).init();
		}
	}

}
