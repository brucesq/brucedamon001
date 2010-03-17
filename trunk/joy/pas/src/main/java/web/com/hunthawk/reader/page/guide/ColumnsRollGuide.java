package com.hunthawk.reader.page.guide;

import java.util.ArrayList;
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
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 栏目轮循向导
 * 
 * @author liuxh
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class ColumnsRollGuide extends SecurityPage implements
		IExternalPage {
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

	public abstract String getColumnids();
	public abstract void setColumnids(String ids);
	
	public abstract Integer getPageSize();
	public abstract void setPageSize(Integer size);
	
	public abstract Integer getNum();

	public abstract void setNum(Integer num);

	public abstract void setParameterValue(String str);

	public abstract String getParameterValue();

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		setTagName((String) parameters[1]);
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
//					System.out.println(kv[0] + "{}" + kv[1]);
					if ("pageSize".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setPageSize(iv);
						}
					} else if ("columnids".equals(kv[0])) {
						
							setColumnids(kv[1]);
						
					} else if("mix".equals(kv[0])){
						PatternCompiler compiler = new Perl5Compiler();
						try {
							Pattern pattern = compiler.compile(CommonGuide.MUTI_START
									+ "[^\\" + CommonGuide.MUTI_LETTER + "]*" + CommonGuide.MUTI_END);
							PatternMatcherInput input = new PatternMatcherInput(
									kv[1]);
							PatternMatcher matcher = new Perl5Matcher();
							while (matcher.contains(input, pattern)) {
								MatchResult result = matcher.getMatch();
								String value = result.group(0);
								value = value.substring(CommonGuide.MUTI_START.length(),
										result.length() - CommonGuide.MUTI_END.length());
								setMixs(new ArrayList());
								getMixs().add(value);
								System.out.println("mix:"+value);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
	}

	public IPropertySelectionModel getMixList() {
		Map<String, String> types = new OrderedMap<String, String>();
		types.put("名称", "name");
		types.put("资源数", "count");
		return new MapPropertySelectModel(types);
	}

	public abstract void setMixs(List resourcetypes);
	public abstract List getMixs();
	
	public void onSubmit(IRequestCycle cycle) {

		StringBuilder sb = new StringBuilder();

		sb.append("$#" + getTagName() + ".");
		if (getMixs() != null && getMixs().size() != 0) {
			List mixList = getMixs();
			sb.append("mix=");
			for (int i = 0; i < mixList.size(); i++) {
				String str = (String) mixList.get(i);
				sb.append("<%" + str + "%>");
			}
			sb.append(",");
		}
		if(getPageSize() != null && getPageSize() > 0){
			sb.append("pageSize=");
			sb.append(getPageSize());
			sb.append(",");
		}
		sb.append("columnids="+getColumnids());
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

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();

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

	public String getColumnURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[1];
		params[0] = "columnids";
		String columnURL = PageHelper.getExternalFunction(service,
				"bussiness/MoreColumnsChoicePage", params);

		return columnURL;
	}

}
