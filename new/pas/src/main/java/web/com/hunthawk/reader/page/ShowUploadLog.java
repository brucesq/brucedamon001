/**
 * 
 */
package com.hunthawk.reader.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;

import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.OffLineLog;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.inter.InteractiveService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
public abstract class ShowUploadLog extends SecurityPage {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
	
	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();
	

	public abstract void setCurrentObject(Object obj);

	public abstract  Object getCurrentObject();

	public List<DetailPageField> getDetailList(){
		
		return getObjectFields(getCurrentObject());
	}

	public abstract Integer getMark();
	public abstract void setMark(Integer Mark);

	public abstract DetailPageField getCurrentDetailField();

	

	private List<DetailPageField> getObjectFields(Object obj) {

		List<DetailPageField> details = new ArrayList<DetailPageField>();	
		//����mark��ʶ���õ��Ѿ��浽���ݿ��е��Ǹ�ֵ
		List<OffLineLog> list = getInteractiveService().findOffLineLogList(getMark());
		for(OffLineLog log: list){
			DetailPageField field = new DetailPageField();
			field.setTitle(log.getPackName());
			String showValue="";
			if(log.getStatus()==0) //��ʼ
				showValue = "<font color='red'>"+"��ʼ"+"</font>"+log.getValue();
			if(log.getStatus()==1) //����
				showValue = log.getValue();
			if(log.getStatus()==2) //����
				showValue = "<font color='red'>"+log.getValue()+"</font>";
			if(log.getStatus()==3) //����
				showValue = "<font color='red'>"+"����"+"</font>"+log.getValue();
			field.setValue(showValue);
			details.add(field);
		}
		
		return details;
	}
}
