/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.SysTagType;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.guide.ParseTemplate;
import com.hunthawk.reader.page.guide.TagGuide;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "templatechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditTemplatePage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return Template.class;
	}

	public String getContent(){
		Template target = (Template)getModel();
		if(StringUtils.isEmpty(target.getPreContent())){
			return target.getContent();
		}else{
			return target.getPreContent();
		}
	}
	public void setContent(String content){
		Template target = (Template)getModel();
		target.setPreContent(content);
		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			Template target = (Template) object;
			if (isModelNew()) {
				if (!verifyTemplate(target.getDownPageId()))
					throw new Exception("下载模板不存在");
				target.setCreateTime(new Date());
				target.setModifyTime(new Date());
				target.setCreateorId(getUser().getId());
				target.setMotifierId(getUser().getId());
				target.setStatus(0);
				target.setPreStatus(0);
				getTemplateService().addTemplate(target);
			} else {
				if (!verifyTemplate(target.getDownPageId()))
					throw new Exception("下载模板不存在");
				target.setModifyTime(new Date());
				target.setMotifierId(getUser().getId());
//				target.setPreStatus(target.getStatus());
				target.setStatus(0);
				getTemplateService().updateTemplate(target);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			Template template = new Template();
			template.setShowType(1);
			template.setSignType(1);
			TemplateType templateType = new TemplateType();
			templateType.setId(TemplateType.COLUMN_PAGE);
			template.setTemplateType(templateType);
			setModel(template);
		}

	}

	private boolean verifyTemplate(Integer id) {
		if (id == null || id == 0) {
			return true;
		}
		if (getTemplateService().getTemplate(id) == null)
			return false;
		return true;
	}

	public IPropertySelectionModel getTemplateTypes() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getTemplateService().getAllTemplateType(), TemplateType.class,
				"getName", "getId", false, "");
		return parentProper;
	}

	public IPropertySelectionModel getTemplateCatalogs() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getTemplateService().getAllTemplateCatalog(),
				TemplateCatalog.class, "getName", "getId", false, "");
		return parentProper;
	}

	public IPropertySelectionModel getBussinessTypeList() {
		return new MapPropertySelectModel(Constants.getBussinessTypes());
	}

	public IPropertySelectionModel getVersionTypeList() {
		return new MapPropertySelectModel(Constants.getVersionTypes());
	}

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
			if(value.equals("userDefTag")){ //自定义标签
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("status",
						1, CompareType.Equal);
				expressions.add(ex);   //检索使用状态的标签
				
				List<UserDefTag> list = 
					getTemplateService().getUserDefTagList(1, Integer.MAX_VALUE, "id", true, expressions);
				List<TagGuide> tags = new ArrayList<TagGuide>();
				for(int i=0 ;i<list.size();i++){
					tags.add(new TagGuide(list.get(i)));
				}
				SysTagType sysTagType = new SysTagType();
				sysTagType.setType(key);
				sysTagType.setTagGuide(tags);
				
				sysTagTypes.add(sysTagType);
				
			}else{
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

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public abstract void setShowPre(boolean bShow);

	@InitialValue("false")
	public abstract boolean isShowPre();

	public void onClick() {
		// getBuilder().updateComponent("preContent");
		setShowPre(true);
		setCancelPullback(true);
	}

	public String getPreContent() {
		if (isShowPre()) {
			return ParseTemplate.parse(getContent(),
					getTemplateService(), getExternalService());

		} else {
			return "";
		}

	}

	public abstract TagGuide getSysTag();

	public abstract void setSysTag(TagGuide tag);

	public String getTemplateURL() {
		IEngineService service = getExternalService();

		TemplateType type = new TemplateType();
		type.setId(TemplateType.DETAIL_PAGE);
		Object[] params = new Object[] { "pageid", type,
				((Template) getModel()).getSignType(),
				((Template) getModel()).getShowType() };
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}
}
