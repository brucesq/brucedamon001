package com.hunthawk.reader.page.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "resource" }, mode = Restrict.Mode.ROLE)
public abstract class ShowChapterImagePage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract EbookChapter getEbookChapter();

	public abstract void setEbookChapter(EbookChapter ebookChapter);

	public abstract String getImageIndex();

	public abstract void setImageIndex(String index);

	public abstract IUploadFile getUploadFile();

	public abstract IUploadFile getUploadFile1();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return EbookChapter.class;
	}

	public boolean isCheckImage() {
		EbookChapter chapter = getEbookChapter();
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
			EbookChapter chapter = getEbookChapter();
			String errormessage = "";
			if (isModelNew()) {
				System.out.println("---�ϴ���-----");
			} else {
				String imageIndex = getImageIndex();// �õ�ͼƬ���
				if (getUploadFile() != null) {	
					String[] images = chapter.getImages();// �õ�ͼƬ�ļ���
					String imagesName = "";
					String uploadfileName = getUploadFile().getFileName()
							.substring(
									getUploadFile().getFileName().lastIndexOf(
											"\\") + 1);

					if (images.length == 0) // �����ǿյ�
						imagesName = uploadfileName;
					else
						imagesName = images[Integer.parseInt(imageIndex)];// �õ�ͼƬ������,У���ͼƬ����
					String uploadName = uploadfileName.substring(0,
							uploadfileName.length() - 4);// �ϴ�ͼƬ����
					if (!uploadName.equals(imagesName.substring(0, imagesName
							.length() - 4)))
						throw new Exception("�ļ�����ͬ��ǰͼƬ���Ʋ���Ӧ��");
					File dir = new File(getResourceService().getChapterAddress(
							chapter.getResourceId()));
					File fileExit = new File(dir + File.separator + imagesName);
					if (fileExit.exists()){
						getResourceService().deleteFile(fileExit.toString());// ֱ��ɾ��
						getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, new String[]{fileExit.getAbsolutePath()});
					}
					String image_name = chapter.getImageName();// �õ�ͼƬString
																// �ַ�����ɾ���������µ�ͼ
					String[] splitImage = image_name.split(imagesName + ",");
					if (splitImage.length == 0) // ������һ��
						image_name = uploadfileName + ",";
					else if (splitImage.length == 1) // �������һ��
						image_name = splitImage[0] + uploadfileName + ",";// ƴ������
					else
						// �����ж���
						image_name = splitImage[0] + uploadfileName + ","
								+ splitImage[1];// ƴ������
					errormessage = getResourceService().upload(getUploadFile(),
							imagesName.substring(0, imagesName.length() - 4),
							dir);
					//ͬ��ͼƬ��	
					chapter.setImageName(image_name);
					File currentFile = new File(dir + File.separator + uploadfileName); //���õ����Ǹ�ͼƬ
					if ("".equals(errormessage)){
						getResourceService().updateResourceChapter(chapter);
						getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{currentFile.getAbsolutePath()});
					}
				}
				else if (getUploadFile1() != null) {
					String imageName = chapter.getImageName();
					String index = chapter.getId();// �õ�ID��
					String uploadFileName = getUploadFile1().getFileName()
							.substring(
									getUploadFile1().getFileName().lastIndexOf(
											"\\") + 1);
					if (!uploadFileName.startsWith(index))
						throw new Exception("ͼƬ����Ӧ����" + index + "��ʼ��ͼƬ");
					if (chapter.getImageName() != null
							&& !"".equals(chapter.getImageName())) {
						if (chapter.getImageName().indexOf(
								uploadFileName.substring(0, uploadFileName
										.length() - 4)) != -1)
							throw new Exception("��ͼƬ�Ѿ����ڣ����ҵ���Ӧ��ͼƬ��ȷ���Ƿ��滻��");
						String[] currentImageList =  chapter.getImages();//��ǰͼƬ�б�
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
					
					//ͬ��ͼƬ��					
					chapter.setImageName(imageName);
					File currentFile = new File(dir + File.separator + uploadFileName); //���õ����Ǹ�ͼƬ
					if ("".equals(errormessage)){
						getResourceService().updateResourceChapter(chapter);
						getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD, new String[]{currentFile.getAbsolutePath()});
					}
				}
				else{
					getResourceService().updateResourceChapter(chapter);
				}
			}
			
			ResourceAll resource = getResourceService().getResource(
					chapter.getResourceId());
			if (resource != null) {
				resource.setStatus(1);
				getResourceService().updateResource(resource,
						resource.RESOURCE_TYPE_BOOK);
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

	public String  reImageName(String imageName){
		// 5000003_2.png ---->50000003_3.png
		String[] str1 = imageName.split("_");
		String str11 = str1[1];
		String[] str2 = str11.split("\\.");
		Integer number = Integer.parseInt(str2[0]);//�õ��˵�ǰ��ͼƬ���������
		
		return str1[0]+"_"+(number+1)+"."+str2[1];
	}
	/**
	 * �ļ�������
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public boolean fileReName(String oldName,String newName){
		File oldFile = new File(oldName);
		File newFile = new File(newName);
		if(!oldFile.exists())
			return false;
		else{
			return oldFile.renameTo(newFile);
		}
	}
	
	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new EbookChapter());
		}

	}

	public String getPreAddress() {
		// �õ���Դ�е�ͼƬ
		EbookChapter chapter = getEbookChapter();
		String[] chapterImages = chapter.getImages();
		String imgPath = "";
		if (chapterImages.length == 0)
			imgPath = "û��ͼƬ������Ϊ�ϴ�ͼƬ";
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

	// ��һ��
	public IPage previousImage(EbookChapter ebookChapter, String imageIndex) {
		Integer index = 0;
		Integer images = ebookChapter.getImages().length; // �õ����е�ͼƬ����
		if (imageIndex != null) {
			if ("0".equals(imageIndex)) { // �õ���ǰ��ҳ���ǵ�һҳ��ʱ��
				index = Integer.parseInt(imageIndex);
				getDelegate().setFormComponent(null);
				getDelegate().record("��ǰ�ǵ�һ��", null);
			} else
				index = Integer.parseInt(imageIndex) - 1;
		}
		this.setImageIndex(index.toString());
		this.setEbookChapter(ebookChapter);
		this.setModel(ebookChapter);
		return this;
	}

	// ��һ��
	public IPage nextImage(EbookChapter ebookChapter, String imageIndex) {
		Integer index = 0;
		Integer images = ebookChapter.getImages().length - 1; // �õ����е�ͼƬ����
		if (imageIndex != null) {
			if (imageIndex.equals(images.toString())) { // �õ���ǰ��ҳ�������һҳ��ʱ��
				index = Integer.parseInt(imageIndex);
				getDelegate().setFormComponent(null);
				getDelegate().record("��ǰ������һ��", null);
			} else
				index = Integer.parseInt(imageIndex) + 1;
		}
		this.setImageIndex(index.toString());
		this.setEbookChapter(ebookChapter);
		this.setModel(ebookChapter);
		return this;
	}
	/**
	 * ɾ��ͼƬ
	 * �½ڶ���
	 * ͼƬ��ʾ���
	 */
	public IPage deleteImage(EbookChapter ebookChapter, String imageIndex) {
		Integer index = 0;
		Integer images = ebookChapter.getImages().length - 1; // �õ����е�ͼƬ����
		String[] imageList =  ebookChapter.getImages();//���ͼƬ������
		String inmageName = ebookChapter.getImageName();//�õ�ͼƬ�Ĵ�ŵ��ַ���
		if (imageIndex != null && inmageName!=null) {
			if(imageList.length==1){ //��һ��
				inmageName = "";
			}else{
			if("0".equals(imageIndex)){   //�õ����ǵ�һ��
				String currentImage = imageList[0];//��ǰͼƬ������
				String[] currentImages = inmageName.split(currentImage+",");
				inmageName = currentImages[1];
				index = 0;
			}
			else if (imageIndex.equals(images.toString())) { // �õ���ǰ��ҳ�������һҳ��ʱ��
				String currentImage = imageList[Integer.parseInt(imageIndex)];//����ͼƬ
				String[] currentImages = inmageName.split(currentImage+",");
				inmageName = currentImages[0];
				index = 0;
			} else{  //�õ������м��ͼƬ
				String currentImage = imageList[Integer.parseInt(imageIndex)];//����ͼƬ
				String[] currentImages = inmageName.split(currentImage+",");
				inmageName = currentImages[0]+currentImages[1];
				index = Integer.parseInt(imageIndex);
				}
			}
		}
		this.setImageIndex(index.toString());
		ebookChapter.setImageName(inmageName);
		try {
			getResourceService().updateResourceChapter(ebookChapter);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		this.setEbookChapter(ebookChapter);
		this.setModel(ebookChapter);
		return this;
	}
	
	public String getAlertInfo() {
		EbookChapter chapter = getEbookChapter();
		if(chapter!=null){
		if (chapter.getImageName() != null && getImageIndex()!=null && !"".equals(chapter.getImageName())){
			System.out.println(chapter.getImageName());
			Integer index = Integer.parseInt(getImageIndex());
			String[] chapterImages = chapter.getImages();
			return "��ǰͼƬ����Ϊ��" + chapterImages[index]
			              					+ "���滻ʱ���밴�մ�ͼƬ�����滻��ͼƬ��ʽ����Ϊ��gif/jpg/png,"+
			              					"��ǰͼƬ����Ϊ��"+chapterImages.length+"��������ͼƬʱ���밴�������...ͼƬ����λ��Ϊ��ǰͼƬ��";
		}else{
			return "��ǰ�½�IDΪ��" + chapter.getId()
			+ ",�ϴ�ͼƬʱ���밴�գ����½ں�+'_'+ͼƬ�����µĵ�λ�ã����磺10000053_1.gif";
		}	
		}else{
		return "";
		}
	}
}