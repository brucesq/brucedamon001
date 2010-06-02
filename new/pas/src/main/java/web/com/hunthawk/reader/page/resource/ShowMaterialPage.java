/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "material" }, mode = Restrict.Mode.ROLE)
public abstract class ShowMaterialPage extends SearchPage {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditMaterialPage")
	public abstract EditMaterialPage getEditPage();

	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();

	public abstract String getName();

	public abstract void setName(String name);
	
	public abstract String getDesc();

	public abstract void setDesc(String desc);

	public abstract Integer getCatalogId();

	public abstract void setCatalogId(Integer catalogId);

	public IPage onEdit(Object obj) {
		Material matr = (Material) obj;
		getEditPage().setModel(obj);
		getEditPage().setCatalogId(matr.getCatalogId());
		return getEditPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		Object obj = getCurrentObject();
		Set selectedObjs = getSelectedObjects();
		// 选择了用户
		if (bSelected) {
			selectedObjs.add(obj);
		} else {
			selectedObjs.remove(obj);
		}
		// persist value
		setSelectedObjects(selectedObjs);

	}

	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedObjects();

	public abstract void setSelectedObjects(Set set);

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract Object getCurrentObject();

	public void search() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.SearchPage#delete(java.lang.Object)
	 */
	@Override
	protected void delete(Object object) {
		try {
			Material matr = (Material) object;
			Variables var = getSystemService().getVariables("media_dir");
			String filename = var.getValue() + matr.getFilename() + "."
					+ matr.getExtName();
			File file = new File(filename);
			file.deleteOnExit();
			getMaterialService().deleteMaterial(matr);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			e.printStackTrace();
		}

	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedObjects()) {
			delete(obj);
		}
		setSelectedObjects(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		SearchCondition catalogC = new SearchCondition();
		catalogC.setName("catalogId");
		catalogC.setValue(getCatalogId());
		searchConditions.add(catalogC);
		
		SearchCondition descC = new SearchCondition();
		descC.setName("desc");
		descC.setValue(getDesc());
		searchConditions.add(descC);

		return searchConditions;
	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (!ParameterCheck.isNullOrEmpty(getDesc())) {
			HibernateExpression descE = new CompareExpression("description", "%"
					+ getDesc() + "%", CompareType.Like);
			hibernateExpressions.add(descE);
		}
		
		
		HibernateExpression cataE = new CompareExpression("catalogId",
				getCatalogId(), CompareType.Equal);
		hibernateExpressions.add(cataE);

		return hibernateExpressions;
	}

	public String getColumns() {
		
		MaterialCatalog catalog = getMaterialService().getMaterialCatalog(
				getCatalogId());
		
		if (catalog.getType().equals(MaterialCatalog.TYPE_IMAGE)) {
			return "!id:ID:id,!name:名称:name,!extName:类型:extName,!pre:预览:pre,!link:链接地址:link,!validate:维护:validate,!delete:删除:delete";
		} else if (catalog.getType().equals(MaterialCatalog.TYPE_MUSIC)) {
			return "!id:ID:id,!name:名称:name,!extName:类型:extName,!link:链接地址:link,!validate:维护:validate,!delete:删除:delete";
		}
		return "";
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			int count = 0;

			public int getRowCount() {
				if (count > 0) {
					return count;
				}
				count = getMaterialService().getMaterialResultCount(
						getSearchExpressions()).intValue();
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getMaterialService().findMaterials(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}
		};
	}

	public IPage onAdd(Integer catalogId) {
		getEditPage().setCatalogId(catalogId);
		return getEditPage();
	}

	public String getUrlAddress() {
		String url = getSystemService().getVariables("media_url").getValue();
		Material mater = (Material) getCurrentObject();

		return "copy('" + url + mater.getFilename() + "." + mater.getExtName()
				+ "');";
	}

	public String getPreAddress() {
		String url = getSystemService().getVariables("media_url").getValue();
		Material mater = (Material) getCurrentObject();
		return url + mater.getFilename() + "." + mater.getExtName();
	}
}
