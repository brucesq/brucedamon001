package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSet;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "pagegroupchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditPageGroupPage extends EditPage implements
		PageBeginRenderListener {
	// @InjectObject("spring:pagegroupService")
	// public abstract PagegroupService getPagegroupService();

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		
		return PageGroup.class;
	}

	@Override
	protected boolean persist(Object object) {
		
		try {
			PageGroup pg = (PageGroup) object;
			pg.setModifier(getUser().getId());
			pg.setModifyTime(new Date());
			if (isModelNew()) {
				pg.setCreateTime(new Date());
				pg.setCreator(getUser().getId());
				pg.setDeleteStatus(1);// 页面组删除状态 1.使用 2.隐藏
				pg.setPkStatus(1);
				if(!verifyTemplateNeed(pg.getPkOneTempId())){
					throw new Exception("首页默认模板不存在");
				}
//				if(!verifyTemplateNeed(pg.getResOneTempId())){
//					throw new Exception("资源详情默认模板不存在");
//				}
				
				if(!verifyTemplate(pg.getPkSecondTempId())){
					throw new Exception("首页WAP2.0模板不存在");
				}
				
				if(!verifyTemplate(pg.getPkThirdTempId())){
					throw new Exception("首页3G模板不存在");
				}
				
//				if(!verifyTemplate(pg.getResSecondTempId())){
//					throw new Exception("资源详情WAP2.0模板不存在");
//				}
				
//				if(!verifyTemplate(pg.getResThirdTempId())){
//					throw new Exception("资源详情3G模板不存在");
//				}
				getBussinessService().addPageGroup(pg);
				
				createDefaultColumns(pg,1,"图书分类");
				createDefaultColumns(pg,2,"报纸分类");
				createDefaultColumns(pg,3,"杂志分类");
				createDefaultColumns(pg,4,"漫画分类");
				createDefaultColumns(pg,0,"搜索");
				createDefaultColumns(pg,6,"视频分类");
				createDefaultColumns(pg,7,"新闻分类");
				createDefaultColumns(pg,8,"软件分类");
					//------------------------------------------------
			} else {
				pg.setDeleteStatus(1);// 页面组删除状态 1.使用 2.隐藏
				getBussinessService().updatePageGroup(pg);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	private boolean verifyTemplate(Integer id) {
		if (id == null || id == 0) {
			return true;
		}
		if (getTemplateService().getTemplate(id) == null)
			return false;
		return true;
	}
	
	private boolean verifyTemplateNeed(Integer id) {
		if (id == null || id == 0) {
			return false;
		}
		if (getTemplateService().getTemplate(id) == null)
			return false;
		return true;
	}
	
	public IPropertySelectionModel getProductStatusList() {
		return new MapPropertySelectModel(Constants.getProductStatus());
	}

	public IPropertySelectionModel getBussinessTypeList() {
		return new MapPropertySelectModel(Constants.getBussinessTypes());
	}

	public void pageBeginRender(PageEvent arg0) {

		if (getModel() == null) {
			PageGroup pagegroup = new PageGroup();
			pagegroup.setShowType(1);
			setModel(pagegroup);
		}
	}

	// 首页模板 资源模板 栏目模板配置页
	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getTemplateSetURL() {
		IEngineService service = getExternalService();

		Object[] params = new Object[] { "pkOneTempId" };
		String templateSetURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateSetPage", params);

		return templateSetURL;
	}

	/**
	 * 
	 * @param value1
	 *            首页模板/资源模板 标识 1=首页 2=资源
	 * @param value2
	 *            模板版本id (1=wap1.x 2=wap2.0 3=3g)
	 * @param pams
	 *            返回字段参数名称
	 * @return
	 */
	public String getTemplateSetURLByParams(Integer typeId, Integer value2,
			String pams) {
		// 得到页面 页面组表示 下拉框的值

		// IEngineService service = getExternalService();
		// TemplateType templateType = new TemplateType();
		// if ("1".equalsIgnoreCase(value1)) {// 首页模板
		// templateType.setId(TemplateType.FIRST_PAGE);
		// } else if ("2".equalsIgnoreCase(value1)) {// 资源模板
		// templateType.setId(TemplateType.RESOURCE_PAGE);
		// } else {
		// templateType.setId(TemplateType.FIRST_PAGE);
		// }
		//
		// Object[] params = new Object[] { "1", templateType,
		// Integer.parseInt(value2), pams };
		// String templateSetURL = PageHelper.getExternalFunction(service,
		// "bussiness/TemplateSetPage", params);
		//
		// return templateSetURL;

		IEngineService service = getExternalService();

		TemplateType type = new TemplateType();
		type.setId(typeId);
		Object[] params = new Object[] { pams, type, value2,
				((PageGroup) getModel()).getShowType() };
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}

	
	

	//---------------从默认模板中查询出 9中分类------------------------
	
	public Integer getColOneTemplateId(){   //栏目 wap1.x 
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType",1 
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.COLUMN_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0)		
			return defaultTemplates.get(0).getTemplateId();
		else
			return -1;
	}
	
	public Integer getColSecondTemplateId(){   //栏目 wap2.0
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType",2// o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.COLUMN_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}else
			return -1;
	
	}	
	public Integer getColThirdTemplateId(){   //栏目 3g
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType", 3//o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.COLUMN_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}else
			return -1;
	}	
	
	
	public Integer getResOneTemplateId(){   //资源 1.x
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType", 1//o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.RESOURCE_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}
		return -1;
	}	
	
	public Integer getResSecondTemplateId(){   //资源 2.0
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType", 2//o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.RESOURCE_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}else
			return -1;
	}	
	public Integer getResThirdTemplateId(){   //资源 3g
		//3g
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType",3//o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.RESOURCE_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}else
			return -1;
	}	
	public Integer getDelOneTemplateId(){   //详情 1.x
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType", 1//o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.DETAIL_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}
		return -1;
	}	
	public Integer getDelSecondTemplateId(){   //详情2.0
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType", 2//o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.DETAIL_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}else
			return -1;
	}	
	public Integer getDelThirdTemplateId(){   //详情 3g
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("wapType", 3//o.getPagegroup().getShowType()
				, CompareType.Equal);
		expressions.add(ex);
		HibernateExpression ex2 = new CompareExpression("pageType", TemplateType.DETAIL_PAGE
				, CompareType.Equal);
		expressions.add(ex2);
		List<DefaultTemplateSet> defaultTemplates=getTemplateService().getDefaultTemplateSetList(1, Integer.MAX_VALUE, "templateId", false, expressions);
		if(defaultTemplates!=null && defaultTemplates.size()>0){
			return defaultTemplates.get(0).getTemplateId();
		}else
			return -1;
	}
	
	public List getResourceType(Integer showType){
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("showType", showType
				, CompareType.Equal);
		expressions.add(ex);
		
		List<ResourceType>  resourcetype = getResourceService().findResourceTypeBy(1, Integer.MAX_VALUE, "id", false,expressions);
		
		Set<ResourceType> parentType = new HashSet<ResourceType>();
		for(ResourceType type:resourcetype){
			if(type.getParent()!=null){
				parentType.add(type.getParent());
			}				
		}	
		if(parentType!=null){
		for(ResourceType parent: parentType){
			resourcetype.remove(parent);
			}
		}		
		return resourcetype;
	}
	
	public  void createDefaultColumns(PageGroup pg,Integer type,String columnName){
		Integer colOneTemplateId = getColOneTemplateId();//栏目1.x
		Integer colSecondTemplateId = getColSecondTemplateId();//栏目2.x
		Integer colThirdTemplateId = getColThirdTemplateId();//栏目3g
		Integer resOneTemplateId = getResOneTemplateId();//资源1.x
		Integer resSecodTemplateId = getResSecondTemplateId();//资源2.x
		Integer resThirdTemplateId = getResThirdTemplateId();//资源3g
		Integer delOneTemplateId = getDelOneTemplateId();//详情1.x
		Integer delSecondTemplateId = getDelSecondTemplateId();//详情2.x
		Integer delThirdTemplateId = getDelThirdTemplateId();//详情3g
		
		 Columns  column = new Columns();
		 	Date date=new  Date();
		 	column.setCreateTime(date);
		 	column.setModifyTime(date);
		 	column.setModifier(getUser().getId());
		 	column.setCreator(getUser().getId());
		 	column.setStatus(1);
		 	column.setCountSet(100);
			column.setOrder(50+type*5);
			column.setPagegroup(pg);
			if(	pg.getShowType()!=1){	//pad app
				if(colThirdTemplateId != -1)
					column.setColOneTempId(colThirdTemplateId);
				if(resThirdTemplateId != -1)
					column.setResOneTempId(resThirdTemplateId);
				if(delThirdTemplateId != -1)
					column.setDelOneTempId(delThirdTemplateId);	
			}else{
				if(colOneTemplateId != -1)
					column.setColOneTempId(colOneTemplateId);
				if(colSecondTemplateId != -1)
					column.setColSecondTempId(colSecondTemplateId);
				if(colThirdTemplateId != -1)
					column.setColThirdTempId(colThirdTemplateId);
				
				if(resOneTemplateId != -1)
					column.setResOneTempId(resOneTemplateId);
				if(resSecodTemplateId != -1)
					column.setResSecondTempId(resSecodTemplateId);
				if(resThirdTemplateId != -1)
					column.setResThirdTempId(resThirdTemplateId);
				
				if(delOneTemplateId != -1)
					column.setDelOneTempId(delOneTemplateId);
				if(delSecondTemplateId != -1)
					column.setDelSecondTempId(delSecondTemplateId);
				if(delThirdTemplateId != -1)
					column.setDelThirdTempId(delThirdTemplateId);	
				}
			
			if(type==0)
				column.setColumnType(2);
			else
				column.setColumnType(0);
			column.setCreateType(1);//系统创建
			column.setTitle(columnName);
			column.setName(columnName);
			column.setComment(columnName);
			try {
				getBussinessService().addColumn(column);
			} catch (Exception e) {
				
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
			}
			if(type==0)
				return;
			List<ResourceType> typeList = getResourceType(type); 
			if(typeList==null)
				return ;
			int i=50;
			for(ResourceType resourceType : typeList){
				Columns columnType = new Columns();
				columnType.setCreateTime(date);
				columnType.setModifyTime(date);
				columnType.setModifier(getUser().getId());
				columnType.setCreator(getUser().getId());
				columnType.setStatus(1);
				columnType.setCountSet(100);
				columnType.setOrder(i);
				i+=5;
				columnType.setPagegroup(pg);
				if(	pg.getShowType()!=1){	//pad app
					if(colThirdTemplateId != -1)
						columnType.setColOneTempId(colThirdTemplateId);
					if(resThirdTemplateId != -1)
						columnType.setResOneTempId(resThirdTemplateId);
					if(delThirdTemplateId != -1)
						columnType.setDelOneTempId(delThirdTemplateId);	
				}else{
					if(colOneTemplateId != -1)
						columnType.setColOneTempId(colOneTemplateId);
					if(colSecondTemplateId != -1)
						columnType.setColSecondTempId(colSecondTemplateId);
					if(colThirdTemplateId != -1)
						columnType.setColThirdTempId(colThirdTemplateId);
					
					if(resOneTemplateId != -1)
						columnType.setResOneTempId(resOneTemplateId);
					if(resSecodTemplateId != -1)
						columnType.setResSecondTempId(resSecodTemplateId);
					if(resThirdTemplateId != -1)
						columnType.setResThirdTempId(resThirdTemplateId);
					
					if(delOneTemplateId != -1)
						columnType.setDelOneTempId(delOneTemplateId);
					if(delSecondTemplateId != -1)
						columnType.setDelSecondTempId(delSecondTemplateId);
					if(delThirdTemplateId != -1)
						columnType.setDelThirdTempId(delThirdTemplateId);	
					}
				columnType.setName(resourceType.getName());
				columnType.setTitle(resourceType.getName());
				columnType.setComment(resourceType.getName());	
				columnType.setParent(column);	
				columnType.setPagegroup(pg);
				columnType.setColumnType(1);//分类
				columnType.setCreateType(1);//系统创建
				columnType.setResourceTypeId(resourceType.getId());
				try {
					getBussinessService().addColumn(columnType);
				} catch (Exception e) {
					getDelegate().setFormComponent(null);
					getDelegate().record(e.getMessage(), null);
				}
			}
	}
}
