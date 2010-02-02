/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 资源引入模板
 * @author yuzs
 *
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ResourceGuide extends SecurityPage implements IExternalPage {

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

	public abstract IUploadFile getUploadFile();
	
	public abstract Integer getTmdId();
	public abstract void setTmdId(Integer id);
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		String paras = (String) parameters[0];
		setTagName((String) parameters[1]);
		setParameter(paras.split(";"));
	}
	
	public boolean isShowColumn(){
		
		String[] parameter = getParameter();
		if(parameter[0]==null||"".equals(parameter[0]))
			return true;
		else{
			if("resource_list".equals(parameter[0]))
				return true;
			if("sametype_resource_list".equals(parameter[0]))
				return false;
			else 
				return true;
		}
	}
	
	public boolean isIphoneTag(){
		String[] parameter = getParameter();
		if(parameter[0]==null||"".equals(parameter[0]))
			return true;
		else{
			if("iresource_list".equals(parameter[0]))
				return true;
			else 
				return false;
		}
	}
	public void onSubmit(IRequestCycle cycle) {
			
		StringBuilder sb = new StringBuilder();
		
			sb.append("$#"+getTagName()+".");
		
		if(getColumnId()!=null)
			sb.append("columnId="+getColumnId()+",");
		
		if(getPageLink()!=null)
			sb.append("pageLink="+getPageLink()+",");
		
		if(getIsDisSN()!=null)
			sb.append("isDisSN="+getIsDisSN()+",");

		if(getTmdId()!=null)
				sb.append("tmd="+getTmdId()+",");
		
		if(getIsRoll()!=null && getIsRoll()==1){
			sb.append("isRoll="+getIsRoll()+",");
			if(getRollTotalCount()!=null)
				sb.append("rollTotalCount="+getRollTotalCount()+",");
			if(getRollRuleId()!=null){
				sb.append("rollRuleId="+getRollRuleId()+",");
				if(getRollRuleId()==0 && getTopCount()!=null)
					sb.append("topCount="+getTopCount()+",");
			}
			if(getRollCount()!=null)
				sb.append("rollCount="+getRollCount()+",");
			if(getRollTime()!=null)
				sb.append("rollTime="+getRollTime()+",");
		}else{
			sb.append("isRoll="+getIsRoll()+",");
			if(getRollNumber()!=null)
				sb.append("rollCount="+getRollNumber()+",");
			if(getListCount()!=null)
				sb.append("listCount="+getListCount()+",");
		}
		if(getMixs()!=null && getMixs().size() !=0){
			List mixList = getMixs();
			sb.append("mix=");
			for(int i=0;i<mixList.size();i++){
				String str = (String) mixList.get(i);
				sb.append("<%"+str+"%>");
			}
			sb.append(",");
		}
		if(getIsFee()!=null)
			sb.append("isFee="+getIsFee());
		if(isIphoneTag() && getShowtype()!=null){
			sb.append(","+"showtype="+getShowtype());
		}
		if(getParam()!=null)
			sb.append(",param="+getParam());
		sb.append("#");
		setReturnValue(sb.toString());
		setNeedReturn(Boolean.TRUE);
	}

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();
	
	public abstract Integer getPageLink();
	public abstract void setPageLink(Integer pageLink);
	
	public abstract Integer getIsDisSN();
	public abstract void setIsDisSN(Integer isDisSN);
	
	public abstract Integer getIsFee();
	public abstract void setIsFee(Integer isFee);
	
	public abstract Integer getIsRoll();
	public abstract void setIsRoll(Integer isRoll);
	
	public abstract Integer getShowtype();
	public abstract void setShowtype(Integer showtype);
	
	public abstract Integer getRollTotalCount();
	public abstract void setRollTotalCount(Integer rollTotalCount);
	
	/***
	 * add by liuxh 091116
	 * @return
	 */
	public abstract Integer getParam();
	public abstract void setParam(Integer param);
	/**
	 * add by liuxh 标签增加参数 listCount  --针对不循环资源 列表范围
	 * @return
	 */
	public abstract Integer getListCount();
	public abstract void setListCount(Integer listCount);
	
	public abstract Integer getRollRuleId();
	public abstract void setRollRuleId(Integer rollRuleId);
	
	public abstract Integer getColumnId();
	public abstract void setColumnId(Integer columnId);
	
	public abstract Integer getTopCount();
	public abstract void setTopCount(Integer topCount);
	
	public abstract Integer getRollCount();
	public abstract void setRollCount(Integer rollCount);
	
	public abstract Integer getRollTime();
	public abstract void setRollTime(Integer rollTime);
	
	public abstract Integer getRollNumber();
	public abstract void setRollNumber(Integer rollNumber);
	
	public IPropertySelectionModel getMixList(){
		Map<String, String> types = new OrderedMap<String, String>();
		types.put("推荐语", "bComment");
		types.put("作者","authorId");
		types.put("点击数","downnum");
		types.put("名称","name");
		return new MapPropertySelectModel(types);
	}
	
	public IPropertySelectionModel getParamList(){
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		types.put(" ", -1);
		types.put("全本", 1);
		types.put("连载",2);
		types.put("已出版",3);
		types.put("未出版",4);
		return new MapPropertySelectModel(types);
	}

	public IPropertySelectionModel getPageLinkList(){
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		types.put("否", -1);
		types.put("是", 1);
		return new MapPropertySelectModel(types);
	}

	public IPropertySelectionModel getShowtypeList(){
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		types.put("点击数", 1);
		types.put("搜索数", 2);
		return new MapPropertySelectModel(types);
	}
	public IPropertySelectionModel getRollTotalCountList(){
		
		Map<String,Integer> totals = new OrderedMap<String,Integer>();
		totals.put("100条", 100);
		totals.put("80条", 80);
		totals.put("60条", 60);
		totals.put("40条", 40);
		totals.put("20条", 20);
		return new MapPropertySelectModel(totals);
	}
	
	public IPropertySelectionModel getRollRuleIdList(){
		Map<String,Integer> rollRule = new OrderedMap<String,Integer>();
		rollRule.put("翻页轮询规则", 1);
		rollRule.put("置顶轮询规则", 0);
		rollRule.put("随机获取轮询规则", 2);
		rollRule.put("间隔一定时间随机获取轮询规则", 3);
		rollRule.put("间隔一定时间_轮询一批记录", 4);
		
		return new MapPropertySelectModel(rollRule);
	}

	public boolean isShowRoll(){
		if(getIsRoll()==null)
			return false;
		else{
			if(getIsRoll()==1)
				return true;
			else 
				return false;
		}
	}
	
	public boolean isShowTopCount(){
		if(getRollRuleId()==null)
			return false;
		else{
			if(getRollRuleId()==0)
				return true;
			else 
				return false;
		}
	}
	
	@InjectComponent("roleList")
	public abstract Block getRoleList();

	@InjectComponent("roleExist")
	public abstract Block getRoleExist();
	
	public abstract void setMixs(List resourcetypes);
	public abstract List getMixs();
	
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

