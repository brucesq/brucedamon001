/**
 * 
 */
package com.hunthawk.reader.page;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.security.simple.MockSecurityContext;
import com.hunthawk.framework.security.simple.SimpleVisit;
import com.hunthawk.framework.util.ImageTool;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.VideoSuite;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.resource.impl.UploadServiceImpl;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author sunquanzhi
 * 
 */
public class CheckThread extends Thread {

	private UserImpl user;
	private ResourceService resourceService;
	private UploadService uploadService;
	private SystemService systemService;

	public CheckThread(UserImpl user, ResourceService resourceService,
			UploadService uploadService,SystemService systemService) {
		this.uploadService = uploadService;
		this.resourceService = resourceService;
		this.user = user;
		this.systemService = systemService;
	}

	public void changeCoverImage(File dirFile) {
		
		if (dirFile.isDirectory()) {
			for (File file : dirFile.listFiles()) {
				String ext = UploadServiceImpl.getFileExtName(file.getName());
				
				if (UploadServiceImpl.isRightFormat(file.getName().toLowerCase())) {
					
					if (file.getName().equalsIgnoreCase("cover." + ext)) {
						
						File cover120 = new File(dirFile, "cover120." + ext);
						if (!cover120.exists()) {
							
							try {
								System.out.println("IMAGE:"+cover120.getAbsolutePath());
								ImageTool.resizeImage(file.getAbsolutePath()
										.replaceAll("\\\\", "/"), cover120
										.getAbsolutePath().replaceAll("\\\\",
												"/"), 120);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						return;
					}
				}
			}
		}
	}
	

	public void run() {
		MockSecurityContext context = new MockSecurityContext();
		SimpleVisit visit = new SimpleVisit();
		visit.setUser(user);
		context.setVisit(visit);
		SecurityContextHolder.setContext(context);
		
		int i = 1;
		List<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		expressions.add(new CompareExpression("id", String
				.valueOf(ResourceAll.RESOURCE_TYPE_VIDEO)
				+ "%", CompareType.Like));
		List<ResourceAll> videos = resourceService.findResourceBy(i++, 1000,
				"id", true, expressions);

		while (true) {
			for (ResourceAll video : videos) {
				List<VideoSuite> suites = resourceService.getResourceChapter(
						VideoSuite.class, video.getId());
				String dir = resourceService.getChapterAddress(video.getId());

				File dirFile = new File(dir);
				if (dirFile.exists()) {
					changeCoverImage(dirFile);
					Map<String, String> mp4FilesMap = new HashMap<String, String>();
					for(VideoSuite suite : suites){
						if(suite.getType().equals("ucs")){
							if(StringUtils.isEmpty(suite.getRelfiles())){
								List<String> mp4Files = new ArrayList<String>();
								Long size = UploadServiceImpl.changeUcsResourceURL(dir+suite.getFilename(),
										uploadService.getVideoResourceDirectory(video.getId()),
										systemService, mp4Files);
								if (size > 0L) {
									suite.setSize(size.intValue());
								}
								String relFiles = "";
								for (String mp4File : mp4Files) {
									mp4FilesMap.put(mp4File, suite.getFiledesc());
									relFiles += mp4File + ";";
								}
								suite.setRelfiles(relFiles);
								try{
									System.out.println("UCS:"+suite.getId()+":"+suite.getFilename());
									resourceService.updateResourceChapter(suite);
								}catch(Exception e){
									e.printStackTrace();
								}
							}//end if is null rel files
						}//end ucs
					}//end for
					try {
						for (VideoSuite suite : suites) {
							if (mp4FilesMap.containsKey(suite.getFilename())) {
								suite.setFiledesc(mp4FilesMap.get(suite.getFilename()));
								System.out.println("MP4:"+suite.getId()+":"+suite.getFilename());
								resourceService.updateResourceChapter(suite);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (videos.size() < 1000) {
				break;
			}
			videos = resourceService.findResourceBy(i++, 1000, "id", true,
					expressions);
		}
		Variables var = systemService.getVariables("media_dir");
		File destFile = new File(var.getValue()+"video");
		resourceService.rsyncUploadFile(ResourceUtil.RSYNC_TYPE_ADD,new String[]{destFile.getAbsolutePath()});
		SecurityContextHolder.clearContext();
	}
}
