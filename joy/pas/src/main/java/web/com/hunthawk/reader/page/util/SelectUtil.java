package com.hunthawk.reader.page.util;

import java.util.ArrayList;
import java.util.List;

public class SelectUtil {
	static final int[] ITEM_CHECKED_KEY = { 0, 1, 2, 3, 4 };

	public static final String[] MSG_STATUSE_VALUE = { "待审", "发布未审", "发布已审",
			"已屏蔽", "已删除" };

	public static final int[] ITEM_REASON_KEY = { 0, 1, 2, 3, 4 };

	public static final String[] MSG_REASON_VALUE = { "未屏蔽", "黑名单", "重复留言",
			"敏感留言", "后台屏蔽" };

	public static List<SelectKeyValuePO> getItemCheckedList() {
		List<SelectKeyValuePO> result = new ArrayList();
		for (int i = 0; i < ITEM_CHECKED_KEY.length - 1; i++) {
			SelectKeyValuePO po = new SelectKeyValuePO();
			po.setId(ITEM_CHECKED_KEY[i]);
			int vInt = (int) ITEM_CHECKED_KEY[i];
			po.setLabel(MSG_STATUSE_VALUE[vInt]);
			result.add(po);
		}
		return result;
	}

	public static List<SelectKeyValuePO> getItemReasonList() {
		List<SelectKeyValuePO> result = new ArrayList();
		for (int i = 0; i < ITEM_REASON_KEY.length; i++) {
			SelectKeyValuePO po = new SelectKeyValuePO();
			po.setId(ITEM_REASON_KEY[i]);
			int vInt = (int) ITEM_REASON_KEY[i];
			po.setLabel(MSG_REASON_VALUE[vInt]);
			result.add(po);
		}
		return result;
	}

	public static String getCheckedValue(Integer key) {
		if (key == null || key < 0 || key >= MSG_STATUSE_VALUE.length)
			return null;
		else
			return MSG_STATUSE_VALUE[(int) key];
	}

	public static String getReasonValue(Integer key) {
		
		if (key == null || key < 0 || key >= MSG_REASON_VALUE.length)
			return null;
		else
			return MSG_REASON_VALUE[(int) key];
	}
}
