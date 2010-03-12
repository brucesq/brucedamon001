package com.hunthawk.reader.page.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "resourcechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditComicsPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract ResourceAll getResourceAll();

	public abstract void setResourceAll(ResourceAll resourceAll);

	@InjectPage("resource/ShowTomePage")
	public abstract ShowTomePage getShowTomePage();

	@InjectObject("spring:uploadService")
	public abstract UploadService getUploadService();

	public abstract IUploadFile getUploadFileImage();

	public abstract Integer getCopyrightId();

	public abstract void setCopyrightId(Integer id);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Magazine.class;
	}

	@Override
	protected boolean persist(Object object) {
		try {
			Comics comics = (Comics) object;
			String errorMessage = "";
			if (!isModelNew()) {
				if (getUploadFileImage() != null) {// 上传了封面图片
					File dir = new File(getResourceService().getChapterAddress(
							comics.getId()));
					if (!dir.exists())
						dir.mkdirs();
					String uploadfileName = getUploadFileImage().getFileName()
							.substring(
									getUploadFileImage().getFileName()
											.lastIndexOf("\\") + 1);

					boolean resIsRightFormat = isRightFormat(uploadfileName);
					if (!resIsRightFormat) {
						// 文件格式判断
						throw new Exception("图片格式不正确[gif/jpg/png]，请重新选择!");
					}
					if (dir.exists()) { // 删除文件
						for (File file : dir.listFiles()) {
							if (file.getName().startsWith("cover")) {
								getResourceService()
										.deleteFile(file.toString());
								//同步删除图片：
								getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, new String[]{file.getAbsolutePath()});
							}
						}
					}

					errorMessage = getResourceService().upload(
							getUploadFileImage(), "", dir);
					if (!"".equals(errorMessage))
						throw new Exception(errorMessage);
					//给文件重命名：
					File file = new File(dir+File.separator+uploadfileName);//上传到服务器的文件名称
					String fileName = "cover."+uploadfileName.split("\\.")[1];
				
					File newFile = new File(dir+File.separator+fileName);
					file.renameTo(newFile);
					
					getUploadService().resizeCoverFile(dir.toString(),
							fileName);
					comics.setImage(fileName);
					//同步图片：
					getUploadService().rsyncDirectry(dir);
				}
				// ___________新增_______________
				Integer copyrighId = getCopyrightId();
				if (copyrighId != null && !"".equals(copyrighId)) {
					if (!copyrighId.equals(comics.getCopyrightId())) { // 表明修改了
						comics.setLastCopyrightId(comics.getCopyrightId());
						comics.setCopyrightId(copyrighId);
					}
				}
				UserImpl user = (UserImpl) getUser();
				comics.setModifierId(user.getId());
				
				if(selectedResourceType.size()==0)
					throw new Exception("请选择资源分类！");
				
				if(selectedResourceAuthor.size()==0)
					throw new Exception("请选择作者");
				
				String authorId = auId(selectedResourceAuthor);
					comics.setAuthorId(authorId);	
					
				getResourceService().updateResource(comics,
						ResourceAll.RESOURCE_TYPE_COMICS);
				// 根据资源id将引用关系先删除
				getResourceService().deleteResourceResType(comics.getId());
				// 添加资源与资源列表关系表
				for (int i = 0; i < selectedResourceType.size(); i++) {
					ResourceType type = (ResourceType) selectedResourceType
							.get(i);
					ResourceResType rel = new ResourceResType();
					rel.setRid(comics.getId());
					rel.setResTypeId(type.getId());
					getResourceService().addResourceResType(rel);
				}
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

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();

		HibernateExpression nameE = new CompareExpression("showType", 4,
				CompareType.Equal);
		hibernateExpressions.add(nameE);

		return hibernateExpressions;
	}

	/***************************************************************************
	 * 返回的分类列表
	 * 
	 * @return
	 */
	public IPropertySelectionModel getPrivileges() {
		List<ResourceType> resourcetype = getResourceService()
				.findResourceTypeBy(1, Integer.MAX_VALUE, "id", false,
						getSearchExpressions());
		//过滤掉父类对象，只放子类的对象------------------------------
		Set<ResourceType> parentType = new HashSet<ResourceType>();
		for(ResourceType type:resourcetype){
			if(type.getParent()!=null){
				parentType.add(type.getParent());
			}				
		}	
		if(parentType!=null){
		for(ResourceType parent: parentType){
			resourcetype.remove(parent);
		}
		}
		//------------------------------------------------------
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				resourcetype, ResourceType.class, "getName", "getId", false,
				null);
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
			Comics comics = (Comics) getModel();
			// ebook.getId();
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("rid", comics
					.getId(), CompareType.Equal);
			expressions.add(ex);
			// List<ResourceResType> list =
			// controller.findBy(ResourceResType.class, 1, 1000, expressions);
			List<ResourceResType> list = getResourceService()
					.findResourceResTypeBy(1, Integer.MAX_VALUE, "rid", false, expressions);
			ArrayList<ResourceType> arrayList = new ArrayList<ResourceType>();
			for (ResourceResType resType : list) {
				// arrayList.add(getResourceService().getresType.getResTypeId());
				arrayList.add(getResourceService().getResourceType(
						resType.getResTypeId()));
			}

			return arrayList;
		}

	}

	public IPropertySelectionModel getPrivileges2() {
		List<ResourceAuthor> resourceauthor = getResourceService()
				.findResourceAuthorBy(1, Integer.MAX_VALUE, "initialLetter", false,
						new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				resourceauthor, ResourceAuthor.class, "getPenName", "getId",
				false, null);
		return model;
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
			Comics ebook = (Comics) getModel();
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
	public abstract String getCopyRightName();
	public abstract void setCopyRightName(String copyRightName);
	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Comics());
		}
		ResourceAll resource = (ResourceAll)getModel();
		if(resource.getCopyrightId()!=null){
			ResourceReferen reference = getResourceService().getResourceReferen(resource.getCopyrightId());
			if(reference!=null)
				setCopyRightName(reference.getName()) ;	
		}
	}

	public String getPreAddress() {
		ResourceAll resource = (ResourceAll) getModel();
		String url = "";
		if (resource instanceof Ebook) { // 图书
			Ebook book = (Ebook) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					book.getBookPic());
		}
		if (resource instanceof Magazine) { // 杂志
			Magazine magazine = (Magazine) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					magazine.getImage());
		}
		if (resource instanceof NewsPapers) { // 报纸
			NewsPapers newsPapers = (NewsPapers) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					newsPapers.getImage());
		}
		if (resource instanceof Comics) { // 漫画
			Comics comics = (Comics) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					comics.getImage());
		}

		String imgPath = "<img src='" + url
				+ "' width='100' height='100' align='absmiddle'/>";
		return imgPath;
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

	@InjectPage("resource/ShowComicsChapterPage")
	public abstract ShowComicsChapterPage getShowComicsChapterPage();

	public IPage showChapter(ResourceAll resourceAll) {
		getShowComicsChapterPage().setResourceAll(resourceAll);
		return getShowComicsChapterPage();

	}

	public IPage showTome(ResourceAll resourceAll) {
		getShowTomePage().setResourceAll(resourceAll);
		return getShowTomePage();
	}
	
	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage getShowEbookPage();
	
	/*public IPage cancelPage(IRequestCycle cycle) {	
		getShowEbookPage().setResourceType(ResourceType.TYPE_COMICS);
		return getShowEbookPage();
	}*/
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
	/*public IPage savePage(IRequestCycle cycle){
		if(save()){
			getShowEbookPage().setResourceType(ResourceType.TYPE_COMICS);
			return getShowEbookPage();
		}else
			return this;
	}*/
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
}