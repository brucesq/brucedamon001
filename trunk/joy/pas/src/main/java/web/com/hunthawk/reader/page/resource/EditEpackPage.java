package com.hunthawk.reader.page.resource;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.security.SecurityComponent;
import com.hunthawk.framework.security.Visit;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.callback.CallbackStack;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.resource.ResourcePackService;

@Restrict(roles = { "packchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditEpackPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return ResourcePack.class;
	}

	@Override
	protected boolean persist(Object object) {
		try {
			ResourcePack pack = (ResourcePack) object;
			UserImpl user = (UserImpl) getUser();
			if (isModelNew()) {
				pack.setCreateTime(new Date());
				pack.setCreator(user.getId());// 创建者
				if (pack.getType() == 3) {
					if ("".equals(pack.getChoice()) || pack.getChoice() == null)
						throw new Exception("请选择几个!!");
				}
				getResourcePackService().addPack(pack);

			} else {
				pack.setCreateTime(new Date());
				pack.setCreator(user.getId());// 创建者
				if (pack.getType() == 3) {
					if ("".equals(pack.getChoice()) || pack.getChoice() == null)
						throw new Exception("请选择几个!!");
				}
				getResourcePackService().updatePack(pack);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public abstract String getFeeName();
	public abstract void setFeeName(String feeName);
	
	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new ResourcePack());
		}else{
			//初始化页面的计费名称
			ResourcePack pack = (ResourcePack)getModel();
			if(pack.getFeeId()!=null && !"".equals(pack.getFeeId())){
			Fee fee = getFeeService().getFee(pack.getFeeId());
			if(fee!=null)
				setFeeName(fee.getName());
			}
		}

	}

	public IPropertySelectionModel getFeeTypeList() {
		return new MapPropertySelectModel(Constants.getFeeTypeMap());
	}

	public abstract void setMap(String map);

	public abstract String getMap();

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getFeeURL() {

		IEngineService service = getExternalService();

		Object[] params = new Object[] { "feeId", "pack" };
		String templateURL = PageHelper.getExternalFunction(service,
				"partner/FeeChoicePage", params);

		return templateURL;

	}
}
