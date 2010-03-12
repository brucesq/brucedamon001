package com.hunthawk.reader.page.resource;

import java.util.Date;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.service.resource.ResourceService;

/**
 * @author xianlaichen
 * 
 */
@Restrict(roles = { "resourcetypechange" }, mode = Restrict.Mode.ROLE)
public abstract class EditResourceTypePage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		// TODO Auto-generated method stub
		return ResourceType.class;
	}

	public abstract ResourceType getParent();

	public abstract void setParent(ResourceType parent);

	public abstract Integer getShowType();

	public abstract void setShowType(Integer showType);
	
	public abstract void setShowTypeName(String name);
	
	public abstract String getShowTypeName();
	@Override
	protected boolean persist(Object object) {
		// TODO Auto-generated method stub
		try {
			ResourceType resourcetype = (ResourceType) object;
			if (isModelNew()) {
				resourcetype.setParent(getParent());
				if (getParent() != null) {
					resourcetype.setShowType(getParent().getShowType());
				}
				resourcetype.setCreateTime(new Date());
				resourcetype.setCreatorId(getUser().getId());

				getResourceService().addResourceType((ResourceType) object);
			} else {
				getResourceService().updateResourceType(resourcetype);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public IPropertySelectionModel getResourceTypeList() {
		return new MapPropertySelectModel(Constants.getResourceType());
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new ResourceType());
		}

	}

}
