/**
 * 
 */
package com.hunthawk.reader.page.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





/**
 * 为页面操作提供公共的方法
 * 
 * @author x_dingjiangtao_a
 * 
 */
public class PageUtil {
	/**
	 * 将字符串按指定的分割符转化成list
	 * 
	 * @param str
	 *            要转化的字符串
	 * @param splitStr
	 *            分割符
	 * @return
	 */
	public static List stringtoList(String str, String splitStr) {
		List strList = new ArrayList();
		if (str == null || str.length() == 0) {
			return strList;
		}

		String[] strGroup = str.split(splitStr);

		for (int i = 0; i < strGroup.length; i++) {
			strList.add(strGroup[i]);
		}

		return strList;
	}

	/**
	 * 将list转化成字符串
	 * 
	 * @param list
	 * @param splitStr
	 * @return
	 */
	public static String listtoString(List list, String splitStr) {
		String listStr = "";
		if (list == null || list.size() == 0) {
			return listStr;
		} else {
			int listSize = list.size();
			for (int i = 0; i < listSize; i++) {
				if (listSize == 1) {
					listStr = (String) list.get(i);
				} else {
					if (i < listSize - 1) {
						listStr += list.get(i) + splitStr;
					} else {
						listStr += list.get(i);
					}
				}

			}
			return listStr;
		}

	}

	public static Map getMapFormString(String str) {
		Map map = new LinkedHashMap();
		if (str == null || str.length() == 0) {
			return map;
		}

		String[] strPair = str.split(";");

		for (int i = 0; i < strPair.length; i++) {
			int location = strPair[i].indexOf("#");
			SearchField search = new SearchField();
			map.put(strPair[i].substring(location + 1), strPair[i].substring(0,
					location));

		}

		return map;

	}

	public static String getTitleFormName(String name, Map map) {
		Set keySet = map.keySet();

		Iterator it = keySet.iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) map.get(key);

