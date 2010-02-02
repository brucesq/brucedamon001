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
		//String offLine_dir = systemService.getVariables("offLine_dir").getValue();//�õ������ϴ���ַ
		String offLine_dir = "C:\\var\\www\\offLine\\";//var/www/offLine
		File offLine = new File(offLine_dir);
		String errormessage="";
		InputStreamReader fr = null;
		BufferedReader br = null;
		System.out.println("fileName----"+fileName);
		System.out.println("propertyName----"+propertyName);
		System.out.println("propertyOrder----"+propertyOrder);
		try{
			File csvFile = new File(offLine_dir+fileName);//csv�ļ�
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
					String resourceId = argsArr[0];
					String propertyValue = argsArr[propertyOrder]; //��Դ������ֵ
					
					// System.out.println("---��ԴId---"+resourceId+"--�����-ֵ---"+propertyValue);
					//propertyName="bookPrice";//��������ֵ�� bookPrice
					 
					ResourceAll resourceAll = resourceService.getResource(resourceId);					
					if(resourceAll==null)
						continue;
					//System.out.println("---���ǰ��ֵ----"+resourceAll.getBookPrice());
					Class clz = resourceAll.getClass();
					Method method = null;
					 Method[] methods = clz.getMethods();//.getDeclaredMethods();
					 for (int i = 0; i < methods.length; i++) {
					   Method m = methods[i];
					   if (m.getName().equals(propertyName) && (m.getParameterTypes().length == 1)) {  //��set��ƥ��
						   method = m;
						   break;
					   }
					  }
					   if (method != null) {
						   method.invoke(resourceAll, propertyValue);//���ݷ����������¸�ֵ
					   }
					  
					resourceAll.setModifierId(user.getId());
					//System.out.println("=---�ı���ֵ----"+resourceAll.getBookPrice());
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
