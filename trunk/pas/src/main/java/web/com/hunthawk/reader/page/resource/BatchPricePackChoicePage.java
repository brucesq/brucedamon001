/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
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
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "basic" }, mode = Restrict.Mode.ROLE)
public abstract class BatchPricePackChoicePage extends SecurityPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();
	@InjectPage("resource/SourceToPackPage")
	public abstract SourceToPackPage getSourceToPackPage();

	@Bean
	public abstract ValidationDelegate getDelegate();

	public abstract ResourcePack getResourcePack();

	public abstract void setResourcePack(ResourcePack resourcePack);

	public abstract String getResourcePackName();

	public abstract void setResourcePackName(String resourcePackName);

	@InitialValue("-1")
	public abstract Integer getSearchId();

	public abstract void setSearchId(Integer id);
	public abstract Integer getType();
	public abstract void setType(Integer typeId);
	
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getResourcePackName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getResourcePackName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getSearchId() != null && getSearchId() > 0) {
			HibernateExpression idE = new CompareExpression("id",
					getSearchId(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		if(getType()!=null && getType()>0){
			HibernateExpression idE = new CompareExpression("type",
					getType(), CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		if(getCurrentSp()!=null){
			HibernateExpression idE = new CompareExpression("spid",
					getCurrentSp().getId()+"", CompareType.Equal);
			hibernateExpressions.add(idE);
		}
		return hibernateExpressions;
	}

	/**
	 * 获得批价包
	 * 
	 * @return
	 */
	public IBasicTableModel getChooseResourcePacks() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getResourcePackService().getEpackCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;

				pageNo++;

				return getResourcePackService().findEpack(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}

		};

	}

	/**
	 * 搜索批价包
	 * 
	 * @param cycle
	 */
	public void searchResourcePack(IRequestCycle cycle) {
		setChoose("false");
		setSearch(true);
		getTableView().reset();

	}

	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);

	public abstract String getChoosePackType();

	public abstract void setChoosePackType(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {

		ValidationDelegate delegate = getDelegate();
		Set selectObjs = getSelectedObjects();
		if (selectObjs.size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("必须选择一个批价包", null);
			return;
		}
		int type = -1;
		String value = "";
		for (Object obj : selectObjs) {
			ResourcePack pack = (ResourcePack) obj;
			if (type == -1) {
				type = pack.getType();
			}
			if (type != pack.getType()) {
				delegate.setFormComponent(null);
				delegate.record("您选择的批价包必须是同一种类型，请重新选择.", null);
				setSelectedObjects(new HashSet());
				return;
			}
			value += pack.getId() + ",";
		}

		setChoose("true");

		setRadioValue(value);
		setChoosePackType(String.valueOf(type));
		setSelectedObjects(new HashSet());
		logger.info("提交的值:" + getRadioValue());
	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板类型
		setReturnElement((String) parameters[0]);

	}

	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();

	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);

	public void pageBeginRender(PageEvent arg0) {

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
	
	public IPropertySelectionModel getFeeTypeList() {
		return new MapPropertySelectModel(Constants.getFeeTypeMap());
	}
	
	public ITableColumn getSpName() {
		return new SimpleTableColumn("spid", "CP名称",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePack pack = (ResourcePack)objRow;
						if(pack.getSpid()!=null){
							Provider provider =	getPartnerService().getProvider(Integer.parseInt(pack.getSpid()));
							if(provider!=null)
								return provider.getIntro();
							else
								return "无CP关联信息";
						}else
							return "无CP关联信息";
					}

				}, false);

	}
	
	public ITableColumn getFeeType(){
		return  new SimpleTableColumn("type","计费类型",new ITableColumnEvaluator(){
			private static final long serialVersionUID = 31491600745851970L;
			public Object getColumnValue(ITableColumn objeColumn,Object objRow){
				ResourcePack pack = (ResourcePack)objRow;
				if(pack.getType()!=null){
					if(pack.getType()==1)
						return "按次";
					if(pack.getType()==2)
						return "包月(VIP)";
					if(pack.getType()==3)
						return "包月(内容控制)";
					if(pack.getType()==4)
						return "包月(常规)";
					if(pack.getType()==5)
						return "免费";
				}
				return "";
			}	
		},false);
	}
	
	/*
	 *增加sp/cp查询维度
	 *2009-11-09
	 *yuzs
	 */
	public IPropertySelectionModel getSpList() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression expression = new CompareExpression("status", 3,
				CompareType.Equal);
		hibernateExpressions.add(expression);
		List<Provider> providers = getPartnerService().findProvider(1,
				Integer.MAX_VALUE, "id", true, hibernateExpressions);
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				providers, Provider.class, "getIntro", "getId", true, "");
		return model;
	}
	
	public abstract Provider getCurrentSp();

	public abstract void setCurrentSp(Provider provider);
	/*
	 * 结束
	 */
}