package com.hunthawk.tag.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;


public class LocalizationMessage {
	private String fileName = null;
	private Date lastModifiedDate = null;
	private Properties message = null;
	private static Logger log = Logger.getLogger(LocalizationMessage.class);
	
	private LocalizationMessage(String fileName){
		this.fileName = fileName;
	}
	
	public static LocalizationMessage forObject(String fileName){
		LocalizationMessage res  = new LocalizationMessage(fileName);
		res.getMessage("hello");
		if(!res.isInit()){
			res = null;
		}
		log.debug("res = " + res);
		return res;
	}
	
	public boolean isInit() {
		return (message == null? false : true);
	}
	private boolean checkVaild(){
		URL url = this.getClass().getResource("/" + fileName);
		log.debug("URL = " + url);
		if(null == url){
			message = null;
			return false;
		}
		
		String path = url.getPath();
		File file = new File(path);
		if (null == lastModifiedDate
				|| lastModifiedDate.before(new Date(file.lastModified()))) {
			lastModifiedDate = new Date(file.lastModified());
			load();
		} 
		
		return true;
	}
	
	public String getMessage(String key) {
		if(checkVaild()) 
			return message.getProperty(key);
		else
			return null;	
	}
	
	public Set keySet() {
		return message.keySet();
	}
	
	private void load() {
		String path = this.getClass().getResource("/" + fileName).getPath();
		InputStream in = null;
		try {
			in = new FileInputStream(path);
			if(null == message)
				message = new Properties();
			else
				message.clear();
			
			message.load(in);
		} catch (FileNotFoundException e) {
			message = null;
			log.error(path + " ≤ª¥Ê‘⁄");
		} catch (IOException e) {
			message = null;
			log.error(path + " ∂¡»°¥ÌŒÛ");
		}finally{
			if(null != in)
				try {
					in.close();
				} catch (IOException e) {
					log.error(path + " πÿ±’¥ÌŒÛ");
				}
		}
	}	
}