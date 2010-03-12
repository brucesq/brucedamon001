package com.hunthawk.reader.page.guide;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;

import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/***
 *   ≈‰∆˜±Í«©œÚµº
 * 
 * @author penglei
 * 
 */
public abstract class AdapterGuide extends SecurityPage implements
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

	public abstract Integer getProperty();

	public abstract void setProperty(Integer property);

	public abstract Integer getIsShowCount();

	public abstract void setIsShowCount(Integer isShowCount);

	public abstract Integer getIsize();

	public abstract void setIsize(Integer isize);

	public abstract Integer getAdapterRuleId();

	public abstract void setAdapterRuleId(Integer adapterRuleId);

	public abstract Integer getAdapterId();

	public abstract void setAdapterId(Integer adapterId);

	public abstract Integer getTmdId();

	public abstract void setTmdId(Integer id);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		setTagName((String) parameters[1]);
	}

	public void onSubmit(IRequestCycle cycle) {

		StringBuilder sb = new StringBuilder();

		sb.append("$#" + getTagName() + ".");
		if (getTmdId() != null)
			sb.append("tmd=" + getTmdId() + ",");

		if (getAdapterId() != null && getAdapterId() != 0) {
			sb.append("adapterId=" + getAdapterId() + ",");
		}

		if (getAdapterRuleId() != null && getAdapterRuleId() != 0) {
			sb.append("adapterRuleId=" + getAdapterRuleId() + ",");
		}
		String str = sb.substring(0, sb.length() - 1);
		str += "#";
		setReturnValue(str);
		setNeedReturn(Boolean.TRUE);
	}

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
		map.put("update", Boolean.FALSE);
		String tagValue = getTagName();

		map.put("tag", "$#imglink#");
		map.put("num", 1);
		return map;
	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getAdapterRuleURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		params[0] = "adapterRuleId";
		TemplateType type = new TemplateType();
		type.setId(6);
		params[1] = type;
		String templateURL = PageHelper.getExternalFunction(service,
				"adapter/AdapterRuleChoicePage", params);

		return templateURL;
	}

	public String getAdapterURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		params[0] = "adapterId";
		TemplateType type = new TemplateType();
		type.setId(6);
		params[1] = type;
		String templateURL = PageHelper.getExternalFunction(service,
				"adapter/AdapterChoicePage", params);

		return templateURL;
	}

	public String getTmdURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		params[0] = "TMDId";
		if (getTagName() != null && !"".equals(getTagName()))
			params[1] = getTagName();
		else
			params[1] = "all";
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TagTemplateChoicePage", params);
		return templateURL;
	}

}
