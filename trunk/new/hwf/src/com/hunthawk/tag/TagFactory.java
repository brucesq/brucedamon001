/**
 * 
 */
package com.hunthawk.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.log4j.Logger;

import com.hunthawk.tag.config.PageConfig;
import com.hunthawk.tag.config.TagConfig;
import com.hunthawk.tag.config.TagConfigFactory;
import com.hunthawk.tag.pool.TagObjectPoolFactory;

/**
 * @author sunquanzhi
 *
 */
public class TagFactory {

	private static Map instances = new HashMap() ;
	private static Logger logger = Logger.getLogger(TagFactory.class);
	private TagConfigFactory tagconfig;
	
	private Map classMaps;
	public static synchronized TagFactory getInstance(String page)
	{
		if(instances.containsKey(page) )
		{
			return (TagFactory)instances.get(page);
		}else{
			TagFactory factory = new TagFactory(page);
			instances.put(page,factory);
			return factory;
		}
		
	}
	
	public List getPageTagset(String pagename)
	{
		PageConfig page =  tagconfig.getPageConfig(pagename);
		if(page != null)
			return page.getTagList();
		return new ArrayList();
	}
	public Tag getTagClass(String tagname)
	{
		ObjectPool pool = (ObjectPool)classMaps.get(tagname);
		try{
			return (Tag)pool.borrowObject();
		}catch(Exception e)
		{
			logger.error(tagname + " Not found !");
		}
		return null;
		
	}
	public void returnTag(String tagname,Tag tag)
	{
		ObjectPool pool = (ObjectPool)classMaps.get(tagname);
		try{
			pool.returnObject(tag);
		}catch(Exception e)
		{
			logger.error(tagname + " Not Return !");
		}
	}
	public String getTemplateClass()
	{
		return tagconfig.getTemplateClass();
	}
	private void init(String page)
	{
		logger.info("Init TagFactory");
		classMaps = new HashMap();
		tagconfig = TagConfigFactory.getInstance(page);
		Map map = tagconfig.getTagConfigs();
		Set keys = map.keySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext())
		{
			TagConfig config = (TagConfig)map.get((String)iter.next());
			try{
				Class tagclass = Class.forName(config.getClassname());
				//Tag tag = (Tag)tagclass.newInstance();
				//classMaps.put(config.getName(),tag);
				TagObjectPoolFactory poolFactory = new TagObjectPoolFactory(tagclass);
				ObjectPool pool = new StackObjectPool(poolFactory,20,3);
				classMaps.put(config.getName(),pool);
			}catch(Exception e)
			{
				e.printStackTrace();
				logger.error(e);
			}
			
		}
	}
	/**
	 * 
	 */
	public TagFactory(String page) {
		init(page);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
