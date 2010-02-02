package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.RichSearchPage;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.partner.Channel;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

@Restrict(roles = { "product" }, mode = Restrict.Mode.ROLE)
public abstract class ShowProductPage extends RichSearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditProductPage")
	public abstract EditProductPage getEditProductPage();

	@InjectPage("bussiness/ShowProductPageGroupPage")
	public abstract ShowProductPageGroupPage getShowProductPageGroupPage();

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@Override
	protected void delete(Object object) {
		// try{
		// getPackGroupProvinceRelationService().deleteById(((Product)object).getId());
		//			
		// getProductService().deleteProduct((Product)object);
		// }catch(Exception e)
		// {
		// getDelegate().setFormComponent(null);
		// getDelegate().record(e.getMessage(), null);
		// }
	}

	public IPage onEdit(Product product) {
		getEditProductPage().setModel(product);
		return getEditProductPage();
	}

	public IPage showProductPageGroup(Product product) {
		getShowProductPageGroupPage().setProductId(product.getId());
		return getShowProductPageGroupPage();
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedProducts();

	public abstract void setSelectedProducts(Set set);

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		Product pro = (Product) getCurrentObject();
		Set selectedPros = getSelectedProducts();
		// 选择了用户
		if (bSelected) {
			selectedPros.add(pro);
		} else {
			selectedPros.remove(pro);
		}
		// persist value
		setSelectedProducts(selectedPros);

	}

	public boolean getCheckboxSelected() {
		return getSelectedProducts().contains(getCurrentObject());
	}

	public void onBatchDelete(IRequestCycle cycle) {

		for (Object obj : getSelectedProducts()) {
			delete(obj);
		}
		setSelectedProducts(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);
	}

	// 渠道公司
	public abstract Channel getChannel();

	public abstract void setChannel(Channel channel);

	// 产品名称
	public abstract String getName();

	public abstract void setName(String name);

	// 产品状态
	public abstract Integer getStatus();

	public abstract void setStatus(Integer status);

	// 创建者
	public abstract UserImpl getCreator();

	public abstract void setCreator(UserImpl creator);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		if (getStatus() != null && getStatus() >= 0) {
			HibernateExpression statusE = new CompareExpression("status",
					getStatus(), CompareType.Equal);
			hibernateExpressions.add(statusE);
		}
		if (getChannel() != null
				&& !ParameterCheck.isNullOrEmpty(getChannel().getId())) {
			HibernateExpression channelE = new CompareExpression("channel",
					getChannel(), CompareType.Equal);
			hibernateExpressions.add(channelE);
		}
		UserImpl user = (UserImpl)getUser();
		if(user.isRoleChannel()){
			HibernateExpression channelE = new CompareExpression("channel",
					user.getChannel(), CompareType.Equal);
			hibernateExpressions.add(channelE);
		}
		if(getCreator() != null ){
			HibernateExpression userE = new CompareExpression("creator",
					getCreator().getId(), CompareType.Equal);
			hibernateExpressions.add(userE);
		}
		// if(getProductId()!=null && getProductId()>0){
		// HibernateExpression useridE = new
		// CompareExpression("id",getProductId(),CompareType.Equal);
		// hibernateExpressions.add(useridE);
		// }

		return hibernateExpressions;
	}

	public void search() {
	}

	
	@InjectPage("bussiness/CopyProductPage")
	public abstract CopyProductPage getCopyProductPage();
	/**
	 * 产品复制
	 */
	public IPage productCopy(){
		String err="";
		CopyProductPage page=getCopyProductPage();
		if(getSelectedProducts().size() == 0){
			err="您至少要选择一个产品";
		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			return null;
		} else {
			page.setProducts(getSelectedProducts());
			setSelectedProducts(new HashSet());
			return page;
		
		}
	}
	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		SearchCondition channelC = new SearchCondition();
		channelC.setName("channel");
		channelC.setValue(getChannel());
		searchConditions.add(channelC);

		SearchCondition statusC = new SearchCondition();
		statusC.setName("status");
		statusC.setValue(getStatus());
		searchConditions.add(statusC);

		SearchCondition creatorC = new SearchCondition();
		creatorC.setName("creator");
		creatorC.setValue(getCreator());
		searchConditions.add(creatorC);

		return searchConditions;
	}

	public IPropertySelectionModel getChannelList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getPartnerService().getAllFirstLevelChannel(), Channel.class,
				"getIntro", "getId", true, "");
		return parentProper;
	}

	// 查询所有用户

	public IPropertySelectionModel getUserList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getUserService().getAll(UserImpl.class), UserImpl.class,
				"getName", "getId", true, "");
		return parentProper;
	}

	public IPropertySelectionModel getProductStatusList() {
		return new MapPropertySelectModel(Constants.getProductStatus(), true,
				"");
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getBussinessService().getProductCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getBussinessService().findProducts(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}
		};
	}

	public String getUrlAddress() {
		String url = getSystemService().getVariables("portal_url").getValue();

		return "copy('" + url + "pd=" + ((Product) getCurrentObject()).getId()
				+ "');";
	}

	/**
	 * 获得当前产品
	 * 
	 * @return
	 */

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedProducts();

		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "您至少得选择一个产品";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					Product product = (Product) obj;
					product.setModifier(getUser().getId());
					product.setModifyTime(new Date());
					getBussinessService().auditProduct(product,
							getStatusValue());
				} catch (Exception e) {
					err += e.getMessage();
				}
			}

		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
		}

		// clear selection
		setSelectedProducts(new HashSet());
		getCallbackStack().popPreviousCallback();
	}

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Product o1 = (Product) objRow;

						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, o1.getCreator());
						if (user == null) {
							return "";
						} else {
							return user.getName();
						}

					}

				}, false);

	}
	
	public ITableColumn getDisplayModifier() {
		return new SimpleTableColumn("modifier", "修改人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Product o1 = (Product) objRow;

						if(o1.getModifier() != null){
							UserImpl user = (UserImpl) getUserService().getObject(
									UserImpl.class, o1.getModifier());
							if (user == null) {
								return "";
							} else {
								return user.getName();
							}
						}else{
							return "";
						}
						
					}

				}, false);

	}

}
