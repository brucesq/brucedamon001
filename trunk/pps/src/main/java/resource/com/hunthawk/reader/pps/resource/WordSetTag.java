package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.vm.VmInstance;
/**
 * ◊÷ ˝…Ë÷√
 * @author liuxh
 *
 */
public class WordSetTag extends BaseTag {

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		int size=getIntParameter("wordset",ParameterConstants.CHAPTER_CONTENT_WORD_SET);
		String title=getParameter("title",String.valueOf(size));
		StringBuilder sb=new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		URLUtil.append(sb, ParameterConstants.PAGE, request);
		URLUtil.append(sb, ParameterConstants.PRODUCT_ID, request);
		URLUtil.append(sb, ParameterConstants.PAGEGROUP_ID, request);
		URLUtil.append(sb, ParameterConstants.AREA_ID, request);
		URLUtil.append(sb, ParameterConstants.COLUMN_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_ID, request);
		URLUtil.append(sb, ParameterConstants.FEE_BAG_RELATION_ID, request);
		URLUtil.append(sb, ParameterConstants.RESOURCE_ID, request);
		URLUtil.append(sb, ParameterConstants.CHAPTER_ID, request);
		URLUtil.append(sb, ParameterConstants.CHANNEL_ID, request);
		URLUtil.append(sb, ParameterConstants.UNICOM_PT, request);
		sb.append(ParameterConstants.PAGE_NUMBER);
		sb.append("=");
		sb.append(1);
		sb.append("&");
		//URLUtil.append(sb, ParameterConstants.PAGE_NUMBER, request);
		sb.append(ParameterConstants.WORDAGE);
		sb.append("=");
		sb.append(size);
		sb.append("&");
		URLUtil.append(sb, ParameterConstants.FEE_ID, request);
		URLUtil.trimURL(sb);
		
		Map velocityMap = new HashMap();
		velocityMap.put("title", title);
		velocityMap.put("url",sb.toString());
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName),VmInstance.getInstance().parseVM(velocityMap, this));
		return resultMap;	
	}

}
