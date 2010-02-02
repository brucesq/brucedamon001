/**
 * 
 */
package com.hunthawk.reader.service.bussiness;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSet;
import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.enhance.annotation.Filter;
import com.hunthawk.reader.enhance.annotation.Memcached;
import com.hunthawk.reader.enhance.annotation.Usersable;

/**
 * @author BruceSun
 *
 */
public interface TemplateService {

	public TemplateType getTemplateType(int typeId);
	
	@Logable(name = "TemplateType", action = "add", keyproperty="id",property = { "id=ID,name=名称" })
	public void addTemplateType(TemplateType type);
	
	@Logable(name = "TemplateType", action = "update", keyproperty="id",property = { "id=ID,name=名称" })
	public void updateTemplateType(TemplateType type);
	
	@Logable(name = "TemplateType", action = "delete", keyproperty="id",property = { "id=ID,name=名称" })
	public void deleteTemplateType(TemplateType type);
	
	public List<TemplateType> getAllTemplateType();
	
	@Logable(name = "SysTag", action = "add", keyproperty="id",property = { "id=ID,title=标签中文名称,type=标签类型,pertaintype=所属类别" })
	public void addSysTag(SysTag tag);
	
	@Logable(name = "SysTag", action = "update", keyproperty="id",property = { "id=ID,title=标签中文名称,type=标签类型,pertaintype=所属类别" })
	public void updateSysTag(SysTag tag);
	
	@Logable(name = "SysTag", action = "delete", keyproperty="id",property = { "id=ID,title=标签中文名称,type=标签类型,pertaintype=所属类别" })
	public void deleteSysTag(SysTag tag);
	
	public SysTag getSysTagbyName(String name);
	
	public UserDefTag getUserDefTag(int tagId);
	
