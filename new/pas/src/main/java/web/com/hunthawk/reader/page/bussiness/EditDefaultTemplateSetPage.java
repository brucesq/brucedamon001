/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.callback.EditCallback;
import com.hunthawk.framework.tapestry.callback.SearchCallback;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSet;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "columnchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditDefaultTemplateSetPage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();
	
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	public abstract Integer getShowType();
	
	public abstract void setShowType(Integer showType);
	

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return DefaultTemplateSet.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	
	public void saveAndReturn(IRequestCycle cycle) {
		if (save()){			
		}
	}
	@Override
	protected boolean persist(Object object) {
		try {
			DefaultTemplateSet defaultTemplate = (DefaultTemplateSet) object;
			defaultTemplate.setPageType(getTemplateType());
			defaultTemplate.setWapType(getWapType());
			defaultTemplate.setTemplateId(getTemplate());
			if(getTemplate()==0)
				throw new Exception("您至少得选择一个模板！");
			getTemplateService().saveOrUpdateDefaultTemplateSet(defaultTemplate);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);

			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new DefaultTemplateSet());	
		}
	}
	public IPropertySelectionModel getPageTypelist() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("栏目模板", TemplateType.COLUMN_PAGE);
		map.put("资源模板", TemplateType.RESOURCE_PAGE);
		map.put("详情模板", TemplateType.DETAIL_PAGE);
		map.put("计费模板",TemplateType.FEE_PAGE);
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map);
		return mapPro;
	}
	
	public abstract Integer getTemplateType();
	public abstract void setTemplateType(Integer templateType);
	
	public abstract Integer getWapType();
	public abstract void setWapType(Integer wapType);
	
	public abstract Integer getTemplate();
	public abstract void setTemplate(Integer tempalte);
	
	public IPropertySelectionModel getWapTypelist(){
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("WAP1.X", 1);//wap1.x
		map.put("WAP2.0",2);//wap2.x
		map.put("3G",3);//3G
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map);
		return mapPro;
	}
	
	public IPropertySelectionModel getTemplatelist(){
		//------初始化模板值-------------
		//初始化选中的值：
		//---------------------初始化显示选中--------------------------------
		if(getTemplateType()==null && getWapType() ==null){
			Collection<HibernateExpression> defaultExpressions1 = new ArrayList<HibernateExpression>();
			HibernateExpression defaultA = new CompareExpression("wapType", 1,
					CompareType.Equal);
			
			defaultExpressions1.add(defaultA);
			HibernateExpression defaultB = new CompareExpression("pageType", 2,
					CompareType.Equal);
			
			defaultExpressions1.add(defaultB);
			
			List<DefaultTemplateSet> defaultList = getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", true, defaultExpressions1);
			if(!defaultList.isEmpty()){//表明有组合的模板
				DefaultTemplateSet defaultTemplateSet= 	defaultList.get(0);
				setTemplate(defaultTemplateSet.getTemplateId());
				}
			//----------------初始化显示列表--------------------------------------
			Collection<HibernateExpression> hibernateExpressions2 = new ArrayList<HibernateExpression>();
			TemplateType templatetypeDefault= new TemplateType();
			templatetypeDefault.setId(2);
			HibernateExpression defaultC = new CompareExpression("templateType", templatetypeDefault,
					CompareType.Equal);
			
			hibernateExpressions2.add(defaultC);
			
			HibernateExpression defaultD = new CompareExpression("signType", 1,
					CompareType.Equal);
			hibernateExpressions2.add(defaultD);
			List<Template> templateList1 = getTemplateService().findTemplate(1,
					Integer.MAX_VALUE, "id", true, hibernateExpressions2);
			
			Map<String,Integer> map1 = new OrderedMap<String,Integer>();
			map1.put("", 0);
			for(Template template :templateList1){
				   map1.put(template.getTitle(), template.getId());
					}	
				MapPropertySelectModel model1 = new MapPropertySelectModel(map1);
				return model1;
				//_______________________初始化结束________________________-
		}
		else{
			Collection<HibernateExpression> defaultTemplateExpressions = new ArrayList<HibernateExpression>();
			if(getWapType()!=null){
			HibernateExpression nameA = new CompareExpression("wapType", getWapType(),
					CompareType.Equal);
			
			defaultTemplateExpressions.add(nameA);
			}
			if(getTemplateType()!=null){
			HibernateExpression nameB = new CompareExpression("pageType", getTemplateType(),
					CompareType.Equal);
			defaultTemplateExpressions.add(nameB);
			}
			List<DefaultTemplateSet> defaultTemplateSetList = getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", true, defaultTemplateExpressions);
			if(!defaultTemplateSetList.isEmpty()){//表明有组合的模板
				DefaultTemplateSet defaultTemplateSet= 	defaultTemplateSetList.get(0);
				setTemplate(defaultTemplateSet.getTemplateId());
			}
			//--------------过滤模板类型----------------------
			Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
			if(getTemplateType()!=null){
				TemplateType templatetype= new TemplateType();
				templatetype.setId(getTemplateType());
			HibernateExpression expression = new CompareExpression("templateType", templatetype,
					CompareType.Equal);
			hibernateExpressions.add(expression);
			}
			//---------------过滤 模板类型标识(WAP1.x 或 WAP2.x 或 3G)-------------
			
			if(getWapType()!=null){
			HibernateExpression expression1 = new CompareExpression("signType", getWapType(),
					CompareType.Equal);
			hibernateExpressions.add(expression1);
			}
			List<Template> templateList = getTemplateService().findTemplate(1,
					Integer.MAX_VALUE, "id", true, hibernateExpressions);
			Map<String,Integer> map = new OrderedMap<String,Integer>();
			map.put("", 0);
			if(getTemplateType()!=null && getWapType() !=null && !templateList.isEmpty()){	
			for(Template template :templateList){
			   map.put(template.getTitle(), template.getId());
				}	
			}
			MapPropertySelectModel model = new MapPropertySelectModel(map);
			return model;
			}
	}
}
