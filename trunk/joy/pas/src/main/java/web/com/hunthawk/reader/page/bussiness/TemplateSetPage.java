package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.ExStringPropertySelectionModel;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.service.bussiness.TemplateService;

@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class TemplateSetPage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {
	private static Logger logger = Logger.getLogger(TemplateChoicePage.class);

	public abstract String getTemplateName();

	public abstract void setTemplateName(String templateName);

	public abstract TemplateType getTemplateType();

	public abstract void setTemplateType(TemplateType templateType);

	public abstract Integer getVersionType();

	public abstract void setVersionType(Integer versionType);

	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InitialValue("-1")
	public abstract int getSearchId();

	public abstract void setSearchId(int id);

	// 保存搜索条件的Map
	public abstract Map getConditionMap();

	public abstract void setConditionMap(Map conditionMap);

	public abstract Template getTemplate();

	public abstract void setTemplate(Template template);

	/**
	 * 获得模板类型下拉列表的数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getTemplateTypeList() {

		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getTemplateService().getAllTemplateType(), TemplateType.class,
				"getName", "getId", false, "");
		return parentProper;

	}

	/**
	 * 获得模板版本类型数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getVersionTypeList() {
		return new MapPropertySelectModel(Constants.getVersionTypes());
	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (isSearch()) {
			String name = (String) getConditionMap().get("title");
			if (!ParameterCheck.isNullOrEmpty(name)) {
				HibernateExpression nameE = new CompareExpression("name", "%"
						+ name + "%", CompareType.Like);
				hibernateExpressions.add(nameE);
			}
			Integer id = (Integer) getConditionMap().get("id");
			if (id != null && id > 0) {
				HibernateExpression idE = new CompareExpression("id", id,
						CompareType.Equal);
				hibernateExpressions.add(idE);
			}
			TemplateType type = (TemplateType) getConditionMap().get(
					"templateType");
			HibernateExpression typeE = new CompareExpression("templateType",
					getTemplateType(), CompareType.Equal);
			hibernateExpressions.add(typeE);
			Integer status = (Integer) getConditionMap().get("status");
			if (status != null && status > 0) {
				HibernateExpression statusE = new CompareExpression("id", id,
						CompareType.Equal);
				hibernateExpressions.add(statusE);
			}
			Integer versionType = (Integer) getConditionMap()
					.get("versionType");
			HibernateExpression versionE = new CompareExpression("signType",
					getVersionType(), CompareType.Equal);
			hibernateExpressions.add(versionE);

		} else {
			HibernateExpression typeE = new CompareExpression("templateType",
					getTemplateType(), CompareType.Equal);
			hibernateExpressions.add(typeE);
			Integer versionType = (Integer) getConditionMap()
					.get("versionType");
			HibernateExpression versionE = new CompareExpression("signType",
					getVersionType(), CompareType.Equal);
			hibernateExpressions.add(versionE);
		}

		return hibernateExpressions;
	}

	/**
	 * 获得模板
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseTemplates() {
		return new IBasicTableModel() {

			// 模板的总数
			int count = 0;

			public int getRowCount() {
				if (count > 0) {

					return count;
				}
				// 如果是搜索请求
				if (isSearch()) {
					buildSearchMap();
					count = getTemplateService().getTemplateResultCount(
							getSearchExpressions()).intValue();
				} else {

					// 设置模板的类型
					getConditionMap().put("templateType", getTemplateType());
					// 设置模板的版本类型 默认为(wap1.x)
					getConditionMap().put("versionType", getVersionType());
					count = getTemplateService().getTemplateResultCount(
							getSearchExpressions()).intValue();
				}
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;

				// 是搜索请求
				if (isSearch()) {
					buildSearchMap();
					return getTemplateService().findTemplate(pageNo, nPageSize,
							"id", false, getSearchExpressions()).iterator();

				} else// 不是搜索请求默认表出 首页/资源/栏目 页的 wap1.x的模板信息
				{
					// 设置模板的类型
					getConditionMap().put("templateType", getTemplateType());
					// 设置模板的版本类型 默认为(wap1.x)
					getConditionMap().put("versionType", getVersionType());
					return getTemplateService().findTemplate(pageNo, nPageSize,
							"id", false, getSearchExpressions()).iterator();

				}

			}

		};

	}

	public void buildSearchMap() {
		// 设置模板的Id
		getConditionMap().put("id", getSearchId());
		// 设置模板的名称
		getConditionMap().put("title", getTemplateName());

		// 设置模板的类型
		getConditionMap().put("templateType", getTemplateType());

		// getConditionMap().put("status",parameters[1]);

		getConditionMap().put("status", getSearchStatus());
		getConditionMap().put("versionType", getVersionType());

	}

	/**
	 * 搜索模板
	 * 
	 * @param cycle
	 */
	public void searchTemplate(IRequestCycle cycle) {
		setChoose("false");
		logger.info("模板名称：" + getTemplateName());

		setSearch(true);
		getTableView().reset();

	}

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getTemplate() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个模板(或模板已被删除)", null);

		} else {
			setRadioValue(String.valueOf((getTemplate().getId())));
		}

		logger.info("提交的值:" + getRadioValue());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	// 可设置多个参数根据情况来定
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板版本类型
		if (parameters.length == 1) {
			// setShowTemplateType(true);
			setShowVersionType(true);
			setReturnElement((String) parameters[0]);
		} else {
			setShowTemplateType(false);// 不显示模板类型下拉框
			setShowVersionType(false);// 不显示模板版本类型下拉框
			setTemplateTypeFlag(Integer.parseInt(parameters[0].toString()));
			setTemplateType((TemplateType) parameters[1]);
			setVersionType((Integer) parameters[2]);
			setReturnElement((String) parameters[3]);

		}
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

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
	 * 搜索时的状态条件
	 * 
	 * @return
	 */
	@InitialValue("-1")
	public abstract int getSearchStatus();

	public abstract void setSearchStatus(int status);

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

	// 是否为搜索请求
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	/**
	 * 是否显示模板类型
	 * 
	 * @return
	 */
	public abstract boolean isShowTemplateType();

	public abstract void setShowTemplateType(boolean show);

	/**
	 * 是否显示版本类型
	 */
	public abstract boolean isShowVersionType();

	public abstract void setShowVersionType(boolean show);

	/**
	 * 获得将所选择的模板id赋于页面上的哪个元素
	 * 
	 * @return
	 */
	public abstract int getTemplateTypeFlag();

	public abstract void setTemplateTypeFlag(int flag);

	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {
		if (getConditionMap() == null) {
			setConditionMap(new HashMap());
		}

	}

}
