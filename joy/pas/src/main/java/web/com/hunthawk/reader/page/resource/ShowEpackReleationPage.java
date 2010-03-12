package com.hunthawk.reader.page.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.FeeXLSData;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.bussiness.ShowColumnPage;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

@Restrict(roles = { "packreleation" }, mode = Restrict.Mode.ROLE)
public abstract class ShowEpackReleationPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	// @InjectObject("spring:resourcePackService")
	// public abstract ResourcePackService getResourcePackService();

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();

	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectPage("resource/EditEpackReleationPage")
	public abstract EditEpackReleationPage getEditEpackReleationPage();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getShowEpackReleationPage();

	@InjectPage("resource/BatchAddEpackReleationPage")
	public abstract BatchAddEpackReleationPage getBatchAddEpackReleationPage();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract void setSortId(Integer sort);

	public abstract Integer getSortId();

	public abstract void setAuthorId(Integer authorId);

	public abstract Integer getAuthorId();

	public abstract String getResourceId();

	public abstract void setResourceId(String resourceId);

	public abstract Integer getPackresourceId();

	public abstract void setPackresourceId(Integer userid);

	public abstract Integer getResourceStatus();

	public abstract void setResourceStatus(Integer resourceStatus);

	public abstract Provider getCurrentSp();

	public abstract void setCurrentSp(Provider provider);

	public abstract Fee getCurrentFee();

	public abstract void setCurrentFee(Fee fee);

	public abstract void setResourceType(Integer type);

	public abstract Integer getResourceType();
	
	@Persist("session")
	public abstract ResourcePack getPack();

	public abstract void setPack(ResourcePack pack);

	protected void delete(Object object) {
		try {
			ResourcePackReleation rpr = (ResourcePackReleation) object;
			getShowEpackReleationPage().setPack(rpr.getPack());
			getResourcePackService().delResourcePackReleation(
					(ResourcePackReleation) object);

		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedPackReleations();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedPackReleations(Set set);

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		ResourcePackReleation rp = (ResourcePackReleation) getCurrentPackReleation();
		Set selectedPack = getSelectedPackReleations();
		// 选择了页面组
		if (bSelected) {
			selectedPack.add(rp);
		} else {
			selectedPack.remove(rp);
		}
		// persist value
		setSelectedPackReleations(selectedPack);

	}

	public boolean getCheckboxSelected() {
		return getSelectedPackReleations().contains(getCurrentPackReleation());
	}

	public abstract Object getCurrentPackReleation();

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedPackReleations()) {
			delete(obj);
		}
		setSelectedPackReleations(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);

	}

	// public IPage searchByHQL(Integer sortid) {
	// this.setAuthorId(getAuthorId());
	// this.setName(getName());
	// this.setSortId(sortid);
	// return this;
	// }

	public void search() {
		// if (getSortId() != null) {
		// searchByHQL(getSortId());
		// }
	}

	public abstract int getResourceCount();

	public abstract void setResourceCount(int resourceCount);

	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (getPack() != null) {
			HibernateExpression useridE = new CompareExpression("pack",
					getPack(), CompareType.Equal);
			hibernateExpressions.add(useridE);
		}
		if (!ParameterCheck.isNullOrEmpty(getResourceId())) {
			HibernateExpression nameE = new CompareExpression("resourceId",
					getResourceId(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}

		if (getPackresourceId() != null && getPackresourceId() > 0) {
			HibernateExpression nameE = new CompareExpression("id",
					getPackresourceId(), CompareType.Equal);
			hibernateExpressions.add(nameE);
		}

		// if (getAuthorId() != null && getAuthorId() != 0) {
		// List listRes = new
		// ArrayList(getResourcePackService().findResourceByHQL(getPack().getId(),getAuthorId(),0));
		// if(listRes.size() > 0)
		// {
		// InExpression authorlistE = new InExpression("resourceId",listRes);
		// hibernateExpressions.add(authorlistE);
		// }
		// }

		return hibernateExpressions;
	}

	public IPropertySelectionModel getAuthorList() {
		Long t1 = System.currentTimeMillis();
		List<ResourceAuthor> resourceauthor = getResourceService()
				.findResourceAuthorBymemcached("resourceAll",1, Integer.MAX_VALUE, "initialLetter",
						true, new ArrayList<HibernateExpression>());
		Map<String, Integer> authors = new OrderedMap<String, Integer>();
		authors.put("全部", 0);
		for (ResourceAuthor author : resourceauthor) {
			authors.put(author.getPenName(), author.getId());
		}
		Long t2 = System.currentTimeMillis();
		System.out.println("----作者时间---"+(t2-t1));
		return new MapPropertySelectModel(authors, false, "全部");
	}

	public IPropertySelectionModel getSortList() {

		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		Integer type = 1;
		if(getResourceType()!=null)
			type = getResourceType();
		HibernateExpression typeF = new CompareExpression("showType", type,
				CompareType.Equal);
		hibernateExpressions.add(typeF);
		List<ResourceType> sortTypes = getResourceService().findResourceTypeBymemcached(type+"",
				1, Integer.MAX_VALUE, "id", false, hibernateExpressions);
		Map<String, Integer> sorts = new OrderedMap<String, Integer>();
		sorts.put("全部", 0);
		for (ResourceType sort : sortTypes) {
			sorts.put(sort.getName(), sort.getId());
		}
		return new MapPropertySelectModel(sorts, false, "全部");
	}

	public IPropertySelectionModel getStatusList() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("全部", -1);
		map.put("商用", 0);
		map.put("暂停", 1);
		return new MapPropertySelectModel(map);
	}
	
	/*
	 * 添加 分类反选，以及排序
	 * yuzs 2009-11-11
	 */
	public abstract Integer getTof();
	public abstract void setTof(Integer tof);
	
	public abstract Integer getOrder();
	public abstract void setOrder(Integer id);
	
	public IPropertySelectionModel getToflist(){
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("否", 0);
		map.put("是", 1);
		return new MapPropertySelectModel(map);		
	}
	
	public IPropertySelectionModel getOrderlist(){
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("排序号", 1);
		map.put("ID降序", 2);
		map.put("PV总排行", 10);
		map.put("PV月排行", 11);
		map.put("订购量总排行", 20);
		map.put("订购量月排行", 21);
		return new MapPropertySelectModel(map);		
	}
	public IPropertySelectionModel getResourceTypeList() {
		Map<String, Integer> types = new TreeMap<String, Integer>();
		types.put("图书", ResourceType.TYPE_BOOK);
		types.put("漫画", ResourceType.TYPE_COMICS);
		types.put("报纸", ResourceType.TYPE_NEWSPAPERS);
		types.put("杂志", ResourceType.TYPE_MAGAZINE);
		types.put("视频", ResourceType.TYPE_VIDEO);
		types.put("资讯", ResourceType.TYPE_INFO);

		return new MapPropertySelectModel(types, false, "");
	}

	//------------end---------------
	public IBasicTableModel getTableModel() {
		// System.out.println(getPack());
		return new IBasicTableModel() {
			public int getRowCount() {
				int count = 0;
				boolean isTof = false;
				if(getTof()==null)
					isTof = false;
				else if(getTof()==0)
					isTof = false;
				else if(getTof()==1)
					isTof = true;
				else
					isTof = false;
				count = getResourcePackService()
						.getResourcePackReleationResultCountByHQL(getResourceType(),	
								isTof,
								getName(),
								getPack(),
								getAuthorId(),
								getSortId(),
								getResourceStatus(),
								getCurrentSp() == null ? null : getCurrentSp()
										.getId(),
								getCurrentFee() == null ? null
										: getCurrentFee().getId()).intValue();
				setResourceCount(count);
				return count;
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				boolean isTof = false;
				if(getTof()==null)
					isTof = false;
				else if(getTof()==0)
					isTof = false;
				else if(getTof()==1)
					isTof = true;
				else
					isTof = false;
				return getResourcePackService().findResourcePackReleationByHQL(getResourceType(),	
						isTof,
						getName(),
						getPack(),
						getAuthorId(),
						getSortId(),
						getResourceStatus(),
						getCurrentSp() == null ? null : getCurrentSp().getId(),
						getCurrentFee() == null ? null : getCurrentFee()
								.getId(), pageNo, nPageSize,getOrder()).iterator();
			}
		};
	}

	public IPage editReleation(ResourcePack pack) {
		// EditEpackReleationPage page = getEditEpackReleationPage();
		// page.setPack(pack);
		// return page;
		BatchAddEpackReleationPage page = getBatchAddEpackReleationPage();
		page.setPack(pack);
		page.clearSelectedEbook();
		return page;
	}

	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage getShowEbookPage();

	public IPage showResourceAll(ResourcePack pack) {
		ShowEbookPage page = getShowEbookPage();
		page.setPack(pack);
		page.setAuthorId(getAuthorId());
		return page;
	}

	public abstract Columns getParent();

	public abstract void setParent(Columns parent);

	@InjectPage("bussiness/ShowColumnPage")
	public abstract ShowColumnPage getShowColumnPage();

	public IPage showColumnsAll(Columns parent) {
		ShowColumnPage page = getShowColumnPage();
		// page.setParent(parent);
		page.setPageGroup(parent.getPagegroup());
		return page;
	}

	@InjectPage("resource/BatchChangePackFeePage")
	public abstract BatchChangePackFeePage getBatchChangePackFeePage();

	public IPage changePackFee(ResourcePack pack) {
		String err = "";
		BatchChangePackFeePage page = getBatchChangePackFeePage();
		if (getSelectedPackReleations().size() == 0)
			err = "您至少得选择一个";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			return null;
		} else {
			page.setPackReleations(getSelectedPackReleations());
			page.setPack(pack);
			setSelectedPackReleations(new HashSet());
			return page;
		}
	}

	public boolean isShowChangeFee() {
		if (getPack() == null)
			return false;
		else {
			ResourcePack pack = getPack();
			if (pack.getType() == 1 || pack.getType() == 2) // vip和 按次
				return true;
			else
				return false;
		}
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("pack");
		nameC.setValue(getPack());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("sortId");
		nameC.setValue(getSortId());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("authorId");
		nameC.setValue(getAuthorId());
		searchConditions.add(nameC);

		return searchConditions;
	}

	public IPage onEdit(ResourcePackReleation pack) {
		getEditEpackReleationPage().setModel(pack);
		getEditEpackReleationPage().setPack(pack.getPack());
		return getEditEpackReleationPage();
	}

	public ITableColumn getFeename() {
		return new SimpleTableColumn("feename", "计费信息",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						if (rp.getFeeId() == null || "".equals(rp.getFeeId()))
							return "无计费信息";
						else {
							Fee fee = getFeeService().getFee(rp.getFeeId());
							if (fee == null)
								return "不存在的计费信息";
							return fee.getName();
						}
					}

				}, false);

	}

	public ITableColumn getResourceName() {
		return new SimpleTableColumn("resourcename", "资源名称",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						if (rp.getResourceId() == null
								|| "".equals(rp.getResourceId()))
							return "资源丢失";
						else {
							
							//Long t3 = System.currentTimeMillis();
							ResourceAll ra = getResourceService().getResource(
									rp.getResourceId());
							//Long t4 = System.currentTimeMillis();
							//System.out.println("---资源取出耗时----"+(t4-t3));
							/*if(ra==null){ //再从数据库中取得
							  ra = getResourceService().getResourceFromMemcached(
										rp.getResourceId());
							}*/
							if (ra == null)
								return "资源丢失";
							return ra.getName();
						}
					}

				}, false);
	}

	public ITableColumn getStatus() {
		return new SimpleTableColumn("status", "状态",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						Integer statust = null;
						if (rp.getResourceId() == null
								|| "".equals(rp.getResourceId()))
							return "资源丢失";
						else {
							//Long t1 = System.currentTimeMillis();
							ResourceAll ra = getResourceService().getResource(
									rp.getResourceId());
							//Long t2 = System.currentTimeMillis();
							//System.out.println("---内存取出耗时----"+(t2-t1));
							/*if (ra == null){//内存取得失败时 在从数据库中取一次
								  ra = getResourceService().getResourceFromMemcached(
											rp.getResourceId());
								  if(ra==null)
									  statust = -1;
								  else
									  statust = ra.getStatus(); 
							}else{
								 statust = ra.getStatus(); 
							}*/
							if(ra==null)
							   statust = -1;
							else
							   statust = ra.getStatus(); 
							if(statust == -1)
								return "丢失状态";
							if (statust == 0)
								return "商用";
							if (statust == 1)
								return "暂停";
							else
								return "下线";
							
						}
					}

				}, false);
	}

	public ITableColumn getSort() {
		return new SimpleTableColumn("sort", "类别", new ITableColumnEvaluator() {

			private static final long serialVersionUID = 625300745851970L;

			public Object getColumnValue(ITableColumn objColumn, Object objRow) {
				ResourcePackReleation rp = (ResourcePackReleation) objRow;
				StringBuffer type = new StringBuffer();
				// 根据资源ID查询资源子类型
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("rid", rp
						.getResourceId(), CompareType.Equal);
				expressions.add(ex);
				List<ResourceResType> list = getResourceService()
						.findResourceResTypeBy(1, Integer.MAX_VALUE, "rid",
								false, expressions);
				for (Iterator it = list.iterator(); it.hasNext();) {
					ResourceResType rrt = (ResourceResType) it.next();
					ResourceType rt = getResourceService().getResourceType(
							rrt.getResTypeId());
					type.append(rt.getName());
					type.append(";");
				}
				// 去掉最后一个分号
				if (list.size() > 0) {
					return type.toString().substring(0, (type.length() - 1));
				} else {
					return "";
				}
			}

		}, false);

	}

	public ITableColumn getAuthorname() {
		return new SimpleTableColumn("authorname", "作者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						ResourceAll ra = null;
						ra = getResourceService().getResource(
								rp.getResourceId());
						if (ra == null)
							return "资源丢失";

						String author = ";";
						int i = 0;
						Integer[] authorIds = ra.getAuthorIds();
						for (Integer authorId : authorIds) {
							ResourceAuthor rAuthor = getResourceService()
									.getResourceAuthorById(authorId);
							if (rAuthor != null) {
								author += rAuthor.getPenName();
								if (++i < authorIds.length) {
									author += ";";
								}
							}
						}
						return author.substring(1);

					}

				}, false);

	}

	@InjectPage("resource/DownloadUebPage")
	public abstract DownloadUebPage getDownloadUebPage();

	public IPage onDownload(Object obj) {
		getDownloadUebPage().setCurrentObject(obj);
		return getDownloadUebPage();
	}

	/****
	 * 获取所有批价包对象
	 * 
	 * 
	 * @author penglei
	 * @return
	 */
	public IPropertySelectionModel getCpspList() {
		List<ResourceAll> resourceList = this.getResourcePackService()
				.findResourceByHQL(getPack().getId(), getAuthorId(),
						getSortId());
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

	public IPropertySelectionModel getFeeList() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression expression = new CompareExpression("status", 1,
				CompareType.Equal);
		hibernateExpressions.add(expression);

		if (getCurrentSp() == null) {
			expression = new NullExpression("provider");
			hibernateExpressions.add(expression);
		} else if (getPack().getType().intValue() == 5) { // 免费则不显示下拉框
			expression = new NullExpression("provider");
			hibernateExpressions.add(expression);
		} else {
			expression = new CompareExpression("provider", getCurrentSp(),
					CompareType.Equal);
			hibernateExpressions.add(expression);
		}

		List<Fee> fees = getFeeService().findFee(1, Integer.MAX_VALUE, "id",
				true, hibernateExpressions);
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				fees, Fee.class, "getName", "getId", true, "");
		return model;
	}
	
	
	
	protected String creatCSV(Integer userId,
			Set<ResourcePackReleation> set) {
		FileOutputStream out;
		try {
			String fileDir = getSystemService().getVariables("userCVS_dir").getValue();
			File fielDir1 = new File(fileDir);
			if (!fielDir1.exists())
				fielDir1.mkdirs();
			File userFile = new File(fileDir + userId);
			if (!userFile.exists())
				userFile.mkdirs();
		
			if(set==null || set.size()==0)
				return "";
			fileDir += userId + File.separator + "product_info.xls";
			File file = new File(fileDir);
			if (file.exists())
				file.delete();
			System.out.println("---1----"+file);
		    Map<String, FeeXLSData> dataMap = 
		    	new HashMap<String, FeeXLSData>();
		    List<FeeXLSData> dataList = 
		    	new ArrayList<FeeXLSData>();
		    List<String[]> feeDataList = 
		    	new ArrayList<String[]>();
			out = new FileOutputStream(file);
		    //out = new FileOutputStream("d:\\product_info.xls");
				// create a new workbook
				Workbook wb = new HSSFWorkbook();
				// create a new sheet
				Sheet s = wb.createSheet("产品信息");
				Row r = null;
				// declare a cell object reference
				Cell c = null;
				 CreationHelper createHelper = wb.getCreationHelper();
				CellStyle cs = wb.createCellStyle();
				//DataFormat df = wb.createDataFormat();
				// create 2 fonts objects
				Font f = wb.createFont();
				Font f2 = wb.createFont();
		
				//set font 1 to 12 point type
				f.setFontHeightInPoints((short) 12);
				//arial is the default font
				f.setBoldweight(Font.BOLDWEIGHT_BOLD);
				
				//set cell stlye
				cs.setFont(f);
				//set the cell format 
				//cs.setDataFormat(df.getFormat("#,##0.0"));
				cs.setWrapText(true); 
				//wb.setSheetName(0, "产品信息");
				String ids="";
				String pro_no = getSystemService().getVariables("iphone_spid").getValue(); //取出sp标识\0015
				String resType="";
				String packId = "";
				String packids = "";//前面补1的批价包ID
				String packReleationId = "";
				String packReleationIds="";//前面补零的对应关系ID
				String showURL = getSystemService().getVariables("iphone_resource_url").getValue(); //取出预览链接
				String showHomeURL =  getSystemService().getVariables("iphone_home_url").getValue(); //取出首页预览链接
				String iphone_fee_channel = getSystemService().getVariables("iphone_fee_channel").getValue(); // IPHONE频道包月计费ID
				String iphone_fee_column = getSystemService().getVariables("iphone_fee_column").getValue(); // IPHONE栏目包月计费ID
				//String iphone_month_packs = getSystemService().getVariables("iphone_month_packs").getValue(); //iphone收费批价包ID列表，多个以;号隔开
				String iphone_column_url = getSystemService().getVariables("iphone_column_url").getValue(); //IPHONE栏目申报地址
				String iphone_column_pack_rel = getSystemService().getVariables("iphone_column_pack_rel").getValue(); //IPHONE栏目和批价包对应关系
				/*
				 * yuzs
				 * 200-11-17
				 * */
				String iphone_fee_channel_magazine = getSystemService().getVariables("iphone_fee_channel_magazine").getValue(); // IPHONE杂志频道包月计费ID
				String iphone_fee_column_magazine = getSystemService().getVariables("iphone_fee_column_magazine").getValue(); // IPHONE杂志栏目包月计费ID
				
				String iphone_fee_channel_comics = getSystemService().getVariables("iphone_fee_channel_comics").getValue(); // IPHONE漫画频道包月计费ID
				String iphone_fee_column_comics = getSystemService().getVariables("iphone_fee_column_comics").getValue(); // IPHONE，漫画栏目包月计费ID
				
				String iphone_fee_channel_newspapers = getSystemService().getVariables("iphone_fee_channel_newspapers").getValue(); // IPHONE报纸频道包月计费ID
				String iphone_fee_column_newspapers = getSystemService().getVariables("iphone_fee_column_newspapers").getValue(); // IPHONE报纸栏目包月计费ID
				
				//----------------
				//http://book.moluck.com/pps/s.do?pg=r&pd=250000002&gd=4069&ad=001&cd=2868&fd={packid}&nd={releationid}&rd={resourceid}&fc=25000000&pn=1
				int rowCount=0;
				//---------------创建产品信息 表头--------------------------
				String[] feeHeadData = {"产品编号-字符长度100，层级关系通过编号体现,编号四位为一级","金额-以分为单位","供应商-各SPNO","产品名称-长度50","产品资源路径-产品查看地址"};
				r = s.createRow(0);
				for(int i=0;i<feeHeadData.length;i++){
					c = r.createCell((short)i);
					c.setCellValue(feeHeadData[i]);
					c.setCellStyle(cs);
				}
				 if (rowCount == 0)
				    {
				        r.setHeight((short) 0x430);
				    }
				 //---------------------------------------

				 ResourcePack pack  =  getPack();
				 packId = pack.getId().toString();
					for(ResourcePackReleation packReleation : set){
						if(packReleation==null)
							continue;
						System.out.println("---2----"+packReleation.getResourceId());
						String feeCode = packReleation.getFeeId();
						 Fee fee = getFeeService().getFee(feeCode);
						if(fee == null)
							continue;
						String feeNumStr = fee.getCode();
						int feeIntNum = changeCode(feeNumStr);
						if(feeIntNum==0)
							continue;
						
						
						packReleationId = packReleation.getId().toString();
						//packReleationIds = changeReleId(packReleationId,"");//修改后的对应关系ID
						packids = changPackId(packId);//修补后的批价包ID
						String resourceId = packReleation.getResourceId();			
						ResourceAll ra = getResourceService().getResource(resourceId);
						//1图书，2报纸，3杂志，4漫画，5铃声，6视频
						String resourceName="";
						if(resourceId.startsWith("1")){
							resType="0001";
							packReleationIds = changeReleId(packReleationId,"1");//修改后的对应关系ID
							resourceName += "图书-"+ra.getName();
						}if(resourceId.startsWith("2")){
							resType="0002";
							packReleationIds = changeReleId(packReleationId,"2");//修改后的对应关系ID
							resourceName += "报纸-"+ra.getName();
						}if(resourceId.startsWith("3")){
							resType="0003";
							packReleationIds = changeReleId(packReleationId,"3");//修改后的对应关系ID
							resourceName += "杂志-"+ra.getName();
						}if(resourceId.startsWith("4")){
							resType="0004";
							packReleationIds = changeReleId(packReleationId,"4");//修改后的对应关系ID
							resourceName += "漫画-"+ra.getName();
						}if(resourceId.startsWith("5")){
							resType="0005";
							packReleationIds = changeReleId(packReleationId,"5");//修改后的对应关系ID
							resourceName += "铃声-"+ra.getName();
						}if(resourceId.startsWith("6")){
							resType="0006";
							packReleationIds = changeReleId(packReleationId,"6");//修改后的对应关系ID
							resourceName += "视频-"+ra.getName();
						}
						
						//---------------新修改--------------------
						//System.out.println("---3----");
						FeeXLSData data = new FeeXLSData();
						ids = 	pro_no+resType+packids+packReleationIds;
						data.setId(ids);
						data.setFeeCode(feeIntNum);
						data.setProName(resourceName);
						String show_url = showURL.replace("{packid}", packId).replace("{releationid}", packReleationId).replace("{resourceid}", resourceId);
						data.setProUrl(show_url);
						data.setSpid(pro_no);
						dataList.add(data);
						
					}
			//	}
				
				// create a sheet, set its title then delete it
				 
				//-----截取字符串-并赋值给对象 加入到 map中----------

				for(FeeXLSData  o : dataList){
					
					String dataId_24 = o.getId();
					String dataId_20 = dataId_24.substring(0, 20);
					String dataId_16 = dataId_24.substring(0, 16);
					String dataId_12 = dataId_24.substring(0, 12);
					String dataId_8 = dataId_24.substring(0, 8);
					String name = o.getProName();
					name = name.substring(0,2);
					if(dataMap.get(dataId_8)==null){ //8位的没有放进去，（首页的）这个地方以后要改进
						//System.out.println("-------进入8---------");
						FeeXLSData data_8 = new FeeXLSData();
						data_8.setId(dataId_8);
						//Fee fee_channel = getFeeService().getFee(iphone_fee_channel); //得到频道包月计费
						/*
						 * 根据不同的资源类型区分不同的计费信息
						 * yuzs
						 * 2009-11-17
						 */ 
						Integer resourceType =  Integer.parseInt(dataId_8.substring(dataId_8.length()-1));
						//System.out.println("---------"+resourceType);
						Fee fee_channel= null;
						String showName="如果您选择书城包月，您将可以查看书城下任何一本小说";
						if(resourceType==1){ //图书							
							fee_channel = getFeeService().getFee(iphone_fee_channel); //得到频道包月计费	
							showName="如果您选择书城图书包月，您将可以查看书城下任何一本小说";
						}else if(resourceType==2){//报纸
							fee_channel = getFeeService().getFee(iphone_fee_channel_newspapers); //得到报纸频道包月计费	
							showName="如果您选择书城报纸包月，您将可以查看书城下任何报纸";
						}else if(resourceType==3){//杂志
							fee_channel = getFeeService().getFee(iphone_fee_channel_magazine); //得到杂志频道包月计费	
							showName="如果您选择书城杂志包月，您将可以查看书城下任何一本杂志";
						}else if(resourceType==4){//漫画
							fee_channel = getFeeService().getFee(iphone_fee_channel_comics); //得到漫画频道包月计费	
							showName="如果您选择书城漫画包月，您将可以查看书城下任何一本漫画";
						}else{//默认图书
							fee_channel = getFeeService().getFee(iphone_fee_channel); //得到频道包月计费	
							showName="如果您选择书城包月，您将可以查看书城下任何一本小说";
						}
						int feeCode = changeCode(fee_channel.getCode());
						data_8.setFeeCode(feeCode);
						data_8.setProName(name+"频道");
						data_8.setProUrl(showHomeURL);
						data_8.setSpid(pro_no);
						dataMap.put(dataId_8, data_8); //把8位的放进去
						String[] feeData ={name+"频道包月",dataId_8,pro_no,feeCode+"","1","2","1",showName};
						feeDataList.add(feeData);
						
					}
					if(dataMap.get(dataId_12)==null){ //12位的没有放进去(作用不大） 001500011001
						//System.out.println("-------进入12---------");
						FeeXLSData data_12 = new FeeXLSData();
						data_12.setId(dataId_12);
						data_12.setFeeCode(0);
						data_12.setProName(name+"频道"+dataId_12.substring(8, 12));
						data_12.setProUrl(showHomeURL);
						data_12.setSpid(pro_no);
						dataMap.put(dataId_12, data_12); //把8位的放进去
						
					}
					if(dataMap.get(dataId_16)==null){ //16位的没有放进去（批价包的）
						//System.out.println("-------进入16---------");
						FeeXLSData data_16 = new FeeXLSData();
						data_16.setId(dataId_16);
						//得到批价包ID：10006250 
						String pack_Id = dataId_16.substring(9,16);//得到的是：0006250 
						Integer pack_num = Integer.parseInt(pack_Id);
						
						ResourcePack currentPack = (ResourcePack) getResourcePackService().getEpack(pack_num);
						data_16.setProName(currentPack.getName());
						if(iphone_column_pack_rel.indexOf(pack_num.toString())>-1){
							String[] column_pack = iphone_column_pack_rel.split(";");
							for(String ss : column_pack){
								//System.out.println("--sslength-->"+ss);
								//System.out.println("--sslength-->"+ss.indexOf(pack_num.toString()));
								if(ss.indexOf(pack_num.toString())>-1){ //6250=2497;6100=2515 表明有对应关系
									String[] columns = ss.split("=");
									
									//Fee fee_column = getFeeService().getFee(iphone_fee_column); //得到栏目包月计费
									Integer resourceType =  Integer.parseInt(dataId_8.substring(dataId_8.length()-1));
									//System.out.println("---111------"+resourceType);
									Fee fee_column= null;
									String showName="";
									if(resourceType==1){ //图书							
										fee_column = getFeeService().getFee(iphone_fee_column); //得到栏目包月计费
										showName="如果您选择"+currentPack.getName()+"栏目包月，您将可以查看此栏目下的任何一本小说";
									}else if(resourceType==2){//报纸
										fee_column = getFeeService().getFee(iphone_fee_column_newspapers); //得到报纸栏目包月计费
										showName="如果您选择"+currentPack.getName()+"栏目包月，您将可以查看此栏目下的任何报纸";
									}else if(resourceType==3){//杂志
										fee_column = getFeeService().getFee(iphone_fee_column_magazine); //得到杂志栏目包月计费	
										showName="如果您选择"+currentPack.getName()+"栏目包月，您将可以查看此栏目下的任何一本杂志";
									}else if(resourceType==4){//漫画
										fee_column = getFeeService().getFee(iphone_fee_column_comics); //得到漫画栏目包月计费	
										showName="如果您选择"+currentPack.getName()+"栏目包月，您将可以查看此栏目下的任何一本漫画";
									}else{//默认图书
										fee_column = getFeeService().getFee(iphone_fee_column); //得到栏目包月计费	
										showName="如果您选择"+currentPack.getName()+"栏目包月，您将可以查看此栏目下的任何一本小说";
									}
									int feeCode = changeCode(fee_column.getCode());
									data_16.setFeeCode(feeCode);
									
									String[] feeData ={currentPack.getName()+"栏目包月",dataId_16,pro_no,feeCode+"","1","2","1",showName};
									feeDataList.add(feeData);
									
									String column_id = columns[1];	
									data_16.setProUrl(iphone_column_url.replace("{columnid}", column_id));	
								}
							}
						}else{
							data_16.setFeeCode(0);
							data_16.setProUrl(showHomeURL);
						}
						data_16.setSpid(pro_no);
						dataMap.put(dataId_16, data_16); //把8位的放进去
						
					}
					if(dataMap.get(dataId_20)==null){ //20位的没有放进去(作用不大）
						//System.out.println("-------进入20---------");
						FeeXLSData data_20 = new FeeXLSData();
						data_20.setId(dataId_20);
						data_20.setFeeCode(0);
						data_20.setProName(name+dataId_20.substring(16, 20));
						data_20.setProUrl(showHomeURL);
						data_20.setSpid(pro_no);
						dataMap.put(dataId_20, data_20); //把20位的放进去
						
					}
					dataMap.put(dataId_24,o);
				}
			
				Set keySet = dataMap.keySet();
				List keyList = new ArrayList();
				keyList.addAll(keySet);
				Collections.sort(keyList);//排序
				for(int i=0;i<keyList.size();i++){
					FeeXLSData currentData = (FeeXLSData)dataMap.get(keyList.get(i));
					//System.out.println("------5----------"+currentData.getId());
					 ++rowCount;
						r = s.createRow(rowCount);
						
						c = r.createCell((short)0);
							c.setCellValue(currentData.getId());
						c = r.createCell((short)1);	
							c.setCellValue(currentData.getFeeCode());
							
						c = r.createCell((short)2);
							c.setCellValue(currentData.getSpid());
							
						c = r.createCell((short)3);
							c.setCellValue(currentData.getProName());
							
						c = r.createCell((short)4);
							//String show_url = showURL.replace("{packid}", packId).replace("{releationid}", packReleationId).replace("{resourceid}", resourceId);
							c.setCellValue(currentData.getProUrl());
							s.setColumnWidth((short) 0, (short) ((55 * 8) / ((double) 1 / 20)));	
							s.setColumnWidth((short) 3, (short) ((55 * 8) / ((double) 1 / 20)));
							s.setColumnWidth((short) 4, (short) ((100 * 8) / ((double) 1 / 20)));
							
				}
				
				//-------------创建计费策略------------------------------
				
				s = wb.createSheet();
				wb.setSheetName(1, "包月策略");
				int secRowCount = 0;
				 //-------------创建包月策略 表头---------------------=---
				String[] headData = {"包期名称－字符长度50以内","所属产品-产品编号","所属SP","包期价格-以分为单位","期长-数字","期长单位-天:5,月:2,年:1;周:4;时:11","期满是否自动续订-1、是,0、否","包期服务温馨提示-字符500以内(可选)"};
			  
				r = s.createRow(0);
				for(int i=0;i<headData.length;i++){
					c = r.createCell((short)i);
					c.setCellValue(headData[i]);
					c.setCellStyle(cs);
					r.setHeight((short) 0x400);
				}		
				if(feeDataList.size()>0){
					
					for(int i=0;i<feeDataList.size();i++){
						r = s.createRow(i+1);
						String[] feeData = feeDataList.get(i);
						for(int j=0;j<feeData.length;j++){
							c = r.createCell((short)j);
							c.setCellValue(feeData[j]);
						}
						r.setHeight((short) 0x400);
					}
					
				}
				s.setColumnWidth((short) 0, (short) ((55 * 8) / ((double) 1 / 20)));
				s.setColumnWidth((short) 1, (short) ((40 * 8) / ((double) 1 / 20)));	
				s.setColumnWidth((short) 5, (short) ((40 * 8) / ((double) 1 / 20)));	
				s.setColumnWidth((short) 6, (short) ((40 * 8) / ((double) 1 / 20)));		
				s.setColumnWidth((short) 7, (short) ((55 * 8) / ((double) 1 / 20)));
				// ------------------------------------------------------*/
				
					wb.write(out);
					out.close();
				return 	file.toString();
				//return  "d:\\product_info.xls";
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
		return getSystemService().getVariables("userCVS_dir").getValue();
		//return  "d:\\product_info.xls";
	}

	
	public String changeReleId(String releationId,String type){
		if(releationId==null || "".equals(releationId))
			return "";
		int length = releationId.length();
		int sublength = 7-length;
		//String subStr="";
		if(sublength>0){
			for(int i= 0;i<sublength;i++)
				type += "0";
		}
		if(type.length()>1)
			releationId = type+releationId;
		return releationId;
	}
	
	public String changPackId(String packid){ 
		if(packid==null || "".equals(packid))
			return "";
		int length = packid.length();
		int sublength = 7-length;
		String subStr="1";
		if(sublength>0){
			for(int i= 0;i<sublength;i++)
				subStr += "0";
		}
		if(subStr.length()>1)
			packid = subStr+packid;
		return packid;
	}
	
	public int  changeCode(String code){   //把字符的计费---int类型
		
		Double feeNum =0.0;
		try{
		 feeNum = Double.parseDouble(code);
		 feeNum = feeNum*100;
		 Integer feeIntNum = feeNum.intValue();
			if(feeIntNum<=0)
				return 0;
			else
				return feeIntNum;
		}catch(Exception e){
			return 0;
		}	
	}
	@InjectObject("service:tapestry.globals.HttpServletResponse")
	public abstract HttpServletResponse getServletResponse();

	public void downloadCSV(IRequestCycle cycle) {
		String err = "";
		if (getSelectedPackReleations().size() == 0)
			err = "您至少得选择一个";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			
		} else {
			try{
			 ResourcePack pack  =  getPack();
			 if(pack.getType()!=1 && pack.getType()!=2) //非计次、 包月vip
				 throw new Exception("当前批价资源没有计费信息");
				// 生成CSV
				UserImpl user = (UserImpl) getUser();
				String fileDir = this.creatCSV(user.getId(),
						getSelectedPackReleations());	
				//String fileDir = getResourcePackService().createXLS(user.getId(), getSelectedPacks());
				setSelectedPackReleations(new HashSet());
				// path是指欲下载的文件的路径。
				  String contextPath = getServletRequest().getContextPath();
				  throw new RedirectException("http://" + PageUtil.getDomainName(getServletRequest().getRequestURL().toString())
		                   + contextPath + "/DownLoadPage.jsp?file="+fileDir);
			}catch(Exception e){
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
			}
		}
	}
	
	/**
	 * 选中关联关系 然后复制给另外的批价包,没有计费信息的 只能复制给没有计费信息的批价包，（免费/包月常规/包月内容控制)
	 * 计次/vip 复制给 中
	 * yuzs 2009-11-11
	 */
	@InjectPage("resource/CopyPackReleationPage")
	public abstract CopyPackReleationPage getCopyPackReleationPage();
	
	public  IPage copyPackReleation(ResourcePack pack){
		Set<ResourcePackReleation> sets = getSelectedPackReleations();
		getCopyPackReleationPage().setPackReleations(sets);
		getCopyPackReleationPage().setPack(pack);
		setSelectedPackReleations(new HashSet());
		return getCopyPackReleationPage();
	}
	
	
	/****************************************************
	 * yuzs 2009-11-12
	 * 批价对应关系 置顶，上移，下移，置底
	 * 批价对应关系 选中后 集体插入到某一位置
	 */

	public void top(IRequestCycle cycle, Object obj) {
		try{
			
			ResourcePackReleation releation = (ResourcePackReleation)obj;
			System.out.println("当前排序号::::"+releation.getOrder());
			Integer minOrder = getResourcePackService().findMinOrderInPack(getPack());
		/*	System.out.println("最小排序号::::"+minOrder);
			Integer maxOrder = getResourcePackService().findMaxOrderInPack(getPack());
			System.out.println("最大排序号::::"+maxOrder);
			ResourcePackReleation downReleation  =   //获取当前下一个资源
				getResourcePackService().findBesideDownReleation(getPack(), releation);
			System.out.println("x下一个排序号::::"+downReleation.getOrder());
			ResourcePackReleation upReleation  =   //获取当前上一个资源
				getResourcePackService().findBesideUpReleation(getPack(), releation);
			System.out.println("上一个排序号::::"+upReleation.getOrder());*/
			if(releation.getOrder()<=minOrder)
				throw new Exception("已经置顶了！");
			String strOrder = minOrder+"";
			minOrder = Integer.parseInt(strOrder.substring(0,6))-1;
			releation.setOrder(minOrder*1000);
			getResourcePackService().updateResourcePackReleationNotCreateUEB(releation);
		}catch(Exception e){
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);	
		}
	}

	public void up(IRequestCycle cycle, Object obj) {
		try {
			ResourcePackReleation releation = (ResourcePackReleation)obj;
			ResourcePackReleation upReleation  =   //获取当前上一个资源
				getResourcePackService().findBesideUpReleation(getPack(), releation);
			Integer currentOrder = releation.getOrder();
			Integer upOrder = upReleation.getOrder();
			System.out.println("----当前页码--"+currentOrder);
			System.out.println("----上一个页码--"+upOrder);
			releation.setOrder(upOrder);
			upReleation.setOrder(currentOrder);
			getResourcePackService().updateResourcePackReleationNotCreateUEB(releation);
			getResourcePackService().updateResourcePackReleationNotCreateUEB(upReleation);	
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}

	}

	public void down(IRequestCycle cycle, Object obj) {
		try {
			ResourcePackReleation releation = (ResourcePackReleation)obj;
			ResourcePackReleation downReleation  =   //获取当前下一个资源
				getResourcePackService().findBesideDownReleation(getPack(), releation);
			Integer currentOrder = releation.getOrder();
			Integer downOrder = downReleation.getOrder();
			System.out.println("----当前页码--"+currentOrder);
			System.out.println("----下一个页码--"+downOrder);
			releation.setOrder(downOrder);
			downReleation.setOrder(currentOrder);
			getResourcePackService().updateResourcePackReleationNotCreateUEB(releation);
			getResourcePackService().updateResourcePackReleationNotCreateUEB(downReleation);	
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	public void buttom(IRequestCycle cycle, Object obj) {
		try {
			ResourcePackReleation releation = (ResourcePackReleation)obj;
			System.out.println("当前排序号::::"+releation.getOrder());
			Integer maxOrder = getResourcePackService().findMaxOrderInPack(getPack());		
			if(releation.getOrder()>=maxOrder)
				throw new Exception("已经置底了！");
			String strOrder = maxOrder+"";
			maxOrder = Integer.parseInt(strOrder.substring(0,6))+1;
			releation.setOrder(maxOrder*1000);		
			//releation.setOrder(maxOrder+1);
			getResourcePackService().updateResourcePackReleationNotCreateUEB(releation);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	
	@InjectPage("resource/BatchChangEpackReleationOrder")
	public abstract BatchChangEpackReleationOrder getBatchChangEpackReleationOrder();
	public IPage insertOrder(ResourcePack pack){
		String err = "";
		if (getSelectedPackReleations().size() == 0) {
			err = "您至少要选择一个选项！";
		}
		if (getSelectedPackReleations() == null)
			err = "请选择要更改的目录！";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			setSelectedPackReleations(new HashSet());
			return this;
		} else {/*
			try{
				Set<ResourcePackReleation> sets = getSelectedPackReleations();
				if(getPlace()==null)
					return;
				else if(getPlace()==1){ //置顶
					Integer minOrder =  //最小值
						getResourcePackService().findMinOrderInPack(getPack());
					String strOrder = minOrder+"";
					minOrder = Integer.parseInt(strOrder.substring(0,6));
					int i=1;
					for(ResourcePackReleation rel : sets){
						rel.setOrder((minOrder-i)*1000);
						//rel.setOrder(min-i);
						i++;
						getResourcePackService().updateResourcePackReleationNotCreateUEB(rel);
					}
				}else if(getPlace()==2){//某一资源下
					System.out.println("---1-----");
					if(getReleationId()==null)
						throw new Exception("选择插入位置为:某一资源下时，请填写资源对应的编号！");
					ResourcePackReleation rel = 
						(ResourcePackReleation) getResourcePackService().getResourcePackReleation(getReleationId());
					Integer firstNum = rel.getFirstOrder();	
					System.out.println("---2-----");
					ResourcePackReleation maxRel =  //找到最大的那个
						 getResourcePackService().getMaxSubOrder(getPack(), firstNum);
					System.out.println("---3-----"+maxRel);
					Integer count = 999 - maxRel.getSubOrder();
					this.changeOrder(rel, maxRel, sets, getPack(), count);
				}else if(getPlace()==3){//置底
					Integer maxOrder = getResourcePackService().findMaxOrderInPack(getPack());
					String strOrder = maxOrder+"";
					maxOrder = Integer.parseInt(strOrder.substring(0,6));
				
					int i=1;
					for(ResourcePackReleation rel : sets){
						//rel.setOrder(max+1);
						rel.setOrder((maxOrder+i)*1000);	
						i++;
						getResourcePackService().updateResourcePackReleationNotCreateUEB(rel);
					}
				}
				setSelectedPackReleations(new HashSet());
			}catch(Exception e){
				getDelegate().setFormComponent(null);
				getDelegate().record(e.getMessage(), null);
				setSelectedPackReleations(new HashSet());
			}
		*/
			Set<ResourcePackReleation> sets = getSelectedPackReleations();
			System.out.println("---"+sets.size());
			getBatchChangEpackReleationOrder().setPackReleations(sets);
			getBatchChangEpackReleationOrder().setPack(pack);
			setSelectedPackReleations(new HashSet());
			return getBatchChangEpackReleationOrder();
			}
	}
	/**
	 * 
	 * @param currentOrder 当前id
	 * @param firstNum 前六位
	 * @param secondNum  前四位
	 * @param sets 需要变更的资源集合，
	 * 
	 */
	/*public void changeOrder(ResourcePackReleation currentRel,ResourcePackReleation lastRel,Set<ResourcePackReleation> sets,ResourcePack pack,Integer count){
		//找到插入位置
		//如果当前剩余的 子排序个数 大于 插入的个数，直接插入
		try {
			System.out.println("count---"+count);
			System.out.println("sets.size---"+sets.size());
			if(count>=sets.size()){
				System.out.println("---2-----");
				
				if(currentRel.getOrder() == lastRel.getOrder() )//从0插入的
					return ;
				System.out.println("--难道到这里了？-----");
				System.out.println("--当前的--1---"+currentRel.getOrder());
				System.out.println("--延伸到最大的那个--2---"+lastRel.getOrder());
				List<ResourcePackReleation> changeReleations = 
					getResourcePackService().getSubOrderList(pack, currentRel.getOrder(), lastRel.getOrder());
				Map<Integer,ResourcePackReleation> changeReleations = 
					getResourcePackService().getSubOrderList(pack, currentRel.getOrder(), lastRel.getOrder());
				System.out.println("----chandu----"+changeReleations.size());
				System.out.println("----chandu-ssssssss-111--"+changeReleations);
				
				Set<Integer> ketSet = changeReleations.keySet();
				Iterator it = ketSet.iterator();
				System.out.println("set集合：：：：："+ketSet);
				System.out.println("map集合：：：：："+changeReleations);
				int i=1;
				for(ResourcePackReleation rel : sets){	//插入新的	
					if(changeReleations.containsKey(rel.getId())){//如果顺延的列表中 包含你要 更新的那些资源，那么在顺延列表中 删除掉
						System.out.println("进入到这里了吗？--"+rel.getId());
						changeReleations.remove(rel.getId());
					}
					System.out.println("当前--"+rel.getId()+"-----"+rel);
					rel.setOrder(currentRel.getOrder()+i);
					System.out.println(rel.getId()+"--i---"+i+"----更新的-选中的-"+(currentRel.getOrder()+i));
					getResourcePackService().updateResourcePackReleationNotCreateUEB(rel);
					i++;
				}	//修改旧的
				
				if(changeReleations==null || changeReleations.size()<1)
					return;
				//changeReleations.contains(o)
				//for(ResourcePackReleation rel : changeReleations){	//更新旧的	
				System.out.println("set集合：：：：："+it);
				for(Integer id : ketSet){
					System.out.println("ids-----"+id);
					ResourcePackReleation rel = changeReleations.get(id);
					System.out.println("----chandu---"+rel.getOrder()+"----id------"+rel);
					rel.setOrder(currentRel.getOrder()+i);
					getResourcePackService().updateResourcePackReleationNotCreateUEB(rel);
					i++;
				}	
				return ;
			}else { //继续查找位置
				System.out.println("---！！！！！---");
				ResourcePackReleation downReleation  =   //获取当前下一个资源
					getResourcePackService().findBesideDownReleation(pack, lastRel);
				//查看下一个资源的 空缺位置：
				System.out.println("---下一个-应该是 ---500018000-"+downReleation.getOrder());
				String strOrder = downReleation.getOrder()+"";
				Integer firstOrder = Integer.parseInt(strOrder.substring(0,6));
				Long subCount = getResourcePackService().getSumSubOrder(pack, firstOrder);//下一个父order 下的资源个数
				System.out.println("---333-");
				count += (1000-subCount);//剩余个数
				ResourcePackReleation lastReleation =
					getResourcePackService().getMaxSubOrder(pack, firstOrder);
				System.out.println("--44----个数----"+count);
				changeOrder(currentRel,lastReleation,sets,pack,count);//继续进行下去
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	public IPropertySelectionModel getPlaceList() {
		Map<String, Integer> authors = new OrderedMap<String, Integer>();
		authors.put("置顶位置",1);
		authors.put("某一资源下",2);
		authors.put("置底位置",3);
		return new MapPropertySelectModel(authors, false, "");
	} 
	
	public abstract Integer getReleationId();
	public abstract void setReleationId(Integer id);
	

	public abstract Integer getPlace();
	public abstract void setPlace(Integer id);
	
	
	/***************结束*************************************
	 */
}
