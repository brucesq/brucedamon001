/**
 * 
 */
package com.hunthawk.reader.service.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.UserImpl;

/**
 * 重写了上传实现
 * 
 * @author BruceSun
 * 
 */
public interface UploadService {

	/**
	 * 解析版权CSV文件
	 * 
	 * @param dirName
	 * @param dir
	 * @return
	 * @throws Exception
	 */
//	public Map<String, Integer> readCopyCsvFile(String dirName, String dir,
//			UserImpl user) throws Exception;
	public Map<String, ResourceReferen> uploadCopyright(String dirName,
			String dir, UserImpl user) throws Exception ;

	/**
	 * 上传作者信息
	 * @param authorFile
	 * @param authorDir
	 * @param user
	 * @throws Exception
	 */
	public void uploadAuthor(String authorFile, String authorDir, UserImpl user)
			throws Exception;

	/**
	 * 上传资源
	 * 
	 * @param dirName
	 *            上传到服务器上的目录
	 * @param csvFileName
	 *            csv文件名
	 * @param copyMap
	 *            版权信息
	 * @param resourceType
	 *            资源类型
	 * @param errInfo
	 *            错误信息
	 * @param user
	 *            上传用户
	 * @throws Exception
	 */
	public void uploadResource(String dirName, String csvFileName,
			Map<String, ResourceReferen> copyMap, Integer resourceType,
			ArrayList<String> errInfo, UserImpl user) throws Exception;

	/**
	 * 获取资源的文件目录
	 * 
	 * @param resourceId
	 * @param resourceType
	 * @return
	 */
	public String getResourceFileDir(String resourceId, Integer resourceType);

	/**
	 * 封面图片裁剪
	 * 
	 * @param destDir
	 * @return
	 * @throws Exception
	 */
	public String resizeCoverFile(String destDir, String coverImg)
			throws Exception;

	/**
	 * 同步目录
	 * @param directry
	 */
	public void rsyncDirectry(File directry);
	
	/**
	 * 裁剪章节图片，目前裁剪176，240两种尺寸，原图为320
	 * @param destDir 图片目录
	 * @param img 图片名称
	 * @param chapterId 章节ID
	 * return 处理完后的图片名称
	 */
	public String resizeChapterImage(String destDir,String img,String chapterId);
	
	public  String getVideoResourceDirectory(String resourceId);
}
