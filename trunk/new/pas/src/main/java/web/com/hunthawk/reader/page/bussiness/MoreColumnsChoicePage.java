package com.hunthawk.reader.page.bussiness;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IExternalPage;
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
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.framework.security.SecurityComponent;
import com.hunthawk.framework.security.Visit;
import com.hunthawk.framework.tapestry.RichSearchPage;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.callback.CallbackStack;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.resource.ComicsChapter;
import com.hunthawk.reader.domain.resource.EbookChapter;
import com.hunthawk.reader.domain.resource.EbookKeyWord;
import com.hunthawk.reader.domain.resource.MagazineChapter;
import com.hunthawk.reader.domain.resource.NewsPapersChapter;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.DetailPage;
import com.hunthawk.reader.page.guide.ColumnsRollGuide;
import com.hunthawk.reader.page.resource.ShowEpackReleationPage;
import com.hunthawk.reader.page.resource.SourceToCheckKeyWordPage;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;
/**
 * 栏目多项选择
 * @author liuxh
 *
 */
@Restrict(roles = { "basic" }, mode =  Restrict.Mode.ROLE)
public abstract class MoreColumnsChoicePage extends RichSearchPage implements
IExternalPage, PageBeginRenderListener {


	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditColumnPage")
	public abstract EditColumnPage getEditColumnPage();

	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getShowEpackReleationPage();
	
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();
	
	@Persist("session")
	public abstract PageGroup getPageGroup();
	public abstract void setPageGroup(PageGroup page);
	
	public abstract ResourcePack getResourcePack();
	public abstract void setResourcePack(ResourcePack resourcePack);
	
	@Persist("session")
	public abstract Columns getParent();
	public abstract void setParent(Columns parent);

	
	@InitialValue("-1")
	public abstract int getSearchId();

	public abstract void setSearchId(int id);
	
	public abstract String getName();

	public abstract void setName(String name);
	
	
	public IPage onEdit(Columns c) {
		getEditColumnPage().setModel(c);
		getEditColumnPage().setPageGroup(c.getPagegroup());
		getEditColumnPage().setShowType(c.getPagegroup().getShowType());
		return getEditColumnPage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		Columns co = (Columns) getCurrentObject();
		Set selectedObjects = getSelectedObjects();
		// 选择了用户
		if (bSelected) {
			selectedObjects.add(co);
		} else {
			selectedObjects.remove(co);
		}
		// persist value
		setSelectedObjects(selectedObjects);
	}

	public void pageBeginRender(PageEvent arg0) {

	}
	
	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {
		logger.info("接受的参数的个数" + parameters.length);

		// 显示模板类型
		
		setReturnElement((String) parameters[0]);
		
	}
	public abstract void setReturnElement(String name);

	public abstract String getReturnElement();
	
	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedObjects();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedObjects(Set set);

//
	public abstract String getRadioValue();

	public abstract void setRadioValue(String radioValue);

	public void chooseSubmit(IRequestCycle cycle) {
		setChoose("true");

		//System.out.println("chooseSubmit:"+getSelectedObjects().size());
		
		ValidationDelegate delegate = getDelegate();

		if ( getSelectedObjects().size() == 0) {
			delegate.setFormComponent(null);
			delegate.record("至少要选择一个栏目", null);

		} else {
			StringBuilder columnids=new StringBuilder();
			List listRes = new ArrayList(getSelectedObjects());
			for(Iterator it=listRes.iterator();it.hasNext();){
				Columns col=(Columns)it.next();
				columnids.append(col.getId());
				columnids.append("-");
			}
			String str=columnids.substring(0,columnids.lastIndexOf("-"));
			setRadioValue(str);
		}
		setSelectedObjects(new HashSet());
		logger.info("提交的值:" + getRadioValue());
	}
	
//	@InjectPage("guide/ColumnsRollGuide")
//	public abstract ColumnsRollGuide getColumnsRollGuide();
//	
//	public IPage backColumnsRollGuide() {
//		String err = "";
//		ColumnsRollGuide page = getColumnsRollGuide();
//		if (getSelectedObjects().size() == 0)
//			err = "您至少要选择一个栏目";
//		if (!ParameterCheck.isNullOrEmpty(err)) {
//			getDelegate().setFormComponent(null);
//			getDelegate().record(err, null);
//			return null;
//		} else {
//			StringBuilder columnids=new StringBuilder();
//			List listRes = new ArrayList(getSelectedObjects());
//			for(Iterator it=listRes.iterator();it.hasNext();){
//				Columns col=(Columns)it.next();
//				columnids.append(col.getId());
//				columnids.append("-");
//			}
//			String str=columnids.substring(0,columnids.lastIndexOf("-"));
//			page.setColumnids(str);
//			setSelectedObjects(new HashSet());
//			return page;
//		}
//	}

	@Override
	protected void delete(Object object) {
	}

	public void onBatchDelete(IRequestCycle cycle) {
	}

	public void search() {
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("pageGroup");
		nameC.setValue(getPageGroup());
		searchConditions.add(nameC);

		SearchCondition nameP = new SearchCondition();
		nameP.setName("parent");
		nameP.setValue(getParent());
		searchConditions.add(nameP);
		return searchConditions;
	}

	/**
	 * 获得模板类型下拉列表的数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IPropertySelectionModel getPagegroupList() {

		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getBussinessService().findPageGroups(1, Integer.MAX_VALUE, "id", false, new ArrayList<HibernateExpression>()), PageGroup.class,
				"getPkName", "getId", true, "");
		return parentProper;

	}

	public abstract String getChoose();

	public abstract void setChoose(String choose);
	// 是否为搜索请求
	public abstract boolean isSearch();

	public abstract void setSearch(boolean isSearch);
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
		HibernateExpression typeF = new CompareExpression("pagegroup",
				getPageGroup(), CompareType.Equal);
		hibernateExpressions.add(typeF);
		
		HibernateExpression statusF = new CompareExpression("status",
				Constants.COLUMNSTATUS_PUBLISH, CompareType.Equal);
		hibernateExpressions.add(statusF);
		return hibernateExpressions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return new Long(getBussinessService().getColumnCount(
						getSearchExpressions())).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getBussinessService().findColumns(pageNo, nPageSize,
						"order", true, getSearchExpressions()).iterator();
			}
		};
	}

	public IPage onAddColumn(Columns c, PageGroup p) {
		EditColumnPage page = getEditColumnPage();
		page.setParent(c);
		page.setPageGroup(p);
		page.setShowType(p.getShowType());
		return page;
	}

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public abstract int getOrderValue();
	public abstract void setOrderValue(int orderValue);
	
	

	public ITableColumn getDisplayModifier() {
		return new SimpleTableColumn("modifier", "修改人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;

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
	
	
	public ITableColumn getDisplayPack() {
		return new SimpleTableColumn("pricepackId", "批价包",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;

						if(o1.getPricepackId()!= null){
							ResourcePack rp=(ResourcePack)getResourcePackService().getEpack(o1.getPricepackId());
							if(rp!=null){
								return rp.getId();
							}else{
								return "";
							}
						}else{
							return "";
						}
						
					}

				}, false);

	}
	
	
	
	public ITableColumn getColumnType() {
		return new SimpleTableColumn("columnType", "栏目创建类型",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;
						if(o1.getCreateType()==1)
							return "系统创建";
						else
							return "用户创建";
					}

				}, false);

	}
	
	public String getPreAddress() {
		Columns co = (Columns) getCurrentObject();
		String imgPath ="";
		if(co.getIcon()==null || StringUtils.isEmpty(co.getIcon())){
			imgPath="无";
		}else{
			String url="";//"http://book.moluck.com/media/ebook/0/10000095/cover75.gif";//调用获取栏目图标的方法
			imgPath += "<img src='"+url+"' width='40' height='40' align='absmiddle'/>";
		}
		return imgPath;
	}
	
	public String getPreViewPage(){
		Columns column = (Columns)getCurrentObject();
		String preUrl = getSystemService().getVariables("preview_url").getValue();
		String portalUrl = getSystemService().getVariables("portal_url").getValue();
		String url = preUrl + URLEncoder.encode(portalUrl + "pg=c&gd="+column.getPagegroup().getId()+"&ad=001&cd="+column.getId()+"&preview=1");
		return "javascript:window.open('"+url+"','','scrollbars=no,width=315,height=450')";
	}
	
	public String getUrlAddress() {
		String url = getSystemService().getVariables("portal_url").getValue();
		Columns column = (Columns)getCurrentObject();
		return "copy('" + url + "pg=c&gd="+column.getPagegroup().getId()+"&ad=001&cd="+column.getId()+"&preview=1"
				+ "');";
	}
	
	
	 /***********************************
	  * 栏目下资源个数
	  */
	 public Long getResourceCount(Columns column,Long number){ //递归计算方法
			//int number=0;
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression name = new CompareExpression("parent", column,
					CompareType.Equal);
			expressions.add(name);
			List<Columns> listType= getBussinessService().findColumns(1, Integer.MAX_VALUE, "id", false, expressions);
			if(listType!=null && listType.size()>0){
				for(int i=0;i<listType.size();i++){
					Columns columnNext = listType.get(i);
				number = getResourceCount(columnNext,number);
				}
			}else{
				//批价包ID
				if(column.getPricepackId()!=null && column.getPricepackId()>0){
					ResourcePack pack= (ResourcePack) getResourcePackService().getEpack(column.getPricepackId());
					if(pack!=null){
					Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
					HibernateExpression nameE = new CompareExpression("pack", pack,
							CompareType.Equal);
					hibernateExpressions.add(nameE);
					number += getResourcePackService().getResourcePackReleationCount(hibernateExpressions);
					}
				}
			}
			return number;
		}
		public ITableColumn getResourceCount() {
			return new SimpleTableColumn("resourceCount", "资源数量",
					new ITableColumnEvaluator() {

						private static final long serialVersionUID = 31491600745851970L;

						public Object getColumnValue(ITableColumn objColumn,
								Object objRow) {
							Columns o1 = (Columns) objRow;
							
							return getResourceCount(o1,0L);
						}

					}, false);

		}

}
