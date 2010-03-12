package com.hunthawk.reader.page;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.security.simple.MockSecurityContext;
import com.hunthawk.framework.security.simple.SimpleVisit;
import com.hunthawk.reader.domain.OffLineLog;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.RandomGUID;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.enhance.util.UnzipFile;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

public class OfflineCover extends Thread {
	
	public static Integer status = 0;

	private SystemService systemService;
	private Integer mark;
	private ResourceService resourceService;
	private UploadService uploadService;
	private UserImpl user;
	public OfflineCover(SystemService systemService, 
			Integer mark,
			ResourceService resourceService,
			UploadService uploadService,UserImpl user){
		this.systemService = systemService;
		this.mark = mark;
		this.resourceService = resourceService;
		this.uploadService = uploadService;
		this.user = user;
	}
	public OfflineCover(){}

	public  boolean resIsRightFormat(String fileName) {
		String patt = "\\.(zip)$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}
	public void run(){
		System.out.println("--�Ҹոĵ��߳�---");
		
		MockSecurityContext context = new MockSecurityContext();
		SimpleVisit visit = new SimpleVisit();
		visit.setUser(user);
		context.setVisit(visit);
		SecurityContextHolder.setContext(context);
		FileOutputStream fos = null;
		
		String offLine_dir = systemService.getVariables("offLine_dir").getValue();//�õ������ϴ���ַ
		//String offLine_dir = "C:/var/www/offLine";
		File offLine = new File(offLine_dir);
		String coverFileName="cover.zip";
		String errormessage="";
		InputStreamReader fr = null;
		BufferedReader br = null;
		
		try{
		if(!offLine.exists()){
			//throw new Exception("��ַ������,����ȷ���µ�ַ");
			offLine.mkdirs();//�������ȴ���
		}
		List<String> list = new ArrayList<String>();
		if (offLine.exists() && offLine.isDirectory()) {
			for (String file : offLine.list()) {
				boolean resIsRightFormat = isRightFormat(file);//����ͼƬ�ļ��Ĳ��ӽ�ȥ����ͼƬ�ŵ�һ��list��
				if(resIsRightFormat)
					list.add(file);
			}
		}
		for(String dirFileName : list){
			//ͼƬ�ļ�����
			String[] imageNames = dirFileName.split("\\.");
			String resourceId = imageNames[0];//��ԴID��
			String imagePic = imageNames[1];//�ļ���չ��
			
			File dir = new File(resourceService.getChapterAddress(resourceId));
			if (!dir.exists())
				dir.mkdirs();
			if (dir.exists()) { // ɾ���ļ�
				for (File file : dir.listFiles()) {
					if (file.getName().startsWith("cover")) {
						resourceService
								.deleteFile(file.toString());
						//ͬ��ɾ��ͼƬ��
					//	resourceService.rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, new String[]{file.getAbsolutePath()});
					}
				}
			}
			//�ļ��������ѵ�ǰ��image ��������Ӧ����Դλ�ã�Ȼ��ü�
			File srcDir = new File(offLine_dir+File.separator+dirFileName);
			if(!srcDir.exists())
				continue;
			File destDir = new File(dir+File.separator+dirFileName);
			FileUtils.copyFile(srcDir, destDir);
			
			
			String fileName = "cover."+imagePic;				
			File newFile = new File(dir+File.separator+fileName);
			destDir.renameTo(newFile);
			//����
			uploadService.resizeCoverFile(dir.toString(),
					fileName);
			
			//ͬ��ͼƬ��
			uploadService.rsyncDirectry(dir);
			
			ResourceAll resourceAll = resourceService.getResource(resourceId);
			if(resourceAll==null)
				continue;
			resourceAll.setModifierId(user.getId());
			if(resourceAll instanceof Ebook){
				Ebook book = (Ebook) resourceAll;
				book.setBookPic(fileName);
				resourceService.updateResourceNOChangeStatus(resourceAll,ResourceAll.RESOURCE_TYPE_BOOK);
			}else if(resourceAll instanceof Magazine){
				Magazine magazine = (Magazine)resourceAll;
				magazine.setImage(fileName);
				resourceService.updateResourceNOChangeStatus(resourceAll,ResourceAll.RESOURCE_TYPE_MAGAZINE);
			}else if(resourceAll instanceof NewsPapers){
				NewsPapers newsPapers = (NewsPapers)resourceAll;
				newsPapers.setImage(fileName);
				resourceService.updateResourceNOChangeStatus(resourceAll,ResourceAll.RESOURCE_TYPE_NEWSPAPER);
			}else if(resourceAll instanceof Comics){
				Comics comics = (Comics)resourceAll;
				comics.setImage(fileName);
				resourceService.updateResourceNOChangeStatus(resourceAll,ResourceAll.RESOURCE_TYPE_COMICS);
			}
			
			FileUtils.forceDelete(srcDir);
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ioe) {
						ioe.getMessage();
					}
				}
			SecurityContextHolder.clearContext();
			status = 0;
		}
	}
	/*public void run(){
		System.out.println("------ִ�����߳�--------");
		MockSecurityContext context = new MockSecurityContext();
		SimpleVisit visit = new SimpleVisit();
		visit.setUser(user);
		context.setVisit(visit);
		SecurityContextHolder.setContext(context);
		FileOutputStream fos = null;
		*//**
		 * 1.�õ��ϴ��ĵ�ַ
		 *//*
		String offLine_dir = systemService.getVariables("offLine_dir").getValue();//�õ������ϴ���ַ
		//String offLine_dir = "C:/var/www/offLine";
		File offLine = new File(offLine_dir);
		String coverFileName="cover.zip";
		String errormessage="";
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {
			if(!offLine.exists()){
				//throw new Exception("��ַ������,����ȷ���µ�ַ");
				offLine.mkdirs();//�������ȴ���
			}
				
			if(!containsDirectory(offLine_dir,coverFileName))
				throw new Exception("û�з������ߵķ���ͼƬ�ϴ���Դ����");
			//��ѹ�ļ�

			String uploadFileDir = coverFileName.substring(0,
					coverFileName.length() - 4);
			
			// ��ѹĿ¼
			String unUploadFileDir = offLine.toString() + File.separator
					+ uploadFileDir;
			// ��ѹ��Դ������
			String unUploadFileName = offLine.toString() + File.separator
					+ coverFileName;

			// ��ѹĿ¼
			File file2 = new File(unUploadFileName);
			File dir2 = new File(unUploadFileDir);
			UnzipFile.unzip(file2, dir2);
			
			unUploadFileDir += File.separator
				+getNextDirectory(unUploadFileDir);
			File csvFile = new File(unUploadFileDir+File.separator+"cover.csv");
			if(!csvFile.exists())
				throw new Exception("û�з��������ļ�");
//------------------------------��ȡcsv ������ͼƬ��Ϣ----------------------------------------------------------------
				fr = new InputStreamReader(new FileInputStream(csvFile));
				br = new BufferedReader(fr);
				String rec = null;
				String[] argsArr = null;

				// ȥ����һ������
				br.readLine();
				while ((rec = br.readLine()) != null) {

					if (StringUtils.isEmpty(rec)) {
						continue;
					}
					argsArr = rec.split(",");
					if(argsArr.length<8)
						continue;
					String resourceId = trim(argsArr[0], true, "��ԴID");
					String picName = trim(argsArr[7], true, "��Ӧ����ͼƬ");
					File dir = new File(resourceService.getChapterAddress(resourceId));
					if (!dir.exists())
						dir.mkdirs();
					if (dir.exists()) { // ɾ���ļ�
						for (File file : dir.listFiles()) {
							if (file.getName().startsWith("cover")) {
								resourceService
										.deleteFile(file.toString());
								//ͬ��ɾ��ͼƬ��
								resourceService.rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, new String[]{file.getAbsolutePath()});
							}
						}
					}
					//�ļ��������ѵ�ǰ��image ������һ���ط���Ȼ��ü�
					File srcDir = new File(unUploadFileDir+File.separator+picName);
					if(!srcDir.exists())
						continue;
					File destDir = new File(dir+File.separator+picName);
					FileUtils.copyFile(srcDir, destDir);
					
					
					String fileName = "cover."+picName.split("\\.")[1];				
					File newFile = new File(dir+File.separator+fileName);
					destDir.renameTo(newFile);
					//����
					uploadService.resizeCoverFile(dir.toString(),
							fileName);
					
					//ͬ��ͼƬ��
					uploadService.rsyncDirectry(dir);
					
					ResourceAll resourceAll = resourceService.getResource(resourceId);
					if(resourceAll==null)
						continue;
					resourceAll.setModifierId(user.getId());
					if(resourceAll instanceof Ebook){
						Ebook book = (Ebook) resourceAll;
						book.setBookPic(fileName);
						resourceService.updateResource(resourceAll,ResourceAll.RESOURCE_TYPE_BOOK);
					}else if(resourceAll instanceof Magazine){
						Magazine magazine = (Magazine)resourceAll;
						magazine.setImage(fileName);
						resourceService.updateResource(resourceAll,ResourceAll.RESOURCE_TYPE_MAGAZINE);
					}else if(resourceAll instanceof NewsPapers){
						NewsPapers newsPapers = (NewsPapers)resourceAll;
						newsPapers.setImage(fileName);
						resourceService.updateResource(resourceAll,ResourceAll.RESOURCE_TYPE_NEWSPAPER);
					}else if(resourceAll instanceof Comics){
						Comics comics = (Comics)resourceAll;
						comics.setImage(fileName);
						resourceService.updateResource(resourceAll,ResourceAll.RESOURCE_TYPE_COMICS);
					}
				}	
				FileUtils.forceDelete(file2);
				FileUtils.forceDelete(dir2);
				
	//-p----------------------------------------------------------------------------------------------------------			
		} catch (Exception ioe) {
			ioe.printStackTrace();
			//cleanUploadDir(offLine);
		} finally {
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.getMessage();
				}
			}
			SecurityContextHolder.clearContext();
			status = 0;
		}
		
		//cleanUploadDir(offLine);
		//return false;	
	}*/
	
	public void cleanUploadDir(File dir) {
		// ����ϴ��ļ�Ŀ¼��Ŀ¼�������ļ�
		try {
			UnzipFile.dircleanup(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		private String trim(String value, boolean isNeed, String name)
		throws Exception {
			if (StringUtils.isEmpty(value)) {
				if (isNeed) {
					throw new Exception(name + "�ֶβ���Ϊ��.");
				}
				return value;
			} else {
				return value.trim();
			}
	
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
	
	public boolean containsDirectory(String dir, String fileName) {
		File dirFile = new File(dir);
		if (dirFile.exists() && dirFile.isDirectory()) {
			for (String file : dirFile.list()) {
				if (file.equalsIgnoreCase(fileName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getNextDirectory(String dir) {
		File dirFile = new File(dir);
		if (dirFile.exists() && dirFile.isDirectory()) {
			for (File file : dirFile.listFiles()) {
				if (file.isDirectory()) {

					return file.getName();
				}
			}
		}
		return "";
	}
}