			if (value.equals(name)) {
				return key;

			}
		}
		return name;

	}

	/**
	 * 将字符传转化成int数组
	 * 
	 * @param str
	 * @param splitStr
	 * @return
	 */
	public static int[] strtoInt(String str, String splitStr) {
		if (str == null || str.equals("")) {
			return null;
		} else {

			String[] intStr = str.split(splitStr);
			int[] strInt = new int[intStr.length];
			for (int i = 0; i < intStr.length; i++) {
				strInt[i] = Integer.parseInt(intStr[i]);
			}

			return strInt;
		}

	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String getURLStream(String preurl) {
		StringBuffer buffer = new StringBuffer();

		try {
			URL url = new URL(preurl);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setDoOutput(false);

			InputStream in = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));

			String line = "";

			while ((line = reader.readLine()) != null)

			{

				buffer.append(line);

			}

			reader.close();

			in.close();

		} catch (Exception e)

		{

			// System.out.println(e);
			return "";

		}
		return buffer.toString();

	}

	/**
	 * 
	 * @author dingjiangtao_a Mar 19, 2008 5:41:12 PM
	 * @version 1.0
	 * @param preurl
	 * @param context
	 * @return
	 */
	public static String getURLStream(String preurl, String context) {

		String content = getURLStream(preurl);
		content = content.replaceAll("wap.i139.cn/pams", "pams.i139.cn/front");
		content = content.replaceAll("js.i139.cn/pams", "pams.i139.cn/front");
		content = content
				.replaceAll("mnews.i139.cn/pams", "pams.i139.cn/front");
		content = content.replaceAll("wapnews.i139.cn/pams",
				"pams.i139.cn/front");
		content = content.replaceAll("p.i139.cn/pams", "pams.i139.cn/front");
		return content.replaceAll("<a.+?href=\"",
				"<a href=\"" + context + "/viewwml.jsp?sp=").replaceAll(
				"<a.+?href='", "<a href='" + context + "/viewwml.jsp?sp=");
	}


	/**
	 * 执行对象的方法
	 * 
	 * @param businessObject
	 * @param methodName
	 * @param params
	 * @return
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	public static Object getRightObject(Object businessObject,
			String methodName, int params) throws Exception,
			NoSuchMethodException {

		Method method = businessObject.getClass().getDeclaredMethod(methodName,
				int.class);
		return method.invoke(businessObject, params);
	}

	public static String getFormatDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	/**
	 * 
	 * @param searchMap
	 * @param object
	 */
	public static void invokeMap(Map searchMap, Object object) {
		try {
			if (searchMap != null) {
				// 获得Map中Key的集合
				Set keySet = searchMap.keySet();

				// System.out.println("*******size*****:" + keySet.size());

				// key的迭代器
				Iterator keyIt = keySet.iterator();

				while (keyIt.hasNext()) {
					String key = (String) keyIt.next();
					Object keyValue = searchMap.get(key);

					// System.out.println("key****:" + key);
					// System.out.println("value****:" + keyValue);

					key = (key.substring(0, 1)).toUpperCase()
							+ key.substring(1);

					Method m = null;
					if (keyValue instanceof Integer) {
						m = object.getClass().getMethod("set" + key, int.class);
					}

					else {
						if (keyValue != null) {
							m = object.getClass().getMethod("set" + key,
									keyValue.getClass());
						}
					}

					if (m != null) {
						Object[] on = new Object[] { keyValue };
						m.invoke(object, on);
					}

				}
			}

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

	/**
	 * 解析字符串返回一个Map
	 * 
	 * @author xiajianci Jan 10, 2008 4:16:06 PM
	 * @version 1.0
	 * @param str
	 *            要解析的字符串 如:key1=value1;key2=value2;
	 * @param firstDelim
	 *            字符串的大分割符 即;
	 * @param secondDelim
	 *            字符串的小分割符 即=
	 * @return Map
	 */
	public static Map getMapFormString(String str, String firstDelim,
			String secondDelim) {
		Map map = new LinkedHashMap();
		if (str == null || str.length() == 0) {
			return map;
		}

		String[] strPair = str.split(firstDelim);

		for (int i = 0; i < strPair.length; i++) {
			int location = strPair[i].indexOf(secondDelim);
			SearchField search = new SearchField();
			map.put(strPair[i].substring(location + 1), strPair[i].substring(0,
					location));

		}

		return map;

	}

	
	/**
	 * 验证密码强度是否符合要求
	 * 
	 * @author dingjiangtao_a Aug 18, 2008 2:38:55 PM
	 * @version 1.0
	 * @param password
	 * @return
	 */
	public static boolean validatePassword(String password) {
		int passwrodIntensity = 0;

		/** **************验证密码的长度**************** */

		if (password.length() >= 15) {
			passwrodIntensity++;
		}
		/** *************end************************** */

		/** ************验证是否包含字母****************** */
		Pattern charP = Pattern.compile("[a-zA-Z]");

		Matcher charM = charP.matcher(password);

		if (charM.find()) {
			passwrodIntensity++;
		}

		/** ************end*************************** */

		/** **********验证是否包含数字******************* */
		Pattern numberP = Pattern.compile("[0-9]");

		Matcher numberM = numberP.matcher(password);

		if (numberM.find()) {
			passwrodIntensity++;
		}

		/** *********end***************************** */

		/** *************验证是否包含特殊字符************** */

		Pattern specialtiesP = Pattern.compile("[^a-zA-Z0-9]");

		Matcher specialtiesM = specialtiesP.matcher(password);

		if (specialtiesM.find()) {
			passwrodIntensity++;
		}

		/** ******************end*********************** */

		if (passwrodIntensity == 4) {
			return true;
		}

		return false;

	}

	public static String getDomainName(String url) {
		int pos1 = url.indexOf("//");

		url = url.substring(pos1 + 2, url.length());

		int pos2 = url.indexOf("/");
		url = url.substring(0, pos2);
		return url;
	}
}
