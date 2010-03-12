/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.domain.resource.VideoSuite;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.RandomGUID;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.enhance.util.UnzipFile;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.resource.impl.UploadServiceImpl;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "resourcechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditVideoPage extends EditPage implements
		PageBeginRenderListener {
	// @SuppressWarnings("unused")
	// private String resourceId;

	public void cancelPage(IRequestCycle cycle) {
		try {
			ICallback callback = (ICallback) getCallbackStack()
					.popPreviousCallback();
			callback.performCallback(cycle);
		} catch (Exception e) {
			cycle.activate("resource/ShowEbookPage");
		}
	}

	public void savePage(IRequestCycle cycle) {
		if (save()) {
			/*
			 * getShowEbookPage().setResourceType(ResourceType.TYPE_MAGAZINE);
			 * return getShowEbookPage();
			 */
			try {
				ICallback callback = (ICallback) getCallbackStack()
						.popPreviousCallback();
				callback.performCallback(cycle);
			} catch (Exception e) {
				cycle.activate("resource/ShowEbookPage");
			}
		}
	}

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:uploadService")
	public abstract UploadService getUploadService();

	@InjectPage("resource/ShowEbookChapterPage")
	public abstract ShowEbookChapterPage getShowEbookChapterPage();

	@InjectPage("resource/ShowTomePage")
	public abstract ShowTomePage getShowTomePage();

	@SuppressWarnings("unchecked")
	public abstract IUploadFile getUploadFile();

	public abstract IUploadFile getUploadFile2();

	public abstract IUploadFile getUploadFileImage();

	public abstract File getServerFile();

	public abstract void setServerFile(File file);

	public abstract ResourceAll getResourceAll();

	public abstract void setResourceAll(ResourceAll resourceAll);

	public abstract Integer getCopyrightId();

	public abstract void setCopyrightId(Integer id);

	public abstract String getCopyRightName();

	public abstract void setCopyRightName(String copyRightName);

	public abstract void setAuthor(String author);

	public abstract String getAuthor();

	public abstract void setAuthorIDs(String ids);

	public abstract String getAuthorIDs();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Video.class;
	}

	private void processOnlineVideo(File file) {
		// 处理UC播放器

		List<String> onlineFiles = new ArrayList<String>();
		System.out.println(file.getAbsolutePath());
		if (file.exists() && file.isDirectory()) {
			File[] videos = file.listFiles();
			for (File vf : videos) {
				if (vf.getName().endsWith(".ucs")) {
					String filename = vf.getName();
					int index = filename.lastIndexOf("_");
					String targetFileName;
					if (index > 0) {
						targetFileName = filename.substring(0, index) + ".mp4";
					
					} else {
						targetFileName = vf.getName().replaceAll("ucs", "mp4");
					}
					logger.info("targetFileName:" + targetFileName);
					onlineFiles.add(UploadServiceImpl.getFilePathDir(vf
							.getAbsolutePath())
							+ File.separator + targetFileName);
				}
			}
		}
		String onlineDir = getOnlineVideoDir() + File.separator;
		for (String online : onlineFiles) {
			File srcFile = new File(online);
			// String filename = srcFile.getName();
			// int index = filename.lastIndexOf("_");
			// String targetFileName = filename;
			// if(index > 0){
			// targetFileName =
			// filename.substring(0,index)+"."+UploadServiceImpl.getFileExtName(filename);
			// }
			if (srcFile.exists()) {
				File destFile = new File(onlineDir + srcFile.getName());
				try {
					FileUtils.moveFile(srcFile, destFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	private String getOnlineVideoDir() {
		// 从数据库配置信息中得到服务器存放上传文件的路径
		Variables var = getSystemService().getVariables("media_dir");
		int idInt = 0;
		// 类别文件夹是否存在
		String tName = var.getValue() + File.separator + "onlinevideo";

		File tNameDir = new File(tName);
		if (!tNameDir.exists())
			tNameDir.mkdirs();
		return tName;
	}


	@Override
	protected boolean persist(Object object) {
		try {
			String errorMessage = "";
			Video video = (Video) object;
			
			if(video.getPublishTime() == null){
				video.setPublishTime(new Date());
			}
			if(video.getCpId() == null || video.getCpId() == 0){
				video.setCpId(5000);
			}
			
			if (isModelNew()) {
				video.setCreatorId(this.getUser().getId());
				getResourceService()
						.addResource(video, ResourceType.TYPE_VIDEO);
			}

			// 修改资源作者ID
			if (getUploadFileImage() != null) {// 上传了封面图片
				File dir = new File(getResourceService().getChapterAddress(
						video.getId()));
				if (!dir.exists())
					dir.mkdirs();
				String uploadfileName = getUploadFileImage().getFileName()
						.substring(
								getUploadFileImage().getFileName().lastIndexOf(
										"\\") + 1);

				boolean resIsRightFormat = isRightFormat(uploadfileName);
				if (!resIsRightFormat) {
					// 文件格式判断
					throw new Exception("图片格式不正确[gif/jpg/png]，请重新选择!");
				}

				if (dir.exists()) { // 删除文件
					for (File file : dir.listFiles()) {
						if (file.getName().startsWith("cover")) {
							getResourceService().deleteFile(file.toString());
							// 同步删除图片：
							getResourceService().rsyncUploadFile(
									ResourceUtil.RSYNC_TYPE_DEL,
									new String[] { file.getAbsolutePath() });
						}
					}
				}
				errorMessage = getResourceService().upload(
						getUploadFileImage(), "", dir);
				if (!"".equals(errorMessage))
					throw new Exception(errorMessage);

				// 给文件重命名：
				File file = new File(dir + File.separator + uploadfileName);// 上传到服务器的文件名称
				String fileName = "cover." + uploadfileName.split("\\.")[1];

				File newFile = new File(dir + File.separator + fileName);
				file.renameTo(newFile);

				getUploadService().resizeCoverFile(dir.toString(), fileName);

				// 同步图片：
				getUploadService().rsyncDirectry(dir);
				video.setImage(fileName);
			}

			if (getUploadFile() != null) {

				if (!resIsRightFormat(getUploadFile().getFileName())) {
					// 文件格式判断
					throw new Exception("内容文件格式不正确[只能为zip]，请重新选择!");
				}
				try {
					File dir = getUploadDir();
					String fileName = getUploadFile().getFileName()
							.substring(
									getUploadFile().getFileName().lastIndexOf(
											"\\") + 1);
					File uploadFile = new File(dir + File.separator + fileName);
					uploadFile(fileName, dir, getUploadFile().getStream());

					String upFileTureName = uploadFile.getName();

					logger.info("[page=editVideoPage,info=资源包文件上穿完毕*****"
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

					if (dir2.isDirectory()) {
						// processOnlineVideo(dir2);
						File[] videos = dir2.listFiles();
						int i = getLastIndex();
						Map<String,String> mp4FilesMap = new HashMap<String,String>(); 
						for (File vf : videos) {
							i++;
							VideoSuite vs = new VideoSuite();
							vs.setResourceId(video.getId());
							vs.setSize(((Long) vf.length()).intValue());
							int index = vf.getName().lastIndexOf("_");
							if (index > 0) {
								String filedesc = vf.getName().substring(
										index + 1);
								index = filedesc.indexOf(".");
								filedesc = filedesc.substring(0, index);
								vs.setFiledesc(filedesc);
							} else {
								vs.setFiledesc("");
							}
							vs.setChapterIndex(i);
							String vfilename = vf.getName();
							vs.setFilename(vfilename);
							// while
							// (getResourceService().isVideoFileNameExists(
							// vs)) {
							// vfilename = i + vfilename;
							// vs.setFilename(vfilename);
							// }
							vs.setType(UploadServiceImpl.getFileExtName(vs
									.getFilename()));
							if (vs.getType().equalsIgnoreCase("ucs")) {
								List<String> mp4Files = new ArrayList<String>();
								String targetUrl = getUploadService()
										.getVideoResourceDirectory(
												vs.getResourceId());
								Long size = UploadServiceImpl.changeUcsResourceURL(vf
										.getAbsolutePath(), targetUrl,
										getSystemService(),mp4Files);
								
								if(size>0L){
									vs.setSize(size.intValue());
								}
								String relFiles = "";
								for(String mp4File : mp4Files){
									mp4FilesMap.put(mp4File, vs.getFiledesc());
									relFiles += mp4File +";";
								}
								vs.setRelfiles(relFiles);
								
							}
							try {
								File destFile = new File(getResourceService()
										.getChapterAddress(video.getId())
										+ vs.getFilename());
								if (!destFile.equals(vf)) {
									FileUtils.copyFile(vf, destFile);
								}
								getResourceService().addResourceChapter(vs,
										ResourceType.TYPE_VIDEO);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						
						try{
							List<VideoSuite> videoSuites = getResourceService().getResourceChapter(VideoSuite.class, video.getId());
							for(VideoSuite suite : videoSuites){
								if(mp4FilesMap.containsKey(suite.getFilename())){
									suite.setFiledesc(mp4FilesMap.get(suite.getFilename()));
									getResourceService().updateResourceChapter(suite);
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
					}
					getUploadService().rsyncDirectry(
							new File(getResourceService().getChapterAddress(
									video.getId())));

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			// ___________新增_______________
			Integer copyrighId = getCopyrightId();
			if (copyrighId != null && !"".equals(copyrighId)) {
				if (!copyrighId.equals(video.getCopyrightId())) { // 表明修改了
					video.setLastCopyrightId(video.getCopyrightId());
					video.setCopyrightId(copyrighId);
				}
			}
			UserImpl user = (UserImpl) getUser();
			video.setModifierId(user.getId());
			if (selectedResourceType.size() == 0)
				throw new Exception("请选择资源分类！");
			// if (StringUtils.isEmpty(getAuthorIDs()))
			// throw new Exception("请选择作者");
			// String authorId = auId(selectedResourceAuthor);
			video.setAuthorId(getAuthorIDs());
			getResourceService().updateResource(video,
					ResourceAll.RESOURCE_TYPE_VIDEO);
			// 根据资源id将引用关系先删除
			getResourceService().deleteResourceResType(video.getId());
			// 添加资源与资源列表关系表
			for (int i = 0; i < selectedResourceType.size(); i++) {
				ResourceType type = (ResourceType) selectedResourceType.get(i);
				ResourceResType rel = new ResourceResType();
				rel.setRid(video.getId());
				rel.setResTypeId(type.getId());
				getResourceService().addResourceResType(rel);
			}

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	
	public IPropertySelectionModel getResourceYesNoList() {
		return new MapPropertySelectModel(Constants.getResourceYesNo());
	}

	public IPropertySelectionModel getResourceExpList() {
		return new MapPropertySelectModel(Constants.getRESOURCEEXP());
	}

	public IPropertySelectionModel getResourceStatusList() {
		return new MapPropertySelectModel(Constants.getReferenStatus());
	}

	public IPropertySelectionModel getInitialLetterList() {
		return new MapPropertySelectModel(Constants.getInitialLetter());
	}

	public IPropertySelectionModel getResourceAuthorList() {
		List<ResourceAuthor> list = getResourceService().getAllResourceAuthor();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (ResourceAuthor author : list) {
			map.put(author.getName(), author.getId());
		}

		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}

	public IPropertySelectionModel getResourceReferenList() {
		/*
		 * 返回对象属性 ObjectPropertySelectionModel parentProper = new
		 * ObjectPropertySelectionModel(
		 * getResourceService().getAllResourceReferen(), ResourceReferen.class,
		 * "getName", "getId", false, ""); return parentProper;
		 */
		// 返回单个
		List<ResourceReferen> list = getResourceService()
				.getAllResourceReferen();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (ResourceReferen referen : list) {
			map.put(referen.getName(), referen.getId());
		}

		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;

	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();

		HibernateExpression nameE = new CompareExpression("showType",
				ResourceType.TYPE_VIDEO, CompareType.Equal);
		hibernateExpressions.add(nameE);

		return hibernateExpressions;
	}

	public IPropertySelectionModel getPrivileges() {
		List<ResourceType> resourcetype = getResourceService()
				.findResourceTypeBy(1, Integer.MAX_VALUE, "id", false,
						getSearchExpressions());
		// 过滤掉父类对象，只放子类的对象------------------------------
		Set<ResourceType> parentType = new HashSet<ResourceType>();
		for (ResourceType type : resourcetype) {
			if (type.getParent() != null) {
				parentType.add(type.getParent());
			}
		}
		if (parentType != null) {
			for (ResourceType parent : parentType) {
				resourcetype.remove(parent);
			}
		}
		// ------------------------------------------------------
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				resourcetype, ResourceType.class, "getName", "getId", false,
				null);
		return model;
	}

	public IPropertySelectionModel getPrivileges2() {
		List<ResourceAuthor> resourceauthor = getResourceService()
				.findResourceAuthorBy(1, Integer.MAX_VALUE, "initialLetter",
						false, new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				resourceauthor, ResourceAuthor.class, "getPenName", "getId",
				false, null);
		return model;
	}

	@InjectComponent("roleList")
	public abstract Block getRoleList();

	@InjectComponent("roleExist")
	public abstract Block getRoleExist();

	private List selectedResourceType;

	public void setSelectedResourceTypes(List resourcetypes) {
		selectedResourceType = resourcetypes;
	}

	public List getSelectedResourceTypes() {
		if (isModelNew()) {// 新增加
			return new ArrayList();
		} else {// 修改的
			Video ebook = (Video) getModel();
			// ebook.getId();
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("rid",
					ebook.getId(), CompareType.Equal);
			expressions.add(ex);
			// List<ResourceResType> list =
			// controller.findBy(ResourceResType.class, 1, 1000, expressions);
			List<ResourceResType> list = getResourceService()
					.findResourceResTypeBy(1, Integer.MAX_VALUE, "rid", false,
							expressions);
			ArrayList<ResourceType> arrayList = new ArrayList<ResourceType>();
			for (ResourceResType resType : list) {
				// arrayList.add(getResourceService().getresType.getResTypeId());
				arrayList.add(getResourceService().getResourceType(
						resType.getResTypeId()));
			}

			return arrayList;
		}

	}

	@InjectComponent("roleList2")
	public abstract Block getRoleList2();

	@InjectComponent("roleExist2")
	public abstract Block getRoleExist2();

	private List selectedResourceAuthor;

	public void setSelectedResourceAuthor(List resourceauthor) {
		selectedResourceAuthor = resourceauthor;

	}

	public List getSelectedResourceAuthor() {
		if (isModelNew()) {// 新增加
			return new ArrayList();
		} else {// 修改的
			Video ebook = (Video) getModel();
			ebook.getAuthorId();
			Integer[] list = ebook.getAuthorIds();
			ArrayList<ResourceAuthor> arrayList = new ArrayList<ResourceAuthor>();
			for (Integer id : list) {
				arrayList.add(getResourceService().getResourceAuthorById(id));
			}

			return arrayList;
		}

	}

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	public IPropertySelectionModel getCpList() {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		List<Provider> list = getPartnerService().findProvider(1,
				Integer.MAX_VALUE, "id", true, expressions);
		// ObjectPropertySelectionModel parentProper = new
		// ObjectPropertySelectionModel(
		// list,Provider.class,"getChName","getId");
		// return parentProper;

		// 返回单个
		// List<ResourceReferen> list =
		// getResourceService().getAllResourceReferen();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Provider provider : list) {
			map.put(provider.getIntro(), provider.getId());
		}

		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
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

	// 资源包格式
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

	public String auId(List auid) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("|");
			for (int i = 0; i < auid.size(); i++) {
				ResourceAuthor author = (ResourceAuthor) auid.get(i);
				sb.append(author.getId()).append("|");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "auid Error";
		}
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Video());
		}
		// 初始化那个版权的名称：
		ResourceAll resource = (ResourceAll) getModel();
		if (resource.getCopyrightId() != null) {
			ResourceReferen reference = getResourceService()
					.getResourceReferen(resource.getCopyrightId());
			if (reference != null)
				setCopyRightName(reference.getName());

		}
		if (resource.getAuthorIds() != null
				&& resource.getAuthorIds().length > 0) {
			StringBuffer name = new StringBuffer();
			StringBuffer ids = new StringBuffer();
			ids.append("|");
			for (int i = 0; i < resource.getAuthorIds().length; i++) {
				Integer id = resource.getAuthorIds()[i];
				ResourceAuthor temp = getResourceService()
						.getResourceAuthorById(id);
				ids.append(temp.getId());
				ids.append("|");

				if (i != resource.getAuthorIds().length - 1) {

					name.append(temp.getName());

					name.append(",");
				} else {
					name.append(temp.getName());
				}
			}
			setAuthor(name.toString());
			setAuthorIDs(ids.toString());
		}

	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getCopyrightURL() {

		IEngineService service = getExternalService();

		Object[] params = new Object[] { "copyrightId" };
		String templateURL = PageHelper.getExternalFunction(service,
				"resource/ReferenToResource", params);

		return templateURL;

	}

	public String getAuthorURL() {

		IEngineService service = getExternalService();

		Object[] params = new Object[] { "authorId", "authorIDs" };
		String templateURL = PageHelper.getExternalFunction(service,
				"resource/AuthorListPage", params);

		return templateURL;

	}

	public String getPreAddress() {
		if (isModelNew())
			return "";
		ResourceAll resource = (ResourceAll) getModel();
		Video video = (Video) resource;
		String url = getResourceService().getPreviewCoverImg(resource.getId(),
				video.getImage());

		String imgPath = "<img src='" + url
				+ "' width='100' height='100' align='absmiddle'/>";
		return imgPath;
	}

	public IPage showChapter(ResourceAll resourceAll) {
		getShowEbookChapterPage().setResourceAll(resourceAll);
		return getShowEbookChapterPage();

	}

	public IPage showTome(ResourceAll resourceAll) {
		getShowTomePage().setResourceAll(resourceAll);
		return getShowTomePage();
	}

	// 资源包格式
	public static boolean isZipRightFormat(String fileName) {
		String patt = "\\.(zip)$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}

	public int getLastIndex() {
		if (isModelNew())
			return 0;
		Video video = (Video) getModel();
		List<HibernateExpression> ex = new ArrayList<HibernateExpression>();
		ex.add(new CompareExpression("resourceId", video.getId(),
				CompareType.Equal));
		List<VideoSuite> suites = getResourceService().getResourceChapterList(
				VideoSuite.class, 1, 1, "id", false, ex);
		if (suites.size() > 0) {
			return suites.get(0).getChapterIndex();
		}
		return 0;
	}

	public List getResourceSuites() {
		if (isModelNew())
			return new ArrayList();
		Video video = (Video) getModel();
		List<HibernateExpression> ex = new ArrayList<HibernateExpression>();
		ex.add(new CompareExpression("resourceId", video.getId(),
				CompareType.Equal));
		return getResourceService().getResourceChapterList(VideoSuite.class, 1,
				1000, "id", false, ex);
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

	public IPage onDeleteSuite(VideoSuite suite) {
		try {
			Video video = (Video) getResourceService().getResource(
					suite.getResourceId(), ResourceType.TYPE_VIDEO);
			this.setModel(video);
			this.setResourceAll(video);
			getResourceService().deleteResourceChapter(suite,
					ResourceType.TYPE_VIDEO);

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			e.printStackTrace();
		}

		return this;
	}

	public abstract void setCurrentSuite(VideoSuite vs);

	public abstract VideoSuite getCurrentSuite();
}
