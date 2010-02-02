package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.ExStringPropertySelectionModel;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.BussinessService;

public abstract class PackGroupProvinceRelationPage extends SecurityPage
		implements IExternalPage, PageBeginRenderListener {
	private static Logger logger = Logger.getLogger(TemplateChoicePage.class);

	public abstract PackGroupProvinceRelation getPackGroupProvinceRelation();

	public abstract void setPackGroupProvinceRelation(
			PackGroupProvinceRelation relation);

	// @InjectComponent("table")
	// public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	public abstract PageGroup getPageGroup();

	public abstract void setPageGroup(PageGroup pageGroup);

	// /**
	// * 获得模板类型下拉列表的数据
	// *
	// * @return
	// */
	// @SuppressWarnings("unchecked")
	// public IPropertySelectionModel getTemplateTypeList()
	// {
	//
	// ObjectPropertySelectionModel parentProper = new
	// ObjectPropertySelectionModel(
	// getTemplateService().getAllTemplateType(), TemplateType.class, "getName",
	// "getId", false, "");
	// return parentProper;
	//
	// }

	// public Collection<HibernateExpression> getSearchExpressions(){
	// Collection<HibernateExpression> hibernateExpressions = new
	// ArrayList<HibernateExpression>();
	// if(isSearch()){
	// String name = (String)getConditionMap().get("title");
	// if(!ParameterCheck.isNullOrEmpty(name)){
	// //HibernateExpression nameE = new
	// CompareExpression("name","%"+name+"%",CompareType.Like);
	// HibernateExpression nameE = new
	// CompareExpression("title","%"+name+"%",CompareType.Like);
	// hibernateExpressions.add(nameE);
	// }
	// Integer id = (Integer)getConditionMap().get("id");
	// if(id != null && id > 0){
	// HibernateExpression idE = new
	// CompareExpression("id",id,CompareType.Equal);
	// hibernateExpressions.add(idE);
	// }
	// TemplateType type = (TemplateType)getConditionMap().get("templateType");
	// HibernateExpression typeE = new
	// CompareExpression("templateType",getTemplateType(),CompareType.Equal);
	// hibernateExpressions.add(typeE);
	// Integer status = (Integer)getConditionMap().get("status");
	// if(status != null && status > 0){
	// HibernateExpression statusE = new
	// CompareExpression("id",id,CompareType.Equal);
	// hibernateExpressions.add(statusE);
	// }
	//			
	//			
	// }else{
	// HibernateExpression typeE = new
	// CompareExpression("templateType",getTemplateType(),CompareType.Equal);
	// hibernateExpressions.add(typeE);
	// }
	//		
	//		
	//		
	// return hibernateExpressions;
	// }

	public abstract String getHiddenMapValue();

	public abstract void setHiddenMapValue(String mapValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getHiddenMapValue();

		ValidationDelegate delegate = getDelegate();

		// if (getTemplate() == null)//得到页面map的字符串的值
		// {
		// delegate.setFormComponent(null);
		// delegate.record("必须选择一个模板(或模板已被删除)", null);
		//
		// }
		// else
		// {
		// setHiddenMapValue(String.valueOf((getTemplate().getId())));
		// }

		logger.info("提交的值:" + getHiddenMapValue());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	// 可设置多个参数根据情况来定
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("packGroupProvinceRelationPage 类接受的参数的个数"
				+ parameters.length);

		// 显示模板类型
		if (parameters.length == 1) {
			// setShowTemplateType(true);
			setReturnElement((String) parameters[0]);
		} else {
			// setShowTemplateType(false);
			// setTemplateTypeFlag((Integer) parameters[0]);
			// setTemplateType((TemplateType) parameters[1]);
			setReturnElement((String) parameters[0]);
			// System.out.println("得到的两个参数 分别为--->");
			// for(Object obj:parameters){
			// System.out.println("params ="+obj);
			// }
			if (parameters.length != 2) {
				setMap((String) parameters[1]);
			}

		}
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	public abstract void setMap(String map);

	public abstract String getMap();

	/**
	 * 获得资源的状态列表
	 * 
	 * @return
	 */
	public IPropertySelectionModel getTemplateStatus() {

		ExStringPropertySelectionModel pamsPro = new ExStringPropertySelectionModel(
				Constants.STATUS, true, "");
		return pamsPro;

	}

	/**
	 * 获得状态的表头信息
	 * 
	 * @return
	 */
	public ITableColumn getDisplayStatus() {
		return new SimpleTableColumn("status", "状态",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Template p1 = (Template) objRow;
						return Constants.STATUS[p1.getStatus()];

					}

				}, true);

	}

	public IPropertySelectionModel getBussinessTypeList() {
		return new MapPropertySelectModel(Constants.getBussinessTypes());
	}

	// 页面组下拉列表
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	public IPropertySelectionModel getPackGroupList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getBussinessService().findPageGroups(1, Integer.MAX_VALUE,
						"id", false, new ArrayList<HibernateExpression>()),
				PageGroup.class, "getPkName", "getId", false, "");
		return parentProper;
	}

	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {
		// if (getConditionMap() == null)
		// {
		// setConditionMap(new HashMap());
		// }

	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getPackGroupSetURLPams(String areaid, String bussinessType) {
		IEngineService service = getExternalService();

		// 得到产品类型
		System.out.println("bussinessType --->>" + bussinessType);
		Object[] params = new Object[] { "pid" + areaid, bussinessType };
		String packGroupSetURL = PageHelper.getExternalFunction(service,
				"bussiness/PackGroupSetPage", params);
		return packGroupSetURL;
	}
}
