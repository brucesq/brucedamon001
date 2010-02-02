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

	// ��������������Map
	public abstract Map getConditionMap();

	public abstract void setConditionMap(Map conditionMap);

	public abstract Template getTemplate();

	public abstract void setTemplate(Template template);

	/**
	 * ���ģ�����������б������
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
	 * ���ģ��汾��������
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
	 * ���ģ��
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseTemplates() {
		return new IBasicTableModel() {

			// ģ�������
			int count = 0;

			public int getRowCount() {
				if (count > 0) {

					return count;
				}
				// �������������
				if (isSearch()) {
					buildSearchMap();
					count = getTemplateService().getTemplateResultCount(
							getSearchExpressions()).intValue();
				} else {

					// ����ģ�������
					getConditionMap().put("templateType", getTemplateType());
					// ����ģ��İ汾���� Ĭ��Ϊ(wap1.x)
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

				// ����������
				if (isSearch()) {
					buildSearchMap();
					return getTemplateService().findTemplate(pageNo, nPageSize,
							"id", false, getSearchExpressions()).iterator();

				} else// ������������Ĭ�ϱ�� ��ҳ/��Դ/��Ŀ ҳ�� wap1.x��ģ����Ϣ
				{
					// ����ģ�������
					getConditionMap().put("templateType", getTemplateType());
					// ����ģ��İ汾���� Ĭ��Ϊ(wap1.x)
					getConditionMap().put("versionType", getVersionType());
					return getTemplateService().findTemplate(pageNo, nPageSize,
							"id", false, getSearchExpressions()).iterator();

				}

			}

		};

	}

	public void buildSearchMap() {
		// ����ģ���Id
		getConditionMap().put("id", getSearchId());
		// ����ģ�������
		getConditionMap().put("title", getTemplateName());

		// ����ģ�������
		getConditionMap().put("templateType", getTemplateType());

		// getConditionMap().put("status",parameters[1]);

		getConditionMap().put("status", getSearchStatus());
		getConditionMap().put("versionType", getVersionType());

	}

	/**
	 * ����ģ��
	 * 
	 * @param cycle
	 */
	public void searchTemplate(IRequestCycle cycle) {
		setChoose("false");
		logger.info("ģ�����ƣ�" + getTemplateName());

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
			delegate.record("����ѡ��һ��ģ��(��ģ���ѱ�ɾ��)", null);

		} else {
			setRadioValue(String.valueOf((getTemplate().getId())));
		}

		logger.info("�ύ��ֵ:" + getRadioValue());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	// �����ö�����������������
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("���ܵĲ����ĸ���" + parameters.length);

		// ��ʾģ��汾����
		if (parameters.length == 1) {
			// setShowTemplateType(true);
			setShowVersionType(true);
			setReturnElement((String) parameters[0]);
		} else {
			setShowTemplateType(false);// ����ʾģ������������
			setShowVersionType(false);// ����ʾģ��汾����������
			setTemplateTypeFlag(Integer.parseInt(parameters[0].toString()));
			setTemplateType((TemplateType) parameters[1]);
			setVersionType((Integer) parameters[2]);
			setReturnElement((String) parameters[3]);

		}
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	/**
	 * �����Դ��״̬�б�
	 * 
	 * @return
	 */
	public IPropertySelectionModel getTemplateStatus() {

		ExStringPropertySelectionModel pamsPro = new ExStringPropertySelectionModel(
				Constants.STATUS, true, "");
		return pamsPro;

	}

	/**
	 * ����ʱ��״̬����
	 * 
	 * @return
	 */
	@InitialValue("-1")
	public abstract int getSearchStatus();

	public abstract void setSearchStatus(int status);

	/**
	 * ���״̬�ı�ͷ��Ϣ
	 * 
	 * @return
	 */
	public ITableColumn getDisplayStatus() {
		return new SimpleTableColumn("status", "״̬",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Template p1 = (Template) objRow;
						return Constants.STATUS[p1.getStatus()];

					}

				}, true);

	}

	// �Ƿ�Ϊ��������
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	/**
	 * �Ƿ���ʾģ������
	 * 
	 * @return
	 */
	public abstract boolean isShowTemplateType();

	public abstract void setShowTemplateType(boolean show);

	/**
	 * �Ƿ���ʾ�汾����
	 */
	public abstract boolean isShowVersionType();

	public abstract void setShowVersionType(boolean show);

	/**
	 * ��ý���ѡ���ģ��id����ҳ���ϵ��ĸ�Ԫ��
	 * 
	 * @return
	 */
	public abstract int getTemplateTypeFlag();

	public abstract void setTemplateTypeFlag(int flag);

	/**
	 * ���ó�ʼ����
	 */
	public void pageBeginRender(PageEvent arg0) {
		if (getConditionMap() == null) {
			setConditionMap(new HashMap());
		}

	}

}
