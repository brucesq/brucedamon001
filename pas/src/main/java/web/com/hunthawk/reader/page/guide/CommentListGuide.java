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
 * 留言引入模板
 * @author yuzs
 *
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class CommentListGuide extends SecurityPage implements IExternalPage {

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
	
	public abstract Integer getMsgType();
	public abstract void setMsgType(Integer msgType);
	
	public abstract String getCustomId();
	public abstract void setCustomId(String customId);
	
	public abstract String getTitle();
	public abstract void setTitle(String title);
	
	public abstract Integer getPageSize();
	public abstract void setPageSize(Integer pageSize);
	
	public abstract Integer getNoPageLink();
	public abstract void setNoPageLink(Integer noPageLink);
	
	public abstract Integer getBoardId();
	public abstract void setBoardId(Integer boardId);
		
	public abstract Integer getShowBack();
	public abstract void setShowBack(Integer showBack);
	

	public abstract Integer getTmdId();
	public abstract void setTmdId(Integer id);
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		setTagName((String) parameters[1]);
	}
	
	public void onSubmit(IRequestCycle cycle) {
			
		StringBuilder sb = new StringBuilder();
		
		sb.append("$#"+getTagName()+".");
		if(getNoPageLink()!=null){
			sb.append("noPageLink="+getNoPageLink()+",");
			if(getNoPageLink()==1){
				sb.append("pageSize="+getPageSize()+",");
			}
		}	

		if(getBoardId()!=null)
			sb.append("boardId="+getBoardId()+",");
		if(getShowBack()!=null){
			sb.append("showBack="+getShowBack()+",");
			if(getShowBack()==1){
				if(getTitle()!=null)
					sb.append("title="+getTitle()+",");
			}
		}
		if(getTmdId()!=null)
			sb.append("tmd="+getTmdId()+",");
		if(getMsgType()!=null){
			if(getMsgType()==4)
				sb.append("id="+getCustomId()+",");
			sb.append("msgType="+getMsgType());
		}
		sb.append("#");
		setReturnValue(sb.toString());
		setNeedReturn(Boolean.TRUE);
	}

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();

	public IPropertySelectionModel getMsgTypeList(){
		Map<String,Integer> msgType = new OrderedMap<String,Integer>();
		msgType.put("内容", 1);
		msgType.put("栏目", 2);
		msgType.put("产品", 3);
		msgType.put("用户定制", 4);
		return new MapPropertySelectModel(msgType);
	}
	
	public IPropertySelectionModel getPageSizeList(){
		Map<String,Integer> msgType = new OrderedMap<String,Integer>();
		msgType.put("3条", 3);
		msgType.put("10条", 10);
		msgType.put("20条", 20);
		return new MapPropertySelectModel(msgType);
	}
	public IPropertySelectionModel getNoPageLinkList(){
		Map<String,Integer> search = new OrderedMap<String,Integer>();
		search.put("是",1);
		search.put("否",-1);
		return new MapPropertySelectModel(search);
	}

	
	public boolean isShowPageSize(){
		if(getNoPageLink()==null)
			return true;
		else{
			if(getNoPageLink()==1)
				return true;
			else if(getNoPageLink()==-1)
				return false;
			else
				return false;	
		}
	}
	
	public boolean isShowLink(){
		if(getShowBack()==null)
			return true;
		else{
			if(getShowBack()==1)
				return true;
			else
				return false;
		}
	}
	
	public boolean isShowCustom(){
		if(getMsgType()==null)
			return false;
		else{
			if(getMsgType()==4)
				return true;
			else
				return false;
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
	
	public String getBoardURL()
	{
		IEngineService service = getExternalService();
		Object[] params = new Object[1];
		params[0] = "boardId";
		String templateURL = PageHelper.getExternalFunction(service,
				"inter/MsgBoardChoicePage", params);

		return templateURL;
	}
	
	public String getRecordURL()
	{
		IEngineService service = getExternalService();
		Object[] params = new Object[1];
		params[0] = "customId";
		String templateURL = PageHelper.getExternalFunction(service,
				"inter/MsgRecordChoicePage", params);

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

