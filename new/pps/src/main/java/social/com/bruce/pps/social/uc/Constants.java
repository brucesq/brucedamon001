/**
 * 
 */
package com.bruce.pps.social.uc;

import java.util.Map;

import com.hunthawk.framework.util.OrderedMap;

/**
 * @author liuxh
 *
 */
public class Constants {

	private static Map<String, Integer> ITEM_INDEX = new OrderedMap<String, Integer>();
	static {
		ITEM_INDEX.put("nickname", 1);
		ITEM_INDEX.put("truename", 2);
		ITEM_INDEX.put("question", 3);
		ITEM_INDEX.put("answer", 4);
		ITEM_INDEX.put("level", 5);
		ITEM_INDEX.put("gender", 6);
		ITEM_INDEX.put("birthday", 7);
		ITEM_INDEX.put("constellation", 8);
		ITEM_INDEX.put("address", 9);
		ITEM_INDEX.put("sign", 10);
		ITEM_INDEX.put("headPic", 11);
		ITEM_INDEX.put("home", 12);
		ITEM_INDEX.put("height", 13);
		ITEM_INDEX.put("weight", 14);
		ITEM_INDEX.put("bodytype", 15);
		ITEM_INDEX.put("bloodtype", 16);
		ITEM_INDEX.put("enjoyBooktype", 17);
		ITEM_INDEX.put("personallty", 18);
		ITEM_INDEX.put("nativePlace", 19);
		ITEM_INDEX.put("education", 20);
		ITEM_INDEX.put("interest", 21);
		ITEM_INDEX.put("income", 22);
		ITEM_INDEX.put("job", 23);
		ITEM_INDEX.put("feeling", 24);
		ITEM_INDEX.put("maritalStatus", 25);
		
		ITEM_INDEX.put("isSmoking", 26);
		ITEM_INDEX.put("isDrinking", 27);
		ITEM_INDEX.put("introduction", 28);
		ITEM_INDEX.put("enjoyBody", 29);
		ITEM_INDEX.put("enjoyBook", 30);
		ITEM_INDEX.put("email", 31);
		ITEM_INDEX.put("qq", 32);
		ITEM_INDEX.put("msn", 33);
	}

	public static int index(String key_name){
		return ITEM_INDEX.get(key_name)==null?1:ITEM_INDEX.get(key_name);
	}
}
