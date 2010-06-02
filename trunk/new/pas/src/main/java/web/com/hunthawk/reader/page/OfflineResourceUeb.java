/**
 * 
 */
package com.hunthawk.reader.page;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.security.simple.MockSecurityContext;
import com.hunthawk.framework.security.simple.SimpleVisit;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UebService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 *
 */
public class OfflineResourceUeb extends Thread {
	public static Integer status = 0;
	private ResourcePackService resourcePackService;
	private SystemService systemService;
	private UebService uebService;
	private UserImpl user;
	public OfflineResourceUeb(SystemService systemService, 
			Integer mark,
			ResourcePackService resourcePackService,
			UebService uebService,UserImpl user){
		this.systemService = systemService;
		this.resourcePackService = resourcePackService;
		this.uebService = uebService;
		this.user = user;
	}
	public void run(){
		MockSecurityContext context = new MockSecurityContext();
		SimpleVisit visit = new SimpleVisit();
		visit.setUser(user);
		context.setVisit(visit);
		SecurityContextHolder.setContext(context);
		status = 1;
		int pageNo = 1;
		int pageSize = 1000;
		boolean flag = true;
		int count = 0;
		while(flag){
			List<ResourcePackReleation> rels = resourcePackService.getResourceFromEpack(pageNo,pageSize,"id",true,new ArrayList());
			for(ResourcePackReleation rel : rels ){
				Variables dirVar = systemService.getVariables("media_dir");
				String dir = dirVar.getValue() + "ueb" + File.separator
						+ (rel.getId() / 1000);
				
				String[] files = new String[3];
				files[0] = dir + File.separator + rel.getId() + "_128.ueb";
				files[1] = dir + File.separator + rel.getId() + "_176.ueb";
				files[2] = dir + File.separator + rel.getId() + "_240.ueb";
				for(int i=0;i<3;i++){
					File file = new File(files[i]);
					if(!file.exists()){
						if(count % 100 == 0){
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						uebService.createUeb(rel);
						count ++;
						break;
					}
				}
			}
			if(rels.size()<pageSize){
				flag = false;
			}
			pageNo++;
		}
		
		SecurityContextHolder.clearContext();
		status = 0;
	}
}
