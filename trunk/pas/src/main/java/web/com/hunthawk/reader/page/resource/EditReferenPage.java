package com.hunthawk.reader.page.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author xianlaichen
 * 
 */
@Restrict(roles = { "copyrightchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditReferenPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:uploadService")
	public abstract UploadService getUploadService();

	@InjectObject("spring:systemServiceTarget")
	public abstract SystemService getSystemService();

	@SuppressWarnings("unchecked")
	public abstract IUploadFile getUploadFile();

	public abstract IUploadFile getUploadFile1();

	public abstract IUploadFile getUploadFile2();

	public abstract IUploadFile getUploadFile3();

	public abstract IUploadFile getUploadFile4();

	public abstract IUploadFile getUploadFile5();

	public abstract IUploadFile getUploadFile6();

	public abstract IUploadFile getUploadFile7();

	public abstract IUploadFile getUploadFile8();

	public abstract IUploadFile getUploadFile9();

	public abstract File getServerFile();

	public abstract void setServerFile(File file);

	private File uploadFile = null;// 最终上传到本地的

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		// TODO Auto-generated method stub
		return ResourceReferen.class;
	}

	public boolean hasRole(String role){
		return super.hasRole(role);
	}
	
	protected void uploadReferen(IUploadFile file, String name, File dir) {

		String fileName = file.getFileName().substring(
				getUploadFile().getFileName().lastIndexOf("\\") + 1);
		InputStream fis = file.getStream();
		FileOutputStream fos = null;

		try {
			String[] fileNames = fileName.split("\\.");
			if (!name.equals(fileNames[0]))
				throw new Exception("文件名称错误！应该是以" + name + "开始的文件！");

			uploadFile = new File(dir + "/" + fileName);
			uploadFile(fileName, dir, fis);
			File entryFile = new File(dir, fileName);
			setServerFile(entryFile);

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
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

	@Override
	protected boolean persist(Object object) {
		try {
			ResourceReferen resourcereferen = (ResourceReferen) object;
			File dir = null;
			if (isModelNew()) {
				resourcereferen.setCreateTime(new Date());
				UserImpl user = (UserImpl) getUser();
				resourcereferen.setCreatorId(user.getId());
				resourcereferen.setShowUrl(" ");
				resourcereferen.setStatus(1);
				resourcereferen.setModifierId(user.getId());
				resourcereferen.setModifyTime(new Date());
				if(resourcereferen.getCpId()==null){ //表明是非运营人员，那么根据当前用户的CP填进去
					if (user.getProvider() != null) {
						resourcereferen.setCpId(user.getProvider().getId());
					}				
				}
				List<ResourceReferen> refernList = 
					getResourceService().getResourceReferenByCPID(resourcereferen.getCpId());
				if(refernList!=null && refernList.size()>0){//当前选择的CP有属于自己的版权，那么搜索出最大的版权标识符 然后+1；
					List<Integer> identifierList = new ArrayList<Integer>();
					for(ResourceReferen referen :refernList){
						if(referen.getIdentifier()!=null && !"".equals(referen.getIdentifier()))
							identifierList.add(Integer.parseInt(referen.getIdentifier()));
					}
					if(identifierList !=null){
						Collections.sort(identifierList);//排序
					Integer identifier = identifierList.get(identifierList.size()-1)+1;
					resourcereferen.setIdentifier(identifier.toString());
					}
				}else{
					resourcereferen.setIdentifier("1");//第一次给 某个cp添加版权，设定标识符为1
				}
				getResourceService().addResourceReferen(resourcereferen);
			}
			dir = getUploadDir(resourcereferen.getId().toString());
			if (getUploadFile() != null) {
				// userpor
				uploadReferen(getUploadFile(), "userpor", dir);
				resourcereferen.setCopyrightUse(uploadFile.getName());
			}
			if (getUploadFile1() != null) {
				// attorn
				uploadReferen(getUploadFile1(), "attorn", dir);
				resourcereferen.setCopyrightAttorn(uploadFile.getName());
			}
			if (getUploadFile2() != null) {
				// cooperate
				uploadReferen(getUploadFile2(), "cooperate", dir);
				resourcereferen.setCooperatePro(uploadFile.getName());
			}
			if (getUploadFile3() != null) {
				// provider
				uploadReferen(getUploadFile3(), "provider", dir);
				resourcereferen.setProviderInfo(uploadFile.getName());
			}
			if (getUploadFile4() != null) {
				// copyright
				uploadReferen(getUploadFile4(), "copyright", dir);
				resourcereferen.setCopyrightCheck(uploadFile.getName());
			}
			if (getUploadFile5() != null) {
				// productinfo
				uploadReferen(getUploadFile5(), "productinfo", dir);
				resourcereferen.setProductInfo(uploadFile.getName());
			}
			if (getUploadFile6() != null) {
				// mcpinfo
				uploadReferen(getUploadFile6(), "mcpinfo", dir);
				resourcereferen.setMcpinfo(uploadFile.getName());
			}
			if (getUploadFile7() != null) {
				// promises
				uploadReferen(getUploadFile7(), "promises", dir);
				resourcereferen.setPromises(uploadFile.getName());
			}
			if (getUploadFile8() != null) {
				// authorinfo
				uploadReferen(getUploadFile8(), "authorinfo", dir);
				resourcereferen.setAuthorName(uploadFile.getName());
			}
			if (getUploadFile9() != null) {
				// other
				uploadReferen(getUploadFile9(), "other", dir);
				resourcereferen.setCopyrightOther(uploadFile.getName());
			}
			String dirpath = dir.toString();
			String[] showUrl = dirpath.split("referen");

			resourcereferen.setShowUrl("referen" + showUrl[1]);
			resourcereferen.setModifierId(getUser().getId());
			resourcereferen.setModifyTime(new Date());
			getResourceService().updateResourceReferen(resourcereferen);

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public IPropertySelectionModel getReferenStatusList() {
		return new MapPropertySelectModel(Constants.getReferenStatus());
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
	protected File getUploadDir(String id) {
		// 从数据库配置信息中得到服务器存放上传文件的路径
		Variables variables = getSystemService().getVariables("media_dir");
		String tName = variables.getValue() + File.separator + "referen";

		File tNameDir = new File(tName);
		if (!tNameDir.exists())
			tNameDir.mkdirs();
		int idInt = 0;
		idInt = Integer.parseInt(id);
		int idIntDir = idInt / 1000;

		String chDir = tNameDir + File.separator + idIntDir;
		File chNameDir = new File(chDir);
		if (!chNameDir.exists())
			chNameDir.mkdirs();

		String uploadDir = chNameDir + File.separator + id;

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
			setModel(new ResourceReferen());
		}

	}

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	public IPropertySelectionModel getCpList() {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		List<Provider> list = getPartnerService().findProvider(1, Integer.MAX_VALUE, "id",
				true, expressions);
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Provider provider : list) {
			map.put(provider.getIntro(), provider.getId());
		}

		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}
}
