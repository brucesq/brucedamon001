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
			if (isModelNew()) // 新增加
			{	
				int currentinput = chapter.getChapterIndex();
				List<MagazineChapter> chapterList = getResourceService().getResourceChapter(MagazineChapter.class, getResourceAll().getId());//得到按照序号排序的List集合
				int i=0;
				int totalsize=0;
				if(chapterList !=null ){ //已经存在章节对象
					totalsize = chapterList.size();//获取总章节数
					for(i=currentinput; i<=totalsize;i++)
					{
						MagazineChapter magazineChapter = chapterList.get(i-1);//根据序号，得到 列表中对应 序号的那个 对象
						magazineChapter.setChapterIndex(i+1);
						getResourceService().updateResourceChapter(magazineChapter);
					}
					//如果添加的不符合对象，请教他重新添加。超出了序号，例如总共10个章节 你添加了章节号是100，不对啊
					if(currentinput>(totalsize+1)) 
						throw new Exception("当前最大的序号是【"+totalsize+"】,请按此序号续...");
				}else{// 表明是没有章节 新添加了一个
					chapter.setChapterIndex(1); //强制给章节设置为 1；
				}

				getResourceService().addResourceChapter(chapter,
						ResourceAll.RESOURCE_TYPE_MAGAZINE);
			} else { // 更新
				getResourceService().updateResourceChapter(chapter);
			}
			String chapterImageName = "";
			if (getUploadFile() != null) {
				errorMessage = getResourceService().upload(getUploadFile(),
						"zip", dir);
				if (!"".equals(errorMessage))
					throw new Exception(errorMessage);
				String fileName = getUploadFile().getFileName().substring( // 上传的文件名称
						getUploadFile().getFileName().lastIndexOf("\\") + 1);
				// 获取资源包解开后文件夹名称，如book.zip解压后，得到的是book
				String uploadFileDir = fileName.substring(0,
						fileName.length() - 4);

				// 解压目录
				String unUploadFileDir = dir.toString();
				// 解压资源包名称
				// String unUploadFileName =
				// dir.toString()+"/"+uploadFile.getName();
				String unUploadFileName = dir.toString() + File.separator
						+ fileName;

				// 解压目录
				File file2 = new File(unUploadFileName);
				File dir2 = new File(unUploadFileDir);
				UnzipFile.unzip(file2, dir2);
				// 读取目录下的文件
				List list = containsDirectory(dir.toString() + File.separator
						+ uploadFileDir, chapterIndex);
				if(list !=null){
					Collections.sort(list);//排序
				for (int i =0; i<list.size(); i++) {
					String dirNames=(String)list.get(i);
					String[] dirNameList =dirNames.split("_");
					String dirImage = chapter.getId()+"_"+dirNameList[1];
					copy(dir.toString() + File.separator + uploadFileDir+ File.separator + list.get(i), 
							dir.toString()+ File.separator + dirImage);
					//同步，此处做的是每张图片的单独同步：
					getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{dir.toString()+ File.separator + dirImage});
					chapterImageName += dirImage + ",";
					}
				}
				// 删除上传文和解压文件
				getResourceService().deleteFile(
						dir.toString() + File.separator + uploadFileDir);
				getResourceService().deleteFile(unUploadFileName);
			}
			if(!"".equals(chapterImageName))
				chapter.setImageName(chapterImageName); // 更新下图片
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
		// 得到资源中的图片
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
	 * 文件拷贝
	 * 
	 * @param 源文件
	 * @param 目标文件
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
