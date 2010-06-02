/**
 * 
 */
package com.hunthawk.reader.pps.service;

/**
 * @author BruceSun
 *
 */
public interface KeyWordService {
	/**
	 * 替换内容中包含的敏感词
	 * @param content
	 * @param type 资源替换类型
	 * @return
	 */
	public String replace(String content,int type);
	
	/**
	 * 判断是否有敏感字
	 * @param content
	 * @return
	 */
	public boolean hasKeyWord(String content);
	

}
