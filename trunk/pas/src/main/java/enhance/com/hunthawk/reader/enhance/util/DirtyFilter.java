/**
 * 
 */
package com.hunthawk.reader.enhance.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BruceSun
 * 
 */
@SuppressWarnings("unchecked")
public class DirtyFilter {

	private static boolean isLoad = false;
	
	private static Map map = new HashMap();
	private static char[] firstChar = null;

	/**
	 * <p>
	 * 检查内容是否包含脏话，包含返回true,否则返回false.
	 * </p>
	 * 
	 * @param content
	 * @return
	 */
	public static boolean check(String content) {
		if (process(content, 1, "") == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * 将内容中的脏话替换成设定的内容
	 * </p>
	 * 
	 * @param content
	 * @param replaceContent
	 * @return
	 */
	public static String replace(String content, String replaceContent) {
		return process(content, 2, replaceContent);
	}

	/**
	 * <p>
	 * 处理内容脏话，如果type=1则直接返回，否则将脏话替换成设定的内容
	 * </p>
	 * 
	 * @param content
	 * @param type
	 * @param replaceContent
	 * @return
	 */
	private static String process(String content, int type,
			String replaceContent) {
		if (!isLoad)
			init();
		StringBuffer buffer = new StringBuffer(content.length());
		int index = 0;
		int length = content.length();
		int start = 0;
		char ch;
		while (index < length) {
			ch = content.charAt(index);
			int i = 0;
			for (; i < firstChar.length; i++) {
				if (firstChar[i] == ch) {
					break;
				}
			}
			if (i < firstChar.length) {
				String line = getDirtyItem(i, content, index, length);
				if (line != null) {
					if (type == 1) {
						return null;
					} else {
						//buffer.append(content.substring(start, index));
						buffer.append(replaceContent);
						index = index + line.length() - 1;
						start = index + 1;
					}

				}else{
					buffer.append(ch);
				}
			}else{
				buffer.append(ch);
			}
			index++;
		}
		return buffer.toString();
	}

	/**
	 * <p>
	 * 获得内容中从特定位置开始包含的脏话，如果不包含脏话返回null
	 * </p>
	 * 
	 * @param id
	 *            firstChar中的序号
	 * @param content
	 *            比较的内容
	 * @param index
	 *            内容的起始位置
	 * @param length
	 *            内容的长度
	 * @return
	 */
	private static String getDirtyItem(int id, String content, int index,
			int length) {
		String key = (new Character(firstChar[id])).toString();
		List list = (List) map.get(key);
		for (int i = 0; i < list.size(); i++) {
			String line = (String) list.get(i);
			int ll = line.length();
			if (ll <= length - index) {
				if ((content.substring(index, index + ll)).compareTo(line) == 0) {
					return line;
				}
			}
		}
		return null;
	}

	/**
	 * <p>
	 * 重新载入脏数据文件
	 * </p>
	 * 
	 */
	public static void reload() {
		isLoad = false;
	}

	/**
	 * <p>
	 * 载入脏数据文件
	 * </p>
	 * 
	 */
	private static synchronized void init() {
		if (isLoad)
			return;
		isLoad = true;
		map.clear();
		InputStreamReader isreader = new InputStreamReader((new CPath())
				.getInputStream("/dirty.txt"));
		BufferedReader breader = new BufferedReader(isreader);
		String line = "";
		try {
			while ((line = breader.readLine()) != null) {
				if (line.length() > 0) {
					String firstStr = line.substring(0, 1);
					ArrayList arr = null;
					if (map.containsKey(firstStr)) {
						arr = (ArrayList) map.get(firstStr);
					} else {
						arr = new ArrayList();
						map.put(firstStr, arr);
					}
					arr.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Set set = map.keySet();
		firstChar = new char[set.size()];
		Iterator iter = set.iterator();
		int i = 0;
		while (iter.hasNext()) {
			firstChar[i] = ((String) iter.next()).charAt(0);
			i++;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println(DirtyFilter.replace("a日本AV和台湾AV啊啊啊", "haha"));
	}

}

class CPath {
	protected InputStream getInputStream(String filename) {
		return this.getClass().getResourceAsStream(filename);
	}
}