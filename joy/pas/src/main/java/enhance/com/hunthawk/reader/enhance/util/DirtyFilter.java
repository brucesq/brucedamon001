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
	 * ��������Ƿ�����໰����������true,���򷵻�false.
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
	 * �������е��໰�滻���趨������
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
	 * ���������໰�����type=1��ֱ�ӷ��أ������໰�滻���趨������
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
	 * ��������д��ض�λ�ÿ�ʼ�������໰������������໰����null
	 * </p>
	 * 
	 * @param id
	 *            firstChar�е����
	 * @param content
	 *            �Ƚϵ�����
	 * @param index
	 *            ���ݵ���ʼλ��
	 * @param length
	 *            ���ݵĳ���
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
	 * ���������������ļ�
	 * </p>
	 * 
	 */
	public static void reload() {
		isLoad = false;
	}

	/**
	 * <p>
	 * �����������ļ�
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
		
		System.out.println(DirtyFilter.replace("a�ձ�AV��̨��AV������", "haha"));
	}

}

class CPath {
	protected InputStream getInputStream(String filename) {
		return this.getClass().getResourceAsStream(filename);
	}
}