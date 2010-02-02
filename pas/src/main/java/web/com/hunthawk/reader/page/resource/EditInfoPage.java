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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.EventListener;
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
import org.apache.tapestry.services.ResponseBuilder;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.SysTagType;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.page.guide.TagGuide;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "resourcechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditInfoPage extends EditPage implements
		PageBeginRenderListener {
	@SuppressWarnings("unused")
	private String resourceId;
	
	public void cancelPage(IRequestCycle cycle) {
		try{
			ICallback callback = (ICallback) getCallbackStack().popPreviousCallback();
			callback.performCallback(cycle);
		}catch(Exception e){
			cycle.activate("resource/ShowEbookPage");
		}
	}
	
	public void savePage(IRequestCycle cycle){
		if(save()){
			/*getShowEbookPage().setResourceType(ResourceType.TYPE_MAGAZINE);
			return getShowEbookPage();*/
			try{
				ICallback callback = (ICallback) getCallbackStack().popPreviousCallback();
				callback.performCallback(cycle);
			}catch(Exception e){
				cycle.activate("resource/ShowEbookPage");
			}
		}
	}

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

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

	

	public abstract Integer getCopyrightId();

	public abstract void setCopyrightId(Integer id);

	public abstract String getCopyRightName();

	public abstract void setCopyRightName(String copyRightName);

	public abstract void setAuthor(String author);

	public abstract String getAuthor();

	public abstract void setAuthorIDs(String ids);

	public abstract String getAuthorIDs();
	
	public abstract void setUploadImage(String image);

	public abstract String getUploadImage();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Infomation.class;
	}

	@Override
	protected boolean persist(Object object) {
		try {
			String errorMessage = "";
			Infomation infomation = (Infomation) object;
			if(isModelNew()){
				infomation.setCreatorId(this.getUser().getId());
				infomation.setDownnum(0);
				getResourceService().addResource(infomation, ResourceType.TYPE_INFO);
			}
			
			if(getUploadImage() != null){
				File image = new File(getUploadImage());
				if(image.exists()){
					boolean resIsRightFormat = isRightFormat(image.getName());
					if (!resIsRightFormat) {
						// 文件格式判断
						throw new Exception("图片格式不正确[gif/jpg/png]，请重新选择!");
					}
					File dir = new File(getResourceService().getChapterAddress(
							infomation.getId()));
					if (!dir.exists())
						dir.mkdirs();
					if (dir.exists()) { // 删除文件
						for (File file : dir.listFiles()) {
							if (file.getName().startsWith("cover")) {
								getResourceService()
										.deleteFile(file.toString());
								// 同步删除图片：
								getResourceService()
										.rsyncUploadFile(
												ResourceUtil.RSYNC_TYPE_DEL,
												new String[] { file
														.getAbsolutePath() });
							}
						}
					}
					
					String fileName = "cover." + image.getName().split("\\.")[1];

					File newFile = new File(dir + File.separator + fileName);
					image.renameTo(newFile);

					getUploadService()
							.resizeCoverFile(dir.toString(), fileName);

					// 同步图片：
					getUploadService().rsyncDirectry(dir);
					infomation.setImage(fileName);
					
				}
			}
			
				
//				// ___________新增_______________
//				Integer copyrighId = getCopyrightId();
//				if (copyrighId != null && !"".equals(copyrighId)) {
//					if (!copyrighId.equals(Infomation.getCopyrightId())) { // 表明修改了
//						video.setLastCopyrightId(video.getCopyrightId());
//						video.setCopyrightId(copyrighId);
//					}
//				}
				UserImpl user = (UserImpl) getUser();
				infomation.setModifierId(user.getId());
				if (selectedResourceType.size() == 0)
					throw new Exception("请选择资源分类！");
//				if (StringUtils.isEmpty(getAuthorIDs()))
//					throw new Exception("请选择作者");
				// String authorId = auId(selectedResourceAuthor);
//				infomation.setAuthorId(getAuthorIDs());
				getResourceService().updateResource(infomation,
						ResourceAll.RESOURCE_TYPE_INFO);
				// 根据资源id将引用关系先删除
				getResourceService().deleteResourceResType(infomation.getId());
				// 添加资源与资源列表关系表
				for (int i = 0; i < selectedResourceType.size(); i++) {
					ResourceType type = (ResourceType) selectedResourceType
							.get(i);
					ResourceResType rel = new ResourceResType();
					rel.setRid(infomation.getId());
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

		HibernateExpression nameE = new CompareExpression("showType", ResourceType.TYPE_INFO,
				CompareType.Equal);
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
			Infomation infomation = (Infomation) getModel();
			// ebook.getId();
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("rid",
					infomation.getId(), CompareType.Equal);
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

	

	private List selectedResourceAuthor;

	public void setSelectedResourceAuthor(List resourceauthor) {
		selectedResourceAuthor = resourceauthor;

	}

//	public List getSelectedResourceAuthor() {
//		if (isModelNew()) {// 新增加
//			return new ArrayList();
//		} else {// 修改的
//			infomation infomation = (infomation) getModel();
//			ebook.getAuthorId();
//			Integer[] list = ebook.getAuthorIds();
//			ArrayList<ResourceAuthor> arrayList = new ArrayList<ResourceAuthor>();
//			for (Integer id : list) {
//				arrayList.add(getResourceService().getResourceAuthorById(id));
//			}
//
//			return arrayList;
//		}
//
//	}

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
	 * 获得上传文件的存放目录
	 */
	protected File getUploadDir() {
		// 从数据库配置信息中得到服务器存放上传文件的路径
		// Variables variables = getResourceService().getVariables("upload");
		// String uploadDir = variables.getValue();
		String uploadDir = "D:\\upload\\ebook";
		// 用GUID作为上传文件的解压缩目录，保证目录唯一
		// RandomGUID randomGuid = new RandomGUID();
		// String guidStr = randomGuid.toString();

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
			setModel(new Infomation());
		}
		// 初始化那个版权的名称：
//		ResourceAll resource = (ResourceAll) getModel();
//		if (resource.getCopyrightId() != null) {
//			ResourceReferen reference = getResourceService()
//					.getResourceReferen(resource.getCopyrightId());
//			if (reference != null)
//				setCopyRightName(reference.getName());
//
//		}
//		if (resource.getAuthorIds() != null
//				&& resource.getAuthorIds().length > 0) {
//			StringBuffer name = new StringBuffer();
//			StringBuffer ids = new StringBuffer();
//			ids.append("|");
//			for (int i = 0; i < resource.getAuthorIds().length; i++) {
//				Integer id = resource.getAuthorIds()[i];
//				ResourceAuthor temp = getResourceService()
//						.getResourceAuthorById(id);
//				ids.append(temp.getId());
//				ids.append("|");
//
//				if (i != resource.getAuthorIds().length - 1) {
//
//					name.append(temp.getName());
//
//					name.append(",");
//				} else {
//					name.append(temp.getName());
//				}
//			}
//			setAuthor(name.toString());
//			setAuthorIDs(ids.toString());
//		}

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
		if(isModelNew()){
			return "";
		}
		ResourceAll resource = (ResourceAll) getModel();
		Infomation video = (Infomation)resource;
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
	
	public List<SysTagType> getSysTagTypes() {
		List<SysTagType> sysTagTypes = new ArrayList<SysTagType>();
		Variables variables = getSystemService().getVariables(
				Variables.SYSTAG_PERTAINT_TYPE);

		Map map = PageUtil.getMapFormString(variables.getValue());

		Set keySet = map.keySet();

		Iterator keyIt = keySet.iterator();

		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			String value = (String) map.get(key);
			if(value.equals("userDefTag")){ //自定义标签
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("status",
						1, CompareType.Equal);
				expressions.add(ex);   //检索使用状态的标签
				
				List<UserDefTag> list = 
					getTemplateService().getUserDefTagList(1, Integer.MAX_VALUE, "id", true, expressions);
				List<TagGuide> tags = new ArrayList<TagGuide>();
				for(int i=0 ;i<list.size();i++){
					tags.add(new TagGuide(list.get(i)));
				}
				SysTagType sysTagType = new SysTagType();
				sysTagType.setType(key);
				sysTagType.setTagGuide(tags);
				
				sysTagTypes.add(sysTagType);
				
			}else{
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("pertaintype",
						value, CompareType.Equal);
				expressions.add(ex);
				List<SysTag> list = getTemplateService().findSysTag(1,
						Integer.MAX_VALUE, "id", true, expressions);
	
				List<TagGuide> tags = new ArrayList<TagGuide>();
				for (int i = 0; i < list.size(); i++) {
					tags.add(new TagGuide(list.get(i), getExternalService()));
				}

				SysTagType sysTagType = new SysTagType();
				sysTagType.setType(key);
				sysTagType.setTagGuide(tags);
				
				sysTagTypes.add(sysTagType);
			}		
		}

		return sysTagTypes;
	}

	public abstract SysTagType getSysTagType();

	public abstract void setSysTagType(SysTagType sysTagType);

	public abstract TagGuide getSysTag();

	public abstract void setSysTag(TagGuide tag);
	
	
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	

    @EventListener(targets = "showContent", events = "onclick", submitForm = "resourceForm")
    public void onClick()
    {
        logger.info("原内容:" + ((Infomation) getModel()).getIntroLon());
        String content = ((Infomation) getModel()).getIntroLon();
        content = this.getResourceService().splitDocContent(content);
        ((Infomation) getModel()).setIntroLon(content);

        getBuilder().updateComponent("docContent");
        // setShowPre(true);
    }

    public abstract ResponseBuilder getBuilder();
    
    public String getUploadPage(String preImg, String uploadFile) {
		IEngineService service = getExternalService();

		
		Object[] params = new Object[] { preImg,  uploadFile};
		String url = PageHelper.getExternalFunction(service,
				"resource/UploadFile", params);

		return url;
	}
	
}
