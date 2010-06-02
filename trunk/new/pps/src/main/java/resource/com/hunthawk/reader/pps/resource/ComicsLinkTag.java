package com.hunthawk.reader.pps.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.components.wml.Anchor;
import com.hunthawk.tag.components.wml.Go;
import com.hunthawk.tag.components.wml.IPostfieldModel;
import com.hunthawk.tag.components.wml.SimplePostfieldModel;
/**
 * 漫画属性标签
 * 参数说明：
 * 		title:链接名称
 * @author liuxh
 *
 */
public class ComicsLinkTag extends BaseTag {

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub
		Map<String, String> res = new HashMap<String, String>();
		String title=getParameter("title","自动播放");
		//拼URL
		StringBuilder sb=new StringBuilder();
		sb.append(request.getContextPath());
		sb.append(ParameterConstants.PORTAL_PATH);
		sb.append("?");
		sb.append(request.getQueryString());
		sb.append("&");
		sb.append(ParameterConstants.AUTO_PLAY);
		sb.append("=");
		sb.append(1);
		StringBuilder builder=new StringBuilder();
		
		Go go = new Go();
		go.setCharset("UTF-8");
		go.setMethod("POST");
		go.setUrl(sb.toString());
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put(ParameterConstants.TIMER, "$T");
		//postMap.put(ParameterConstants.AUTO_PLAY, "1");
	
		IPostfieldModel model = new SimplePostfieldModel(postMap);

		go.setPostfieldModel(model);

		Anchor anchor = new Anchor();
		anchor.setGo(go);
		anchor.setTitle(title);
		anchor.setText(title);

		builder.append(anchor.renderComponent());

		res.put(ParameterConstants.PRE_TAG_SUFFIX + tagName
				+ ParameterConstants.END_TAG_SUFFIX, builder.toString());
		return res;
	}

}
