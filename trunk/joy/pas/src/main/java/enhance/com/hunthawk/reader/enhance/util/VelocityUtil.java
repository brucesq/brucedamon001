/**
 * 
 */
package com.hunthawk.reader.enhance.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * @author BruceSun
 *
 */
public class VelocityUtil {
	
	private static VelocityUtil instance = new VelocityUtil();
	
	private static Logger logger = Logger.getLogger(VelocityUtil.class);
	
	public static VelocityUtil getInstance(){
		return instance;
	}
	
	private VelocityUtil(){
		try{
			Properties p = new Properties();
		    p.setProperty("file.resource.loader.path", this.getClass().getResource("/velocity").getPath());
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
	
	public String parse(String filename,Map<String,Object> map){
		VelocityContext context = new VelocityContext();
		for(String key : map.keySet())
		{
			context.put(key, map.get(key));
		}
		try{
			Template template = Velocity.getTemplate("/"+filename);
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			return writer.getBuffer().toString();
		}catch(Exception e)
		{
			logger.error("Get Velocity Template Error", e);
			return "";
		}
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("fullPath", "OEBPS/content.opf");
//		map.put("mediaType", "application/oebps-package+xml");
		String content = VelocityUtil.getInstance().parse("OEBPS/cover.html", map);
		System.out.println(content);
		FileOutputStream fout = new FileOutputStream("d:\\1.txt");
		DataOutputStream out = new DataOutputStream(fout);
		out.write(content.getBytes("utf-8"));
		
		File file = new File("d:\\1.txt");
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		byte[] b = new byte[256];
		raf.seek(0);
		raf.read(b);
		System.out.println(new String(b,"utf-8"));
	}

}
