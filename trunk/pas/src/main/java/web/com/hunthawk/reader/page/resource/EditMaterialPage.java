/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "material" }, mode = Restrict.Mode.ROLE)
public abstract class EditMaterialPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Material.class;
	}

	public abstract Integer getCatalogId();

	public abstract void setCatalogId(Integer catalogId);

	public abstract IUploadFile getUploadFile();

	public boolean isRightFormat(String fileName) {
		MaterialCatalog catalog = getMaterialService().getMaterialCatalog(
				getCatalogId());
		String patt = "";
		if (catalog.getType().equals(MaterialCatalog.TYPE_IMAGE)) {
			patt = "\\.(jpg|gif|png|bmp)$";
		} else if (catalog.getType().equals(MaterialCatalog.TYPE_MUSIC)) {
			patt = "\\.(mid|mp3|wma|wav)$";
		} else {
			return false;
		}

		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {

		File entryFile = null;
		if (getUploadFile() != null) {
			String fileName = getUploadFile().getFileName().substring(
					getUploadFile().getFileName().lastIndexOf("\\") + 1);
			boolean isRightFormat = isRightFormat(getUploadFile().getFileName());
			if (!isRightFormat) {

				ValidationDelegate delegate = getDelegate();
				delegate.setFormComponent(null);
				delegate
						.record(
								"文件格式不正确[图片素材只能为gif或jpg或png或bmp,音乐素材只能是mid或mp3或wma或wav]，请重新选择",
								null);
				return false;
			}
			InputStream fis = getUploadFile().getStream();
			FileOutputStream fos = null;
			
			try {
				Variables var = getSystemService().getVariables("upload_dir");
				File dir = new File(var.getValue());
				
				uploadFile(fileName, dir, fis);
				entryFile = new File(dir, fileName);

			} catch (Exception ioe) {
				ValidationDelegate delegate = getDelegate();
				delegate.setFormComponent(null);
				delegate.record("素材上传失败", null);
				return false;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException ioe) {
					}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ioe) {
					}
				}
			}

		} else if (isModelNew()) {
			ValidationDelegate delegate = getDelegate();
			delegate.setFormComponent(null);
			delegate.record("素材文件不能为空", null);
			return false;
		}

		try {
			Material matr = (Material) object;
			 matr.setCatalogId(getCatalogId());
			 if(StringUtils.isEmpty(matr.getName())){
				 matr.setName("默认");
			 }
			if (isModelNew()) {
				matr.setCreateTime(new Date());
				matr.setCreator(getUser().getId());
				matr.setModifyTime(new Date());
				matr.setModifier(getUser().getId());
				getMaterialService().addMaterial(matr);
			} 
			matr.setModifyTime(new Date());
			matr.setModifier(getUser().getId());
			if(entryFile != null){
				//文件扩展名
				Variables var = getSystemService().getVariables("media_dir");
	            String fileType = entryFile.getName().substring(entryFile.getName().
	                    lastIndexOf(".") + 1).toLowerCase();
	            matr.setExtName(fileType);
	            
	            matr.setFilename("material/"+String.valueOf(matr.getId()/1000)+ "/" + matr.getId());
	            
	            File destFile = new File(var.getValue()+ "material"+File.separator+String.valueOf(matr.getId()/1000)+ File.separator + matr.getId()+"."+matr.getExtName());

	            
	            FileUtils.copyFile(entryFile, destFile);
	            
	            getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD,new String[]{destFile.getAbsolutePath()});
				
	            
	            matr.setSize((int)destFile.length());
	            FileUtils.forceDeleteOnExit(entryFile);
				
	            getMaterialService().updateMaterial(matr);
			}else if(!isModelNew()){
				getMaterialService().updateMaterial(matr);
			}
			
			
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	
	


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
			logger.error("实体资源文件上传异常:", e);
		} finally {

		}
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Material());
		}

	}
}