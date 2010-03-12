/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.KeyWordService;

/**
 * @author BruceSun
 * 
 */
public class KeyWordServiceImpl implements KeyWordService {

	private int reloadInterval = 60 * 60 * 1000;

	private Long lastReloadTime = null;

	private Map map = new HashMap();

	private char[] firstChar = null;

	private static Object lock = new Object();

	private HibernateGenericController controller;
	private BussinessService bussinessService;

	public void setHibernateGenericController(
			HibernateGenericController controller) {
		this.controller = controller;
	}

	public void setBussinessService(BussinessService bussinessService) {
		this.bussinessService = bussinessService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hunthawk.reader.pps.service.KeyWordService#replace(java.lang.String,
	 *      int)
	 */
	public String replace(String content, int type) {
		int isGuan = 0;
		try {
			isGuan = Integer.parseInt(bussinessService.getVariables(
					"pps_keyword_switch").getValue());
		} catch (Exception e) {
		}
		
		if (isGuan == 0) {
			return content;
		}
		if (type == 0 && isGuan == 2) {
			return content;
		}
		judgeLoad();
		String replaceWord = "***";
		try {
			replaceWord = bussinessService.getVariables("pps_keyword_replace")
					.getValue();
		} catch (Exception e) {
		}
		return process(content, replaceWord);

	}

	private void judgeLoad() {
		Long currentTime = System.currentTimeMillis();
		if (lastReloadTime == null
				|| (currentTime - lastReloadTime) > reloadInterval) {
			synchronized (lock) {
				if (lastReloadTime == null
						|| (currentTime - lastReloadTime) > reloadInterval) {
					lastReloadTime = currentTime;
					load();
				}
			}

		}
	}

	private void load() {
		int groupId = 0;
		try {
			groupId = Integer.parseInt(bussinessService.getVariables(
					"pps_keyword_group_id").getValue());
		} catch (Exception e) {
		}
		KeyWordType wordType = new KeyWordType();
		wordType.setId(groupId);
		List<KeyWord> keywords = controller.findBy(KeyWord.class, "type",
				wordType);
		
		map.clear();
		for (KeyWord word : keywords) {
			String line = word.getKeyWord();
			if (StringUtils.isEmpty(line))
				continue;
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
		Set set = map.keySet();
		firstChar = new char[set.size()];
		Iterator iter = set.iterator();
		int i = 0;
		while (iter.hasNext()) {
			firstChar[i] = ((String) iter.next()).charAt(0);
			i++;
		}
	}

	private String process(String content, String replaceContent) {
		StringBuffer buffer = new StringBuffer(content.length());
		int index = 0;
		int length = content.length();

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
					buffer.append(replaceContent);
					index = index + line.length() - 1;
				} else {
					buffer.append(ch);
				}
			} else {
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
	private String getDirtyItem(int id, String content, int index, int length) {
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
}
