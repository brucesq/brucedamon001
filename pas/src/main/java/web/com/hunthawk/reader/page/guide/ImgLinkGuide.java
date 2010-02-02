/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 图标引入模板
 * @author BruceSun
 *
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ImgLinkGuide extends SecurityPage implements IExternalPage {

	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract String[] getParameter();

	public abstract String getTagName();

	public abstract void setTagName(String name);

	public abstract void setParameter(String[] para);

	public abstract IUploadFile getUploadFile();
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		
	}
	
	public abstract String getUrlLink();
	public abstract void setUrlLink(String urlLink);
	
	public boolean isRightFormat(String fileName) {
		MaterialCatalog catalog = getMaterialService().getMaterialCatalog(
				Integer.parseInt(getSystemService().getVariables("default_pic_material_catalog_id").getValue()));
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
	public void uploadMaterial(IRequestCycle cycle){
		//-------------上传图片开始------------
		try{
		if(getUploadFile()!=null){	
			File entryFile = null;
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
				return ;
			}
			Variables var = getSystemService().getVariables("upload_dir");
			File dir = new File(var.getValue());
			if(!dir.exists())
				dir.mkdirs();
			getResourceService().upload(getUploadFile(), "", dir);
			entryFile = new File(dir, fileName);
			
			Material matr = new Material();		
			 matr.setCatalogId(Integer.parseInt(getSystemService().getVariables("default_pic_material_catalog_id").getValue()));
			 if(StringUtils.isEmpty(matr.getName())){
				 matr.setName("默认");
			 }
				matr.setCreateTime(new Date());
				matr.setCreator(getUser().getId());
				matr.setModifyTime(new Date());
				matr.setModifier(getUser().getId());
				getMaterialService().addMaterial(matr);
				
				
				Variables var1 = getSystemService().getVariables("media_dir");
	            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	            matr.setExtName(fileType);
	            
	            matr.setFilename("material/"+String.valueOf(matr.getId()/1000)+ "/" + matr.getId());
	            
	            File destFile = new File(var1.getValue()+ "material"+File.separator+String.valueOf(matr.getId()/1000)+ File.separator + matr.getId()+"."+matr.getExtName());

	         
	            FileUtils.copyFile(entryFile, destFile);
	            getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD,new String[]{destFile.getAbsolutePath()});
				
	            matr.setSize((int)destFile.length());
	            FileUtils.forceDeleteOnExit(entryFile);
	
	            getMaterialService().updateMaterial(matr);
	            setImageId( matr.getId());
	            String url = getSystemService().getVariables("media_url").getValue();
				Material mater =  getMaterialService().getMaterial(getImageId());
				setImgUrl( url + mater.getFilename() + "." + mater.getExtName());
	            setReturnValue("");
				setNeedReturn(Boolean.FALSE);
				return;
			}
		}catch(Exception e){
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			}
			
	}
	public void onSubmit(IRequestCycle cycle) {
		if(getImageId() == null){
			setReturnValue("");
			setNeedReturn(Boolean.FALSE);
			return;
		}
			String url = getSystemService().getVariables("media_url").getValue();
			Material mater =  getMaterialService().getMaterial(getImageId());
			setImgUrl( url + mater.getFilename() + "." + mater.getExtName());
			
		StringBuilder sb = new StringBuilder();
		if(getLinkType() != null && getLinkType() == 1){
			sb.append("<a href=\"");
			sb.append(getUrlLink());
			sb.append("\">");
		}
		sb.append("<img src=\"");
		sb.append(getImgUrl());
		sb.append("\" ");
		if(getWidth() != null && getWidth() > 0){
			sb.append(" width=\"");
			sb.append(getWidth());
			sb.append("\" ");
		}
		if(getHeight() != null && getHeight() > 0){
			sb.append(" height=\"");
			sb.append(getHeight());
			sb.append("\" ");
		}
		sb.append("alt=\"\" />");
		if(getLinkType() != null && getLinkType() == 1){
			sb.append("</a>");
		}
		setReturnValue(sb.toString());
		setNeedReturn(Boolean.TRUE);
	}

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();
	
	public abstract Integer getLinkType();
	public abstract void setLinkType(Integer type);
	
	public IPropertySelectionModel getLinkTypeList(){
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		types.put("是", 1);
		types.put("否", 2);
		return new MapPropertySelectModel(types, false, "");
	}

	public abstract Integer getWidth();
	public abstract Integer getHeight();
	
	public abstract Integer getImageId();
	public abstract void setImageId(Integer imageId);
	
	public abstract void setImgUrl(String url);
	public abstract String getImgUrl();
	

	public Map getScriptSymbols() {
		Map map = new HashMap();
		if (getNeedReturn() == null) {
			setNeedReturn(Boolean.FALSE);
		}
		if (getReturnValue() == null) {
			setReturnValue("");
		}
		map.put("needreturn", getNeedReturn());
		map.put("content", getReturnValue());
		map.put("update", Boolean.FALSE);
		String tagValue = getTagName();
		

		map.put("tag", "$#imglink#");
		map.put("num", 1);
		return map;
	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();
	
	
	public String getChooseImageURL(){
		IEngineService service = getExternalService();
		Object[] params = new Object[3];
		params[0] = "imgid";
		params[1] = "imgurl";
		params[2] = String.valueOf(MaterialCatalog.TYPE_IMAGE);
		String resURL = PageHelper.getExternalFunction(service,
				"resource/MaterialChoosePage", params);

		return resURL;
	}
}

