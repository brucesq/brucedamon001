/**
 * 
 */
package com.hunthawk.tag.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * @author sunquanzhi
 *
 */
public class TagConfigFactory {

	private static Logger logger = Logger.getLogger(TagConfigFactory.class);
	private static Map instances = new HashMap();
	
	private Map tags;
	private Map pages;
	private String templateClass = "";
	/**
	 * 
	 */
	public TagConfigFactory() {
		tags = new HashMap();
		pages = new HashMap();
	}
	public void addPageConfig(PageConfig page)
	{
		pages.put(page.getName(),page);
	}
	public void addTagConfig(TagConfig tag)
	{
		
		tags.put(tag.getName(),tag);
	}
	public TagConfig getTagConfig(String name)
	{
		if(tags.containsKey(name))
		{
			return (TagConfig)tags.get(name);
		}else{
			return null;
		}
	}
	public void setTemplate(String template)
	{
		this.templateClass = template;
	}
	public String getTemplateClass()
	{
		return this.templateClass;
	}
	public PageConfig getPageConfig(String name)
	{
		if(pages.containsKey(name))
		{
			return (PageConfig)pages.get(name);
		}else{
			return null;
		}
	}
	public Map getTagConfigs()
	{
		return tags;
	}
	public Map getPageConfigs()
	{
		return pages;
	}
	public synchronized static TagConfigFactory getInstance(String page)
	{
		if(instances.containsKey(page))
		{
			return (TagConfigFactory)instances.get(page);
		}else{
			TagConfigFactory factory = loadConfigs(page);
			if(factory != null)
			{
				instances.put(page,factory);
			}
			return factory;
		}
		
	
	}
	
	private static TagConfigFactory  loadConfigs(String page)
	{
		logger.info("Loading tag configs !");
		
		Digester digester = new Digester();
        digester.setValidating( false );
        digester.addObjectCreate("configs",TagConfigFactory.class);
        digester.addBeanPropertySetter("configs/template");
        digester.addObjectCreate("configs/tag",TagConfig.class);
        digester.addObjectCreate("configs/page",PageConfig.class);
        
        digester.addBeanPropertySetter("configs/tag/name");
        digester.addBeanPropertySetter("configs/tag/classname");
        digester.addBeanPropertySetter("configs/tag/desc");
        digester.addSetNext("configs/tag","addTagConfig");
        
        digester.addBeanPropertySetter("configs/page/name");
        digester.addBeanPropertySetter("configs/page/tagset");
        digester.addSetNext("configs/page","addPageConfig");
        
        
        try{
//        	TagConfigFactory configs = (TagConfigFactory)digester.parse((new Path()).getInputStream("/"+page));
//        	return configs;
        	TagConfigFactory configs = new TagConfigFactory();
        	File[] files = ResourceConfigUtils.getResource((new Path()).getResouceURL("/").getFile(), page);
        	for(File file : files)
        	{
        		TagConfigFactory config = (TagConfigFactory)digester.parse(new FileInputStream(file));
        		if(StringUtils.isNotBlank(config.getTemplateClass()))
        		{
        			configs.setTemplate(config.getTemplateClass());
        		}
        		configs.getTagConfigs().putAll(config.getTagConfigs());
        		configs.getPageConfigs().putAll(config.getPageConfigs());
        	}
        	return configs;
        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        	logger.error(e);
        	return new TagConfigFactory();
        }
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//	TagConfigFactory factory = TagConfigFactory.getInstance();
	//	TagConfig config = factory.getTagConfig("#topdoc#");
	//	System.out.println(config.getClassname());
	//	PageConfig p = factory.getPageConfig("list.jsp");
	//	List list = p.getTagList();
	//	System.out.println((String)list.get(0)+list.size());
	}

}
class Path 
{
	protected InputStream getInputStream(String filename)
	{
		return this.getClass().getResourceAsStream(filename);	
	}
	protected URL getResouceURL(String path)
	{
		return this.getClass().getResource(path);	
	}
}
