package com.hunthawk.reader.page.bussiness;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class TemplateChoicePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	public abstract String getTemplateName();

	public abstract void setTemplateName(String templateName);

	public abstract TemplateType getTemplateType();

	public abstract TemplateCatalog getTemplateCatalog();

	public abstract void setTemplateType(TemplateType templateType);

	public abstract void setTemplateCatalog(TemplateCatalog templateCatalog);

	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InitialValue("-1")
	public abstract int getSearchId();

	public abstract void setSearchId(int id);

	@InitialValue("-1")
	public abstract int getShowType();

	public abstract void setShowType(int id);

	@InitialValue("-1")
	public abstract int getSignType();

	public abstract void setSignType(int id);

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
				"getName", "getId", true, "");
		return parentProper;

	}

	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getTemplateCatalogList() {

		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getTemplateService().getAllTemplateCatalog(),
				TemplateCatalog.class, "getName", "getId", true, "");
		return parentProper;

	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getTemplateName())) {
			HibernateExpression nameE = new CompareExpression("title", "%"
					+ getTemplateName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		if (getShowType() > 0) {
			HibernateExpression showE = new CompareExpression("showType",
					getShowType(), CompareType.Equal);
			hibernateExpressions.add(showE);
		}

		if (getShowType() == 1 && getSignType() > 0) {
			HibernateExpression showE = new CompareExpression("signType",
					getSignType(), CompareType.Equal);
			hibernateExpressions.add(showE);
		}
	/*	if (getSignType() > 0) {
			HibernateExpression showE = new CompareExpression("signType",
				getSignType(), CompareType.Equal);
			hibernateExpressions.add(showE);
		}
*/
		HibernateExpression typeE = new CompareExpression("templateType",
				getTemplateType(), CompareType.Equal);
		hibernateExpressions.add(typeE);

		HibernateExpression typeF = new CompareExpression("templateCatalog",
				getTemplateCatalog(), CompareType.Equal);
		hibernateExpressions.add(typeF);

		HibernateExpression statusE = new CompareExpression("status",
				Constants.STATUS_PUBLISH, CompareType.Equal);
		hibernateExpressions.add(statusE);

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

				count = getTemplateService().getTemplateResultCount(
						getSearchExpressions()).intValue();

				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getTemplateService().findTemplate(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();

			}

		};

	}

	/**
	 * ����ģ��
	 * 
	 * @param cycle
	 */
	public void searchTemplate(IRequestCycle cycle) {
		setChoose("false");
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

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("���ܵĲ����ĸ���" + parameters.length);

		// ��ʾģ������
		if (parameters.length == 1) {
			setShowTemplateType(true);
			setReturnElement((String) parameters[0]);
		} else if (parameters.length == 2) {
			setShowTemplateType(false);
			setTemplateType((TemplateType) parameters[1]);
			setReturnElement((String) parameters[0]);

		} else if (parameters.length == 4) {
			setShowTemplateType(false);
			setTemplateType((TemplateType) parameters[1]);
			setReturnElement((String) parameters[0]);
			setSignType((Integer) parameters[2]);
			setShowType((Integer) parameters[3]);
		}
		/*else if(parameters.length == 3){ //��ҳ������Ĳ��������������ҵģ�Ҫ����
			setReturnElement((String) parameters[0]);//���÷���ֵ
			{ // ���� ����
				Integer templateTypeId = (Integer)parameters[1]+2;
				System.out.println("-------templateTypeId------"+templateTypeId);
				TemplateType type = new TemplateType();
				type.setId(templateTypeId);
				setTemplateType(type);
			}
			{//ģ�����ͱ�ʶ(WAP1.x �� WAP2.x �� 3G)
				Integer signType = (Integer) parameters[2];
				if(signType==0)
					signType = 3; //���� 3G
				System.out.println("-------signType------"+signType);
				setSignType((Integer) parameters[2]);	
			}
			//setShowType(1);	//Ĭ�����ó�1wap
		}*/
	}

	public abstract void setTemplateTypeId(Integer id);
	public abstract Integer getTemplateTypeId();
	
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

	}

	public abstract Object getCurrentObject();

	public abstract void setCurrentObject(Object m);

	public String getPreViewPage() {
		Template template = (Template) getCurrentObject();
		String preUrl = getSystemService().getVariables("preview_url")
				.getValue();
		String portalUrl = getSystemService().getVariables("portal_url")
				.getValue();
		String url = preUrl +  URLEncoder.encode(portalUrl + "td="+template.getId()+"&preview=1");
		
		return "javascript:window.open('" + url
				+ "','','scrollbars=no,width=315,height=450')";
	}

}
