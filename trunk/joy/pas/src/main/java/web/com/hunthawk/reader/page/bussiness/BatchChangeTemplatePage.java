/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NotNullExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSet;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.resource.ResourceService;

/**
 * @author yuzs
 * 
 */
@Restrict(roles = { "columnchange" }, mode = Restrict.Mode.ROLE)
public abstract class BatchChangeTemplatePage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();
	
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	/**
	 * 栏目columns集合
	 */
	public abstract Set getColumnsList();
	public abstract void setColumnsList(Set ColumnSet);
	
	public abstract PageGroup getPageGroup();
	public abstract void setPageGroup(PageGroup p);

	//栏目模板
	public abstract Integer getColOneTempId();
	public abstract void setColOneTempId(Integer colOneTempId);
	
	public abstract Integer getColSecondTempId();
	public abstract void setColSecondTempId(Integer colSecondTempId);
	
	public abstract Integer getColThirdTempId();
	public abstract void setColThirdTempId(Integer colThirdTempId);
	
	//资源模板
	public abstract Integer getResOneTempId();
	public abstract void setResOneTempId(Integer resOneTempId);
	
	public abstract Integer getResSecondTempId();
	public abstract void setResSecondTempId(Integer resSecondTempId);
	
	public abstract Integer getResThirdTempId();
	public abstract void setResThirdTempId(Integer resThirdTempId);
	
	//详情模板
	public abstract Integer getDelOneTempId();
	public abstract void setDelOneTempId(Integer delOneTempId);
	
	public abstract Integer getDelSecondTempId();
	public abstract void setDelSecondTempId(Integer delSecondTempId);
	
	public abstract Integer getDelThirdTempId();
	public abstract void setDelThirdTempId(Integer delThirdTempId);
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return Columns.class;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			Set columns = getColumnsList();
			if(columns==null)
				throw new Exception("请选择栏目或者选择的栏目已经丢失，请重新操作！");
			Iterator it = columns.iterator();
			while(it.hasNext()){
				Columns column =(Columns)it.next();
				
				if(verifyTemplateNeed(getColOneTempId())){
					column.setColOneTempId(getColOneTempId());
				}
				if(verifyTemplateNeed(getColSecondTempId())){
					column.setColSecondTempId(getColSecondTempId());
				}
				if(verifyTemplateNeed(getColThirdTempId())){
					column.setColThirdTempId(getColThirdTempId());
				}
				
				if(verifyTemplateNeed(getResOneTempId())){
					column.setResOneTempId(getResOneTempId());
				}
				if(verifyTemplateNeed(getResSecondTempId())){
					column.setResSecondTempId(getResSecondTempId());
				}
				if(verifyTemplateNeed(getResThirdTempId())){
					column.setResThirdTempId(getResThirdTempId());
				}
				
				if(verifyTemplateNeed(getDelOneTempId())){
					column.setDelOneTempId(getDelOneTempId());
				}
				if(verifyTemplateNeed(getDelSecondTempId())){
					column.setDelSecondTempId(getDelSecondTempId());
				}
				if(verifyTemplateNeed(getDelThirdTempId())){
					column.setDelThirdTempId(getDelThirdTempId());
				}
				
				getBussinessService().updateColumn(column);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
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
	
	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Columns());
		}
	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getTemplateURL(int templateType, int wapType, String pageid) {
		IEngineService service = getExternalService();

		TemplateType type = new TemplateType();
		type.setId(templateType);
		Integer showType=1;
		if(getPageGroup()!=null)
			showType = getPageGroup().getShowType();
		Object[] params = new Object[] { pageid, type, wapType,showType};
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}
	
	public   IBasicTableModel getTableModel()
	{
		return new IBasicTableModel()
		{
			public int getRowCount()
			{
				return getColumnsList().size();
			}
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder)
			{
				//List<ResourceAll> list  = new ArrayList<ResourceAll>();
				//list.addAll(getResources());
				return  getColumnsList().iterator();
			}
		};
	}
	
	@InjectPage("bussiness/ShowColumnPage")
	public abstract ShowColumnPage getShowColumnPage();
	
	public IPage savePage(IRequestCycle cycle){
		if(save()){
			getShowColumnPage().setPageGroup(getPageGroup());
			return getShowColumnPage();
		}else
			return this;
	}
	
	public IPage cancelPage(IRequestCycle cycle,PageGroup pg) {	
		getShowColumnPage().setPageGroup(pg);
		return getShowColumnPage();
	}
	
}
