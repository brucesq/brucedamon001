package com.hunthawk.reader.page.guide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
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
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		setTagName((String) parameters[1]);
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
		sb.append("columnids="+getColumnids());
		sb.append("#");
		setReturnValue(sb.toString());
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

	public String getColumnURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[1];
		params[0] = "columnids";
		String columnURL = PageHelper.getExternalFunction(service,
				"bussiness/MoreColumnsChoicePage", params);

		return columnURL;
	}

}
