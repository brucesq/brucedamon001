package com.hunthawk.tag.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class LocalizationMessageFactory {
	private static LocalizationMessageFactory instance = new LocalizationMessageFactory();
	private Map<String, LocalizationMessage> map = new HashMap<String, LocalizationMessage>();
	private LocalizationMessageFactory(){}
	private static Logger log = Logger.getLogger(LocalizationMessageFactory.class);
	
	public static LocalizationMessageFactory getInstance(){
		return instance;
	}
	
	
	public synchronized LocalizationMessage getLocalizationMessage(String local) {
		String key = "message_" + local + ".properties";
		
		if(StringUtils.isEmpty(local)){
			key = "message.properties";
		}
		
		log.debug("key = " + key);
		if(!map.containsKey(key)) {
			LocalizationMessage message = LocalizationMessage.forObject(key);
			log.debug("message = " + message);
			if(null != message)
				map.put(key, message);
			else
				return (LocalizationMessage) map.get("message.properties");
		}
		log.debug("res = " + map.get(key));
		
		return (LocalizationMessage) map.get(key);
	}
	
//	public static void main(String[] args){
//		LocalizationMessageFactory factory = LocalizationMessageFactory.getInstance();
//		
//		LocalizationMessage msg1 = factory.getLocalizationMessage("EN");
//		System.out.println("____" + msg1.getMessage("nextPage"));
//		LocalizationMessage msg2 = factory.getLocalizationMessage(null);
//		System.out.println("____" + msg2.getMessage("nextPage"));
//	}
}
