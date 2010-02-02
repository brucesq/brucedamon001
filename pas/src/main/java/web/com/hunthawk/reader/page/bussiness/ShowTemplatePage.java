/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
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
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.InExpression;
import com.hunthawk.framework.tapestry.RichSearchPage;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.Template;
import com.hunthawk.reader.domain.bussiness.TemplateCatalog;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.Preview;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "template" }, mode = Restrict.Mode.ROLE)
public abstract class ShowTemplatePage extends RichSearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("bussiness/EditTemplatePage")
	public abstract EditTemplatePage getEditTemplatePage();

	@InjectPage("Preview")
	public abstract Preview getPreviewPage();

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	public abstract Integer getTemplateId();

	public abstract void setTemplateId(Integer templateId);

	public abstract void setUrlName(String urlName);

	public abstract String getUrlName();

	public IPage onEdit(Template type) {
		getEditTemplatePage().setModel(type);
		return getEditTemplatePage();
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		Object obj = getCurrentObject();
		Set selecteds = getSelectedObjects();
		// 选择了用户
		if (bSelected) {
			selecteds.add(obj);
		} else {
			selecteds.remove(obj);
		}
		// persist value
		setSelectedObjects(selecteds);

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

			getTemplateService().deleteTemplate((Template) object);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		getCallbackStack().popPreviousCallback();
	}

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedObjects()) {
			delete(obj);
		}
		setSelectedObjects(new HashSet());
		getCallbackStack().popPreviousCallback();

	}

	public abstract Integer getTemplateCatalog();

	public abstract void setTemplateCatalog(Integer templateCatalog);

	public IPropertySelectionModel getCatalogList() {
		List<TemplateCatalog> list = getTemplateService()
				.getAllTemplateCatalog();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (TemplateCatalog catalog : list) {
			map.put(catalog.getName(), catalog.getId());
		}

		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}

	public abstract Integer getCreator();

	public abstract void setCreator(Integer id);

	public IPropertySelectionModel getCreatorList() {
		List<UserImpl> list = getUserService().getAll(UserImpl.class);

		Map<String, Integer> map = new HashMap<String, Integer>();
		for (UserImpl user : list) {
			map.put(user.getName(), user.getId());
		}
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map, true,
				"");
		return mapPro;
	}

	/**
	 * 更改目录
	 * 
	 */
	public IPage changeCatalog() {
		String err = "";
		if (getSelectedObjects().size() == 0) {
			err = "您至少要选择一个选项！";
		}
		if (getTemplateCatalog() == null)
			err = "请选择要更改的目录！";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			setSelectedObjects(new HashSet());
			return this;
		} else {
			List<Template> list = new ArrayList(getSelectedObjects());
			TemplateCatalog catalog = getTemplateService().getTemplateCatalog(
					getTemplateCatalog());
			for (Template template : list) {
				template.setTemplateCatalog(catalog);
				try {
					getTemplateService().updateTemplate(template);
					setSelectedObjects(new HashSet());
				} catch (Exception e) {
					getDelegate().setFormComponent(null);
					getDelegate().record(e.getMessage(), null);
					setSelectedObjects(new HashSet());
				}
			}
			return this;
		}
	}

	/**
	 * 模版复制
	 * 
	 * @return
	 */
	public IPage templateCopy() {
		String err = "";
		if (getSelectedObjects().size() == 0) {
			err = "您至少要选择一个选项";
		}
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			setSelectedObjects(new HashSet());
			return this;
		} else {
			// 进行复制操作 然后返回
			List<PageGroup> list = new ArrayList(getSelectedObjects());
			for (Iterator it = list.iterator(); it.hasNext();) {
				Template target = (Template) it.next();
				target.setTitle("复件    " + target.getTitle());
				target.setCreateTime(new Date());
				target.setModifyTime(new Date());
				target.setCreateorId(getUser().getId());
				target.setMotifierId(getUser().getId());
				target.setStatus(0);
				try {
					getTemplateService().addTemplate(target);
				} catch (Exception e) {
					getDelegate().setFormComponent(null);
					getDelegate().record("模版复制失败,请联系管理员！", null);
					setSelectedObjects(new HashSet());
					return this;
				}
			}
			setSelectedObjects(new HashSet());
		}
		return this;
	}

	public void onChangeStatus(IRequestCycle cycle) {
		Set setSelectedObjects = getSelectedObjects();

		int setSize = setSelectedObjects.size();
		String err = "";
		ValidationDelegate delegate = getDelegate();
		if (setSize == 0) {
			err = "您至少得选择一个模板";
		} else {
			for (Object obj : setSelectedObjects) {
				try {
					getTemplateService().auditTemplate((Template) obj,
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

	public abstract int getStatusValue();

	public abstract void setStatusValue(int statusValue);

	public void search() {
		if (getTemplateId() != null && getTemplateId() > 0) {
			Template template = getTemplateService().getTemplate(
					getTemplateId());
			if (template != null)
				setTemplateType(template.getTemplateType().getId());
		}
	}

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getStatus();

	public abstract void setStatus(Integer status);

	public abstract void setUrl(String url);

	public abstract String getUrl();

	// private Integer parseUrl(String url){
	// int index = url.indexOf("?");
	// if(index >= 0){
	// url = url.substring(index+1);
	// }
	// url = url.replaceAll("&amp;", "&");
	// String[] paramStrs = url.split("\\&");
	// Map<String,String> params = new HashMap<String,String>();
	// for(String param : paramStrs){
	// index = param.indexOf("=");
	// if(index > 0){
	// String key = param.substring(0,index);
	// String value = param.substring(index+1);
	// params.put(key, value);
	// }
	// }
	// String td = params.get(TEMPLATE_ID);
	// if(StringUtils.isNumeric(td)){
	// return Integer.parseInt(td);
	// }else{
	// String page = params.get(PAGE);
	// if(PAGE_DETAIL.equals(page)){
	// String columnId = params.get(COLUMN_ID);
	// if(StringUtils.isNumeric(columnId)){
	//					
	// }else{
	// return 0;
	// }
	// }
	// }
	// return 0;
	// }

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("status");
		nameC.setValue(getStatus());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("templateCatalog");
		nameC.setValue(getTemplateCatalog());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("templateType");
		nameC.setValue(getTemplateType());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("creator");
		nameC.setValue(getCreator());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("showType");
		nameC.setValue(getShowType());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("templateId");
		nameC.setValue(getTemplateId());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("urlName");
		nameC.setValue(getUrlName());
		searchConditions.add(nameC);

		return searchConditions;
	}

	public abstract Integer getTemplateType();

	public abstract void setTemplateType(Integer templateType);

	/**
	 * 按模版类型查询
	 * 
	 * @param templateType
	 * @return
	 */
	public IPage searchByType(String templateType) {
		if (templateType == null) {
			templateType = String.valueOf(TemplateType.FIRST_PAGE);
		}
		this.setTemplateType(Integer.parseInt(templateType));
		return this;
	}

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (getTemplateByUrl() != null && getTemplateByUrl().size() > 0) {
			HibernateExpression nameE = new InExpression("id",
					getTemplateByUrl());
			hibernateExpressions.add(nameE);
		} else {
			if (!ParameterCheck.isNullOrEmpty(getName())) {
				HibernateExpression nameE = new CompareExpression("title", "%"
						+ getName() + "%", CompareType.Like);
				hibernateExpressions.add(nameE);
			}
			if (getStatus() != null && getStatus() >= 0) {
				HibernateExpression statusE = new CompareExpression("status",
						getStatus(), CompareType.Equal);
				hibernateExpressions.add(statusE);
			}

			if (getTemplateType() != null && getTemplateType() != 0) {
				HibernateExpression typeE = new CompareExpression(
						"templateType",
						getTemplateService()
								.getTemplateType(
										(getTemplateType() == null ? TemplateType.FIRST_PAGE
												: getTemplateType())),
						CompareType.Equal);
				hibernateExpressions.add(typeE);
			}
			if (getTemplateCatalog() != null && !getTemplateCatalog().equals(0)) {
				HibernateExpression catalogE = new CompareExpression(
						"templateCatalog", getTemplateService()
								.getTemplateCatalog(getTemplateCatalog()),
						CompareType.Equal);
				hibernateExpressions.add(catalogE);
			}

			if (getTemplateId() != null && getTemplateId() > 0) {
				HibernateExpression templateId = new CompareExpression("id",
						getTemplateId(), CompareType.Equal);
				hibernateExpressions.add(templateId);
			}
			if (getShowType() != null && getShowType() > 0) {
				HibernateExpression templateId = new CompareExpression(
						"showType", getShowType(), CompareType.Equal);
				hibernateExpressions.add(templateId);
			}
			if (getCreator() != null && getCreator() > 0) {
				HibernateExpression createorId = new CompareExpression(
						"createorId", getCreator(), CompareType.Equal);
				hibernateExpressions.add(createorId);
			}
		}

		return hibernateExpressions;
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			int count = 0;

			public int getRowCount() {
				if (count > 0) {
					return count;
				}
				count = getTemplateService().getTemplateResultCount(
						getSearchExpressions()).intValue();
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getTemplateService().findTemplate(pageNo, nPageSize,
						"id", false, getSearchExpressions()).iterator();
			}
		};
	}

	public ITableColumn getDisplayStatus() {
		return new SimpleTableColumn("status", "状态",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Template p1 = (Template) objRow;
						return Constants.STATUS[p1.getStatus()];

					}

				}, false);

	}

	public ITableColumn getOutputType() {
		return new SimpleTableColumn("outputType", "输出类型",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Template p1 = (Template) objRow;
						if (p1.getShowType() == 1)
							return "WAP";
						else if (p1.getShowType() == 2)
							return "APP";
						else if (p1.getShowType() == 3)
							return "PAD";
						else
							return "";
					}

				}, false);

	}

	public IPage onView(Template template) {
		String portalUrl = getSystemService().getVariables("portal_url")
				.getValue()
				+ "td=" + template.getId() + "&preview=1";
		String content = PageUtil.getURLStream(portalUrl,getServletRequest().getContextPath());
		logger.info("内容:" + content);
		Preview pre = getPreviewPage();
		pre.setTitle(template.getTitle());
		pre.setContent(content);
		return pre;
	}

	// public String getPreViewPage() {
	// Template template = (Template) getCurrentObject();
	// String preUrl = getSystemService().getVariables("preview_url")
	// .getValue();
	// String portalUrl = getSystemService().getVariables("portal_url")
	// .getValue()+ "td=" + template.getId() + "&preview=1";
	//		
	// String url = preUrl
	// + URLEncoder.encode(portalUrl + "td=" + template.getId()
	// + "&preview=1");
	// return "javascript:window.open('" + url
	// + "','','scrollbars=no,width=315,height=450')";
	// }

	public IPropertySelectionModel getStatusList() {
		Map map = new HashMap();
		int i = 0;
		for (String status : Constants.STATUS) {
			map.put(status, i++);
		}
		return new MapPropertySelectModel(map, true, "");
	}

	public abstract Integer getShowType();

	public abstract void setShowType(Integer showType);

	public IPropertySelectionModel getShowTypeList() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("全部", 0);
		map.put("WAP", 1);
		map.put("PAD", 3);
		map.put("APP", 2);
		return new MapPropertySelectModel(map);
	}

	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						Template o1 = (Template) objRow;

						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, o1.getCreateorId());
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
						Template o1 = (Template) objRow;

						if (o1.getMotifierId() != null) {
							UserImpl user = (UserImpl) getUserService()
									.getObject(UserImpl.class,
											o1.getMotifierId());
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

	/**
	 * 解析url获取模版ID
	 * 
	 * @return 模版ID的集合
	 * @author penglei
	 */
	private List<Integer> getTemplateByUrl() {
		List<Integer> list = null;
		if (StringUtils.isNotEmpty(getUrlName())) {
			String[] urls = getUrlName().split("\\?");
			if (urls.length == 2 && StringUtils.isNotEmpty(urls[1])) {
				String[] temps = urls[1].split("\\&");
				if (temps != null && temps.length != 0) {
					String pageParam = null;
					String columnID = null;
					String gd = null;
					String td = null;
					for (String temp : temps) {
						String[] param = temp.split("=");
						if (param[0].equalsIgnoreCase("pg")) {
							pageParam = param[1];
							continue;
						} else if (param[0].equalsIgnoreCase("cd")) {
							columnID = param[1];
							continue;
						} else if (param[0].equalsIgnoreCase("gd")) {
							gd = param[1];
							continue;
						} else if (param[0].equalsIgnoreCase("td")) {// 如果有td默认采用td
							td = param[1];
							if (StringUtils.isNotEmpty(td)) {
								list = new ArrayList<Integer>();
								list.add(Integer.parseInt(td));
								return list;
							}

						}
					}
					Integer one = null;
					Integer second = null;
					Integer third = null;
					if (StringUtils.isNotEmpty(pageParam)
							&& StringUtils.isNotEmpty(columnID)) {

						Columns columns = (Columns) getBussinessService()
								.getObjectById(Columns.class,
										Integer.parseInt(columnID));
						if (columns != null) {
							list = new ArrayList<Integer>();

							if (pageParam.equalsIgnoreCase("d")) {
								one = columns.getDelOneTempId();
								second = columns.getDelSecondTempId();
								third = columns.getColThirdTempId();

							} else if (pageParam.equalsIgnoreCase("c")) {
								one = columns.getColOneTempId();
								second = columns.getColSecondTempId();
								third = columns.getColThirdTempId();
							} else if (pageParam.equalsIgnoreCase("r")) {
								one = columns.getResOneTempId();
								second = columns.getResSecondTempId();
								third = columns.getResThirdTempId();
							}

						}
					} else if (StringUtils.isNotEmpty(pageParam)
							&& StringUtils.isNotEmpty(gd)) {
						list = new ArrayList<Integer>();
						if (pageParam.equalsIgnoreCase("p")) {
							PageGroup pg = getBussinessService().getPageGroup(
									Integer.parseInt(gd));
							one = pg.getPkOneTempId();
							second = pg.getPkSecondTempId();
							third = pg.getPkThirdTempId();
						}
					}
					if (one != null && one != 0) {
						list.add(one);
					}
					if (second != null && second != 0) {
						list.add(second);
					}
					if (third != null && third != 0) {
						list.add(third);
					}
				}

			}
		}

		return list;
	}

	// public static final String TEMPLATE_ID = "td";
	// // 产品ID
	// public static final String PRODUCT_ID = "pd";
	// // 页面组ID
	// public static final String PAGEGROUP_ID = "gd";
	// // 栏目ID
	// public static final String COLUMN_ID = "cd";
	//	
	// public static final String PAGE = "pg";
	//	
	// // 首页
	// public static final String PAGE_PRODUCT = "p";
	// // 栏目页
	// public static final String PAGE_COLUMN = "c";
	// // 资源页
	// public static final String PAGE_RESOURCE = "r";
	// // 下载页
	// public static final String PAGE_DETAIL = "d";
}
