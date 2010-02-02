/**
 * 
 */
package com.hunthawk.framework.util;

import com.hunthawk.reader.domain.Constants;

/**
 * @author BruceSun
 *
 */
@SuppressWarnings("unchecked")
public class Utility {

	
	public static String getMemcachedKey(Class clasz,String... args){
		StringBuilder builder = new StringBuilder();
		builder.append(clasz.getName());
		for(String key : args){
			builder.append( Constants.MEMCACHED_SLASH );
			builder.append(key);
		}
		return builder.toString();
	}
	
	public static void main(String[] args){
		
	}
}
