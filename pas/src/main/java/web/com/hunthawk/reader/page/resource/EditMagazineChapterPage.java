package com.hunthawk.reader.page.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookTome;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.enhance.util.UnzipFile;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "resourcechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditMagazineChapterPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	public abstract ResourceAll getResourceAll();

	public abstract void setResourceAll(ResourceAll resourceAll);

	public abstract IUploadFile getUploadFile();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return MagazineChapter.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			MagazineChapter chapter = (MagazineChapter) object;
			String errorMessage = "";

			if (getResourceAll() != null)
				chapter.setResourceId(getResourceAll().getId());
			if (chapter.getContent() != null
					&& !"".equals(chapter.getContent()))
				chapter.setChapterSize(chapter.getContent().length());
			String chapterIndex = chapter.getChapterIndex().toString();
			File dir = new File(getResourceService().getChapterAddress(
					chapter.getResourceId()));
			if (!dir.exists())
				dir.mkdirs();
			if (isModelNew()) // ������
			{	
				int currentinput = chapter.getChapterIndex();
				List<MagazineChapter> chapterList = getResourceService().getResourceChapter(MagazineChapter.class, getResourceAll().getId());//�õ�������������List����
				int i=0;
				int totalsize=0;
				if(chapterList !=null ){ //�Ѿ������½ڶ���
					totalsize = chapterList.size();//��ȡ���½���
					for(i=currentinput; i<=totalsize;i++)
					{
						MagazineChapter magazineChapter = chapterList.get(i-1);//������ţ��õ� �б��ж�Ӧ ��ŵ��Ǹ� ����
						magazineChapter.setChapterIndex(i+1);
						getResourceService().updateResourceChapter(magazineChapter);
					}
					//�����ӵĲ����϶��������������ӡ���������ţ������ܹ�10���½� ��������½ں���100�����԰�
					if(currentinput>(totalsize+1)) 
						throw new Exception("��ǰ��������ǡ�"+totalsize+"��,�밴�������...");
				}else{// ������û���½� �������һ��
					chapter.setChapterIndex(1); //ǿ�Ƹ��½�����Ϊ 1��
				}

				getResourceService().addResourceChapter(chapter,
						ResourceAll.RESOURCE_TYPE_MAGAZINE);
			} else { // ����
				getResourceService().updateResourceChapter(chapter);
			}
			String chapterImageName = "";
			if (getUploadFile() != null) {
				errorMessage = getResourceService().upload(getUploadFile(),
						"zip", dir);
				if (!"".equals(errorMessage))
					throw new Exception(errorMessage);
				String fileName = getUploadFile().getFileName().substring( // �ϴ����ļ�����
						getUploadFile().getFileName().lastIndexOf("\\") + 1);
				// ��ȡ��Դ���⿪���ļ������ƣ���book.zip��ѹ�󣬵õ�����book
				String uploadFileDir = fileName.substring(0,
						fileName.length() - 4);

				// ��ѹĿ¼
				String unUploadFileDir = dir.toString();
				// ��ѹ��Դ������
				// String unUploadFileName =
				// dir.toString()+"/"+uploadFile.getName();
				String unUploadFileName = dir.toString() + File.separator
						+ fileName;

				// ��ѹĿ¼
				File file2 = new File(unUploadFileName);
				File dir2 = new File(unUploadFileDir);
				UnzipFile.unzip(file2, dir2);
				// ��ȡĿ¼�µ��ļ�
				List list = containsDirectory(dir.toString() + File.separator
						+ uploadFileDir, chapterIndex);
				if(list !=null){
					Collections.sort(list);//����
				for (int i =0; i<list.size(); i++) {
					String dirNames=(String)list.get(i);
					String[] dirNameList =dirNames.split("_");
					String dirImage = chapter.getId()+"_"+dirNameList[1];
					copy(dir.toString() + File.separator + uploadFileDir+ File.separator + list.get(i), 
							dir.toString()+ File.separator + dirImage);
					//ͬ�����˴�������ÿ��ͼƬ�ĵ���ͬ����
					getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{dir.toString()+ File.separator + dirImage});
					chapterImageName += dirImage + ",";
					}
				}
				// ɾ���ϴ��ĺͽ�ѹ�ļ�
				getResourceService().deleteFile(
						dir.toString() + File.separator + uploadFileDir);
				getResourceService().deleteFile(unUploadFileName);
			}
			if(!"".equals(chapterImageName))
				chapter.setImageName(chapterImageName); // ������ͼƬ
			getResourceService().updateResourceChapter(chapter);
			ResourceAll resouceAll = getResourceAll();
			UserImpl user = (UserImpl) getUser();
			resouceAll.setModifierId(user.getId());
			getResourceService().updateResource(resouceAll,
					ResourceAll.RESOURCE_TYPE_MAGAZINE);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new MagazineChapter());
		}

	}

	private List containsDirectory(String dir, String fileName) {
		File dirFile = new File(dir);
		List list = new ArrayList();
		if (dirFile.exists() && dirFile.isDirectory()) {
			for (String file : dirFile.list()) {
				if (file.startsWith(fileName)) {
					list.add(file);
				}
			}
		}
		return list;
	}

	public String getPreAddress() {
		// �õ���Դ�е�ͼƬ
		MagazineChapter chapter = (MagazineChapter) getModel();
		String[] chapterImages = chapter.getImages();
		String imgPath = "";
		for (int i = 0; i < chapterImages.length; i++) {
			String url = getResourceService().getChapterImg(
					chapter.getResourceId(), chapterImages[i]);
			imgPath += "<img src='" + url
					+ "' width='40' height='40' align='absmiddle'/>";
		}
		return imgPath;
	}

	public IPropertySelectionModel getResourceTomeList() {
		String resourceId = "";
		MagazineChapter chapter = (MagazineChapter) getModel();
		if (getResourceAll() == null)
			resourceId = chapter.getResourceId();
		else
			resourceId = getResourceAll().getId();
		List<EbookTome> list1 = getResourceService().getEbookTomeByResourceId(
				resourceId);
		Map<String, String> map = new HashMap<String, String>();
		for (EbookTome tome : list1) {
			map.put(tome.getName(), tome.getId());
		}
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}

	/***************************************************************************
	 * �ļ�����
	 * 
	 * @param Դ�ļ�
	 * @param Ŀ���ļ�
	 * @return
	 */
	public boolean copy(String file1, String file2) {
		try {
			File file_in = new File(file1);
			File file_out = new File(file2);
			FileInputStream in1 = new FileInputStream(file_in);
			FileOutputStream out1 = new FileOutputStream(file_out);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = in1.read(bytes)) != -1)
				out1.write(bytes, 0, c);
			in1.close();
			out1.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
