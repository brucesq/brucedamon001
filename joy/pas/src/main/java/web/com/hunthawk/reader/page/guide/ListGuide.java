/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.domain.Constants;
/**
 * 用户行为引入模板
 * @author yuzs
 *
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ListGuide extends SecurityPage implements IExternalPage {

	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract String[] getParameter();

	public abstract String getTagName();

	public abstract void setTagName(String name);

	public abstract void setParameter(String[] para);
	
	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Integer getTemplateId();
	public abstract void setTemplateId(Integer templateId);
	
	public abstract Integer getNoPageLink();
	public abstract void setNoPageLink(Integer noPageLink);
	
	public abstract Integer getShowDelLink();
	public abstract void setShowDelLink(Integer showDelLink);
	
	public abstract Integer getIsConfirm();
	public abstract void setIsConfirm(Integer isConfirm);
	
	public abstract Integer getPageSize();
	public abstract void setPageSize(Integer pageSize);

	public abstract Integer getTmdId();
	public abstract void setTmdId(Integer id);
	
	public abstract Boolean getNeedReturn();
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		
		String paras = (String) parameters[0];
		setParameter(paras.split(";"));
		setTagName((String) parameters[1]);
		
	}
	
	public void onSubmit(IRequestCycle cycle) {
			
		StringBuilder sb = new StringBuilder();
		
		sb.append("$#"+getTagName()+".");
		if(getShowDelLink()!=null)
			sb.append("showDelLink="+getShowDelLink()+",");
		if(getPageSize()!=null)
			sb.append("pageSize="+getPageSize()+",");
		if(getIsConfirm()!=null)
			sb.append("isConfirm="+getIsConfirm()+",");
		if(getTmdId()!=null)
			sb.append("tmd="+getTmdId()+",");
		
		if(getIsConfirm()==1){
			if(getTemplateId()!=null)
				sb.append("templateId="+getTemplateId()+",");
		}
		if(getNoPageLink()!=null)
			sb.append("noPageLink="+getNoPageLink());
		sb.append("#");
		setReturnValue(sb.toString());
		setNeedReturn(Boolean.TRUE);
	}
	
	public IPropertySelectionModel getNoPageLinkList(){
		Map<String,Integer> search = new OrderedMap<String,Integer>();
		search.put("是", 1);
		search.put("否", -1);
		return new MapPropertySelectModel(search);
	}
	
	public boolean isShowTemplate(){
		if(getIsConfirm()==null)
			return true;
		else{
			if(getIsConfirm()==-1)
				return false;
			else
				return true;	
		}
	}
	

	public Map getScriptSymbols() {
		Map map = new HashMap();
		if (getNeedReturn() == null) {
			setNeedReturn(Boolean.FALSE);
		}
		if (getReturnValue() == null) {
			setReturnValue("");
		}
		map.put("needreturn", getNeedReturn());
		map.put("content", getReturnValue());
		map.put("update", Boolean.FALSE);
		String tagValue = getTagName();
		

		map.put("tag", "$#imglink#");
		map.put("num", 1);
		return map;
	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getTemplateURL(){
		
		TemplateType type = new TemplateType();
		type.setId(6);
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		params[0] = "templateId";
		params[1] = type;
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);
		return templateURL;
	}
	

	public String getTmdURL(){
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		params[0] = "TMDId";
		if(getTagName()!=null && !"".equals(getTagName()))
			params[1] = getTagName();
		else
			params[1] ="all";
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TagTemplateChoicePage", params);
		return templateURL;
	}
	
}

