package com.hunthawk.reader.page.resource;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;

@Restrict(roles = { "packreleationchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditEpackReleationPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();

	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getShowSourcePage();

	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	@InjectObject("spring:partnerServiceTarget")
	public abstract PartnerService getPartnerService();
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return ResourcePackReleation.class;
	}

	public abstract ResourcePack getPack();

	public abstract void setPack(ResourcePack pack);

	public abstract void setResourceIds(String resourceIds);

	public abstract String getResourceIds();
	
	public abstract String getFeeName();
	public abstract void setFeeName(String feeName);
	
	private void saveReleation(ResourcePackReleation releation)
			throws Exception {

		if (releation.getChoice() == null) {
			releation.setChoice(0);
		}
		if (releation.getFeeId() != null && releation.getCpid() != null) {
			Fee fee = getFeeService().getFee(releation.getFeeId());
			String feeid = fee.getProvider().getId().toString();
			if (!feeid.equals(releation.getCpid()))
				throw new Exception("所选资源【" + releation.getResourceId()
						+ "】CPID代码与当前所选择的计费关联的CPID不一致！请重新选择！");
		}
		if (releation.getId() != null && releation.getId() > 0) {
			getResourcePackService().updateResourcePackReleation(releation);
		} else {
			getResourcePackService().addResourcePackReleation(releation);
		}

	}

	@Override
	protected boolean persist(Object object) {
		try {
			ResourcePackReleation packReleation = (ResourcePackReleation) object;
			packReleation.setPack(getPack());
			String[] resourceIds = this.getResourceIds().split(",");
			int i = 0;
			String errorInfo = "";
			for (String resourceId : resourceIds) {
				ResourcePackReleation rel = new ResourcePackReleation();
				rel.setResourceId(resourceId);
				if (i == 0) {
					rel.setId(packReleation.getId());
				}
				rel.setChoice(packReleation.getChoice());
				rel.setCpid(packReleation.getCpid());
				rel.setFeeId(packReleation.getFeeId());
				rel.setOrder(packReleation.getOrder() + i);
				i++;
				rel.setPack(packReleation.getPack());
				rel.setStatus(packReleation.getStatus());
				try {
					saveReleation(rel);
				} catch (Exception e) {
					errorInfo += e.getMessage();
					e.printStackTrace();
				}
			}

			if (StringUtils.isNotEmpty(errorInfo))
				throw new Exception(errorInfo);
			// 把pack传递会起始页面
			ShowEpackReleationPage page = getShowSourcePage();
			page.setPack(getPack());
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new ResourcePackReleation());
		} else {
			ResourcePackReleation packReleation = (ResourcePackReleation) getModel();
			setResourceIds(packReleation.getResourceId());
			//初始化页面的计费名称
			ResourcePackReleation pack = (ResourcePackReleation)getModel();
			if(StringUtils.isNotEmpty(pack.getFeeId())){
				Fee fee = getFeeService().getFee(pack.getFeeId());
				if(fee!=null)
					setFeeName(fee.getName());
			}
			
		}

	}

	public abstract void setMap(String map);

	public abstract String getMap();

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getSourceURL() {
		IEngineService service = getExternalService();

		Object[] params = new Object[] { "pageid" };
		String templateURL = PageHelper.getExternalFunction(service,
				"resource/BatchSourceChoicePage", params);

		return templateURL;
	}

	public String getFeeURL() {

		IEngineService service = getExternalService();
		ResourcePackReleation packReleation  = (ResourcePackReleation)getModel();
		Provider provider = getPartnerService().getProvider(Integer.parseInt(packReleation.getCpid()));
		Object[] params = new Object[] { "feeId", "packReleation",provider};
		String templateURL = PageHelper.getExternalFunction(service,
				"partner/FeeChoicePage", params);
		return templateURL;

	}

	public boolean isCheckPack() {
		ResourcePack pack = getPack();
		if (pack.getType() == 1 || pack.getType() == 2)
			return true;
		else
			return false;
	}

	public boolean isCheckPackFee() {
		ResourcePack pack = getPack();
		if (pack.getType() == 1 || pack.getType() == 2)
			return true;
		else
			return false;
	}
}
