package com.hunthawk.reader.page.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author xianlaichen
 * 
 */
@Restrict(roles = { "author" }, mode = Restrict.Mode.ROLE)
public abstract class EditAuthorPage extends EditPage implements IExternalPage,
		PageBeginRenderListener {

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@SuppressWarnings("unchecked")
	public abstract IUploadFile getUploadFile();

	public abstract File getServerFile();

	public abstract void setServerFile(File file);

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return ResourceAuthor.class;
	}

	@Override
	protected boolean persist(Object object) {

		try {
			File entryFile = null;// 最终上传到本地的
			if (getUploadFile() != null) {
				String fileName = getUploadFile().getFileName().substring(
						getUploadFile().getFileName().lastIndexOf("\\") + 1);

				boolean isRightFormat = isRightFormat(getUploadFile()
						.getFileName());

				if (!isRightFormat) {
					throw new Exception("文件格式不正确[只能为gif或jpg或png或bmp]，请重新选择!");
				}
				InputStream fis = getUploadFile().getStream();
				FileOutputStream fos = null;

				try {

					File dir = getUploadDir();

					uploadFile(fileName, dir, fis);
					entryFile = new File(dir, fileName);

				} catch (Exception ioe) {
					ioe.printStackTrace();
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
			}
			ResourceAuthor resourceauthor = (ResourceAuthor) object;
			if (isModelNew()) {
				resourceauthor.setCreateTime(new Date());
				resourceauthor.setCreatorId(getUser().getId());
				resourceauthor.setStatus(0);
				getResourceService().addResourceAuthor(resourceauthor);
			}
			if (entryFile != null) {
				Variables var = getSystemService().getVariables("media_dir");
				String fileType = entryFile.getName().substring(
						entryFile.getName().lastIndexOf(".") + 1).toLowerCase();

				resourceauthor.setAuthorPic("author/"
						+ String.valueOf(resourceauthor.getId() / 1000) + "/"
						+ resourceauthor.getId() + "." + fileType);
				File destFile = new File(var.getValue() + "author"
						+ File.separator
						+ String.valueOf(resourceauthor.getId() / 1000)
						+ File.separator + resourceauthor.getId() + "."
						+ fileType);
				FileUtils.copyFile(entryFile, destFile);
				FileUtils.forceDeleteOnExit(entryFile);

				getResourceService().updateResourceAuthor(resourceauthor);
			} else if (!isModelNew()) {
				getResourceService().updateResourceAuthor(resourceauthor);
			}

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public IPropertySelectionModel getInitialLetterList() {
		return new MapPropertySelectModel(Constants.getInitialLetter());
	}

	public IPropertySelectionModel getSexList() {
		Map<String,Integer> sexs = new HashMap<String,Integer>();
		sexs.put("男", 1);
		sexs.put("女", 2);
		sexs.put("未知", 3);
		return new MapPropertySelectModel(sexs);
	}
	

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

	/**
	 * 获得上传文件的存放目录
	 */
	protected File getUploadDir() {
		// 从数据库配置信息中得到服务器存放上传文件的路径
		Variables variables = getSystemService().getVariables("upload_dir");
		String uploadDir = variables.getValue();

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
			getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{entryFile.getAbsolutePath()});
			dest.flush();
			dest.close();
			fos.close();
			is.close();
		} catch (Exception e) {
			System.out.println("***:" + e.toString());

		} finally {

		}

	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new ResourceAuthor());
		}

	}

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);
		Integer authorId = (Integer)parameters[0];
		ResourceAuthor author = getResourceService().getResourceAuthorById(authorId);
		setModel(author);
		setShowLink(false);
		
	}
	@InitialValue("true")
	public abstract boolean isShowLink();
	public abstract void setShowLink(boolean show); 
	
	
	public abstract String getChoose();
	public abstract void setChoose(String choose);
	public void savePage(){
		setChoose("true");
		save();
	}
}