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
import org.apache.hivemind.HiveMind;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
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
public abstract class UserActionGuide extends SecurityPage implements IExternalPage {

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

	public abstract Integer getProperty();
	public abstract void setProperty(Integer property);
	
	public abstract Integer getIsDel();
	public abstract void setIsDel(Integer isDel);
	
	public abstract String getSplit();
	public abstract void setSplit(String split);
	
	public abstract String getNumber();
	public abstract void setNumber(String number);
	
	public abstract String getTitle();
	public abstract void setTitle(String title);
	
	public abstract Integer getTemplateId();
	public abstract void setTemplateId(Integer templateId);

	public abstract Integer getTemplateId1();
	public abstract void setTemplateId1(Integer templateId);
	
	public abstract Integer getTmdId();
	public abstract void setTmdId(Integer id);
	
	public abstract Boolean getNeedReturn();
	
	public abstract Integer getNum();

	public abstract void setNum(Integer num);

	public abstract void setParameterValue(String str);

	public abstract String getParameterValue();
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		
		String paras = (String) parameters[0];
		setTagName((String) parameters[1]);
		setParameter(paras.split(";"));
		if (parameters.length == 5) {
			setNum((Integer) parameters[4]);
			setParameterValue((String) parameters[3]);
			parseParameterValue();
		}
	}
	private Integer getInteger(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return -999;
		}
	}

	private void parseParameterValue() {
		if (getParameterValue() != null) {
			String[] strs = getParameterValue().split(",");
			for (int i = 0; i < strs.length; i++) {

				String[] kv = strs[i].split("=");
				if (kv.length == 2) {
					
					if ("split".equals(kv[0])) {
						
							setSplit(kv[1]);
						
					} else if ("number".equals(kv[0])) {
						
							setNumber(kv[1]);
						
					} else if ("tmd".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setTmdId(iv);
						}
					}else if ("title".equals(kv[0])) {
						
							setTitle(kv[1]);
						
					} else if ("templateId".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setTemplateId(iv);
						}
					}else if ("isDel".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setIsDel(iv);
						}
					} else if ("property".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setProperty(iv);
						}
					}
				}

			}
		}
	}
	public void onSubmit(IRequestCycle cycle) {
			
		StringBuilder sb = new StringBuilder();
		
		sb.append("$#"+getTagName()+".");
		if(getSplit()!=null)
			sb.append("split="+getSplit()+",");
		if(getNumber()!=null)
			sb.append("number="+getNumber()+",");
		if(getTitle()!=null)
			sb.append("title="+getTitle()+",");
		if(getTmdId()!=null)
			sb.append("tmd="+getTmdId()+",");
		if(getProperty()!=null){
			if(getProperty()==-1){ //删除
				sb.append("isDel="+getIsDel()+",");
				if(getIsDel()==1){
					if(getTemplateId()!=null)
						sb.append("templateId="+getTemplateId()+",");
				}
			}else{
				if(getTemplateId1()!=null)
					sb.append("templateId="+getTemplateId1()+",");
			}		
			sb.append("property="+getProperty());
		}
		sb.append("#");
		setReturnValue(sb.toString());
		setNeedReturn(Boolean.TRUE);
		if (getParameterValue() != null) {
			setUpdate(true);
		}
	}
	public abstract void setUpdate(boolean b);

	@InitialValue("false")
	public abstract boolean isUpdate();
	public IPropertySelectionModel getPropertyList(){
		Map<String,Integer> search = new OrderedMap<String,Integer>();
		search.put("添加", 1);
		search.put("删除", -1);
		return new MapPropertySelectModel(search);
	}
	
	public IPropertySelectionModel getIsDelList(){
		Map<String,Integer> search = new OrderedMap<String,Integer>();
		search.put("是", 1);
		search.put("否", -1);
		return new MapPropertySelectModel(search);
	}
	
	public boolean isShowDel(){
		if(getProperty()==null)
			return false;
		else{
			if(getProperty()==-1)
				return true;
			else 
				return false;
		}
	}
	
	public boolean isShowTemplate(){
		if(getIsDel()==null)
			return true;
		else{
			if(getIsDel()==-1)
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
		map.put("update", isUpdate());
		String tagValue = getTagName();
		if (HiveMind.isNonBlank(getParameterValue())) {
			tagValue += "." + getParameterValue();
		}
		map.put("tag", "$#"+tagValue+"#");
		map.put("num", getNum());
		return map;
	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getTemplateURL(){
		
		TemplateType type = new TemplateType();
		type.setId(6);
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		if(isShowDel())
			params[0] = "templateId";
		else
			params[0] = "templateId1";
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

