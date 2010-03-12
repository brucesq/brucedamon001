/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.bussiness.TemplateType;
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
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class UploadFile extends SecurityPage implements IExternalPage,
		PageBeginRenderListener {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract IUploadFile getUploadFile();

	public abstract void setImagePre(String img);
	public abstract String getImagePre();

	public abstract void setReturnElement(String name);
	public abstract String getReturnElement();
	
	public abstract void setImagePreValue(String img);
	public abstract String getImagePreValue();

	public abstract void setReturnElementValue(String name);
	public abstract String getReturnElementValue();

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);
		setImagePre((String) parameters[0]);
		setReturnElement((String) parameters[1]);
	}

	// public boolean isRightFormat(String fileName) {
	// MaterialCatalog catalog = getMaterialService().getMaterialCatalog(
	// Integer.parseInt(getSystemService().getVariables("default_pic_material_catalog_id").getValue()));
	// String patt = "";
	// if (catalog.getType().equals(MaterialCatalog.TYPE_IMAGE)) {
	// patt = "\\.(jpg|gif|png|bmp)$";
	// } else if (catalog.getType().equals(MaterialCatalog.TYPE_MUSIC)) {
	// patt = "\\.(mid|mp3|wma|wav)$";
	// } else {
	// return false;
	// }

	// Pattern p = Pattern.compile(patt);
	// Matcher m = p.matcher(fileName);
	//
	// if (m.find()) {
	// return true;
	// } else {
	// return false;
	// }
	//
	// }

	public void uploadMaterial(IRequestCycle cycle) {
		// -------------上传图片开始------------
		try {
			if (getUploadFile() != null) {
				File entryFile = null;
				String fileName = getUploadFile().getFileName().substring(
						getUploadFile().getFileName().lastIndexOf("\\") + 1);

				// boolean isRightFormat =
				// isRightFormat(getUploadFile().getFileName());
				// if (!isRightFormat) {
				//
				// ValidationDelegate delegate = getDelegate();
				// delegate.setFormComponent(null);
				// delegate
				// .record(
				// "文件格式不正确[图片素材只能为gif或jpg或png或bmp,音乐素材只能是mid或mp3或wma或wav]，请重新选择",
				// null);
				// return ;
				// }
				Variables var = getSystemService().getVariables("upload_dir");
				File dir = new File(var.getValue());
				if (!dir.exists())
					dir.mkdirs();

				getResourceService().upload(getUploadFile(), "", dir);
				entryFile = new File(dir, fileName);

				// Material matr = new Material();
				// matr.setCatalogId(Integer.parseInt(getSystemService().getVariables("default_pic_material_catalog_id").getValue()));
				// if(StringUtils.isEmpty(matr.getName())){
				// matr.setName("默认");
				// }
				// matr.setCreateTime(new Date());
				// matr.setCreator(getUser().getId());
				// matr.setModifyTime(new Date());
				// matr.setModifier(getUser().getId());
				// getMaterialService().addMaterial(matr);

				Variables var1 = getSystemService().getVariables("media_dir");
				String fileType = fileName.substring(
						fileName.lastIndexOf(".") + 1).toLowerCase();
				// matr.setExtName(fileType);
				//	            
				// matr.setFilename("material/"+String.valueOf(matr.getId()/1000)+
				// "/" + matr.getId());
				//	            
				File destFile = new File(var1.getValue() + "raw"
						+ File.separator + entryFile.getName());

				FileUtils.copyFile(entryFile, destFile);
				// getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD,new
				// String[]{destFile.getAbsolutePath()});
				//				
				// matr.setSize((int)destFile.length());
				FileUtils.forceDeleteOnExit(entryFile);

				// getMaterialService().updateMaterial(matr);
				// setImageId( matr.getId());
				// String url =
				// getSystemService().getVariables("media_url").getValue();
				// Material mater =
				// getMaterialService().getMaterial(getImageId());
				// setImgUrl( url + mater.getFilename() + "." +
				// mater.getExtName());
				// setReturnValue("");
				// setNeedReturn(Boolean.FALSE);
				Variables var2 = getSystemService().getVariables("media_url");
				setImagePreValue(var2.getValue()+"raw/"+entryFile.getName());
				setReturnElementValue(destFile.getAbsolutePath());
				setNeedReturn("true");
				return;
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}

	}
	public void pageBeginRender(PageEvent arg0) {

	}
	public abstract void setNeedReturn(String bReturn);
	public abstract String getNeedReturn();

}
