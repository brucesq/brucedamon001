/**
 * 
 */
package com.hunthawk.reader.page.partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "feechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditFeePage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	public abstract Provider getProvider();
	public abstract void setProvider(Provider provider);

	public abstract Integer getPartnerId();
	public abstract void setPartnerId(Integer partnerId);
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return Fee.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		Fee o = (Fee) object;
		try {
			Provider provider = new Provider();
			if(getProvider()!=null){
				provider = getProvider();
			}
			if(getPartnerId()!=null && getProvider()==null){
				provider = getPartnerService().getProvider(getPartnerId());
			}
			if(provider.getStatus()!=null){
				if(provider.getStatus() != 3){
					throw new Exception("SPCP状态必须是商用状态时才允许添加计费代码");
				}
			}
			if(o.getProductId()!=null && !"".equals(o.getProductId())){  //表明添加了产品代码
				if(!StringUtils.isNumeric(o.getProductId()) || o.getProductId().length()!=12){
					throw new Exception("产品代码只能是数字并且只能是12位!");
				}
			}
			if(o.getServiceId()!=null && !"".equals(o.getServiceId())){  //表明添加了服务代码
				if(!StringUtils.isNumeric(o.getServiceId()) || o.getServiceId().length()!=10){
					throw new Exception("服务代码只能是数字并且只能是10位!");
				}
			}
			if (isModelNew()) {
				if (!verifyTemplate(o.getTemplateId()))
					throw new Exception("模板不存在");
				o.setCreateTime(new Date());
				o.setModifyTime(new Date());
				o.setCreateorId(getUser().getId());
				o.setMotifierId(getUser().getId());
				o.setProvider(provider);
				getFeeService().addFee(o);
			} else {
				if (!verifyTemplate(o.getTemplateId()))
					throw new Exception("模板不存在");
				o.setModifyTime(new Date());
				o.setMotifierId(getUser().getId());
				getFeeService().updateFee(o);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			if (isModelNew()) {
				o.setId(null);
			}
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

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			Fee fee = new Fee();
			fee.setDiscount(0);
			setModel(fee);
		}
		if(getProvider() !=null) //空值过来的
			setShowSPCP(false);
		if(getModel() != null){
			Fee  fee = (Fee)getModel();
			if(fee.getProvider()!=null)
				setPartnerId(fee.getProviderId());
		}
	}
	@InitialValue("true")
	public abstract boolean isShowSPCP();
	public abstract void setShowSPCP(boolean isShow);
	
	public IPropertySelectionModel getFeeTypeList() {
		return new MapPropertySelectModel(Fee.getFeeTypes());
	}

	public IPropertySelectionModel getIsOutList() {
		return new MapPropertySelectModel(Fee.getYesNo());
	}

	public IPropertySelectionModel getDiscountList() {
		return new MapPropertySelectModel(Fee.getDiscountTypes());
	}

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();
	
	public IPropertySelectionModel getCpList() {
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		List<Provider> list = getPartnerService().findProvider(1, Integer.MAX_VALUE, "id",
				true, expressions);
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Provider provider : list) {
			map.put(provider.getIntro(), provider.getId());
		}

		MapPropertySelectModel mapPro = new MapPropertySelectModel(map);
		return mapPro;
	}

	
	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getTemplateURL() {
		IEngineService service = getExternalService();
		TemplateType type= new TemplateType();
		type.setId(5);
		Object[] params = new Object[] { "pageid",type};
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}
	public String getTemplateURL1() {
		IEngineService service = getExternalService();
		TemplateType type= new TemplateType();
		type.setId(5);
		Object[] params = new Object[] { "pageid1",type};
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}
	public String getTemplateURL2() {
		IEngineService service = getExternalService();
		TemplateType type= new TemplateType();
		type.setId(5);
		Object[] params = new Object[] { "pageid2",type};
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}
}
