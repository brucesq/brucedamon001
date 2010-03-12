package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.annotations.EventListener;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.services.ResponseBuilder;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.system.SystemService;

@Restrict(roles={"tagTemplate"},mode=Restrict.Mode.ROLE)
public abstract class EditTagTemplatePage extends EditPage implements
PageBeginRenderListener{

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return TagTemplate.class;
	}
	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new TagTemplate());
		}else{
			TagTemplate tagTem = (TagTemplate)getModel();
			SysTag sysTag= getTemplateService().getSysTagbyName(tagTem.getTagName());
			if(sysTag!=null)
				setPertaintype(sysTag.getPertaintype());
		}
	}
	
	@Override
	protected boolean persist(Object object) {
		try {
			TagTemplate tagTemplate = (TagTemplate)object;	
			UserImpl user = (UserImpl)getUser();
			if(isModelNew()){
				tagTemplate.setCreator(user.getId());
				tagTemplate.setCreateTime(new Date());
				tagTemplate.setModifier(user.getId());
				tagTemplate.setModifyTime(new Date());
				getTemplateService().addTagTemplate(tagTemplate);
			}else{
				tagTemplate.setModifier(user.getId());
				tagTemplate.setModifyTime(new Date());
				getTemplateService().updateTagTemplate(tagTemplate);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}
	
	public abstract String getPertaintype();
	public abstract void setPertaintype(String type);
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	public IPropertySelectionModel getContentsources()
	{
		
		Variables variables = getSystemService().getVariables("tag_of_type");
		Map map = PageUtil.getMapFormString(variables.getValue());
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}
	
	
	public IPropertySelectionModel getNameList() {
		
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(!ParameterCheck.isNullOrEmpty(getPertaintype())){
			HibernateExpression nameE = 
				new CompareExpression("pertaintype", getPertaintype(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}
			
		List<SysTag> list = getTemplateService().findSysTag( 1, Integer.MAX_VALUE, "name", true, hibernateExpressions);
		Map<String, String> map = new OrderedMap<String, String>();		
		for (SysTag systag : list) {
			map.put( systag.getName(),systag.getName());
		}
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}
	
	@EventListener(targets = "contentSource1", events = "onchange", submitForm = "userForm1",async=true)
	public void onProductChanged()
	{
			getBuilder().updateComponent("updatecolumn");
	}
	   
	public abstract ResponseBuilder getBuilder();
	
}
