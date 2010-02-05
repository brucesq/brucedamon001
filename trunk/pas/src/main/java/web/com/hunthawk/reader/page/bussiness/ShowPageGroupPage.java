package com.hunthawk.reader.page.bussiness;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.framework.tapestry.RichSearchPage;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.Preview;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

@Restrict(roles = { "pagegroup" }, mode = Restrict.Mode.ROLE)
public abstract class ShowPageGroupPage extends RichSearchPage {
	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditPageGroupPage")
	public abstract EditPageGroupPage getEditPageGroupPage();

	@InjectPage("bussiness/ShowColumnPage")
	public abstract ShowColumnPage getShowColumnPage();

	@InjectPage("Preview")
	public abstract Preview getPreviewPage();
	// @InjectObject("spring:pagegroupService")
	// public abstract PagegroupService getPagegroupService();
	
	
	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();
	
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@Override
	protected void delete(Object object) {
		try {
			PageGroup pageGroup=(PageGroup) object;
			
			boolean allowDel=false;
			//先判断页面组状态  已上线产品不允许被删除
			if(!pageGroup.getPkStatus().equals(Constants.PAGEGROUPSTATUS_PUBLISH)){
				//查询 此页面组是否有栏目 如果有则提示不允许被删除
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("pagegroup", pageGroup
						, CompareType.Equal);
				expressions.add(ex);
				List<Columns> cols=getBussinessService().findColumns(1, Integer.MAX_VALUE, "id", false, expressions);
				if(cols==null || cols.size()<1){
					allowDel=true;
				}
			}
			if(allowDel){
				getBussinessService().deletePageGroup(pageGroup);
			}else{
				getDelegate().setFormComponent(null);
				getDelegate().record("删除失败,此页面组已被栏目引用!要删除此页面组请先将栏目删除", null);
			}
			
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public IPage onEdit(PageGroup pagegroup) {
		getEditPageGroupPage().setModel(pagegroup);
		return getEditPageGroupPage();
	}

	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedPagegroups();

	public abstract void setSelectedPagegroups(Set set);

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		PageGroup pg = (PageGroup) getCurrentObject();
		Set selectedPros = getSelectedPagegroups();
		// 选择了用户
		if (bSelected) {
			selectedPros.add(pg);
		} else {
			selectedPros.remove(pg);
		}
		// persist value
		setSelectedPagegroups(selectedPros);

	}

	public boolean getCheckboxSelected() {
		return getSelectedPagegroups().contains(getCurrentObject());
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedPagegroups()) {
			delete(obj);
		}
		setSelectedPagegroups(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);
	}

	// public abstract Integer getPagegroupId();
	// public abstract void setPagegroupId(Integer id);
	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getShowType();

	public abstract void setShowType(Integer showType);
	
	// 创建者
	public abstract UserImpl getCreator();

	public abstract void setCreator(UserImpl creator);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("pkName", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		// if(getPagegroupId()!=null && getPagegroupId()>0){
		// HibernateExpression useridE = new
		// CompareExpression("pagegroupId",getPagegroupId(),CompareType.Equal);
		// hibernateExpressions.add(useridE);
		// }
		if (getShowType() != null) {
			HibernateExpression showTypeE = new CompareExpression("showType",
					getShowType(), CompareType.Equal);
			hibernateExpressions.add(showTypeE);
		}
		if(getCreator() != null ){
			HibernateExpression userE = new CompareExpression("creator",
					getCreator().getId(), CompareType.Equal);
			hibernateExpressions.add(userE);
		}

		return hibernateExpressions;
	}

	public void search() {
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		SearchCondition showTypeC = new SearchCondition();
		showTypeC.setName("showType");
		showTypeC.setValue(getShowType());
		searchConditions.add(showTypeC);
		
		SearchCondition creatorC = new SearchCondition();
		creatorC.setName("creator");
		creatorC.setValue(getCreator());
		searchConditions.add(creatorC);
		
		return searchConditions;
	}

	public IPropertySelectionModel getBussinessTypeList() {
		return new MapPropertySelectModel(Constants.getBussinessTypes(),true,"");
	}
	
	// 查询所有用户

