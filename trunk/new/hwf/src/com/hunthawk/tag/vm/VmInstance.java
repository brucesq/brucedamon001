/**
 * 
 */
package com.hunthawk.tag.vm;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.hunthawk.tag.protocol.VersionHolder;


/**
 * @author sunquanzhi
 *
 */
public class VmInstance {
	private static VmInstance instance = new VmInstance();
	
	private static Logger logger = Logger.getLogger(VmInstance.class);
	
	public static VmInstance getInstance()
	{
		return instance;
	}
	private VmInstance()
	{
		try{
			Properties p = new Properties();
		    p.setProperty("file.resource.loader.path", this.getClass().getResource("/vm").getPath());
		    p.setProperty("runtime.log.logsystem.class","org.apache.velocity.runtime.log.Log4JLogChute");
		    p.setProperty("input.encoding","GBK");
		    p.setProperty("output.encoding","GBK");
		    p.setProperty("file.resource.loader.cache", "true");
		    p.setProperty( "file.resource.loader.modificationCheckInterval", "1800" );  
			Velocity.init(p);
		}catch(Exception e)
		{
			logger.error("Init Velocity Error", e);
		}
	}
	
	public String parseVM(Map<String,Object> map,Object obj)
	{
		
		VelocityContext context = new VelocityContext();
		for(String key : map.keySet())
		{
			context.put(key, map.get(key));
		}
		context.put("this", obj);
		try{
			String path = obj.getClass().getName();
			path = path.replaceAll("\\.", "/");
//			long startTime = System.currentTimeMillis();
			Template template = Velocity.getTemplate("/"+path+VersionHolder.getVersion().getSuffix());
//			long endTime = System.currentTimeMillis();
//			logger.info("获取模板时间："+(endTime-startTime));
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
//			startTime = System.currentTimeMillis();
//			logger.info("解析模板时间："+(startTime-endTime));
			return writer.getBuffer().toString();
		}catch(Exception e)
		{
			logger.error("Get Velocity Template Error", e);
			return "";
		}
		
		

	}

}
