/**
 * 
 */
package com.hunthawk.reader.pps.basic;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;
import com.hunthawk.tag.process.Refresh;

/**
 * @author sunquanzhi
 *
 */
public class RedirectResultTag  extends BaseTag {
	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		int timer = getIntParameter("timer",1);
		String gourl = request.getParameter("gourl");
		String title = getParameter("title", "点击进入");// 链接文字
		gourl = new String(Base64.decodeBase64(gourl.getBytes()));
		StringBuilder sb = new StringBuilder();
		sb.append(request.getContextPath());
		sb.append("/redirect?gourl=");
		try {
			sb.append(URLEncoder.encode(gourl, "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Refresh.pageRefresh(timer, sb.toString());
		String content = "<a href=\"" + sb.toString() + "\">" + title + "</a>";
		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), content);
		return resultMap;
		
	}

}
