/**
 * 
 */
package com.hunthawk.tag;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sunquanzhi
 * @version 1.0
 *
 */
public interface Tag {
	public Map parseTag(HttpServletRequest request,String tagName);
	
}
