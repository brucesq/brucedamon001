package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.resource.ResourceReferen;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class DownloadReferenPage extends SecurityPage {

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

		ResourceReferen referen = (ResourceReferen)obj;
		String url = getSystemService().getVariables("media_url").getValue();
		int idInt = referen.getId();
		int idIntDir = idInt / 1000;
		url += "referen/"+idIntDir+"/"+idInt;
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		if(referen.getCopyrightUse()!=null&&!"".equals(referen.getCopyrightUse())){
			DetailPageField field = new DetailPageField();
			field.setTitle("����Ȩ���ʹ��Э��");
			field.setValue(url+"/"+referen.getCopyrightUse());
			details.add(field);
		}
		if(referen.getCopyrightAttorn()!=null&&!"".equals(referen.getCopyrightAttorn())){
				DetailPageField field = new DetailPageField();
				field.setTitle("����Ȩת��Э��");
				field.setValue(url+"/"+referen.getCopyrightAttorn());
				details.add(field);
			}
		if(referen.getCooperatePro()!=null&&!"".equals(referen.getCooperatePro())){
			DetailPageField field = new DetailPageField();
			field.setTitle("����Э��");
			field.setValue(url+"/"+referen.getCooperatePro());
			details.add(field);
		}
		if(referen.getProviderInfo()!=null&&!"".equals(referen.getProviderInfo())){
			DetailPageField field = new DetailPageField();
			field.setTitle("��Ȩ��");
			field.setValue(url+"/"+referen.getProviderInfo());
			details.add(field);
		}
		if(referen.getCopyrightCheck()!=null&&!"".equals(referen.getCopyrightCheck())){
			DetailPageField field = new DetailPageField();
			field.setTitle("����Ȩ�Ǽ�֤��");
			field.setValue(url+"/"+referen.getCopyrightCheck());
			details.add(field);
		}
		if(referen.getProductInfo()!=null&&!"".equals(referen.getProductInfo())){
			DetailPageField field = new DetailPageField();
			field.setTitle("��Ʒ��Ȩ״��˵����");
			field.setValue(url+"/"+referen.getProductInfo());
			details.add(field);
		}
		if(referen.getMcpinfo()!=null&&!"".equals(referen.getMcpinfo())){
			DetailPageField field = new DetailPageField();
			field.setTitle("MCP��Ȩ�Բ����˵����");
			field.setValue(url+"/"+referen.getMcpinfo());
			details.add(field);
		}
		if(referen.getPromises()!=null&&!"".equals(referen.getPromises())){
			DetailPageField field = new DetailPageField();
			field.setTitle("MCP��Ȩ�������ŵ��");
			field.setValue(url+"/"+referen.getPromises());
			details.add(field);
		}
		if(referen.getAuthorName()!=null&&!"".equals(referen.getAuthorName())){
			DetailPageField field = new DetailPageField();
			field.setTitle("������Ϣ��");
			field.setValue(url+"/"+referen.getAuthorName());
			details.add(field);
		}
		if(referen.getCopyrightOther()!=null&&!"".equals(referen.getCopyrightOther())){
			DetailPageField field = new DetailPageField();
			field.setTitle("����");
			field.setValue(url+"/"+referen.getCopyrightOther());
			details.add(field);
		}
		return details;
	}

	private Object getProperty(Object obj,String prop){
		Object value = null;
		try{
			value = BeanUtils.forceGetProperty(obj, prop);
		}catch(Exception e){
			logger.error("��Ȩ����ҳ��ȡ����������Ϣʱ����.", e);
			value = "";
		}
		return value;
	}
}
