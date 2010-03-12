package com.hunthawk.reader.page.resource;

import java.io.File;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "resource" }, mode = Restrict.Mode.ROLE)
public abstract class ShowMagazineChapterImagePage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract MagazineChapter getMagazineChapter();

	public abstract void setMagazineChapter(MagazineChapter magazineChapter);

	public abstract String getImageIndex();

	public abstract void setImageIndex(String index);

	public abstract IUploadFile getUploadFile();

	public abstract IUploadFile getUploadFile1();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return MagazineChapter.class;
	}

	public boolean isCheckImage() {
		MagazineChapter chapter = getMagazineChapter();
		if(chapter!=null){
			if (chapter.getImageName() == null || "".equals(chapter.getImageName()))
				return false;
			else
				return true;
			}else
				return false;
	}

	@Override
	public void saveAndReturn(IRequestCycle cycle) {
		if (save()) {
			return;
		}
	}

	@Override
	protected boolean persist(Object object) {
		try {
			MagazineChapter chapter = getMagazineChapter();
			String errormessage = "";
			if (isModelNew()) {
				System.out.println("---上传了-----");
			} else {
				String imageIndex = getImageIndex();// 得到图片序号
				if (getUploadFile() != null) {
					String[] images = chapter.getImages();// 得到图片的集合
					String imagesName = "";
					String uploadfileName = getUploadFile().getFileName()
							.substring(
									getUploadFile().getFileName().lastIndexOf(
											"\\") + 1);

					if (images.length == 0) // 表明是空的
						imagesName = uploadfileName;
					else
						imagesName = images[Integer.parseInt(imageIndex)];// 得到图片的名称,校验的图片名称
					String uploadName = uploadfileName.substring(0,
							uploadfileName.length() - 4);// 上传图片名称
					if (!uploadName.equals(imagesName.substring(0, imagesName
							.length() - 4)))
						throw new Exception("文件名称同当前图片名称不对应！");
					File dir = new File(getResourceService().getChapterAddress(
							chapter.getResourceId()));
					File fileExit = new File(dir + File.separator + imagesName);
					if (fileExit.exists()){
						getResourceService().deleteFile(fileExit.toString());// 直接删除
						getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, new String[]{fileExit.getAbsolutePath()});
					}
					String image_name = chapter.getImageName();// 得到图片String
																// 字符串，删除，加入新的图
					String[] splitImage = image_name.split(imagesName + ",");
					if (splitImage.length == 0) // 表明就一张
						image_name = uploadfileName + ",";
					else if (splitImage.length == 1) // 表明最后一张
						image_name = splitImage[0] + uploadfileName + ",";// 拼接起来
					else
						// 表明有多张
						image_name = splitImage[0] + uploadfileName + ","
								+ splitImage[1];// 拼接起来
					errormessage = getResourceService().upload(getUploadFile(),
							imagesName.substring(0, imagesName.length() - 4),
							dir);
					chapter.setImageName(image_name);
					File currentFile = new File(dir + File.separator + uploadfileName); //最后得到的那个图片
					if ("".equals(errormessage)){
						getResourceService().updateResourceChapter(chapter);
						getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{currentFile.getAbsolutePath()});
					}
				}
				if (getUploadFile1() != null) {
					String imageName = chapter.getImageName();
					String index = chapter.getId();// 得到ID号
					String uploadFileName = getUploadFile1().getFileName()
							.substring(
									getUploadFile1().getFileName().lastIndexOf(
											"\\") + 1);
					if (!uploadFileName.startsWith(index))
						throw new Exception("图片名称应该以" + index + "开始的图片");
					if (chapter.getImageName() != null
							&& !"".equals(chapter.getImageName())) {
						if (chapter.getImageName().indexOf(
								uploadFileName.substring(0, uploadFileName
										.length() - 4)) != -1)
							throw new Exception("此图片已经存在！请找到相应的图片，确认是否替换！");
						String[] currentImageList =  chapter.getImages();//当前图片列表
						imageName="";
						for(int i=0;i<=Integer.parseInt(imageIndex);i++){
							imageName += currentImageList[i]+",";
						}
						imageName += uploadFileName + ",";
						for(int i=Integer.parseInt(imageIndex)+1;i<currentImageList.length;i++){
							String oldName = currentImageList[i];
							imageName +=oldName+",";
						}
					}
					else{
						if(imageName==null)
							imageName="";
						imageName += uploadFileName + ",";
					}
					File dir = new File(getResourceService().getChapterAddress(
							chapter.getResourceId()));
					errormessage = getResourceService().upload(
							getUploadFile1(), index, dir);
					
					chapter.setImageName(imageName);
					File currentFile = new File(dir + File.separator + uploadFileName); //最后得到的那个图片
					if ("".equals(errormessage)){
						getResourceService().updateResourceChapter(chapter);
						getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{currentFile.getAbsolutePath()});
					}
				}
			}
			ResourceAll resource = getResourceService().getResource(
					chapter.getResourceId());
			if (resource != null) {
				resource.setStatus(1);
				getResourceService().updateResource(resource,
						ResourceAll.RESOURCE_TYPE_MAGAZINE);
			}
			if (!"".equals(errormessage))
				throw new Exception(errormessage);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new MagazineChapter());
		}

	}

	public String getPreAddress() {
		// 得到资源中的图片
		MagazineChapter chapter = getMagazineChapter();
		String[] chapterImages = chapter.getImages();
		String imgPath = "";
		if (chapterImages.length == 0)
			imgPath = "没有图片！下面为上传图片";
		else {
			Integer index = 0;
			if (getImageIndex() != null)
				index = Integer.parseInt(getImageIndex());
			String url = getResourceService().getChapterImg(
					chapter.getResourceId(), chapterImages[index]);
			imgPath += "<img src='" + url + "' align='absmiddle'/>";
		}
		return imgPath;

	}

	// 上一张
	public IPage previousImage(MagazineChapter magazineChapter,
			String imageIndex) {
		Integer index = 0;
		Integer images = magazineChapter.getImages().length; // 得到所有的图片数量
		if (imageIndex != null) {
			if ("0".equals(imageIndex)) { // 得到当前的页面是第一页的时候
				index = Integer.parseInt(imageIndex);
				getDelegate().setFormComponent(null);
				getDelegate().record("当前是第一张", null);
			} else
				index = Integer.parseInt(imageIndex) - 1;
		}
		this.setImageIndex(index.toString());
		this.setMagazineChapter(magazineChapter);
		this.setModel(magazineChapter);
		return this;
	}

	// 下一张
	public IPage nextImage(MagazineChapter magazineChapter, String imageIndex) {
		Integer index = 0;
		Integer images = magazineChapter.getImages().length - 1; // 得到所有的图片数量
		if (imageIndex != null) {
			if (imageIndex.equals(images.toString())) { // 得到当前的页面是最后一页的时候
				index = Integer.parseInt(imageIndex);
				getDelegate().setFormComponent(null);
				getDelegate().record("当前是最后第一张", null);
			} else
				index = Integer.parseInt(imageIndex) + 1;
		}
		this.setImageIndex(index.toString());
		this.setMagazineChapter(magazineChapter);
		this.setModel(magazineChapter);
		return this;
	}
	/**
	 * 删除图片
	 * 章节对象
	 * 图片显示序号
	 */
	public IPage deleteImage(MagazineChapter magazineChapter, String imageIndex) {
		Integer index = 0;
		Integer images = magazineChapter.getImages().length - 1; // 得到所有的图片数量
		String[] imageList =  magazineChapter.getImages();//存放图片的数组
		String inmageName = magazineChapter.getImageName();//得到图片的存放的字符串
		if (imageIndex != null && inmageName!=null) {
			if(imageList.length==1){ //就一张
				inmageName = "";
			}else{
			if("0".equals(imageIndex)){   //得到的是第一张
				String currentImage = imageList[0];//当前图片的名称
				String[] currentImages = inmageName.split(currentImage+",");
				inmageName = currentImages[1];
				index = 0;
			}
			else if (imageIndex.equals(images.toString())) { // 得到当前的页面是最后一页的时候
				String currentImage = imageList[Integer.parseInt(imageIndex)];//当期图片
				String[] currentImages = inmageName.split(currentImage+",");
				inmageName = currentImages[0];
				index = 0;
			} else{  //得到的是中间的图片
				String currentImage = imageList[Integer.parseInt(imageIndex)];//当期图片
				String[] currentImages = inmageName.split(currentImage+",");
				inmageName = currentImages[0]+currentImages[1];
				index = Integer.parseInt(imageIndex);
				}
			}
		}
		this.setImageIndex(index.toString());
		magazineChapter.setImageName(inmageName);
		try {
			getResourceService().updateResourceChapter(magazineChapter);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		this.setMagazineChapter(magazineChapter);
		this.setModel(magazineChapter);
		return this;
	}
	public String getAlertInfo() {
		MagazineChapter chapter = getMagazineChapter();
		if(chapter!=null){
			if (chapter.getImageName() != null && getImageIndex()!=null && !"".equals(chapter.getImageName())){
				Integer index = Integer.parseInt(getImageIndex());
				String[] chapterImages = chapter.getImages();
				return "当前图片名称为：" + chapterImages[index]
				              					+ "，替换时，请按照此图片名称替换！图片格式可以为：gif/jpg/png,"+
				              					"当前图片总数为【"+chapterImages.length+"】，插入图片时，请按此序号续...图片插入位置为当前图片后";
			}else{
				return "当前章节ID为：" + chapter.getId()
				+ ",上传图片时，请按照：此章节号+'_'+图片在文章的的位置，例如：10000053_1.gif";
			}	
			}else{
				return "";
			}
	}
}