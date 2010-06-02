/**
 * <p>参数处理类</p>
 * @author sunquanzhi
 */

package com.hunthawk.tag.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hunthawk.tag.TagConstants;
import com.hunthawk.tag.TagUtil;

public class ParamUtil {

	public static String getParameter(HttpServletRequest request,
			String paramName) {
		String temp = request.getParameter(paramName);
		if (temp != null && !temp.equals("")) {
			return temp;
		} else {
			return null;
		}
	}

	public static String getParameter(HttpServletRequest request,
			String paramName, boolean emptyStringsOK) {
		String temp = request.getParameter(paramName);
		if (emptyStringsOK) {
			if (temp != null) {
				return temp;
			} else {
				return null;
			}
		} else {
			return getParameter(request, paramName);
		}
	}

	public static boolean getBooleanParameter(HttpServletRequest request,
			String paramName) {
		String temp = request.getParameter(paramName);
		if (temp != null && temp.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	public static int getIntParameter(HttpServletRequest request,
			String paramName, int defaultNum) {
		String temp = request.getParameter(paramName);
		if (temp != null && !temp.equals("")) {
			int num = defaultNum;
			try {
				num = Integer.parseInt(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	public static boolean getCheckboxParameter(HttpServletRequest request,
			String paramName) {
		String temp = request.getParameter(paramName);
		if (temp != null && temp.equals("on")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getAttribute(HttpServletRequest request,
			String attribName) {
		String temp = (String) request.getAttribute(attribName);
		if (temp != null && !temp.equals("")) {
			return temp;
		} else {
			return null;
		}
	}

	public static boolean getBooleanAttribute(HttpServletRequest request,
			String attribName) {
		String temp = (String) request.getAttribute(attribName);
		if (temp != null && temp.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	public static int getIntAttribute(HttpServletRequest request,
			String attribName, int defaultNum) {
		String temp = (String) request.getAttribute(attribName);
		if (temp != null && !temp.equals("")) {
			int num = defaultNum;
			try {
				num = Integer.parseInt(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	/**
	 * <p>
	 * url生成
	 * </p>
	 * 
	 * @param request
	 * @param params
	 *            新增加的参数集合
	 * @param page
	 *            页面
	 * @param blackName
	 *            禁用的参数
	 * @param mode
	 * @return
	 */
	public static String urlChange(HttpServletRequest request, Map params,
			String page, List blackName, int mode) {
		String url = "";
		String http = request.getRequestURL().toString();
		if (page != null && !page.equals("")) {
			http = page;
		}
		url = http + "?";
		if (blackName == null) {
			blackName = new ArrayList();
		}
		blackName.add("flag");
		blackName.add("tmpl");
		if (params != null) {
			Iterator iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				if (name != null && !name.equals("")) {
					String value = (String) params.get(name);
					url += name + "=" + value + "&";
					blackName.add(name);
				}
			}
		}

		Enumeration enume = request.getParameterNames();
		while (enume.hasMoreElements()) {
			String name = (String) enume.nextElement();
			if (name != null && !name.equals("")) {
				boolean isBlack = false;
				for (int i = 0; i < blackName.size(); i++) {
					if (name.equals((String) blackName.get(i))) {
						isBlack = true;
						break;
					}
				}
				if (!isBlack) {
					url += name + "=" + escapeJS(request.getParameter(name))
							+ "&";
				}
			}
		}
		if (url.charAt(url.length() - 1) == '&') {
			url = url.substring(0, url.length() - 1);
		}
		if (mode == 0) {
			url = url.replaceAll("&", "&amp;");
		}
		return url;
	}

	public static String urlChange(HttpServletRequest request,
			String changeName, String changeValue, int mode) {
		Map map = new HashMap();
		map.put(changeName, changeValue);
		List list = new ArrayList();
		list.add(TagConstants.PARA_PAGE_NAME);
		return urlChange(request, map, null, list, mode);
	}

	/**
	 * <p>
	 * 页面替换但是仅改变一个参数
	 * </p>
	 * 
	 * @param request
	 * @param changeName
	 * @param changeValue
	 * @param page
	 * @param mode
	 * @return
	 */
	public static String urlChange(HttpServletRequest request,
			String changeName, String changeValue, String page, int mode) {
		Map map = new HashMap();
		map.put(changeName, changeValue);
		List list = new ArrayList();
		list.add(TagConstants.PARA_PAGE_NAME);
		return urlChange(request, map, page, list, mode);
	}

	/**
	 * <p>
	 * 同级页面仅改变页码
	 * </p>
	 * 
	 * @param request
	 * @param changeName
	 * @param changeValue
	 * @param mode
	 * @return
	 */
	public static String urlChangeNoblack(HttpServletRequest request,
			String changeName, String changeValue, int mode) {
		Map map = new HashMap();
		map.put(changeName, changeValue);
		List list = new ArrayList();
		return urlChange(request, map, null, list, mode);
	}

	/**
	 * <p>
	 * 组装URL，request中提供URI,params提供参数
	 * </p>
	 * 
	 * @param request
	 * @param params
	 * @param mode
	 * @return
	 */
	public static String makeUrl(HttpServletRequest request, Map params,
			int mode) {
		String url = "";
		String http = request.getRequestURL().toString();
		url = http + "?";
		if (params != null) {
			Iterator iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				if (TagUtil.isNonBlank(name)) {
					String value = (String) params.get(name);
					url += name + "=" + value + "&";
				}
			}
		}
		if (url.charAt(url.length() - 1) == '&') {
			url = url.substring(0, url.length() - 1);
		}
		if (mode == 0) {
			url = url.replaceAll("&", "&amp;");
		}
		return url;
	}

	/**
	 * <p>
	 * 组装url地址
	 * </p>
	 * 
	 * @param params
	 * @param page
	 * @param parameter
	 * @param mode
	 * @return
	 */
	public static String makeUrl(Map params, String page, String parameter,
			int mode) {
		String url = "";

		url = page + "?";

		if (params != null) {
			Iterator iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				if (name != null && !name.equals("")) {
					String value = (String) params.get(name);
					url += name + "=" + value + "&";

				}
			}
		}
		if (parameter != null) {
			url += parameter;
		}

		if (url.charAt(url.length() - 1) == '&') {
			url = url.substring(0, url.length() - 1);
		}

		if (mode == 0) {
			url = url.replaceAll("&", "&amp;");
		}
		return url;
	}

	/**
	 * <p>
	 * 获得手机号码
	 * </p>
	 * 
	 * @param request
	 * @return
	 */
	public static String getMobile(HttpServletRequest request) {
		String mobile = "";

		mobile = request.getHeader("x-up-calling-line-id");

		if (mobile == null || mobile.equals("")) {
			mobile = request.getParameter("MISC_MSISDN");
		}

		if (mobile == null || mobile.trim().length() < 11)
			return "";
		if (mobile.startsWith("86") && mobile.length() == 13) {
			mobile = mobile.substring(2);
		}
		if (mobile.startsWith("+86") && mobile.length() == 14) {
			mobile = mobile.substring(3);
		}

		return mobile;
	}

	public static String getUa(HttpServletRequest request) {
		String uaStr = request.getHeader("user-agent");
		String result = "";
		try {
			if (uaStr == null || uaStr.trim().equals(""))
				return "";
			int n = 0;
			n = uaStr.indexOf("/");
			if (n != -1) {
				result = uaStr.substring(0, n);
			} else {
				result = uaStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result.length() > 255) {
			result = result.substring(0, 255);
		}
		return result;
	}

	public static String escapeJS(String pstrString) {
		StringBuffer escapeJS = new StringBuffer();

		/**
		 * Short circuit on empty strings.
		 */
		if (pstrString == null || pstrString.length() == 0) {
			return pstrString;
		}

		char[] chars = pstrString.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char x = chars[i];
			switch (x) {
			case '\'':
				escapeJS.append('\\').append(x);
				break;
			case '"':
				escapeJS.append('\\').append(x);
				break;
			case '\\':
				escapeJS.append('\\').append(x);
				break;
			case '<':
				escapeJS.append("&lt;");
				break;
			case '>':
				escapeJS.append("&gt;");
				break;
			case '&':
				escapeJS.append("&amp;");
				break;
			default:
				escapeJS.append(x);
				break;
			}
		}
		String result = escapeJS.toString();
		result = result.replaceAll("iframe", "");
		result = result.replaceAll("eval", "");
		result = result.replaceAll("alert", "");
		result = result.replaceAll("scrpit", "");
		return result;
	}

	public static void main(String[] args) {
		System.out.println(ParamUtil.escapeJS("eval(var s='sss';);alert(sss)"));
	}
}
