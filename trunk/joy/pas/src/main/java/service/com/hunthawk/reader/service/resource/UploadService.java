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
 * ��д���ϴ�ʵ��
 * 
 * @author BruceSun
 * 
 */
public interface UploadService {

	/**
	 * ������ȨCSV�ļ�
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
	 * �ϴ�������Ϣ
	 * @param authorFile
	 * @param authorDir
	 * @param user
	 * @throws Exception
	 */
	public void uploadAuthor(String authorFile, String authorDir, UserImpl user)
			throws Exception;

	/**
	 * �ϴ���Դ
	 * 
	 * @param dirName
	 *            �ϴ����������ϵ�Ŀ¼
	 * @param csvFileName
	 *            csv�ļ���
	 * @param copyMap
	 *            ��Ȩ��Ϣ
	 * @param resourceType
	 *            ��Դ����
	 * @param errInfo
	 *            ������Ϣ
	 * @param user
	 *            �ϴ��û�
	 * @throws Exception
	 */
	public void uploadResource(String dirName, String csvFileName,
			Map<String, ResourceReferen> copyMap, Integer resourceType,
			ArrayList<String> errInfo, UserImpl user) throws Exception;

	/**
	 * ��ȡ��Դ���ļ�Ŀ¼
	 * 
	 * @param resourceId
	 * @param resourceType
	 * @return
	 */
	public String getResourceFileDir(String resourceId, Integer resourceType);

	/**
	 * ����ͼƬ�ü�
	 * 
	 * @param destDir
	 * @return
	 * @throws Exception
	 */
	public String resizeCoverFile(String destDir, String coverImg)
			throws Exception;

	/**
	 * ͬ��Ŀ¼
	 * @param directry
	 */
	public void rsyncDirectry(File directry);
	
	/**
	 * �ü��½�ͼƬ��Ŀǰ�ü�176��240���ֳߴ磬ԭͼΪ320
	 * @param destDir ͼƬĿ¼
	 * @param img ͼƬ����
	 * @param chapterId �½�ID
	 * return ��������ͼƬ����
	 */
	public String resizeChapterImage(String destDir,String img,String chapterId);
	
	public  String getVideoResourceDirectory(String resourceId);
}
