package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.SysTagType;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.guide.TagGuide;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.system.SystemService;


@Restrict(roles = { "userDefTag" }, mode = Restrict.Mode.ROLE)
public abstract class EditUserDefTagPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return UserDefTag.class;
	}

	@Override
	protected boolean persist(Object object) {
		try {
			UserDefTag userDefTag = (UserDefTag)object;
			if (isModelNew()) {
				userDefTag.setStatus(1);
				userDefTag.setName("userDefTag");
				userDefTag.setContent(getContent());
				getTemplateService().addUserDefTag(userDefTag);
			} else {
				userDefTag.setContent(getContent());
				getTemplateService().updateUserDefTag(userDefTag);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}
	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new UserDefTag());
		}else{  //初始化页面的显示：		
			UserDefTag target = (UserDefTag)getModel();
			setContent(target.getContent());
		}
	}
	
	
	public abstract String getContent();
	public abstract void setContent(String content);
	
	public List<SysTagType> getSysTagTypes() {
		List<SysTagType> sysTagTypes = new ArrayList<SysTagType>();
		Variables variables = getSystemService().getVariables(
				Variables.SYSTAG_PERTAINT_TYPE);

		Map map = PageUtil.getMapFormString(variables.getValue());

		Set keySet = map.keySet();

		Iterator keyIt = keySet.iterator();

		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			String value = (String) map.get(key);
			if(!value.equals("userDefTag")){
				
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("pertaintype",
						value, CompareType.Equal);
				expressions.add(ex);
				List<SysTag> list = getTemplateService().findSysTag(1,
						Integer.MAX_VALUE, "id", true, expressions);
	
				List<TagGuide> tags = new ArrayList<TagGuide>();
				for (int i = 0; i < list.size(); i++) {
					tags.add(new TagGuide(list.get(i), getExternalService()));
				}

				SysTagType sysTagType = new SysTagType();
				sysTagType.setType(key);
				sysTagType.setTagGuide(tags);
				
				sysTagTypes.add(sysTagType);
			}		
		}

		return sysTagTypes;
	}

	public abstract SysTagType getSysTagType();

	public abstract void setSysTagType(SysTagType sysTagType);
	
	public abstract TagGuide getSysTag();

	public abstract void setSysTag(TagGuide tag);

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();
	
}
