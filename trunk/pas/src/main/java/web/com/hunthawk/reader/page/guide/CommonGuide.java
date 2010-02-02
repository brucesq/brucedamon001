/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.HiveMind;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.page.util.PageHelper;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class CommonGuide extends SecurityPage implements IExternalPage {

	private static final String TEMPLATE = "TEMPLATE";
	private static final String MUTI_SELECT = "MUTI_SELECT";
	private static final String IMAGE = "IMAGE";
	private static final String ADAPTER = "ADAPTER";
	private static final String MUL_RES = "MUL_RES";

	private static final String MUTI_START = "<%";
	private static final String MUTI_END = "%>";
	private static final String MUTI_LETTER = "%><";

	public abstract String[] getParameter();

	public abstract String getTagName();

	public abstract void setTagName(String name);

	public abstract void setParameter(String[] para);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		String paras = (String) parameters[0];
		setParameter(paras.split(";"));
		setTagName((String) parameters[1]);
		if (parameters.length == 5) {
			setNum((Integer) parameters[4]);
			setParameterValue((String) parameters[3]);

		}
		parseParameterValue();
		parserParameter();
	}

	private void parseParameterValue() {
		if (getParameterValue() != null) {
			Map map = new HashMap();
			String[] strs = getParameterValue().split(",");
			for (int i = 0; i < strs.length; i++) {

				String[] kv = strs[i].split("=");
				if (kv.length == 2) {
					map.put(kv[0], kv[1]);
				}

			}
			setParameterVMap(map);
		}
	}

	private void parserParameter() {

		for (String para : getParameter()) {

			String[] strs = para.split(":");
			if (strs.length < 3) {
				continue;
			}

			if ("@M".equals(strs[0])) {// 多选
				String[] nt = strs[1].split("=");

				if (nt.length == 2) {
					setShowMutiSelect(true);
					getBasicTypeMap().put(MUTI_SELECT, nt[0]);
					setMutiSelectTitle(nt[1]);
					setMutiSelectMode(TypeUtil.parseSelect(strs[2]));
					if (getParameterVMap().containsKey(nt[0])) {
						String values = (String) getParameterVMap().get(nt[0]);
						PatternCompiler compiler = new Perl5Compiler();
						try {
							Pattern pattern = compiler.compile(MUTI_START
									+ "[^\\" + MUTI_LETTER + "]*" + MUTI_END);
							PatternMatcherInput input = new PatternMatcherInput(
									values);
							PatternMatcher matcher = new Perl5Matcher();
							while (matcher.contains(input, pattern)) {
								MatchResult result = matcher.getMatch();
								String value = result.group(0);
								value = value.substring(MUTI_START.length(),
										result.length() - MUTI_END.length());
								getSelectedItems().add(value);
							}
						} catch (Exception e) {
						}
					}
				}
			} else if ("$S".equals(strs[0])) {// 选择LIST

				String[] nt = strs[1].split("=");
				if (nt.length == 2) {
					Select select = new Select();
					select.setName(nt[0]);
					select.setTitle(nt[1]);
					select.setPropertySelectionModel(TypeUtil
							.parseSelect(strs[2]));
					if (getParameterVMap().containsKey(nt[0])) {
						try {
							select.setValue(getParameterVMap().get(nt[0]));

						} catch (Exception e) {
							logger.error("", e);
						}
					}
					getSelectList().add(select);

				}
			} else if ("$T".equals(strs[0])) {// 文本LIST
				String[] nt = strs[1].split("=");
				if (nt.length == 2) {
					TextInput select = new TextInput();
					select.setName(nt[0]);
					select.setTitle(nt[1]);
					// select.setPropertySelectionModel(TypeUtil.parseSelect(strs[2]));
					TypeUtil.parseTextInput(strs[2], select);
					if (getParameterVMap().containsKey(nt[0])) {
						try {
							select.setValue(getParameterVMap().get(nt[0]));

						} catch (Exception e) {
						}
					}
					getTextList().add(select);

				}
			}else if("@C".equals(strs[0]))
			{//栏目@C:columnId=xxxx:{}
				setShowColumn(true);
				String[] nt = strs[1].split("=");
				if(nt.length == 2){
					setColumnName(nt[0]);
					setColumnTitle(nt[1]);
				}else{
					setColumnName(strs[1]);
					setColumnTitle("请选择栏目");
				}
				
				if(getParameterVMap().containsKey(getColumnName()))
				{
					try{
						String value =  (String)getParameterVMap().get(getColumnName());
						setColumnId((Integer.parseInt(value)));
						
					}catch(Exception e){
						logger.error("",e);
					}
				}
			}else if("@T".equals(strs[0])){  //模板
				setShowTemplate(true);
				String[] nt = strs[1].split("=");
				String templateType = strs[2];//大括号中的数据
				TemplateType type = new TemplateType();
				type.setId(Integer.parseInt(templateType));
				setTemplateType(type);
				if(nt.length == 2){
					setTemplateName(nt[0]);
					setTemplateTitle(nt[1]);
				}else{
					setTemplateName(strs[1]);
					setTemplateTitle("请选择模板");
				}
				if(getParameterVMap().containsKey(getTemplateName()))
				{
					try{
						String value =  (String)getParameterVMap().get(getTemplateName());
						setTemplateId((Integer.parseInt(value)));
						
					}catch(Exception e){
						logger.error("",e);
					}
				}
			}
			else if("@TMD".equals(strs[0])){//标签模板@TMD:tmd=xxxx:{}
				setShowTMD(true);
				String[] nt = strs[1].split("=");
				if(nt.length == 2){
					setTMDName(nt[0]);
					setTMDTitle(nt[1]);
				}else{
					setTMDName(strs[1]);
					setTMDTitle("请选择标签模板");
				}
				
				if(getParameterVMap().containsKey(getTMDName()))
				{
					try{
						String value =  (String)getParameterVMap().get(getTMDName());
						setTMDId((Integer.parseInt(value)));
						
					}catch(Exception e){
						logger.error("",e);
					}
				}
			}
		}
	}
	
	/////栏目///////
	@InitialValue("false")
	public abstract boolean isShowColumn();
	public abstract void setShowColumn(boolean isShow);
	public abstract void setColumnName(String name);
	public abstract String getColumnName();
	public abstract void setColumnId(Integer id);
	public abstract Integer getColumnId();
	public abstract void setColumnTitle(String title);
	public abstract String getColumnTitle();
	/////end 栏目////////
	
	/////////模板////////////////
	@InitialValue("false")
	public abstract boolean isShowTemplate();
	public abstract void setShowTemplate(boolean isShow);
	public abstract void setTemplateName(String name);
	public abstract String getTemplateName();
	public abstract void setTemplateId(Integer id);
	public abstract Integer getTemplateId();
	public abstract void setTemplateTitle(String title);
	public abstract String getTemplateTitle();
	public abstract TemplateType getTemplateType();
	public abstract void setTemplateType(TemplateType template);
	////////end模板/////////////////
	
