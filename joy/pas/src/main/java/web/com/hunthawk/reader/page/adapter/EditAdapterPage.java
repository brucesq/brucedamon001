package com.hunthawk.reader.page.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.adapter.Adapter;
import com.hunthawk.reader.domain.adapter.AdapterType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.adapter.AdapterService;

/**
 * 新增或修改适配器
 * 
 * @author penglei
 * 
 */
public abstract class EditAdapterPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:adapterService")
	public abstract AdapterService getAdapterService();

	public abstract Integer getAdapterTypeId();

	public abstract void setAdapterTypeId(Integer adapterId);

	@InjectPage("adapter/ShowAdapterPage")
	public abstract ShowAdapterPage getShowAdapterPage();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Adapter.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			Adapter adapter = (Adapter) object;
			UserImpl user = (UserImpl)getUser();
			adapter.setAdapterTypeId(getAdapterTypeId());
			if(adapter.getId()!=null){
				getAdapterService().updateAdapter(adapter,user);
			}else{
				getAdapterService().saveAdapter(adapter,user);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		getShowAdapterPage().setAdapterTypeId(getAdapterTypeId());
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Adapter());
			return;
		}
		Adapter adapter = (Adapter)getModel();
		setAdapterTypeId(adapter.getAdapterTypeId());

	}

	public IPropertySelectionModel getAdapterTypeList() {
		List<AdapterType> list = getAdapterService().findAdapterTypeList(1,
				Integer.MAX_VALUE, "id", true, new ArrayList<HibernateExpression>());
		Map<String, Object> map = new HashMap<String, Object>();
		for (AdapterType adapterType : list) {
			map.put(adapterType.getName(), adapterType.getId());
		}
		return new MapPropertySelectModel(map);
	}
}
