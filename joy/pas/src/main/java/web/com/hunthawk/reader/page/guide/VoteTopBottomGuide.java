package com.hunthawk.reader.page.guide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.inter.InteractiveService;

/**
 * 投票引入模版
 * 
 * @author liuxh
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class VoteTopBottomGuide extends SecurityPage implements
		IExternalPage {

	@InjectObject("spring:interactiveService")
	public abstract InteractiveService getInteractiveService();

	public abstract String[] getParameter();

	public abstract String getTagName();

	public abstract void setTagName(String name);

	public abstract void setParameter(String[] para);

	public abstract Integer getProperty();

	public abstract void setProperty(Integer property);

	public abstract void setMixs(List resourcetypes);

	public abstract List getMixs();

	public abstract Integer getVoteType();

	public abstract void setVoteType(Integer voteType);

	public abstract String getCustomId();

	public abstract void setCustomId(String customId);

	public abstract Integer getTemplateId();

	public abstract void setTemplateId(Integer templateId);

	public abstract Integer getItemId();

	public abstract void setItemId(Integer itemId);

	public abstract Integer getVoteId();

	public abstract void setVoteId(Integer voteId);

	public IPropertySelectionModel getMixList() {
		Map<String, String> types = new OrderedMap<String, String>();
		types.put("图标", "imgId");
		types.put("名称", "name");
		return new MapPropertySelectModel(types);
	}

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		setTagName((String) parameters[1]);
	}

	public void onSubmit(IRequestCycle cycle) {

		StringBuilder sb = new StringBuilder();

		sb.append("$#" + getTagName() + ".");
		if (getVoteType() != null) { // 投票类型
			if (getTemplateId() != null)
				sb.append("templateId=" + getTemplateId() + ",");
			if (getVoteId() != null)// 投票ID
				sb.append("voteId=" + getVoteId() + ",");
			if (getItemId() != null)// 投票子项ID
				sb.append("itemId=" + getItemId() + ",");
			if (getVoteType() == 4) { // 用户定制
				if (getCustomId() != null)
					sb.append("customId=" + getCustomId() + ",");
			}

			if (getMixs() != null && getMixs().size() != 0) {
				List mixList = getMixs();
				sb.append("mix=");
				for (int i = 0; i < mixList.size(); i++) {
					String str = (String) mixList.get(i);
					sb.append("<%" + str + "%>");
				}
				sb.append(",");
			}
			sb.append("voteType=" + getVoteType());
		}
		sb.append("#");
		setReturnValue(sb.toString());
		setNeedReturn(Boolean.TRUE);
	}

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();

	public IPropertySelectionModel getVoteTypeList() {
		Map<String, Integer> msgType = new OrderedMap<String, Integer>();
		msgType.put("内容", 1);
		msgType.put("栏目", 2);
		msgType.put("产品", 3);
		msgType.put("用户定制", 4);
		return new MapPropertySelectModel(msgType);
	}

	public boolean isShowCustom() {
		if (getVoteType() == null)
			return false;
		else {
			if (getVoteType() == 4)
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

	public String getTemplateURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[2];
		params[0] = "templateId";
		TemplateType type = new TemplateType();
		type.setId(6);
		params[1] = type;
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}

	public String getVoteURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[1];
		params[0] = "voteId";
		String templateURL = PageHelper.getExternalFunction(service,
				"inter/VoteChoicePage", params);

		return templateURL;
	}
	// public String getItemURL(String voteId)
	// {
	// IEngineService service = getExternalService();
	// System.out.println("voteId="+voteId);
	// Object[] params = new Object[2];
	// params[0] = "itemId";
	// params[1]=voteId;
	// String templateURL = PageHelper.getExternalFunction(service,
	// "inter/VoteItemChoicePage", params);
	//
	// return templateURL;
	// }

}
