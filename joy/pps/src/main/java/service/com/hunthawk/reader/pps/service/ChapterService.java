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
	 * 获取章节内容总页数及该页内容
	 * @param chapterId
	 * @param pageNum 页码，以1开始
	 * @param size 分页字数以500为单位，如1代表500字、2代表1000字
	 * @return object[0]是总页数Integer行,object[1]是内容String类型
	 */
	public Object[] getEbookChapterContent(String chapterId,int pageNum,int size);
}
