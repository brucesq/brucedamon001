/**
 * 
 */
package com.hunthawk.reader.service.system;

import java.util.List;

import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;

/**
 * @author BruceSun
 *
 */
public interface KeyWordFilter {

	/**
	 * 获取内容中的关键词列表
	 * @param content
	 * @return
	 */
	public List<String> getContentKeyWord(String content,List<KeyWord> keyWordList);
	/**
	 * 将内容中的关键词高亮显示
	 * @param content
	 * @return
	 */
	public String highlight(String content,List<KeyWord> keyWordList);
	/**
	 * 判断内容中是否包含关键词
	 * @param content
	 * @return
	 */
	public boolean hasKeyWord(String content,List<KeyWord> keyWordList);

}
