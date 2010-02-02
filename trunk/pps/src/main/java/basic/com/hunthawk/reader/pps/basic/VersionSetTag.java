/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.tag.BaseTag;

/**
 * @author BruceSun
 *
 */
public class VersionSetTag extends BaseTag {
	
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		String versionType = request.getParameter(ParameterConstants.VERSION_TYPE);
		if(versionType != null && StringUtils.isNumeric(versionType)){
			RequestUtil.addCookie(ParameterConstants.VERSION_SET, versionType);
		}
		return new HashMap();
	}

	
}
