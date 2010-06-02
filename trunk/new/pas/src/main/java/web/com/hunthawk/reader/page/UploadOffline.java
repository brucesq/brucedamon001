package com.hunthawk.reader.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.job.StatDataRankDate;
import com.hunthawk.reader.service.job.StatDataRankMonth;
import com.hunthawk.reader.service.job.StatDataRankWeek;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UebService;
import com.hunthawk.reader.service.resource.UploadService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 重新修改上传页面
 * 
 * @author yuzs
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class UploadOffline extends SecurityPage implements
PageBeginRenderListener{

	@SuppressWarnings("unused")
	private String resourceId;
	private File uploadFile = null;// 最终上传到本地的
	
	

	public abstract File getServerFile();

	public abstract void setServerFile(File file);

	@InjectObject("spring:uploadService")
	public abstract UploadService getUploadService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();
	
	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:uebService")
	public abstract UebService getUebService();
	
//	@InjectObject("spring:statDataRankDateObj")
//	public abstract StatDataRankDate getStatDataRankDate();
	
	
	public abstract List<File> getFiles();
	public abstract void setFiles(List<File> files);
	
	
	public Integer getMark(){
		return getUser().getId();
	}
//	public abstract void setMark(Integer Mark);
	
	public void savePage(String filename){
		if(OfflineThread.status == 1){
			return;
		}else{
			OfflineThread.status = 1;
		}
		try{
				saveOffline(filename);
			
		}catch(Exception e){
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		
	}
	
	
	
	protected void saveOffline(String filename) {
		UserImpl user = (UserImpl)getUser();
			Thread thread1 = new OfflineThread(getSystemService(),getMark(),
					getInteractiveService(),getUploadService(),user,filename);
			thread1.start();
		}


	/*public void commitCover(IRequestCycle cycle){
		if(OfflineThread.status == 1){
			return;
		}else{
			OfflineThread.status = 1;
		}
		try{
			saveCover(cycle);		
		}catch(Exception e){
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		
	}*/
	
	public void commitCover(IRequestCycle cycle){
		UserImpl user = (UserImpl)getUser();
			Thread thread1 = new OfflineCover(getSystemService(),getMark(),
					getResourceService(),getUploadService(),user);
			thread1.start();
	}
	public void commitUebCheck(IRequestCycle cycle){
		UserImpl user = (UserImpl)getUser();
		Thread thread1 = new OfflineResourceUeb(getSystemService(),getMark(),
				getResourcePackService(),getUebService(),user);
		thread1.start();
	}
	
	public void commitKeyWord(IRequestCycle cycle){
		if(OfflineThread.status == 1){
			return;
		}else{
			OfflineThread.status = 1;
		}
		try{
			saveKeyWord(cycle);		
		}catch(Exception e){
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		
	}
	
	public void doStat(String type){
//		StatThread thread = new StatThread(Integer.parseInt(type),this.getStatDataRankDate());
//		thread.start();
	}
	
	public void doDivision(IRequestCycle cycle){
		UserImpl user = (UserImpl)getUser();
		System.out.println("---yonghum-----"+user.getId());
		OfflineDivision thread = new OfflineDivision(getResourceService(),getResourcePackService());
		thread.start();
	}
	public void saveKeyWord(IRequestCycle cycle){
		UserImpl user = (UserImpl)getUser();
		Thread thread1 = new OfflineKeyWord(getSystemService(),getMark(),
				getResourceService(),getUploadService(),user);
		thread1.start();
	}
	
	public void editResource(IRequestCycle cycle){
		UserImpl user = (UserImpl)getUser();
		System.out.println("文件名----"+getFileName());
		System.out.println("方法名----"+getMethodName());
		System.out.println("位置------"+getPropertyName());
		Thread thread1 = new OfflineChangResource(getResourceService(),user,getSystemService(),getFileName(),getMethodName(),getPropertyName());
		thread1.start();
	}
	
	public abstract String getFileName();
	public abstract void setFileName(String fileName);
	
	public abstract String getMethodName();
	public abstract void setMethodName(String methodName);
	
	public abstract Integer getPropertyName();
	public abstract void setPropertyName(Integer propertyName);
	/*************************页面显示************************************************************************
	 * 
	 */
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
	
	public abstract void setCurrentObject(Object obj);

	public abstract  Object getCurrentObject();

	public List<DetailPageField> getDetailList(){
		
		return getObjectFields(getCurrentObject());
	}

	

	public abstract DetailPageField getCurrentDetailField();

	private List<DetailPageField> getObjectFields(Object obj) {
		
		List<DetailPageField> details = new ArrayList<DetailPageField>();	
		return details;
	}
	@InjectPage("UploadOffline")
	public abstract UploadOffline getUploadOffline();

	
	@InitialValue("true")
	public abstract boolean isSubmit();
	public abstract void setSubmit(boolean isShow);
	
	public  boolean getStatus(){
		return OfflineThread.status == 1;
	}
	
	
	
	public void pageBeginRender(PageEvent event) {
		//Timestamp time =new Timestamp();
//		if(getMark()==null){
//			String ss = Calendar.getInstance().getTimeInMillis()+"";
//			ss = ss.substring(7);
			
//			setMark(Integer.parseInt(ss));
//		}
		System.out.println("-------初始值-----"+getStatus());
		//if(!getStatus())	
		//	setStatus(false);
		String offLine_dir = getSystemService().getVariables("offLine_dir")
		.getValue();
		setFiles(new ArrayList<File>());
		File offLine = new File(offLine_dir);
		if(offLine.exists() && offLine.isDirectory()){
			for(File file : offLine.listFiles()){
				
				if(file.isDirectory()){
						getFiles().add(file);
				}
			}
		}
	}
	
	@InjectPage("ShowUploadLog")
	public abstract ShowUploadLog getShowUploadLog();
	

	
	public IPage execThread(Integer markId){
		getShowUploadLog().setMark(markId);
		return getShowUploadLog();
		}
}
