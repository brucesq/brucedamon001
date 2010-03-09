package com.hunthawk.reader.page;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.security.simple.MockSecurityContext;
import com.hunthawk.framework.security.simple.SimpleVisit;
import com.hunthawk.reader.domain.OffLineLog;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.RandomGUID;
import com.hunthawk.reader.enhance.util.UnzipFile;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

public class OfflineThread extends Thread {

	public static Integer status = 0;

	private SystemService systemService;
	private Integer mark;
	private InteractiveService interactiveService;
	private UploadService uploadService;
	private UserImpl user;

	public OfflineThread(SystemService systemService, Integer mark,
			InteractiveService interactiveService, UploadService uploadService,
			UserImpl user) {
		this.systemService = systemService;
		this.mark = mark;
		this.interactiveService = interactiveService;
		this.uploadService = uploadService;
		this.user = user;
	}

	public OfflineThread() {
	}

	public SystemService getSystemService() {
		return systemService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	public InteractiveService getInteractiveService() {
		return interactiveService;
	}

	public void setInteractiveService(InteractiveService interactiveService) {
		this.interactiveService = interactiveService;
	}

	public UploadService getUploadService() {
		return uploadService;
	}

	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	public UserImpl getUser() {
		return user;
	}

	public void setUser(UserImpl user) {
		this.user = user;
	}

	public boolean resIsRightFormat(String fileName) {
		String patt = "\\.(zip)$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}

	public void run() {
		System.out.println("------ִ�����߳�--------");
		MockSecurityContext context = new MockSecurityContext();
		SimpleVisit visit = new SimpleVisit();
		visit.setUser(user);
		context.setVisit(visit);
		SecurityContextHolder.setContext(context);
		FileOutputStream fos = null;
		String upFileTureName = "";
		/**
		 * 1.�õ��ϴ��ĵ�ַ
		 */
		String offLine_dir = systemService.getVariables("offLine_dir")
				.getValue();// �õ������ϴ���ַ
		// String offLine_dir = "C:/var/www/offLine";
		File offLine = new File(offLine_dir);
		String errormessage = "";
		try {

			/**
			 * 2.����offLine�ļ����µ����е���Դ����
			 */
			if (!offLine.exists()) {
				// throw new Exception("��ַ������,����ȷ���µ�ַ");
				offLine.mkdirs();// �������ȴ���
			}
			List<String> list = new ArrayList<String>();
			if (offLine.exists() && offLine.isDirectory()) {
				for (File file : offLine.listFiles()) {
					boolean resIsRightFormat = resIsRightFormat(file.getName());// ����zip�Ĳ��ӽ�ȥ
					if (resIsRightFormat || file.isDirectory())
						list.add(file.getName());
				}
			}

			if (list != null) {
				Collections.sort(list);// �����ļ���������
				for (String dirFileName : list) {
					String errInfo = "";

					// �ϴ���Դ�������ƣ���book.zip
					upFileTureName = dirFileName;

					{ // ��¼��־--��ʼ----
						OffLineLog log = new OffLineLog();
						log.setValue("��ʼ������" + upFileTureName + "�ļ���");
						log.setMark(mark);
						log.setStatus(0);
						log.setPackName(upFileTureName);
						interactiveService.addLog(log);
					}
					// ��ȡ��Դ���⿪���ļ������ƣ���book.zip��ѹ�󣬵õ�����book
					String uploadFileDir = upFileTureName.substring(0,
							upFileTureName.length() - 4);

					// ��ѹĿ¼
					String unUploadFileDir = offLine.toString()
							+ File.separator + uploadFileDir;
					// ��ѹ��Դ������
					// String unUploadFileName =
					// dir.toString()+"/"+uploadFile.getName();
					String unUploadFileName = offLine.toString()
							+ File.separator + upFileTureName;

					// ��ѹĿ¼
					File file2 = new File(unUploadFileName);
					File dir2 = new File(unUploadFileDir);
					if (file2.isDirectory()) {
						dir2 = file2;
						unUploadFileDir = unUploadFileName;
					} else {
						UnzipFile.unzip(file2, dir2);
					}

					// ��ȡĿ¼�µ��ļ�

					if (!containsDirectory(unUploadFileDir, "stream")) {
						// ������һ��Ŀ¼
						unUploadFileDir += File.separator
								+ getNextDirectory(unUploadFileDir);

					}

					if (!containsDirectory(unUploadFileDir, "stream")) {// ��չ����û��streamĿ¼
						{
							OffLineLog log = new OffLineLog();
							log
									.setValue("����Ŀ¼�ṹ���󣬽�ѹ���������layout,stream�������Ŀ¼!");
							log.setMark(mark);
							log.setStatus(2);
							log.setPackName(upFileTureName);
							interactiveService.addLog(log);
						}
						continue;
					}

					// �����Ȩ�ļ�
					Map<String, ResourceReferen> copyMap = processCopyRight(unUploadFileDir);
					// ����������Ϣ
					try {
						processAuthor(unUploadFileDir);

					} catch (Exception e) {
						errInfo += e.getMessage();
					}
					// ����ͼ��
					try {
						processResourceUpload(unUploadFileDir, copyMap,
								ResourceAll.RESOURCE_TYPE_BOOK, "book.csv");
					} catch (Exception e) {
						errInfo += e.getMessage();
					}

					try {
						processResourceUpload(unUploadFileDir, copyMap,
								ResourceAll.RESOURCE_TYPE_COMICS, "comics.csv");
					} catch (Exception e) {
						errInfo += e.getMessage();
					}

					try {
						processResourceUpload(unUploadFileDir, copyMap,
								ResourceAll.RESOURCE_TYPE_MAGAZINE,
								"magazine.csv");

					} catch (Exception e) {
						errInfo += e.getMessage();
					}
					try {
						processResourceUpload(unUploadFileDir, copyMap,
								ResourceAll.RESOURCE_TYPE_NEWSPAPER,
								"newspapers.csv");

					} catch (Exception e) {
						errInfo += e.getMessage();
					}

					try {
						processResourceUpload(unUploadFileDir, copyMap,
								ResourceAll.RESOURCE_TYPE_VIDEO, "video.csv");
					} catch (Exception e) {
						errInfo += e.getMessage();
					}

					{ // ��¼��־--����----
						OffLineLog log = new OffLineLog();
						log.setValue("�ļ���" + upFileTureName + "������ϡ�");
						log.setMark(mark);
						log.setStatus(3);
						log.setPackName(upFileTureName);
						interactiveService.addLog(log);
					}
					if (StringUtils.isNotEmpty(errInfo)) { // ������ǰ�ϴ������zip��û�д���,
						errormessage += errInfo;
						{ // ��¼��־--����----
							OffLineLog log = new OffLineLog();
							log.setValue("�ļ���" + upFileTureName + "������󣺡�"
									+ errInfo);
							log.setMark(mark);
							log.setStatus(2);
							log.setPackName(upFileTureName);
							interactiveService.addLog(log);
						}
					}
					// ɾ���ļ�
					cleanUploadDir(file2);
					cleanUploadDir(dir2);
				}
			}
			if (StringUtils.isNotEmpty(errormessage)) {
				throw new Exception(errormessage);
			}
		} catch (Exception ioe) {
			{ // ��¼��־--����----
				OffLineLog log = new OffLineLog();
				log.setValue("���д���" + errormessage);
				log.setMark(mark);
				log.setStatus(2);
				log.setPackName(upFileTureName);
				interactiveService.addLog(log);
			}
			// cleanUploadDir(offLine);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.getMessage();
				}
			}
			SecurityContextHolder.clearContext();
			status = 0;
		}

		// cleanUploadDir(offLine);
		// return false;
	}

