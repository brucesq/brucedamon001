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
					
					if ("voteId".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setVoteId(iv);
						}
					} else if ("itemId".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setItemId(iv);
						}
					} else if ("customId".equals(kv[0])) {
						
							setCustomId(kv[1]);
						
					} else if ("templateId".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setTemplateId(iv);
						}
					}else if ("voteType".equals(kv[0])) {
						Integer iv = getInteger(kv[1]);
						if (iv != -999) {
							setVoteType(iv);
						}
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
								if(getMixs() == null){
									setMixs(new ArrayList());
								}
								getMixs().add(value);
							}
						} catch (Exception e) {
						}
					}
				}

			}
		}
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
