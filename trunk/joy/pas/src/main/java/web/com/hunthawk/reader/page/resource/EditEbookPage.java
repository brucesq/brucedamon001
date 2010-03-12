package com.hunthawk.reader.page.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
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
import com.hunthawk.reader.enhance.util.UnzipFile;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;

/**
 * @author xianlaichen
 * 
 */
@Restrict(roles = { "resourcechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditEbookPage extends EditPage implements
		PageBeginRenderListener {

	@SuppressWarnings("unused")
	private String resourceId;
	private File uploadFile = null;// �����ϴ������ص�

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
		return Ebook.class;
	}

	@Override
	protected boolean persist(Object object) {
		try {
			String errorMessage = "";
			Ebook ebook = (Ebook) object;
			if (!isModelNew()) {
				// �޸���Դ����ID
				if (getUploadFileImage() != null) {// �ϴ��˷���ͼƬ
					File dir = new File(getResourceService().getChapterAddress(
							ebook.getId()));
					if (!dir.exists())
						dir.mkdirs();
					String uploadfileName = getUploadFileImage().getFileName()
							.substring(
									getUploadFileImage().getFileName()
											.lastIndexOf("\\") + 1);

					boolean resIsRightFormat = isRightFormat(uploadfileName);
					if (!resIsRightFormat) {
						// �ļ���ʽ�ж�
						throw new Exception("ͼƬ��ʽ����ȷ[gif/jpg/png]��������ѡ��!");
					}

					if (dir.exists()) { // ɾ���ļ�
						for (File file : dir.listFiles()) {
							if (file.getName().startsWith("cover")) {
								getResourceService()
										.deleteFile(file.toString());
								// ͬ��ɾ��ͼƬ��
								getResourceService()
										.rsyncUploadFile(
												ResourceUtil.RSYNC_TYPE_DEL,
												new String[] { file
														.getAbsolutePath() });
							}
						}
					}
					errorMessage = getResourceService().upload(
							getUploadFileImage(), "", dir);
					if (!"".equals(errorMessage))
						throw new Exception(errorMessage);

					// ���ļ���������
					File file = new File(dir + File.separator + uploadfileName);// �ϴ������������ļ�����
					String fileName = "cover." + uploadfileName.split("\\.")[1];

					File newFile = new File(dir + File.separator + fileName);
					file.renameTo(newFile);

					getUploadService()
							.resizeCoverFile(dir.toString(), fileName);

					// ͬ��ͼƬ��
					getUploadService().rsyncDirectry(dir);
					ebook.setBookPic(fileName);
				}
				// ___________����_______________
				Integer copyrighId = getCopyrightId();
				if (copyrighId != null && !"".equals(copyrighId)) {
					if (!copyrighId.equals(ebook.getCopyrightId())) { // �����޸���
						ebook.setLastCopyrightId(ebook.getCopyrightId());
						ebook.setCopyrightId(copyrighId);
					}
				}
				UserImpl user = (UserImpl) getUser();
				ebook.setModifierId(user.getId());
				if (selectedResourceType.size() == 0)
					throw new Exception("��ѡ����Դ���࣡");
				if (StringUtils.isEmpty(getAuthorIDs()))
					throw new Exception("��ѡ������");
				// String authorId = auId(selectedResourceAuthor);
				ebook.setAuthorId(getAuthorIDs());
				getResourceService().updateResource(ebook,
						ResourceAll.RESOURCE_TYPE_BOOK);
				// ������Դid�����ù�ϵ��ɾ��
				getResourceService().deleteResourceResType(ebook.getId());
				// �����Դ����Դ�б��ϵ��
				for (int i = 0; i < selectedResourceType.size(); i++) {
					ResourceType type = (ResourceType) selectedResourceType
							.get(i);
					ResourceResType rel = new ResourceResType();
					rel.setRid(ebook.getId());
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
		 * ���ض������� ObjectPropertySelectionModel parentProper = new
		 * ObjectPropertySelectionModel(
		 * getResourceService().getAllResourceReferen(), ResourceReferen.class,
		 * "getName", "getId", false, ""); return parentProper;
		 */
		// ���ص���
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

		HibernateExpression nameE = new CompareExpression("showType", 1,
				CompareType.Equal);
		hibernateExpressions.add(nameE);

		return hibernateExpressions;
	}

	public IPropertySelectionModel getPrivileges() {
		List<ResourceType> resourcetype = getResourceService()
				.findResourceTypeBy(1, Integer.MAX_VALUE, "id", false,
						getSearchExpressions());
		// ���˵��������ֻ������Ķ���------------------------------
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
		if (isModelNew()) {// ������
			return new ArrayList();
		} else {// �޸ĵ�
			Ebook ebook = (Ebook) getModel();
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
		if (isModelNew()) {// ������
			return new ArrayList();
		} else {// �޸ĵ�
			Ebook ebook = (Ebook) getModel();
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

		// ���ص���
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
	 * ����ϴ��ļ��Ĵ��Ŀ¼
	 */
	protected File getUploadDir() {
		// �����ݿ�������Ϣ�еõ�����������ϴ��ļ���·��
		// Variables variables = getResourceService().getVariables("upload");
		// String uploadDir = variables.getValue();
		String uploadDir = "D:\\upload\\ebook";
		// ��GUID��Ϊ�ϴ��ļ��Ľ�ѹ��Ŀ¼����֤Ŀ¼Ψһ
		// RandomGUID randomGuid = new RandomGUID();
		// String guidStr = randomGuid.toString();

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
			setModel(new Ebook());
		}
		// ��ʼ���Ǹ���Ȩ�����ƣ�
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
		ResourceAll resource = (ResourceAll) getModel();
		String url = "";
		if (resource instanceof Ebook) { // ͼ��
			Ebook book = (Ebook) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					book.getBookPic());
		}
		if (resource instanceof Magazine) { // ��־
			Magazine magazine = (Magazine) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					magazine.getImage());
		}
		if (resource instanceof NewsPapers) { // ��ֽ
			NewsPapers newsPapers = (NewsPapers) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					newsPapers.getImage());
		}
		if (resource instanceof Comics) { // ����
			Comics comics = (Comics) resource;
			url = getResourceService().getPreviewCoverImg(resource.getId(),
					comics.getImage());
		}

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
}