	public void cleanUploadDir(File dir) {
		// ����ϴ��ļ�Ŀ¼��Ŀ¼�������ļ�
		try {
			if (dir.exists()) {
				UnzipFile.dircleanup(dir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ͼƬ��ʽ
	public boolean isRightFormat(String fileName) {
		String patt = "\\.(jpg|gif|png|bmp)$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * ����ϴ��ļ��Ĵ��Ŀ¼
	 */
	public File getUploadDir() {
		// �����ݿ�������Ϣ�еõ�����������ϴ��ļ���·��
		// Variables variables = getResourceService().getVariables("upload");
		// String uploadDir = variables.getValue();
		// ��GUID��Ϊ�ϴ��ļ��Ľ�ѹ��Ŀ¼����֤Ŀ¼Ψһ
		RandomGUID randomGuid = new RandomGUID();
		String guidStr = randomGuid.toString();
		Variables var = systemService.getVariables("upload_dir");
		// linux
		String uploadDir = var.getValue() + File.separator + guidStr;

		File unzipDir = new File(uploadDir);
		if (!unzipDir.exists())
			unzipDir.mkdirs();

		return unzipDir;
	}

	/**
	 * �ϴ��ļ�
	 * 
	 * @param fileName
	 * @param dir
	 */
	public void uploadFile(String fileName, File dir, InputStream is) {
		BufferedOutputStream dest = null;

		byte data[] = new byte[4916];

		try {

			File entryFile = new File(dir, fileName);
			int count;

			FileOutputStream fos = new FileOutputStream(entryFile);
			dest = new BufferedOutputStream(fos, 4916);
			while ((count = is.read(data, 0, 4916)) != -1) {
				dest.write(data, 0, count);

			}

			dest.flush();
			dest.close();
			fos.close();
			is.close();
		} catch (Exception e) {
			System.out.println("***:" + e.toString());

		} finally {

		}

	}

	/**
	 * �����Ȩ��Ϣ
	 * 
	 * @param unUploadFileDir
	 * @return
	 * @throws Exception
	 */
	public Map<String, ResourceReferen> processCopyRight(String unUploadFileDir)
			throws Exception {
		String csv = unUploadFileDir + File.separator + "stream"
				+ File.separator + "copyright.csv";
		File csvFile = new File(csv);
		if (!csvFile.exists()) {
			// logger.info(csv + " file not found.");
			{ // ��¼��־--��Ȩ�ļ�----
				OffLineLog log = new OffLineLog();
				log.setValue(unUploadFileDir + "��" + csv + " file not found.");
				log.setMark(mark);
				log.setStatus(2);
				log.setPackName(unUploadFileDir);
				interactiveService.addLog(log);
			}
			return new HashMap<String, ResourceReferen>();
		}
		Map<String, ResourceReferen> result = uploadService.uploadCopyright(
				unUploadFileDir + File.separator + "stream" + File.separator
						+ "copyright.csv", unUploadFileDir + File.separator
						+ "stream" + File.separator + "copyright", user);
		{ // ��¼��־--��Ȩ�ļ�----
			OffLineLog log = new OffLineLog();
			log.setValue("���ڴ���" + unUploadFileDir + "���İ�Ȩ�ļ�");
			log.setMark(mark);
			log.setStatus(1);
			log.setPackName(unUploadFileDir);
			interactiveService.addLog(log);
		}
		return result;
	}

	public void processAuthor(String unUploadFileDir) throws Exception {
		String csv = unUploadFileDir + File.separator + "stream"
				+ File.separator + "author.csv";
		File csvFile = new File(csv);
		System.out.println("interactiveService--" + interactiveService);
		System.out.println("user--" + user + "------" + user.getChName());
		if (!csvFile.exists()) {
			// logger.info(csv + " file not found.");
			{
				OffLineLog log = new OffLineLog();
				log.setValue(unUploadFileDir + "��" + csv + " file not found.");
				log.setMark(mark);
				log.setStatus(2);
				log.setPackName(unUploadFileDir);
				interactiveService.addLog(log);
			}
			return;
		}
		uploadService.uploadAuthor(csv, unUploadFileDir + File.separator
				+ "stream" + File.separator + "author", user);

		{
			OffLineLog log = new OffLineLog();
			log.setValue("���ڴ���" + unUploadFileDir + "���������ļ�");
			log.setMark(mark);
			log.setStatus(1);
			log.setPackName(unUploadFileDir);
			interactiveService.addLog(log);
		}
	}

	public void processResourceUpload(String unUploadFileDir,
			Map<String, ResourceReferen> copyMap, Integer resourceType,
			String filename) throws Exception {
		String csv = unUploadFileDir + File.separator + "stream"
				+ File.separator + filename;

		File csvFile = new File(csv);
		if (!csvFile.exists()) {
			{
				OffLineLog log = new OffLineLog();
				log.setValue(unUploadFileDir + "��" + csv + " file not found.");
				log.setMark(mark);
				log.setStatus(2);
				log.setPackName(unUploadFileDir);
				interactiveService.addLog(log);
			}
			return;
		}
		ArrayList<String> errInfo = new ArrayList<String>();
		uploadService.uploadResource(unUploadFileDir + File.separator
				+ "stream", filename, copyMap, resourceType, errInfo, user);
		{
			OffLineLog log = new OffLineLog();
			log.setValue("���ڴ���" + unUploadFileDir + "����������Ϣ");
			log.setMark(mark);
			log.setStatus(1);
			log.setPackName(unUploadFileDir);
			interactiveService.addLog(log);
		}
		if (errInfo.size() > 0) {
			StringBuilder errs = new StringBuilder();
			for (String err : errInfo) {
				errs.append(err);
				errs.append("\r\n");
			}
			{ // ��¼��־--��Ȩ�ļ�----
				OffLineLog log = new OffLineLog();
				log.setValue(unUploadFileDir + "������Ϣ���ֵ����⣺" + errs.toString());
				log.setMark(mark);
				log.setStatus(2);
				log.setPackName(unUploadFileDir);
				interactiveService.addLog(log);
			}
			throw new Exception(errs.toString());
		}
	}

	public boolean containsDirectory(String dir, String fileName) {
		File dirFile = new File(dir);
		if (dirFile.exists() && dirFile.isDirectory()) {
			for (String file : dirFile.list()) {
				if (file.equalsIgnoreCase(fileName)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getNextDirectory(String dir) {
		File dirFile = new File(dir);
		if (dirFile.exists() && dirFile.isDirectory()) {
			for (File file : dirFile.listFiles()) {
				if (file.isDirectory()) {

					return file.getName();
				}
			}
		}
		return "";
	}
}
