/**
 * 
 */
package com.hunthawk.reader.pps.service;

/**
 * @author BruceSun
 *
 */
public interface ChapterService {

	
	/**
	 * ��ȡ�½�������ҳ������ҳ����
	 * @param chapterId
	 * @param pageNum ҳ�룬��1��ʼ
	 * @param size ��ҳ������500Ϊ��λ����1����500�֡�2����1000��
	 * @return object[0]����ҳ��Integer��,object[1]������String����
	 */
	public Object[] getEbookChapterContent(String chapterId,int pageNum,int size);
}
