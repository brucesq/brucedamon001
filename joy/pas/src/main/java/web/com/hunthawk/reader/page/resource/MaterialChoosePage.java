/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
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
import com.hunthawk.framework.hibernate.InExpression;
import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class MaterialChoosePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getMaterialType();

	public abstract MaterialCatalog getMaterialCatalog();

	public abstract void setMaterialType(Integer materialType);

	public abstract void setMaterialCatalog(MaterialCatalog materialCatalog);

	@InjectComponent("table")
	public abstract TableView getTableView();

	@Bean
	public abstract ValidationDelegate getDelegate();

	@InitialValue("-1")
	public abstract int getSearchId();

	public abstract void setSearchId(int id);

	public abstract Material getMaterial();

	public abstract void setMaterial(Material material);

	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getMaterialCatalogsList() {

		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getAllCatalogs(), MaterialCatalog.class, "getName", "getId", true, "");
		return parentProper;

	}

	protected List<MaterialCatalog> getAllCatalogs() {
		List<MaterialCatalog> catalogs = getMaterialService()
				.getMaterialCatalogs();
		List<MaterialCatalog> typelist = new ArrayList<MaterialCatalog>();

		for (MaterialCatalog catalog : catalogs) {
			if (catalog.getType().equals(getMaterialType())) {
				typelist.add(catalog);
			}
		}
		return typelist;
	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}

		
		
		if (getMaterialCatalog() != null) {
			HibernateExpression typeF = new CompareExpression("catalogId",
					getMaterialCatalog().getId(), CompareType.Equal);
			hibernateExpressions.add(typeF);
		}else{
			List<MaterialCatalog> catalogs =  getAllCatalogs();
			Integer[] ids = new Integer[catalogs.size()];
			int i=0;
			for(MaterialCatalog catalog : catalogs){
				ids[i++] = catalog.getId();
			}
			HibernateExpression typeG = new InExpression("catalogId",ids);
			hibernateExpressions.add(typeG);
		}

		return hibernateExpressions;
	}

	/**
	 * 获得模板
	 * 
	 * @return
	 */
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {

			// 模板的总数
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

	/**
	 * 搜索模板
	 * 
	 * @param cycle
	 */
	public void searchTemplate(IRequestCycle cycle) {
		setChoose("false");
		setSearch(true);
		getTableView().reset();

	}

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);
	
	public abstract String getRadioValue1();

	public abstract void setRadioValue1(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		String value = getRadioValue();

		ValidationDelegate delegate = getDelegate();

		if (getMaterial() == null) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个素材", null);

		} else {
			setRadioValue(String.valueOf((getMaterial().getId())));
			
			String url = getSystemService().getVariables("media_url").getValue();
			
			setRadioValue1( url + getMaterial().getFilename() + "." + getMaterial().getExtName());
		}

		logger.info("提交的值:" + getRadioValue());

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		
			
			setMaterialType(Integer.parseInt( (String)parameters[2]));
			setReturnElement((String) parameters[0]);
			setReturnElement1((String) parameters[1]);
		
	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();
	
	public abstract void setReturnElement1(String name);

	public abstract String getReturnElement1();


	
	/**
	 * 搜索时的状态条件
	 * 
	 * @return
	 */
	@InitialValue("-1")
	public abstract int getSearchStatus();

	public abstract void setSearchStatus(int status);

	

	// 是否为搜索请求
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	

	

	/**
	 * 设置初始属性
	 */
	public void pageBeginRender(PageEvent arg0) {

	}

	public abstract Material getCurrentObject();
	public abstract void setCurrentObject(Material m);
	
	public String getPreAddress() {
		String url = getSystemService().getVariables("media_url").getValue();
		Material mater =  getCurrentObject();
		return url + mater.getFilename() + "." + mater.getExtName();
	}
}
