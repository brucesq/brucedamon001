/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 *
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class DownloadUebPage extends SecurityPage {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
	
	

	public abstract void setCurrentObject(Object obj);

	public abstract  Object getCurrentObject();

	public List<DetailPageField> getDetailList(){
		
		return getObjectFields(getCurrentObject());
	}

	

	public abstract DetailPageField getCurrentDetailField();

	private List<DetailPageField> getObjectFields(Object obj) {

		ResourcePackReleation rel = (ResourcePackReleation)obj;
		String url = getSystemService().getVariables("media_url").getValue();
		int idInt = rel.getId();
		int idIntDir = idInt / 1000;
		url += "ueb/"+idIntDir+"/"+idInt;
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		DetailPageField field = new DetailPageField();
		field.setTitle("128格式文件");
		field.setValue(url+"_128.ueb");
		details.add(field);
		
		field = new DetailPageField();
		field.setTitle("176格式文件");
		field.setValue(url+"_176.ueb");
		details.add(field);
		
		
		field = new DetailPageField();
		field.setTitle("240格式文件");
		field.setValue(url+"_240.ueb");
		
		
		details.add(field);
		return details;
	}

	private Object getProperty(Object obj,String prop){
		Object value = null;
		try{
			value = BeanUtils.forceGetProperty(obj, prop);
		}catch(Exception e){
			logger.error("版权下载页获取对象属性信息时报错.", e);
			value = "";
		}
		return value;
	}
}
