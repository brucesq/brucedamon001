/**
 * 
 */
package com.hunthawk.reader.service.bussiness.impl;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSet;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSetPK;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.domain.device.PersonInfoPK;
import com.hunthawk.reader.service.bussiness.TemplateService;

/**
 * @author BruceSun
 *
 */
public class TemplateServiceImpl implements TemplateService {

	private HibernateGenericController controller ;
	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.bussiness.TemplateService#addTemplateType(com.hunthawk.reader.domain.bussiness.TemplateType)
	 */
	public void addTemplateType(TemplateType type) {
		controller.save(type);
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.bussiness.TemplateService#getAllTemplateType()
	 */
	public List<TemplateType> getAllTemplateType() {
		return controller.getAll(TemplateType.class);
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.bussiness.TemplateService#getTemplateType(int)
	 */
	public TemplateType getTemplateType(int typeId) {
		return controller.get(TemplateType.class, typeId);
	}

	/* (non-Javadoc)
	 * @see com.hunthawk.reader.service.bussiness.TemplateService#updateTemplateType(com.hunthawk.reader.domain.bussiness.TemplateType)
	 */
	public void updateTemplateType(TemplateType type) {
		controller.update(type);
	}
	
	public void deleteTemplateType(TemplateType type){
		controller.delete(type);
	}

	public void setHibernateGenericController(HibernateGenericController controller){
		this.controller = controller;
	}

	public void addSysTag(SysTag tag) {
		controller.save(tag);
	}

	public void deleteSysTag(SysTag tag) {
		controller.delete(tag);
		
	}

	public List<SysTag> findSysTag(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		
		return controller.findBy(SysTag.class, pageNo, pageSize, orderBy, isAsc, expressions);
	}

	public Long getSysTagResultCount(
			Collection<HibernateExpression> expressions) {
		
		return controller.getResultCount(SysTag.class, expressions);
	}

	public void updateSysTag(SysTag tag) {
		controller.update(tag);
		
	}
	
	public void addTemplate(Template template) throws Exception {
		if(controller.isUnique(Template.class, template , "title"))
		{
			controller.save(template);
		}else{
			throw new Exception("模板名已经存在!");
		}
		
	}

	public void deleteTemplate(Template template) throws Exception {
		List<Template> ts = controller.findBy(Template.class, "downPageId", template.getId());
		if(ts.size() > 0){
			StringBuilder err = new StringBuilder();
			for(Template t:ts){
				err.append("模板[");
				err.append(t.getId());
				err.append("],名称[");
				err.append(t.getTitle());
				err.append("];");
			}
			err.append("已经引用了该模板，请清除所有引用后再进行删除");
			throw new Exception(err.toString());
			
		}
		controller.delete(template);
	}

	public List<Template> findTemplate(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(Template.class, pageNo, pageSize, orderBy, isAsc, expressions);
	}

	public Template getTemplate(int templateId) {
		
		return controller.get(Template.class, templateId);
	}

	public Long getTemplateResultCount(
			Collection<HibernateExpression> expressions) {
		
		return controller.getResultCount(Template.class, expressions);
	}

	public void updateTemplate(Template template) {
		controller.update(template); 
		
	}
	public void auditTemplate(Template template,int status) throws Exception{
		if(template.getStatus().equals(status)){
			throw new Exception(template.getTitle()+"状态已经是"+Constants.STATUS[status]+"状态了!");
		}
		if(status == 1){
			template.setPreStatus(1);
			template.setContent(template.getPreContent());
		}
		
		template.setStatus(status);
		controller.update(template);
	}
	public SysTag getSysTagbyName(String name){
		List<SysTag> tags = controller.findBy(SysTag.class, "name", name);
		if(tags.size() > 0){
			return tags.get(0);
		}
		return null;
	}
	
	public UserDefTag getUserDefTag(int id){
		return controller.get(UserDefTag.class, id);
	}

	public void addTemplateCatalog(TemplateCatalog type) {
		 controller.save(type);
	}

	public void deleteTemplateCatalog(TemplateCatalog type) {
		controller.delete(type);
		
	}

	public List<TemplateCatalog> getAllTemplateCatalog() {
		return controller.getAll(TemplateCatalog.class);
	}

	public TemplateCatalog getTemplateCatalog(int typeId) {
		return controller.get(TemplateCatalog.class, typeId);
	}

	public void updateTemplateCatalog(TemplateCatalog type) {
		controller.update(type);
		
	}

	public void saveOrUpdateDefaultTemplateSet(DefaultTemplateSet defaultTemplateSet) {
		DefaultTemplateSetPK pk = new DefaultTemplateSetPK();
		pk.setPageType(defaultTemplateSet.getPageType());
		pk.setWapType(defaultTemplateSet.getWapType());
		if(controller.get(DefaultTemplateSet.class, pk)!=null){
			controller.update(defaultTemplateSet);
		}else{
			controller.save(defaultTemplateSet);
		}
	}

	public void deleteDefaultTemplateSet(DefaultTemplateSet defaultTemplateSet) {
		controller.delete(defaultTemplateSet);
	}

	public Long getDefaultTemplateSetCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(DefaultTemplateSet.class, expressions);
	}

	public List<DefaultTemplateSet> getDefaultTemplateSetList(int pageNo,
			int pageSize, String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(DefaultTemplateSet.class, pageNo, pageSize,orderBy,isAsc, expressions);
	}


	public boolean defaultTemplateIsExit(DefaultTemplateSet defaultTemplateSet) {
		return controller.isUnique(DefaultTemplateSet.class, defaultTemplateSet, "pageType,wapType");
	}

	public void addUserDefTag(UserDefTag userDefTag) {
		controller.save(userDefTag);
	}

	public void delUserDefTag(UserDefTag userDefTag) {
		controller.delete(userDefTag);
	}

	public Long getUserDefTagCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(UserDefTag.class, expressions);
	}

	public List<UserDefTag> getUserDefTagList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(UserDefTag.class, pageNo, pageSize, orderBy, isAsc, expressions);
	}

	public void updateUserDefTag(UserDefTag userDefTag) {
		controller.update(userDefTag);
	}

	public void auditUserDefTag(UserDefTag userDefTag, Integer status)
			throws Exception {
		if(userDefTag.getStatus().equals(status)){
			throw new Exception(userDefTag.getTitle()+"状态已经是"+Constants.STATUS[status]+"状态了!");
		}else{
			userDefTag.setStatus(status);
			controller.update(userDefTag);
		}
	}

	public void addTagTemplate(TagTemplate tagTemplate) {
		controller.save(tagTemplate);	
	}

	public void delTagTemplate(TagTemplate tagTemplate) {
		controller.delete(tagTemplate);
	}

	public Long getTagTemplateCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(TagTemplate.class,expressions);
	}

	public TagTemplate getTagTemplate(Integer id) {
		return controller.get(TagTemplate.class, id);
	}

	public List<UserDefTag> getTagTemplateList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(TagTemplate.class,pageNo, pageSize, orderBy, isAsc, expressions );
	}

	public void updateTagTemplate(TagTemplate tagTemplate) {
		controller.update(tagTemplate);
	}
	
}
