/**
 * 
 */
package com.hunthawk.reader.pps;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.tag.protocol.VersionHolder;
import com.hunthawk.tag.vm.VmInstance;

/**
 * @author BruceSun
 * 
 */
public class DBVmInstance {

	private static DBVmInstance instance = new DBVmInstance();

	private static Logger logger = Logger.getLogger(DBVmInstance.class);

	public static DBVmInstance getInstance() {
		return instance;
	}

	private VelocityEngine ve = new VelocityEngine();

	private DBVmInstance() {
		try {
			Properties p = new Properties();
			p.setProperty("file.resource.loader.path", this.getClass()
					.getResource("/vm").getPath());
			p.setProperty("runtime.log.logsystem.class",
					"org.apache.velocity.runtime.log.Log4JLogChute");
			p.put("input.encoding", "GB2312");
			p.put("output.encoding", "GB2312");
			p.put("resource.loader", "srl");
			p.put("srl.resource.loader.class",
					"com.hunthawk.reader.pps.StringResourceLoader");
			ve.init(p);
		} catch (Exception e) {
			logger.error("Init Velocity Error", e);
		}
	}

	public  String parseVM(Map<String, Object> map, Object obj, TagTemplate tem) {

		VelocityContext context = new VelocityContext();
		for(Map.Entry<String, Object> entry : map.entrySet()){
			context.put(entry.getKey(), entry.getValue());
		}
		
		context.put("this", obj);
		if(tem == null){
			return VmInstance.getInstance().parseVM(map,obj);
		}
		try {
			String content = tem.getWmlContent();
			if (VersionHolder.getVersion().getSuffix().indexOf("html") > 0) {
				content = tem.getHtmlContent();
			}
			Template template = ve.getTemplate(content);

			StringWriter writer = new StringWriter();
			template.merge(context, writer);

			return writer.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Get Velocity DB Template Error", e);
			return "";
		}

	}
}
