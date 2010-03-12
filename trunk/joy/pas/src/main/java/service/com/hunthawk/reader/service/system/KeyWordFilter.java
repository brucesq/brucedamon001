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
	 * ��ȡ�����еĹؼ����б�
	 * @param content
	 * @return
	 */
	public List<String> getContentKeyWord(String content,List<KeyWord> keyWordList);
	/**
	 * �������еĹؼ��ʸ�����ʾ
	 * @param content
	 * @return
	 */
	public String highlight(String content,List<KeyWord> keyWordList);
	/**
	 * �ж��������Ƿ�����ؼ���
	 * @param content
	 * @return
	 */
	public boolean hasKeyWord(String content,List<KeyWord> keyWordList);

}