	public List<SysTag> findSysTag(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	
	public Long getSysTagResultCount(Collection<HibernateExpression> expressions);
	
	public Template getTemplate(int templateId);
	
	@Usersable
	@Memcached(targetClass = Template.class, properties = { "id" })
	@Logable(name = "Template", action = "add", property = { "id=ID,title=名称,templateType.id=类型ID,content=内容,status=状态,signType=模板类型标识,showType=输出类型,downPageId=下载页模板,templateCatalog.id=目录ID" })
	public void addTemplate(Template template)throws Exception;
	
	@Memcached(targetClass = Template.class, properties = { "id" })
	@Logable(name = "Template", action = "update", property = { "id=ID,title=名称,templateType.id=类型ID,content=内容,status=状态,signType=模板类型标识,showType=输出类型,downPageId=下载页模板,templateCatalog.id=目录ID" })
	public void updateTemplate(Template template)throws Exception;
	
	@Memcached(targetClass = Template.class, properties = { "id" })
	@Logable(name = "Template", action = "delete", property = { "id=ID,title=名称,templateType.id=类型ID,content=内容,status=状态,signType=模板类型标识,showType=输出类型,downPageId=下载页模板,templateCatalog.id=目录ID" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "templatechange" })
	public void deleteTemplate(Template template)throws Exception;
	
	@Memcached(targetClass = Template.class, properties = { "id" })
	@Logable(name = "Template", action = "audit", property = { "id=ID,title=名称,templateType.id=类型ID,content=内容,status=状态,signType=模板类型标识,showType=输出类型,downPageId=下载页模板,templateCatalog.id=目录ID" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "templatechange" })
	public void auditTemplate(Template template,int status)throws Exception;
	
	@Filter(targetClass=Template.class,value=Filter.Position.ARG_5)
	public List<Template> findTemplate(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	@Filter(targetClass=Template.class,value=Filter.Position.ARG_1)
	public Long getTemplateResultCount(Collection<HibernateExpression> expressions);
	
	
	
	public TemplateCatalog getTemplateCatalog(int typeId);
	
	@Logable(name = "TemplateCatalog", action = "add", property = { "id=ID,name=名称" })
	public void addTemplateCatalog(TemplateCatalog type);
	@Logable(name = "TemplateCatalog", action = "update", property = { "id=ID,name=名称" })
	public void updateTemplateCatalog(TemplateCatalog type);
	@Logable(name = "TemplateCatalog", action = "delete", property = { "id=ID,name=名称" })
	public void deleteTemplateCatalog(TemplateCatalog type);
	
	public List<TemplateCatalog> getAllTemplateCatalog();
	
	/**
	 * 默认模板管理
	 */
	@Logable(name = "DefaultTemplateSet", action = "saveOrUpdate", property = { "pageType=模板类型,wapType=版本类型,templateId=模板ID" })
	public void saveOrUpdateDefaultTemplateSet(DefaultTemplateSet defaultTemplateSet);

	@Logable(name = "DefaultTemplateSet", action = "delete", property = { "pageType=模板类型,wapType=版本类型,templateId=模板ID" })
	public void deleteDefaultTemplateSet(DefaultTemplateSet defaultTemplateSet);
	
	public Long getDefaultTemplateSetCount(Collection<HibernateExpression> expressions);
	
	public List<DefaultTemplateSet> getDefaultTemplateSetList(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public boolean defaultTemplateIsExit(DefaultTemplateSet defaultTemplateSet);
	
	/**
	 * 自定义标签管理
	 */
	@Usersable
	@Memcached(targetClass = UserDefTag.class, properties = { "id" })
	@Logable(name = "UserDefTag", action = "add", property = { "id=ID,title=标签显示名,content=标签的内容,status=状态" })
	public void addUserDefTag(UserDefTag userDefTag);
	
	@Memcached(targetClass = UserDefTag.class, properties = { "id" })
	@Logable(name = "UserDefTag", action = "update", property = { "id=ID,title=标签显示名,content=标签的内容,status=状态" })
	public void updateUserDefTag(UserDefTag userDefTag);
	
	@Memcached(targetClass = UserDefTag.class, properties = { "id" })
	@Logable(name = "UserDefTag", action = "delete", property = { "id=ID,title=标签显示名,content=标签的内容,status=状态" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "userDefTag" })
	public void delUserDefTag(UserDefTag userDefTag);
	
	@Filter(targetClass=UserDefTag.class,value=Filter.Position.ARG_1)
	public Long getUserDefTagCount(Collection<HibernateExpression> expressions);
	
	@Filter(targetClass=UserDefTag.class,value=Filter.Position.ARG_5)
	public List<UserDefTag>  getUserDefTagList(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	@Memcached(targetClass = UserDefTag.class, properties = { "id" })
	@Logable(name = "UserDefTag", action = "audit", property = { "id=ID,title=标签显示名,content=标签的内容,status=状态" })
	@Restrict(mode = Restrict.Mode.ROLE, roles = { "userDefTag" })
	public void auditUserDefTag(UserDefTag userDefTag,Integer status)throws Exception;
	
	
	/**
	 * 标签模板管理
	 */
	@Memcached(targetClass = TagTemplate.class, properties = { "id" })
	@Logable(name = "TagTemplate", action = "add", property = { "id=ID,name=标签显示名,tagName=标签名称" })
	public void addTagTemplate(TagTemplate tagTemplate);
	
	@Memcached(targetClass = TagTemplate.class, properties = { "id" })
	@Logable(name = "TagTemplate", action = "update", property = { "id=ID,name=标签显示名,tagName=标签名称" })
	public void updateTagTemplate(TagTemplate tagTemplate);
	
	@Memcached(targetClass = TagTemplate.class, properties = { "id" })
	@Logable(name = "TagTemplate", action = "delete", property = { "id=ID,name=标签显示名,tagName=标签名称" })
	public void delTagTemplate(TagTemplate tagTemplate);
	
	public Long getTagTemplateCount(Collection<HibernateExpression> expressions);
	
	public List<UserDefTag>  getTagTemplateList(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public TagTemplate getTagTemplate(Integer id);
}