	public IPropertySelectionModel getUserList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getUserService().getAll(UserImpl.class), UserImpl.class,
				"getName", "getId", true, "");
		return parentProper;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getBussinessService().getPageGroupCount(
						getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getBussinessService().findPageGroups(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}
		};
	}

	
	public IPage pagegroupCopy(){
		String err="";
		if(getSelectedPagegroups().size() == 0){
			err="您至少要选择一个选项";
		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			setSelectedPagegroups(new HashSet());
			return this;
		} else {
			//进行复制操作  然后返回
			List<PageGroup> list=new ArrayList(getSelectedPagegroups());
			UserImpl user = (UserImpl)getUser();
			for(Iterator it=list.iterator();it.hasNext();){
				PageGroup pg=(PageGroup)it.next();
				//根据当前页面组得到属于他的所有的父栏目
				Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
				HibernateExpression nameE = new CompareExpression("pagegroup", pg, CompareType.Equal);
				hibernateExpressions.add(nameE);				
				NullExpression name = new NullExpression("parent");
				hibernateExpressions.add(name);		
				List<Columns> oldColumns = 
					getBussinessService().findColumns(1, Integer.MAX_VALUE, "id", false, hibernateExpressions);
				Map<Integer,Columns> maps = new HashMap<Integer,Columns>();
				pg.setPkName("复件  "+pg.getPkName());
				Date date=new Date();
				pg.setCreateTime(date);
				pg.setModifyTime(date);
				pg.setModifier(getUser().getId());
				pg.setCreator(getUser().getId());
				pg.setDeleteStatus(1);// 页面组删除状态 1.使用 2.隐藏
				pg.setPkStatus(Constants.PAGEGROUPSTATUS_CHECK);
				try {
					getBussinessService().addPageGroup(pg);
					saveColumns(oldColumns,pg,user,null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					getDelegate().setFormComponent(null);
					getDelegate().record("页面组复制失败,请联系管理员！", null);
					setSelectedPagegroups(new HashSet());
					return this;
				}
			}
			setSelectedPagegroups(new HashSet());
		}
		return this;
	}
	/**
	 * 栏目的复制
	 * @param colList
	 * @param pg
	 * @param user
	 * @param parentCol
	 * yuzs 2009-11-17
	 */
	public void saveColumns(List<Columns> colList,PageGroup pg,UserImpl user,Columns parentCol){
		
		for(Columns col : colList){
			col.setPagegroup(pg);
			col.setCreateTime(new Date());
			col.setCreator(user.getId());
			col.setModifier(user.getId());
			col.setModifyTime(new Date());
			if(parentCol!=null && parentCol.getId()>0){
				col.setParent(parentCol);
			}
			try {
				Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
				HibernateExpression nameE = new CompareExpression("parent", col, CompareType.Equal);
				hibernateExpressions.add(nameE);					
				List<Columns> oldColumns = 
					getBussinessService().findColumns(1, Integer.MAX_VALUE, "id", false, hibernateExpressions);
				getBussinessService().addColumn(col);
				if(oldColumns!=null && oldColumns.size()>0){
					saveColumns(oldColumns,pg,user,col);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}	
	}
	/**
	 * 获得当前页面组
	 * 
	 * @return
	 */

	public IPage showColumn(PageGroup p) {
		ShowColumnPage page = getShowColumnPage();
		page.setPageGroup(p);
		page.setParent(null);
		return page;
	}

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedPagegroups();

		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "您至少得选择一个产品";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					PageGroup pageGroup = (PageGroup) obj;
					pageGroup.setModifier(getUser().getId());
					pageGroup.setModifyTime(new Date());
					getBussinessService().auditPageGroup(pageGroup,
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
		setSelectedPagegroups(new HashSet());
		getCallbackStack().popPreviousCallback();
	}

	public ITableColumn getDisplayModifier() {
		return new SimpleTableColumn("modifier", "修改人",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						PageGroup o1 = (PageGroup) objRow;

						if (o1.getModifier() != null) {
							UserImpl user = (UserImpl) getUserService()
									.getObject(UserImpl.class, o1.getModifier());
							if (user == null) {
								return "";
							} else {
								return user.getName();
							}
						} else {
							return "";
						}

					}

				}, false);

	}
	
	public ITableColumn getTempName(){
		return new SimpleTableColumn("pkOneTempId", "首页模板",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						PageGroup o1 = (PageGroup) objRow;
							if(o1.getPkOneTempId()!=null){
								Template template = getTemplateService().getTemplate(o1.getPkOneTempId());
								if(template!=null)
									return template.getTitle();
								else
									 return "";
							}else
								return "";
					}

				}, false);

	}
//	public ITableColumn getResTempName(){
//		return new SimpleTableColumn("resOneTempId", "资源模板",
//				new ITableColumnEvaluator() {
//
//					private static final long serialVersionUID = 31491600745851970L;
//
//					public Object getColumnValue(ITableColumn objColumn,
//							Object objRow) {
//						PageGroup o1 = (PageGroup) objRow;
//							if(o1.getPkOneTempId()!=null){
//								Template template = getTemplateService().getTemplate(o1.getResOneTempId());
//								if(template!=null)
//									return template.getTitle();
//								else
//									 return "";
//							}else
//								return "";
//					}
//
//				}, false);
//	}
	public String getPreViewPage(){
		PageGroup pg = (PageGroup)getCurrentObject();
		String preUrl = getSystemService().getVariables("preview_url").getValue();
		String portalUrl = getSystemService().getVariables("portal_url").getValue();
		String url = preUrl + URLEncoder.encode(portalUrl + "pg=p&gd="+pg.getId()+"&ad=001&preview=1");
		return "javascript:window.open('"+url+"','','scrollbars=no,width=315,height=450')";
	}
	
	public IPage onView(PageGroup pg) {
		String portalUrl = getSystemService().getVariables("portal_url")
				.getValue()
				+ "pg=p&gd="+pg.getId();//+"&ad=001&preview=1";
		String content = PageUtil.getURLStream(portalUrl,getServletRequest().getContextPath());
		logger.info("内容:" + content);
		Preview pre = getPreviewPage();
		pre.setTitle(pg.getPkName());
		pre.setContent(content);
		return pre;
	}
	
	
	public String getUrlAddress() {
		String url = getSystemService().getVariables("portal_url").getValue();
		PageGroup pg = (PageGroup)getCurrentObject();
		return "copy('" + url + "pg=p&gd="+pg.getId()//+"&ad=001&preview=1"
				+ "');";
	}

}
