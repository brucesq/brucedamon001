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
 * 搜索引入模板
 * @author yuzs
 *
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class SearchGuide extends SecurityPage implements IExternalPage {

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

	public abstract String getProperty();
	public abstract void setProperty(String property);
	
	public abstract String getDefaultkey();
	public abstract void setDefaultkey(String defaultkey);
	
	public abstract Integer getIsize();
	public abstract void setIsize(Integer isize);
	
	public abstract String getRestype();
	public abstract void setRestype(String restype);
	
	public abstract String getSearchby();
	public abstract void setSearchby(String searchby);
	
	public abstract String getTitle();
	public abstract void setTitle(String title);
	
	public abstract Integer getColumnId();
	public abstract void setColumnId(Integer columnId);

	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		setTagName((String) parameters[1]);
	}
	
	public void onSubmit(IRequestCycle cycle) {
			
		StringBuilder sb = new StringBuilder();
		
		sb.append("$#"+getTagName()+".");
		if(getProperty()!=null){
			sb.append("property="+getProperty()+",");
			if("input".equals(getProperty())){
				if(getDefaultkey()!=null)
					sb.append("defaultkey="+getDefaultkey()+",");
				if(getIsize()!=null)
					sb.append("isize="+getIsize());
			}
			if("link".equals(getProperty())){
				if(getSearchby()!=null)
					sb.append("searchby="+getSearchby()+",");
				if(getTitle()!=null)
					sb.append("title="+getTitle()+",");
				if(getColumnId()!=null)
					sb.append("columnId="+getColumnId()+",");
				if(getRestype()!=null)
					sb.append("restype="+getRestype());
			}
		}
		sb.append("#");
		setReturnValue(sb.toString());
		setNeedReturn(Boolean.TRUE);
	}

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();

	public IPropertySelectionModel getRestypeList(){
		return new MapPropertySelectModel(Constants.getResourceType());
	}
	
	public IPropertySelectionModel getSearchbyList(){
		Map<String,Integer> search = new OrderedMap<String,Integer>();
		search.put("按书名", 1);
		search.put("按作者", 2);
		search.put("快速搜索",3);
		search.put("按关键字", 4);
		return new MapPropertySelectModel(search);
	}
	
	public IPropertySelectionModel getPropertyList(){
		Map<String,String> search = new OrderedMap<String,String>();
		search.put("文本输入框","input");
		search.put("文本链接", "link");
		return new MapPropertySelectModel(search);
	}

	public abstract String getVersion();
	public abstract void setVersion(String version);
	
	public IPropertySelectionModel getVersionList(){
		Map<String,String> search = new OrderedMap<String,String>();
		search.put("wap1.x","1");
		search.put("wap2.0","2");
		return new MapPropertySelectModel(search);
	}
	public boolean isShowSearch(){
		if(getVersion()==null)
			return true;
		else{
			if("2".equals(getVersion()))
				return false;
			else
				return true;
		}
	}
	public boolean isShowProperty(){
		if(getProperty()==null)
			return true;
		else{
			if("input".equals(getProperty()))
				return true;
			else if("link".equals(getProperty()))
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

	public String getColumnURL()
	{
		IEngineService service = getExternalService();
		Object[] params = new Object[1];
		params[0] = "columnId";
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/ColumnChoicePage", params);

		return templateURL;
	}
}

