package com.hunthawk.reader.page.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.RandomGUID;
import com.hunthawk.reader.enhance.util.UnzipFile;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * �����޸��ϴ�ҳ��
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "uploadresource" }, mode = Restrict.Mode.ROLE)
public abstract class EditResourcePage extends SecurityPage {

	@SuppressWarnings("unused")
	private String resourceId;
	private File uploadFile = null;// �����ϴ������ص�

	@SuppressWarnings("unchecked")
	public abstract IUploadFile getUploadFile();

	public abstract File getServerFile();

	public abstract void setServerFile(File file);

	@InjectObject("spring:uploadService")
	public abstract UploadService getUploadService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	public void saveAndReturn(IRequestCycle cycle) {

		if (getUploadFile() != null) {
			String fileName = getUploadFile().getFileName().substring(
					getUploadFile().getFileName().lastIndexOf("\\") + 1);
			InputStream fis = getUploadFile().getStream();
			FileOutputStream fos = null;
			String upFileTureName = "";
			File dir = getUploadDir();
			try {
				boolean resIsRightFormat = resIsRightFormat(getUploadFile()
						.getFileName());
				if (!resIsRightFormat) {
					// �ļ���ʽ�ж�
					throw new Exception("�ļ���ʽ����ȷ[ֻ��Ϊzip]��������ѡ��!");

				}
				// �ļ������ڱ���
				uploadFile = new File(dir + File.separator + fileName);
				uploadFile(fileName, dir, fis);
				File entryFile = new File(dir, fileName);
				setServerFile(entryFile);

				// �ϴ���Դ�������ƣ���book.zip
				upFileTureName = uploadFile.getName();

				logger.info("[page=editResourcePage,info=��Դ���ļ��ϴ����*****"
						+ upFileTureName + "]");
				// ��ȡ��Դ���⿪���ļ������ƣ���book.zip��ѹ�󣬵õ�����book
				String uploadFileDir = upFileTureName.substring(0,
						upFileTureName.length() - 4);

				// ��ѹĿ¼
				String unUploadFileDir = dir.toString() + File.separator
						+ uploadFileDir;
				// ��ѹ��Դ������
				// String unUploadFileName =
				// dir.toString()+"/"+uploadFile.getName();
				String unUploadFileName = dir.toString() + File.separator
						+ upFileTureName;

				// ��ѹĿ¼
				File file2 = new File(unUploadFileName);
				File dir2 = new File(unUploadFileDir);
				UnzipFile.unzip(file2, dir2);

				// ��ȡĿ¼�µ��ļ�

				if (!containsDirectory(unUploadFileDir, "stream")) {
					// ������һ��Ŀ¼
					unUploadFileDir += File.separator
							+ getNextDirectory(unUploadFileDir);

				}

				if (!containsDirectory(unUploadFileDir, "stream")) {// ��չ����û��streamĿ¼
					throw new Exception("����Ŀ¼�ṹ���󣬽�ѹ���������layout,stream�������Ŀ¼!");

				}

				// ��ȡbook.csv,copyright.csv�ļ�
				// if(uDirThree.indexOf("")!=-1 ||
				// uDirThree.indexOf("")!=-1){
				// �����Ȩ�ļ�
				Map<String, ResourceReferen> copyMap = processCopyRight(unUploadFileDir);

				String errInfo = "";
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
							ResourceAll.RESOURCE_TYPE_MAGAZINE, "magazine.csv");
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
							ResourceAll.RESOURCE_TYPE_VIDEO,
							"video.csv");
				} catch (Exception e) {
					errInfo += e.getMessage();
				}
				if(StringUtils.isNotEmpty(errInfo)){
					throw new Exception(errInfo);
				}

			} catch (Exception ioe) {
				getDelegate().setFormComponent(null);
				getDelegate().record(ioe.getMessage(), null);
				logger.error("�ϴ�����", ioe);
//				cleanUploadDir(dir);
				return;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException ioe) {
						ioe.getMessage();
					}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ioe) {
						ioe.getMessage();
					}
				}
			}
			cleanUploadDir(dir);
		}

		cycle.activate(getShowEbookPage());
	}

	private void cleanUploadDir(File dir) {
		// ����ϴ��ļ�Ŀ¼��Ŀ¼�������ļ�
		try {
			UnzipFile.dircleanup(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage getShowEbookPage();

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

	// ��Դ����ʽ
	public static boolean resIsRightFormat(String fileName) {
		String patt = "\\.(zip)$";
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
	private File getUploadDir() {
		// �����ݿ�������Ϣ�еõ�����������ϴ��ļ���·��
		// Variables variables = getResourceService().getVariables("upload");
		// String uploadDir = variables.getValue();
		// ��GUID��Ϊ�ϴ��ļ��Ľ�ѹ��Ŀ¼����֤Ŀ¼Ψһ
		RandomGUID randomGuid = new RandomGUID();
		String guidStr = randomGuid.toString();
		Variables var = getSystemService().getVariables("upload_dir");
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
	protected Map<String, ResourceReferen> processCopyRight(
			String unUploadFileDir) throws Exception {
		String csv = unUploadFileDir + File.separator + "stream"
				+ File.separator + "copyright.csv";
		File csvFile = new File(csv);
		if (!csvFile.exists()) {
			logger.info(csv + " file not found.");
			return new HashMap<String, ResourceReferen>();
		}
		Map<String, ResourceReferen> result = getUploadService()
				.uploadCopyright(
						unUploadFileDir + File.separator + "stream"
								+ File.separator + "copyright.csv",
						unUploadFileDir + File.separator + "stream"
								+ File.separator + "copyright",
						(UserImpl) getUser());
		return result;
	}

	protected void processAuthor(String unUploadFileDir) throws Exception {
		String csv = unUploadFileDir + File.separator + "stream"
				+ File.separator + "author.csv";
		File csvFile = new File(csv);
		if (!csvFile.exists()) {
			logger.info(csv + " file not found.");
			return;
		}
		getUploadService().uploadAuthor(
				csv,
				unUploadFileDir + File.separator + "stream" + File.separator
						+ "author", (UserImpl) getUser());
	}

	protected void processResourceUpload(String unUploadFileDir,
			Map<String, ResourceReferen> copyMap, Integer resourceType,
			String filename) throws Exception {
		String csv = unUploadFileDir + File.separator + "stream"
				+ File.separator + filename;

		File csvFile = new File(csv);
		if (!csvFile.exists()) {
			logger.info(csv + " file not found.");
			return;
		}
		ArrayList<String> errInfo = new ArrayList<String>();
		getUploadService().uploadResource(
				unUploadFileDir + File.separator + "stream", filename, copyMap,
				resourceType, errInfo, (UserImpl) getUser());

		if (errInfo.size() > 0) {
			StringBuilder errs = new StringBuilder();
			for (String err : errInfo) {
				errs.append(err);
				errs.append("\r\n");
			}
			logger.info(errs.toString());
			throw new Exception(errs.toString());
		}
	}

	private boolean containsDirectory(String dir, String fileName) {
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

	private String getNextDirectory(String dir) {
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
