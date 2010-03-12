/**
 * 
 */
package com.hunthawk.reader.page.statistics;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.service.system.StatisticsService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "bi" }, mode = Restrict.Mode.ROLE)
public abstract class EditURLConfigPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:statisticsService")
	public abstract StatisticsService getStatisticsService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return URLConfig.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			if (isModelNew()) {
				getStatisticsService().addURLConfig((URLConfig) object);
			} else {
				getStatisticsService().updateURLConfig((URLConfig) object);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new URLConfig());
		}

	}

	// public IPropertySelectionModel getPrivileges() {
	// List<Privilege> privileges = getUserService().findBy(Privilege.class,
	// 1, Integer.MAX_VALUE, "id", false,
	// new ArrayList<HibernateExpression>());
	// ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
	// privileges, Privilege.class, "getTitle", "getId", false, null);
	// return model;
	// }
	//
	// @InjectComponent("roleList")
	// public abstract Block getRoleList();
	//
	// @InjectComponent("roleExist")
	// public abstract Block getRoleExist();
	//
	// public void setSelectedPrivileges(List privileges) {
	// ((Role) getModel()).setPrivileges(new HashSet<Privilege>(privileges));
	// }
	//
	// public List getSelectedPrivileges() {
	// return new ArrayList(((Role) getModel()).getPrivileges());
	//	}

}
