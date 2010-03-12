package com.hunthawk.reader.page.partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * 
 * @author yuzs
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class FeeChoicePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	public abstract String getSearchId();

	public abstract void setSearchId(String id);

	public abstract void setProvider(Provider provider);

	public abstract Provider getProvider();

	// 修改过的
	public abstract Fee getTemplate();

	public abstract void setTemplate(Fee fee);

	public abstract String getFeeName();

	public abstract void setFeeName(String feeName);

	public abstract Integer getCpId();

	public abstract void setCpId(Integer cpid);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getFeeName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getFeeName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (!ParameterCheck.isNullOrEmpty(getSearchId())) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId(), CompareType.Like);
			hibernateExpressions.add(idE);
		}

		if (getProvider() != null) {
			HibernateExpression idE = new CompareExpression("provider.id",
					getProvider(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		if (getCpId() != null && getCpId() > 0) {
			HibernateExpression idE = new CompareExpression("provider.id",
					getCpId(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}

		HibernateExpression status = new CompareExpression("status", 1,
				CompareType.Equal);
		hibernateExpressions.add(status);
		//System.out.println("getFeeFrom()--->"+getFeeFrom());
		if (getFeeFrom() != null) {
			if (getFeeFrom().equals("pack")) {
				HibernateExpression type = new CompareExpression("type", 2,
						CompareType.Equal);
				hibernateExpressions.add(type);
			} else if (getFeeFrom().equals("packReleation")) {
				HibernateExpression type = new CompareExpression("type", 3,
						CompareType.Equal);
				hibernateExpressions.add(type);
			}
		}
		return hibernateExpressions;
	}

	/**
	 * 获得计费
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseResources() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return new Long(getFeeService().findFeeResultCount(
						getSearchExpressions())).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getFeeService().findFee(pageNo, nPageSize, "id", false,
						getSearchExpressions()).iterator();
			}

		};

	}

	/**
	 * 搜索计费
	 * 
	 * @param cycle
	 */
	public void searchFee(IRequestCycle cycle) {
		setChoose("false");
		setSearch(true);
		getTableView().reset();

	}

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);

	public abstract String getRadioValueCpid();

	public abstract void setRadioValueCpid(String radioValue);

	public abstract String getRadioValueFeeCode();

	public abstract void setRadioValueFeeCode(String radioValueFeeCode);

	public abstract String getRadioValueFeeName();

	public abstract void setRadioValueFeeName(String radioValueFeeName);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getTemplate() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个资源(或资源已被删除)", null);

		} else {
			setRadioValue(String.valueOf((getTemplate().getId())));
			setRadioValueCpid(String.valueOf(getTemplate().getProvider()
					.getId()));
			setRadioValueFeeCode(getTemplate().getCode());
			setRadioValueFeeName(getTemplate().getName());
		}
		logger.info("提交的值:" + getRadioValue() + ":::" + getRadioValueCpid());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板类型
		if (parameters.length == 1) {
			setShowTemplateType(true);
			setReturnElement((String) parameters[0]);
		} else if (parameters.length == 2) {
			setShowTemplateType(false);
			setReturnElement((String) parameters[0]);
			setFeeFrom((String) parameters[1]);
		} else if (parameters.length == 3) {
			setShowTemplateType(false);
			setReturnElement((String) parameters[0]);
			setFeeFrom((String) parameters[1]);
			Provider provider = (Provider) parameters[2];
			setProvider(provider);
		} else if (parameters.length == 4) {
			setShowTemplateType(false);
			setReturnElement((String) parameters[0]);
		}
	}

	public abstract void setFeeFrom(String from);

	public abstract String getFeeFrom();

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	/*
	 * public abstract void setReturnElementCpid(String name);
	 * 
	 * public abstract String getReturnElementCpid();
	 */

	// 是否为搜索请求
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	public abstract boolean isShowTemplateType();

	public abstract void setShowTemplateType(boolean show);

	public abstract int getTemplateTypeFlag();

	public abstract void setTemplateTypeFlag(int flag);

	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {

	}

	public IPropertySelectionModel getCpList() {

		Map<String, Integer> TYPE = new OrderedMap<String, Integer>();
		List<Provider> list = getPartnerService().findProvider(1,
				Integer.MAX_VALUE, "id", false,
				new ArrayList<HibernateExpression>());
		TYPE.put("全部", -1);
		for (Provider provider : list) {
			TYPE.put(provider.getId().toString(), provider.getId());
		}

		return new MapPropertySelectModel(TYPE, false, "全部");
	}

}
