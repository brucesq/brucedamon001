/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * @author sunquanzhi
 *
 */
public class CurrentURLTag extends BaseTag {

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String url = URLUtil.getCurrentURL(request);
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),url);
		return resultMap;
	}
}
