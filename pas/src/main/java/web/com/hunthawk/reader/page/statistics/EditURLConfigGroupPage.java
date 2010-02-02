/**
 * 
 */
package com.hunthawk.reader.page.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.statistics.URLConfig;
import com.hunthawk.reader.domain.statistics.URLConfigGroup;
import com.hunthawk.reader.service.system.StatisticsService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "bi" }, mode = Restrict.Mode.ROLE)
public abstract class EditURLConfigGroupPage extends EditPage implements
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
		return URLConfigGroup.class;
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
				getStatisticsService().addURLConfigGroup(
						(URLConfigGroup) object);
			} else {
				getStatisticsService().updateURLConfigGroup(
						(URLConfigGroup) object);
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
			setModel(new URLConfigGroup());
		}

	}

	public IPropertySelectionModel getConfigs() {
		List<URLConfig> privileges = getStatisticsService().findURLConfigBy(1,
				Integer.MAX_VALUE, "id", false,
				new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				privileges, URLConfig.class, "getTitle", "getId", false, null);
		return model;
	}

	@InjectComponent("roleList")
	public abstract Block getRoleList();

	@InjectComponent("roleExist")
	public abstract Block getRoleExist();

	public void setSelectedConfigs(List configs) {
		((URLConfigGroup) getModel())
				.setConfigs(new HashSet<URLConfig>(configs));
	}

	public List getSelectedConfigs() {
		return new ArrayList(((URLConfigGroup) getModel()).getConfigs());
	}

}