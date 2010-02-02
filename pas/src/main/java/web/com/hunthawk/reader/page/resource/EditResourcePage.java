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
 * 重新修改上传页面
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "uploadresource" }, mode = Restrict.Mode.ROLE)
public abstract class EditResourcePage extends SecurityPage {

	@SuppressWarnings("unused")
	private String resourceId;
	private File uploadFile = null;// 最终上传到本地的

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
					// 文件格式判断
					throw new Exception("文件格式不正确[只能为zip]，请重新选择!");

				}
				// 文件保存在本地
				uploadFile = new File(dir + File.separator + fileName);
				uploadFile(fileName, dir, fis);
				File entryFile = new File(dir, fileName);
				setServerFile(entryFile);

				// 上传资源包的名称，如book.zip
				upFileTureName = uploadFile.getName();

				logger.info("[page=editResourcePage,info=资源包文件上穿完毕*****"
						+ upFileTureName + "]");
				// 获取资源包解开后文件夹名称，如book.zip解压后，得到的是book
				String uploadFileDir = upFileTureName.substring(0,
						upFileTureName.length() - 4);

				// 解压目录
				String unUploadFileDir = dir.toString() + File.separator
						+ uploadFileDir;
				// 解压资源包名称
				// String unUploadFileName =
				// dir.toString()+"/"+uploadFile.getName();
				String unUploadFileName = dir.toString() + File.separator
						+ upFileTureName;

				// 解压目录
				File file2 = new File(unUploadFileName);
				File dir2 = new File(unUploadFileDir);
				UnzipFile.unzip(file2, dir2);

				// 读取目录下的文件

				if (!containsDirectory(unUploadFileDir, "stream")) {
					// 进入下一级目录
					unUploadFileDir += File.separator
							+ getNextDirectory(unUploadFileDir);

				}

				if (!containsDirectory(unUploadFileDir, "stream")) {// 包展开后没有stream目录
					throw new Exception("包的目录结构错误，解压后必需是有layout,stream这个两个目录!");

				}

				// 读取book.csv,copyright.csv文件
				// if(uDirThree.indexOf("")!=-1 ||
				// uDirThree.indexOf("")!=-1){
				// 处理版权文件
				Map<String, ResourceReferen> copyMap = processCopyRight(unUploadFileDir);

				String errInfo = "";
				try {
					processAuthor(unUploadFileDir);
				} catch (Exception e) {
					errInfo += e.getMessage();
				}
				// 处理图书
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
				logger.error("上传错误", ioe);
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
		// 清除上传文件目录及目录下所有文件
		try {
			UnzipFile.dircleanup(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage getShowEbookPage();

	// 图片格式
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

	// 资源包格式
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
	 * 获得上传文件的存放目录
	 */
	private File getUploadDir() {
		// 从数据库配置信息中得到服务器存放上传文件的路径
		// Variables variables = getResourceService().getVariables("upload");
		// String uploadDir = variables.getValue();
		// 用GUID作为上传文件的解压缩目录，保证目录唯一
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
	 * 上传文件
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
	 * 处理版权信息
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
