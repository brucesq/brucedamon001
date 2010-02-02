package com.hunthawk.reader.page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.security.SecurityContextHolder;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

public class OfflineChangResource extends Thread{

	private ResourceService resourceService;
	private UserImpl user;
	private SystemService systemService;
	private String fileName;
	private String propertyName;
	private Integer propertyOrder;
	public OfflineChangResource(
			ResourceService resourceService,
			UserImpl user,
			SystemService systemService,
			String fileName,
			String propertyName,
			Integer propertyOrder){
		this.resourceService = resourceService;
		this.user = user;
		this.systemService = systemService;
		this.fileName = fileName;
		this.propertyName = propertyName;
		this.propertyOrder = propertyOrder;
	}
	public OfflineChangResource(){}
	
	public void run(){
		this.changeResourceProperty(fileName,propertyName,propertyOrder);
	}
	
	
	public void changeResourceProperty(String fileName,String propertyName,Integer propertyOrder){
		//String offLine_dir = systemService.getVariables("offLine_dir").getValue();//得到离线上传地址
		String offLine_dir = "C:\\var\\www\\offLine\\";//var/www/offLine
		File offLine = new File(offLine_dir);
		String errormessage="";
		InputStreamReader fr = null;
		BufferedReader br = null;
		System.out.println("fileName----"+fileName);
		System.out.println("propertyName----"+propertyName);
		System.out.println("propertyOrder----"+propertyOrder);
		try{
			File csvFile = new File(offLine_dir+fileName);//csv文件
			if(!csvFile.exists())
				throw new Exception("没有发现配置文件");
//------------------------------读取csv ，处理图片信息----------------------------------------------------------------
				fr = new InputStreamReader(new FileInputStream(csvFile));
				br = new BufferedReader(fr);
				String rec = null;
				String[] argsArr = null;

				// 去掉第一行数据
				br.readLine();
				while ((rec = br.readLine()) != null) {

					if (StringUtils.isEmpty(rec)) {
						continue;
					}
					argsArr = rec.split(",");
					String resourceId = argsArr[0];
					String propertyValue = argsArr[propertyOrder]; //资源的属性值
					
					// System.out.println("---资源Id---"+resourceId+"--输入的-值---"+propertyValue);
					//propertyName="bookPrice";//例如属性值是 bookPrice
					 
					ResourceAll resourceAll = resourceService.getResource(resourceId);					
					if(resourceAll==null)
						continue;
					//System.out.println("---输出前的值----"+resourceAll.getBookPrice());
					Class clz = resourceAll.getClass();
					Method method = null;
					 Method[] methods = clz.getMethods();//.getDeclaredMethods();
					 for (int i = 0; i < methods.length; i++) {
					   Method m = methods[i];
					   if (m.getName().equals(propertyName) && (m.getParameterTypes().length == 1)) {  //与set相匹配
						   method = m;
						   break;
					   }
					  }
					   if (method != null) {
						   method.invoke(resourceAll, propertyValue);//根据方法名称重新赋值
					   }
					  
					resourceAll.setModifierId(user.getId());
					//System.out.println("=---改编后的值----"+resourceAll.getBookPrice());
					resourceService.updateResourceNOChangeStatus(resourceAll,-1);
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
		}
	}
}
