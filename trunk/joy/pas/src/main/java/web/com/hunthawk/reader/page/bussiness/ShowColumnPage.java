package com.hunthawk.reader.page.bussiness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
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
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PackGroupProvinceRelation;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.Preview;
import com.hunthawk.reader.page.resource.ShowEpackReleationPage;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * 
 * @author BruceSun
 * 
 */
@Restrict(roles = { "column" }, mode = Restrict.Mode.ROLE)
public abstract class ShowColumnPage extends RichSearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditColumnPage")
	public abstract EditColumnPage getEditColumnPage();

	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getShowEpackReleationPage();
	
	@InjectPage("Preview")
	public abstract Preview getPreviewPage();

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
	
	/*
	 * 修改栏目的返回，添加查询条件----
	 */
	public  boolean isShowParent(){
		if(getParent()!=null && getParent().getId()>0){
			return true;
		}else{
			return false;
		}
	}
	public void backParentPage(Columns col,PageGroup pg){
		if(col.getParent()!=null)		
			this.setParent(col.getParent());
		else
			this.setParent(null);
		this.setPageGroup(pg);
		
	}
	public IPage onEdit(Columns c) {
		getEditColumnPage().setModel(c);
		getEditColumnPage().setPageGroup(c.getPagegroup());
		getEditColumnPage().setShowType(c.getPagegroup().getShowType());
		return getEditColumnPage();
	}

	public  abstract String getColName();
	public  abstract void setColName(String name);
	
	public abstract Integer getColId();
	public abstract void setColId(Integer id);
	
	public abstract Integer getSortId();
	public abstract void setSortId(Integer id);
	
	public abstract Integer getResourceStatus();
	public abstract void setResourceStatus(Integer id);
	
	public IPropertySelectionModel getStatusList() {
		Map<String, Integer> statuslist = new OrderedMap<String, Integer>();
		statuslist.put("全部", -1);
		statuslist.put("上线", 1);
		statuslist.put("下线", 2);
		return new MapPropertySelectModel(statuslist, false, "全部");
	}

	public IPropertySelectionModel getSortList() {
		Map<String, Integer> map = Constants.getOrderTypeMap();
		return new MapPropertySelectModel(map,true,"全部");
	}

	@InitialValue("false")
	public abstract boolean isShowParentColumn();
	public abstract void setShowParentColumn(boolean isShow);
	public void search() {
		setShowParentColumn(true);
	}
	
	//----------end----------------------
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

	/**
	 * 栏目复制
	 */
	public IPage columnCopy() {
		if (getSelectedObjects().size() == 0) {
			getDelegate().setFormComponent(null);
			getDelegate().record("您至少要选择一个选项!", null);
			setSelectedObjects(new HashSet());
			return this;
		}
		List<Columns> list = new ArrayList(getSelectedObjects());
		UserImpl user = (UserImpl)getUser();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Columns col = (Columns) it.next();
			col.setName("复件   " + col.getName());
			col.setCreateTime(new Date());
			col.setCreator(getUser().getId());
			col.setModifyTime(new Date());
			col.setModifier(getUser().getId());
			col.setStatus(Constants.COLUMNSTATUS_PUBLISH);
			try {
				Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
				HibernateExpression nameE = new CompareExpression("parent", col, CompareType.Equal);
				hibernateExpressions.add(nameE);					
				List<Columns> oldColumns = 
					getBussinessService().findColumns(1, Integer.MAX_VALUE, "id", false, hibernateExpressions);
				
				getBussinessService().addColumn(col);
				if(oldColumns!=null && oldColumns.size()>0){
					saveColumns(oldColumns,user,col);
				}
			} catch (Exception e) {
				getDelegate().setFormComponent(null);
				getDelegate().record("栏目复制失败,请联系管理员！", null);
				setSelectedObjects(new HashSet());
				return this;
			}
		}
		setSelectedObjects(new HashSet());
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
	public void saveColumns(List<Columns> colList,UserImpl user,Columns parentCol){
		
		for(Columns col : colList){
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
					saveColumns(oldColumns,user,col);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}	
	}
	public boolean getCheckboxSelected() {
		return getSelectedObjects().contains(getCurrentObject());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedObjects();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedObjects(Set set);

	@Override
	protected void delete(Object object) {
		try {
			Columns column = (Columns) object;
			// 进行判断 如果没有子栏目 则导入到批价包列表页 如果批价包ID 不为空则表示 为一级栏目
			boolean allowDel = true;
			if (column.getPricepackId() != null
					&& !column.getPricepackId().equals(0)) {
				allowDel = false;
			} else {// 判断是否有子栏目 如果没有子栏目则允许被删除
				// 查询是否有子分类 将ID赋值为parentID 根据parentId进行查询 如果查询结果不为空 则表示有子分类
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				Collection<HibernateExpression> expressions2 = new ArrayList<HibernateExpression>();
				HibernateExpression ex2 = new CompareExpression("id", column
						.getId(), CompareType.Equal);
				expressions2.add(ex2);
				HibernateExpression ex = new CompareExpression("parent",
						getBussinessService().findColumns(1, Integer.MAX_VALUE,
								"id", false, expressions2).get(0),
						CompareType.Equal);
				expressions.add(ex);
				List<Columns> list = getBussinessService().findColumns(1,
						Integer.MAX_VALUE, "id", false, expressions);
				if (list != null && list.size() > 0) {// 说明有子栏目
					allowDel = false;
				}
			}
			if (allowDel) {
				getBussinessService().auditColumn((Columns) object, 3);
			} else {
				getDelegate().setFormComponent(null);
				getDelegate().record("该栏目不允许被删除,该栏目可能存在子栏目可是已有批价包加入到该栏目", null);
			}

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedObjects()) {
			try {
				getBussinessService().updateColumn((Columns) obj);
			} catch (Exception e) {
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
			}
		}
		setSelectedObjects(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);

	}

	/****************************************************
	 */
	public void onBatchChangeOrder(IRequestCycle cycle) {
		for (Object obj : getSelectedObjects()) {
			Columns column = (Columns) obj;
			try {
				getBussinessService().updateColumn(column);
			} catch (Exception e) {
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
			}
		}
		setSelectedObjects(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);

	}

	public void top(IRequestCycle cycle, Object obj) {
		try {
			Columns column = (Columns) obj;
			Integer minorder = getBussinessService().getMaxMinOrder(
					getParent(), getPageGroup(), "min");
			if (column.getOrder().equals(minorder))
				throw new Exception("当前已经置顶了！");
			column.setOrder(minorder - 1);
			getBussinessService().updateColumn(column);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void up(IRequestCycle cycle, Object obj) {
		try {
			Columns column = (Columns) obj;
			Columns columnUp = getBussinessService().getUpDownOrder(
					getParent(), getPageGroup(), column, "up");

			Integer upOrder = 0;
			Integer currentOrder = column.getOrder();
			if (columnUp != null) {
				if (columnUp.getOrder() != null) {
					upOrder = columnUp.getOrder();
					column.setOrder(upOrder);
					columnUp.setOrder(currentOrder);
					getBussinessService().updateColumn(column);
					getBussinessService().updateColumn(columnUp);
				}
			}

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}

	}

	public void down(IRequestCycle cycle, Object obj) {
		try {
			Columns column = (Columns) obj;
			Columns columnDown = getBussinessService().getUpDownOrder(
					getParent(), getPageGroup(), column, "down");
			Integer downOrder = 0;
			Integer currentOrder = column.getOrder();
			if (columnDown != null) {
				if (columnDown.getOrder() != null) {
					downOrder = columnDown.getOrder();
					column.setOrder(downOrder);
					columnDown.setOrder(currentOrder);

					getBussinessService().updateColumn(column);
					getBussinessService().updateColumn(columnDown);
				}
			}

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void buttom(IRequestCycle cycle, Object obj) {
		try {
			Columns column = (Columns) obj;
			Integer minorder = getBussinessService().getMaxMinOrder(
					getParent(), getPageGroup(), "max");
			if (column.getOrder().equals(minorder))
				throw new Exception("当前已经置底了！");
			column.setOrder(minorder + 5);
			getBussinessService().updateColumn(column);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	/****************************************************
	 */

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

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("pagegroup",
				getPageGroup(), CompareType.Equal);
		hibernateExpressions.add(ex);

		if(!isShowParentColumn()){ //当执行栏目查询时，父栏目不生效
			if (getParent() == null) {
				HibernateExpression ex1 = new NullExpression("parent");
				hibernateExpressions.add(ex1);
			} else {
				HibernateExpression ex1 = new CompareExpression("parent",
						getParent(), CompareType.Equal);
				hibernateExpressions.add(ex1);
			}
		}
		if(getColName()!=null && !"".equals(getColName())){//name
			HibernateExpression ex1 = new CompareExpression("name",
					"%"+getColName()+"%", CompareType.Like);
			hibernateExpressions.add(ex1);
		}
		if(getColId()!=null && getColId()>0){//id
			HibernateExpression ex1 = new CompareExpression("id",
					getColId(), CompareType.Equal);
			hibernateExpressions.add(ex1);
		}
		if(getSortId()!=null && getSortId()>-1){//排序规则
			HibernateExpression ex1 = new CompareExpression("orderType",
					getSortId(), CompareType.Equal);
			hibernateExpressions.add(ex1);
		}
		if(getResourceStatus()!=null && getResourceStatus()>-1){
			HibernateExpression ex1 = new CompareExpression("status",
					getResourceStatus(), CompareType.Equal);
			hibernateExpressions.add(ex1);
		}
		HibernateExpression ex1 = new CompareExpression("status", //过滤下线的
				3, CompareType.NotEqual);
		hibernateExpressions.add(ex1);
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

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedObjects();
		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "您至少得选择一个";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					getBussinessService().auditColumn((Columns) obj,
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
		setSelectedObjects(new HashSet());
		getCallbackStack().popPreviousCallback();
	}

	public IPage showChildColumn(Columns column) {
		// 进行判断 如果没有子栏目 则导入到批价包列表页 如果批价包ID 不为空则表示 为一级栏目
		boolean haveChilds = false;
		ResourcePack pack = null;
		if (column.getPricepackId() == null || column.getPricepackId() == 0) {
			haveChilds = true;
		} else {
			pack = (ResourcePack) getResourcePackService().getEpack(
					column.getPricepackId());
			if (pack == null)
				haveChilds = true;
		}
		if (haveChilds) {
			this.setParent(column);
			this.setPageGroup(column.getPagegroup());
			return this;
		} else {
			ShowEpackReleationPage page = getShowEpackReleationPage();
			page.setPack(pack);
			page.setParent(column);
			return page;
		}
	}

	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;

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
						Columns o1 = (Columns) objRow;

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

	public ITableColumn getDisplayParent() {
		return new SimpleTableColumn("parentcoloumn", "上级栏目",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;

						if (o1.getParent() != null) {
							return o1.getParent().getName();
						} else {
							return "";
						}
					}

				}, false);

	}

	public ITableColumn getDisplayPack() {
		return new SimpleTableColumn("pricepackId", "资源包",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;

						if (o1.getPricepackId() != null) {
							ResourcePack rp = (ResourcePack) getResourcePackService()
									.getEpack(o1.getPricepackId());
							if (rp != null) {
								return rp.getId();
							} else {
								return "";
							}
						} else {
							return "";
						}

					}

				}, false);

	}

	public ITableColumn getDisplayColTemplate() {
		return new SimpleTableColumn("colOneTempId", "栏目模板",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;
						if (o1.getColOneTempId() != null) {
							Template template = getTemplateService()
									.getTemplate(o1.getColOneTempId());
							if (template != null)
								return template.getTitle();
							else
								return "";
						} else {
							return "";
						}

					}

				}, false);

	}

	public ITableColumn getDisplayResTemplate() {
		return new SimpleTableColumn("resOneTempId", "资源模板",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;
						if (o1.getResOneTempId() != null) {
							Template template = getTemplateService()
									.getTemplate(o1.getResOneTempId());
							if (template != null)
								return template.getTitle();
							else
								return "";
						} else {
							return "";
						}

					}

				}, false);

	}

	public ITableColumn getDisplayDelTemplate() {
		return new SimpleTableColumn("delOneTempId", "详情模板",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Columns o1 = (Columns) objRow;
						if (o1.getDelOneTempId() != null) {
							Template template = getTemplateService()
									.getTemplate(o1.getDelOneTempId());
							if (template != null) {
								if (template.getTitle() == null
										|| StringUtils.isEmpty(template
												.getTitle())) {
									return "";
								} else {
									return template.getTitle();
								}
							} else {
								return "";
							}
						} else {
							return "";
						}

					}

				}, false);

	}

	public ITableColumn getDisplayIcon() {
		return new SimpleTableColumn("icon", "栏目图标",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						return getPreAddress();
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
						if (o1.getCreateType() == 1)
							return "系统创建";
						else
							return "用户创建";
					}

				}, false);

	}

	public String getPreAddress() {
		Columns co = (Columns) getCurrentObject();
		String imgPath = "";
		if (co.getIcon() == null || StringUtils.isEmpty(co.getIcon())) {
			imgPath = "无";
		} else {
			String url = "";// "http://book.moluck.com/media/ebook/0/10000095/cover75.gif";//调用获取栏目图标的方法
			imgPath += "<img src='" + url
					+ "' width='40' height='40' align='absmiddle'/>";
		}
		return imgPath;
	}
	
	public IPage onView(Columns column) {
		String portalUrl = getSystemService().getVariables("portal_url")
				.getValue()
				+ "pg=c&gd="
				+ column.getPagegroup().getId() + "&cd="
				+ column.getId() ;//+ "&preview=1";
		String content = PageUtil.getURLStream(portalUrl,getServletRequest().getContextPath());
		logger.info("内容:" + content);
		Preview pre = getPreviewPage();
		pre.setTitle(column.getTitle());
		pre.setContent(content);
		return pre;
	}

	public String getPreViewPage() {
		Columns column = (Columns) getCurrentObject();
		String preUrl = getSystemService().getVariables("preview_url")
				.getValue();
		String portalUrl = getSystemService().getVariables("portal_url")
				.getValue();
		String url = preUrl
				+ URLEncoder.encode(portalUrl + "pg=c&gd="
						+ column.getPagegroup().getId() + "&cd="
						+ column.getId() );
		return "javascript:window.open('" + url
				+ "','','scrollbars=no,width=315,height=450')";
	}

	public String getUrlAddress() {
		String url = getSystemService().getVariables("portal_url").getValue();
		Columns column = (Columns) getCurrentObject();
		return "copy('" + url + "pg=c&gd=" + column.getPagegroup().getId()
				+ "&cd=" + column.getId() + "" + "');";
	}

	@InjectPage("bussiness/BatchChangeTemplatePage")
	public abstract BatchChangeTemplatePage getBatchChangeTemplatePage();

	public IPage changeTemplate(PageGroup pg) {
		String err = "";
		if (getSelectedObjects().size() == 0)
			err = "您至少得选择一个";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			return null;
		}
		getBatchChangeTemplatePage().setColumnsList(getSelectedObjects());
		getBatchChangeTemplatePage().setPageGroup(pg);
		setSelectedObjects(new HashSet());
		return getBatchChangeTemplatePage();
	}

	/***********************************
	 * 栏目下资源个数
	 */
	public Long getResourceCount(Columns column, Long number) { // 递归计算方法
		// int number=0;
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression name = new CompareExpression("parent", column,
				CompareType.Equal);
		expressions.add(name);
		List<Columns> listType = getBussinessService().findColumns(1,
				Integer.MAX_VALUE, "id", false, expressions);
		if (listType != null && listType.size() > 0) {
			for (int i = 0; i < listType.size(); i++) {
				Columns columnNext = listType.get(i);
				number = getResourceCount(columnNext, number);
			}
		} else {
			// 批价包ID
			if (column.getPricepackId() != null && column.getPricepackId() > 0) {
				ResourcePack pack = (ResourcePack) getResourcePackService()
						.getEpack(column.getPricepackId());
				if (pack != null) {
					Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
					HibernateExpression nameE = new CompareExpression("pack",
							pack, CompareType.Equal);
					hibernateExpressions.add(nameE);
					number += getResourcePackService()
							.getResourcePackReleationCount(hibernateExpressions);
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

						return getResourceCount(o1, 0L);
					}

				}, false);

	}

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	private String productMessage;
	private String columnMessage;
	private String resourceMessage;

	protected String creatCSV(Integer userId, Set<Columns> set) {
		try {
			// correspondence
			// 得到当前用户下载的路径路径 用户ID+文件名称
			// String csvName = "correspondence.csv";
			String fileDir = getSystemService().getVariables("userCVS_dir")
					.getValue();
			// String fileDir="C:\\var\\www\\html\\downCSV\\";
			File fielDir1 = new File(fileDir);
			if (!fielDir1.exists())
				fielDir1.mkdirs();
			File userFile = new File(fileDir + userId);
			if (!userFile.exists())
				userFile.mkdirs();

			fileDir += userId + File.separator + "corres";
			File file = new File(fileDir);
			if (file.exists())
				FileUtils.forceDelete(file); // 删除存在的那个文件夹
			file.mkdirs();
			String fileDirZip = file + ".zip";
			File fileZip = new File(fileDirZip);
			if (fileZip.exists())
				FileUtils.forceDelete(fileZip);// 删除存在的那个zip包

			Set<Columns> columnList = getColumnList(set);
			Set<Product> productSet = getProductByColumns(set); // 得到引用此栏目的产品集合
			if (productSet == null)
				throw new Exception("此页面组还没有被产品引用！");
			for (Product product : productSet) {
				String productMessage = ""; // 第一层 产品信息的拼接。
				if (product.getShowType() == 1)
					productMessage += "WAP,";
				if (product.getShowType() == 3)
					productMessage += "PAD,";
				if (product.getShowType() == 2)
					productMessage += "APP,";

				productMessage += product.getId() + ",";
				productMessage += product.getName() + ",";
				this.productMessage = productMessage;

				for (Columns column : columnList) {

					String fwName = fileDir + File.separator + product.getId()
							+ "_" + column.getId() + ".csv"; // 以产品ID+栏目ID号命名
					FileWriter fw = new FileWriter(fwName);
					fw
							.write("产品类型,产品ID,产品名称,栏目ID,栏目名称,批价包ID,批价包名称,批价包类型,资源ID,资源名称,资源类别,作者,计费信息,资源状态(0:商用;1:暂停)\r\n");

					String columnMessage = "";// 第三层 栏目信息的拼接

					columnMessage += column.getId() + ",";
					columnMessage += column.getName() + ",";
					Integer packId = column.getPricepackId();// 得到批价包ID
					ResourcePack pack = (ResourcePack) getResourcePackService()
							.getEpack(packId);
					if (pack == null)
						continue;
					columnMessage += packId + ",";
					columnMessage += pack.getName() + ",";

					if (pack.getType() == 1)
						columnMessage += "按次,";
					if (pack.getType() == 2)
						columnMessage += "包月(VIP),";
					if (pack.getType() == 3)
						columnMessage += "包月(内容控制),";
					if (pack.getType() == 4)
						columnMessage += "包月(常规),";
					if (pack.getType() == 5)
						columnMessage += "免费,";

					List<ResourcePackReleation> packReleations = // 得到批价资源关系对应数据
					getResourcePackService().findResourcePackReleationByPack(
							pack);
					this.columnMessage = columnMessage;
					int count = 0;
					if (packReleations != null && packReleations.size() > 0) {

						for (ResourcePackReleation rel : packReleations) {
							String resourceMessage = "";// 最后一层，资源信息的拼接
							String message = "";// 最后书写的
							String resourceId = rel.getResourceId();
							ResourceAll resource = getResourceService()
									.getResource(resourceId);
							if (resource == null)
								continue;
							resourceMessage += resourceId + ",";
							resourceMessage += resource.getName() + ",";
							if (resource instanceof Ebook)
								resourceMessage += "图书,";
							if (resource instanceof Magazine)
								resourceMessage += "杂志,";
							if (resource instanceof NewsPapers)
								resourceMessage += "报纸,";
							if (resource instanceof Comics)
								resourceMessage += "漫画,";
							// ----------得到作者-----------------
							Integer[] authorList = resource.getAuthorIds();// 得到作者数组
							String authorStr = "";
							if (authorList.length > 0) {
								for (int i = 0; i < authorList.length; i++) {
									ResourceAuthor author = getResourceService()
											.getResourceAuthorById(
													authorList[i]);
									authorStr += author.getPenName() + "/";
								}
							}
							if ("".equals(authorStr))
								resourceMessage += "作者丢失,";
							else {
								authorStr = authorStr.substring(0, authorStr
										.lastIndexOf("/"));
								resourceMessage += authorStr + ",";
							}
							// --------------处理计费---------------
							if (rel.getPack().getType() != 1)
								resourceMessage += "包月/免费,";
							else
								resourceMessage += rel.getFeeId() + ",";
							// ---------------处理状态--------------------------
							resourceMessage += resource.getStatus() + "\r\n";

							this.resourceMessage = resourceMessage;

							message = this.productMessage + this.columnMessage
									+ this.resourceMessage;
							count++;
							if (count > 3000)
								break;
							fw.write(message);
						}

					}
					fw.close();
				}
			}

			// ----------创建zip----------------------------------------------
			if (createZip(fileDir))
				return fileDirZip;
			else
				return fileDir;
			// ------------------------------------------------------
			// return fileDirZip;
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		return getSystemService().getVariables("userCVS_dir").getValue();
	}

	@InjectObject("service:tapestry.globals.HttpServletResponse")
	public abstract HttpServletResponse getServletResponse();

	public void downloadCSV(IRequestCycle cycle) {
		String err = "";
		if (getSelectedObjects().size() == 0)
			err = "您至少得选择一个";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);

		} else {

			// 生成CSV
			UserImpl user = (UserImpl) getUser();
			String fileDir = this.creatCSV(user.getId(), getSelectedObjects());
			setSelectedObjects(new HashSet());
			// path是指欲下载的文件的路径。
			String contextPath = getServletRequest().getContextPath();
			throw new RedirectException("http://"
					+ PageUtil.getDomainName(getServletRequest()
							.getRequestURL().toString()) + contextPath
					+ "/DownLoadPage.jsp?file=" + fileDir);

		}
	}

	// 根据集合columns得到属于他的所有子集
	public Set<Columns> getColumnList(Set<Columns> selectColumns) {

		for (Columns col : selectColumns) {
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();

			HibernateExpression ex = new CompareExpression("parent", col,
					CompareType.Like);
			expressions.add(ex);

			List<Columns> columnList = getBussinessService().findColumns(1,
					Integer.MAX_VALUE, "id", false, expressions); // 得到栏目列表
			if (columnList != null && columnList.size() > 0) {// 表明选择的这个栏目是有子集的
				selectColumns.addAll(columnList);
				selectColumns.remove(col);
				return getColumnList(selectColumns);
			}
		}
		return selectColumns;
	}

	// 根据column 得到 所属的页面组 /产品

	public Set<Product> getProductByColumns(Set<Columns> columns) {

		Set<PageGroup> pageGroupSet = new HashSet<PageGroup>();
		for (Columns column : columns) {
			if (column.getPagegroup() != null)
				pageGroupSet.add(column.getPagegroup());
		}

		if (pageGroupSet == null || pageGroupSet.size() < 1)
			return new HashSet<Product>();
		Set<Product> productSet = new HashSet<Product>();
		for (PageGroup pg : pageGroupSet) {
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();

			HibernateExpression ex = new CompareExpression("pgid", pg.getId(),
					CompareType.Like);
			expressions.add(ex);

			List<PackGroupProvinceRelation> ppr = getBussinessService()
					.findPackGroupProvinceRelations(1, Integer.MAX_VALUE, "id",
							false, expressions);
			if (ppr == null || ppr.size() < 1)
				continue;
			for (PackGroupProvinceRelation pgp : ppr) {
				String productId = pgp.getPid();
				Product product = getBussinessService().getProduct(productId);
				if (product != null)
					productSet.add(product);
			}
			// productList.addAll(ppr);
		}
		return productSet;
	}

	public boolean createZip(String fileDir) {
		// fileDir
		try {
			File d = new File(fileDir);
			if (!d.isDirectory())
				throw new IllegalArgumentException("Not a directory:  " + d);
			String[] entries = d.list();
			byte[] buffer = new byte[4096]; // Create a buffer for copying
			int bytesRead;
			File zipfile = new File(d + ".zip");
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipfile));

			for (int i = 0; i < entries.length; i++) {
				File f = new File(d, entries[i]);
				if (f.isDirectory())
					continue;
				FileInputStream in = new FileInputStream(f);
				ZipEntry entry = new ZipEntry(f.getName());
				out.putNextEntry(entry);
				while ((bytesRead = in.read(buffer)) != -1)
					out.write(buffer, 0, bytesRead);
				in.close();
			}
			out.close();
			if (zipfile.exists())
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean resIsRightFormat(String fileName) {
		String patt = "\\.(zip)$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}
}
