package com.hunthawk.reader.page.resource;

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
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.resource.ResourceService;

/**
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class SourceChoicePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	public abstract String getResourceName();

	public abstract void setResourceName(String resourceName);

	public abstract TemplateType getTemplateType();

	public abstract TemplateCatalog getTemplateCatalog();

	public abstract void setTemplateType(TemplateType templateType);

	public abstract void setTemplateCatalog(TemplateCatalog templateCatalog);

	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InitialValue("-1")
	public abstract Integer getSearchId();
 
	public abstract void setSearchId(Integer id);
	
	@InitialValue("-1")
	public abstract Integer getCpid();
	
	public abstract void setCpid(Integer cpid);
	// �޸Ĺ���
	public abstract ResourceAll getTemplate();

	public abstract void setTemplate(ResourceAll resource);


	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getResourceName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getResourceName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getSearchId()!=null && getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId().toString(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		
		if(getCpid()!=null && getCpid()>0){
			HibernateExpression idE = new CompareExpression("cpId",
					getCpid(), CompareType.Equal);
			hibernateExpressions.add(idE);
			
		}
		//HibernateExpression typeE = new CompareExpression("templateType",
		//		getTemplateType(), CompareType.Equal);
		//hibernateExpressions.add(typeE);
		//ѡȡ����״̬�Ĳ�Ʒ
		HibernateExpression status = new CompareExpression("status",
				0, CompareType.Equal);
		hibernateExpressions.add(status);
		
		UserImpl user = (UserImpl)getUser();
		if(user.isRoleProvider()){//SPCPֻ�ܿ����Լ�������
			HibernateExpression cpE = new CompareExpression("cpId",user.getProvider().getId(),CompareType.NotEqual);
			hibernateExpressions.add(cpE);
			
		}
		return hibernateExpressions;
	}

	/**
	 * �����Դ
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseResources() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResourceService().getResourceResultCount(getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;
				
				return getResourceService().findResourceBy(pageNo, nPageSize, "id", false, getSearchExpressions()).iterator();
			}

		};

	}

	/**
	 * ������Դ
	 * 
	 * @param cycle
	 */
	public void searchResource(IRequestCycle cycle) {
		setChoose("false");
		setSearch(true);
		getTableView().reset();

	}

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);

	public abstract String getRadioValueCpid();

	public abstract void setRadioValueCpid(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getTemplate() == null) {
			delegate.setFormComponent(null);
			delegate.record("����ѡ��һ����Դ(����Դ�ѱ�ɾ��)", null);

		} else {
			setRadioValue(String.valueOf((getTemplate().getId())));
			setRadioValueCpid(String.valueOf(getTemplate().getCpId()));
		}
		logger.info("�ύ��ֵ:" + getRadioValue()+":::"+getRadioValueCpid());

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
			//setReturnElementCpid("cpid");
		} else if (parameters.length == 2){
			setShowTemplateType(false);
			setTemplateType((TemplateType) parameters[1]);
			setReturnElement((String) parameters[0]);

		}else if (parameters.length == 4){
			setShowTemplateType(false);
			setTemplateType((TemplateType) parameters[1]);
			setReturnElement((String) parameters[0]);
		}
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();
/*
	public abstract void setReturnElementCpid(String name);

	public abstract String getReturnElementCpid();*/

	// �Ƿ�Ϊ��������
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	/**
	 * �Ƿ���ʾ��Դ����
	 * 
	 * @return
	 */
	public abstract boolean isShowTemplateType();

	public abstract void setShowTemplateType(boolean show);

	/**
	 * ��ý���ѡ�����Դid����ҳ���ϵ��ĸ�Ԫ��
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

}
