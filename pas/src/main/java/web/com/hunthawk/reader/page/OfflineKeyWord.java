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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.framework.security.simple.MockSecurityContext;
import com.hunthawk.framework.security.simple.SimpleVisit;
import com.hunthawk.reader.domain.OffLineLog;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
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

public class OfflineKeyWord extends Thread {
	
	public static Integer status = 0;

	private SystemService systemService;
	private Integer mark;
	private ResourceService resourceService;
	private UploadService uploadService;
	private UserImpl user;
	public OfflineKeyWord(SystemService systemService, 
			Integer mark,
			ResourceService resourceService,
			UploadService uploadService,UserImpl user){
		this.systemService = systemService;
		this.mark = mark;
		this.resourceService = resourceService;
		this.uploadService = uploadService;
		this.user = user;
	}
	public OfflineKeyWord(){}

	public void run(){
		List<ResourceAll> resourceAlllist = 
			resourceService.findResourceBy(1, Integer.MAX_VALUE, "id", false, new ArrayList<HibernateExpression>());
		
		for(ResourceAll resource:resourceAlllist){
			
			String showWord = "";
			Integer[] authorList = resource.getAuthorIds();// 得到作者数组
			Set<String> keyWords = resource.getRKeyWords();
			if(keyWords==null)
				keyWords = new HashSet<String>();
			keyWords.add(resource.getName());
			if (authorList.length > 0) {
				for (int i = 0; i < authorList.length; i++) {
					ResourceAuthor author = resourceService
							.getResourceAuthorById(authorList[i]);
					keyWords.add(author.getPenName());
				}
			}
			if(keyWords!=null){
				for(String words :keyWords ){
					showWord += words +"/";
				}
			}
			showWord = showWord.substring(0, showWord.lastIndexOf("/"));
			resource.setModifierId(user.getId());
			resource.setRKeyword(showWord);
			resourceService.updateResourceNOChangeStatus(resource,-1);
		}
	}
	
}