/////标签模板///////
	@InitialValue("false")
	public abstract boolean isShowTMD();
	public abstract void setShowTMD(boolean isShow);
	public abstract void setTMDName(String name);
	public abstract String getTMDName();
	public abstract void setTMDId(Integer id);
	public abstract Integer getTMDId();
	public abstract void setTMDTitle(String title);
	public abstract String getTMDTitle();
	/////end 标签模板////////
	
	
	@InitialValue("new java.util.ArrayList()")
	public abstract List getTextList();

	public abstract void setTextList(List list);

	public abstract Integer getNum();

	public abstract void setNum(Integer num);

	public abstract void setParameterValue(String str);

	public abstract String getParameterValue();

	@InitialValue("new java.util.HashMap()")
	public abstract Map getParameterVMap();

	public abstract void setParameterVMap(Map map);

	@InitialValue("new java.util.HashMap()")
	public abstract Map getBasicTypeMap();

	public abstract void setBasicTypeMap(Map map);

	@InitialValue("new java.util.ArrayList()")
	public abstract List getSelectList();

	public abstract void setSelectList(List list);

	public abstract void setSelectResult(String s);

	@InitialValue("new java.lang.String()")
	public abstract String getSelectResult();

	public abstract void setCurrentSelect(Type select);

	public abstract Type getCurrentSelect();

	public void updateSelect(IRequestCycle cycle) {
		if (cycle.isRewinding()) {
			Type select = getCurrentSelect();
			if (select.getValue() != null) {
				setSelectResult(getSelectResult() + select.getName() + "="
						+ select.getValue().toString() + ",");
			}
		}
	}

	public void onSubmit(IRequestCycle cycle) {

		parserParameter();
		String returnValue = "$#" + getTagName() + ".";

		if (isShowMutiSelect()) {// 多选
			returnValue += (String) getBasicTypeMap().get(MUTI_SELECT) + "=";
			for (int i = 0; i < getSelectedItems().size(); i++) {
				String str = (String) getSelectedItems().get(i);
				returnValue += MUTI_START + str + MUTI_END;

			}
			returnValue += ",";
		}
		
		if(isShowColumn()  && getColumnId() != null && getColumnId()> 0)
		{//栏目
			returnValue += getColumnName()+"="+getColumnId()+",";
		}
		if(isShowTemplate()  && getTemplateId() != null && getTemplateId()> 0)
		{//模板
			returnValue += getTemplateName()+"="+getTemplateId()+",";
		}
		if(isShowTMD()  && getTMDId() != null && getTMDId()> 0)
		{//标签模板
			returnValue += getTMDName()+"="+getTMDId()+",";
		}
		returnValue += getSelectResult().replaceAll("\\#","!0!");

		if (returnValue.charAt(returnValue.length() - 1) == ',') {
			returnValue = returnValue.substring(0, returnValue.length() - 1);
		}
		if (returnValue.charAt(returnValue.length() - 1) == '.') {
			returnValue = returnValue.substring(0, returnValue.length() - 1);
		}

		returnValue += "#";
		setReturnValue(returnValue);
		setNeedReturn(Boolean.TRUE);
		if (getParameterValue() != null) {
			setUpdate(true);
		}

	}

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();

	// 是否多选
	private IPropertySelectionModel mutiModel;

	@InitialValue("new java.util.ArrayList()")
	public abstract List getSelectedItems();

	public abstract void setSelectedItems(List list);

	public abstract void setShowMutiSelect(boolean bShow);

	public abstract boolean isShowMutiSelect();

	public IPropertySelectionModel getMutiSelectMode() {
		if (mutiModel == null) {
			parserParameter();
		}
		return mutiModel;
	}

	public void setMutiSelectMode(IPropertySelectionModel model) {
		mutiModel = model;
	}

	public abstract void setMutiSelectTitle(String name);

	public abstract String getMutiSelectTitle();

	public abstract void setUpdate(boolean b);

	@InitialValue("false")
	public abstract boolean isUpdate();

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

		map.put("tag", "$#" + tagValue + "#");
		map.put("num", getNum());
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
	
	public String getTemplateURL(){
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		params[0] = "templateId";
		params[1] = getTemplateType();
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
