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
		// ѡ����ҳ����
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
		authors.put("ȫ��", 0);
		for (ResourceAuthor author : resourceauthor) {
			authors.put(author.getPenName(), author.getId());
		}
		Long t2 = System.currentTimeMillis();
		System.out.println("----����ʱ��---"+(t2-t1));
		return new MapPropertySelectModel(authors, false, "ȫ��");
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
		sorts.put("ȫ��", 0);
		for (ResourceType sort : sortTypes) {
			sorts.put(sort.getName(), sort.getId());
		}
		return new MapPropertySelectModel(sorts, false, "ȫ��");
	}

	public IPropertySelectionModel getStatusList() {
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("ȫ��", -1);
		map.put("����", 0);
		map.put("��ͣ", 1);
		return new MapPropertySelectModel(map);
	}
	
	/*
	 * ��� ���෴ѡ���Լ�����
	 * yuzs 2009-11-11
	 */
	public abstract Integer getTof();
	public abstract void setTof(Integer tof);
	
	public abstract Integer getOrder();
	public abstract void setOrder(Integer id);
	
	public IPropertySelectionModel getToflist(){
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("��", 0);
		map.put("��", 1);
		return new MapPropertySelectModel(map);		
	}
	
	public IPropertySelectionModel getOrderlist(){
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("�����", 1);
		map.put("ID����", 2);
		map.put("PV������", 10);
		map.put("PV������", 11);
		map.put("������������", 20);
		map.put("������������", 21);
		return new MapPropertySelectModel(map);		
	}
	public IPropertySelectionModel getResourceTypeList() {
		Map<String, Integer> types = new TreeMap<String, Integer>();
		types.put("ͼ��", ResourceType.TYPE_BOOK);
		types.put("����", ResourceType.TYPE_COMICS);
		types.put("��ֽ", ResourceType.TYPE_NEWSPAPERS);
		types.put("��־", ResourceType.TYPE_MAGAZINE);
		types.put("��Ƶ", ResourceType.TYPE_VIDEO);
		types.put("��Ѷ", ResourceType.TYPE_INFO);

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
			err = "�����ٵ�ѡ��һ��";
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
			if (pack.getType() == 1 || pack.getType() == 2) // vip�� ����
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
		return new SimpleTableColumn("feename", "�Ʒ���Ϣ",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						if (rp.getFeeId() == null || "".equals(rp.getFeeId()))
							return "�޼Ʒ���Ϣ";
						else {
							Fee fee = getFeeService().getFee(rp.getFeeId());
							if (fee == null)
								return "�����ڵļƷ���Ϣ";
							return fee.getName();
						}
					}

				}, false);

	}

	public ITableColumn getResourceName() {
		return new SimpleTableColumn("resourcename", "��Դ����",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						if (rp.getResourceId() == null
								|| "".equals(rp.getResourceId()))
							return "��Դ��ʧ";
						else {
							
							//Long t3 = System.currentTimeMillis();
							ResourceAll ra = getResourceService().getResource(
									rp.getResourceId());
							//Long t4 = System.currentTimeMillis();
							//System.out.println("---��Դȡ����ʱ----"+(t4-t3));
							/*if(ra==null){ //�ٴ����ݿ���ȡ��
							  ra = getResourceService().getResourceFromMemcached(
										rp.getResourceId());
							}*/
							if (ra == null)
								return "��Դ��ʧ";
							return ra.getName();
						}
					}

				}, false);
	}

	public ITableColumn getStatus() {
		return new SimpleTableColumn("status", "״̬",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						Integer statust = null;
						if (rp.getResourceId() == null
								|| "".equals(rp.getResourceId()))
							return "��Դ��ʧ";
						else {
							//Long t1 = System.currentTimeMillis();
							ResourceAll ra = getResourceService().getResource(
									rp.getResourceId());
							//Long t2 = System.currentTimeMillis();
							//System.out.println("---�ڴ�ȡ����ʱ----"+(t2-t1));
							/*if (ra == null){//�ڴ�ȡ��ʧ��ʱ �ڴ����ݿ���ȡһ��
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
								return "��ʧ״̬";
							if (statust == 0)
								return "����";
							if (statust == 1)
								return "��ͣ";
							else
								return "����";
							
						}
					}

				}, false);
	}

	public ITableColumn getSort() {
		return new SimpleTableColumn("sort", "���", new ITableColumnEvaluator() {

			private static final long serialVersionUID = 625300745851970L;

			public Object getColumnValue(ITableColumn objColumn, Object objRow) {
				ResourcePackReleation rp = (ResourcePackReleation) objRow;
				StringBuffer type = new StringBuffer();
				// ������ԴID��ѯ��Դ������
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
				// ȥ�����һ���ֺ�
				if (list.size() > 0) {
					return type.toString().substring(0, (type.length() - 1));
				} else {
					return "";
				}
			}

		}, false);

	}

	public ITableColumn getAuthorname() {
		return new SimpleTableColumn("authorname", "����",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePackReleation rp = (ResourcePackReleation) objRow;
						ResourceAll ra = null;
						ra = getResourceService().getResource(
								rp.getResourceId());
						if (ra == null)
							return "��Դ��ʧ";

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
	 * ��ȡ�������۰�����
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
		} else if (getPack().getType().intValue() == 5) { // �������ʾ������
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
				Sheet s = wb.createSheet("��Ʒ��Ϣ");
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
				//wb.setSheetName(0, "��Ʒ��Ϣ");
				String ids="";
				String pro_no = getSystemService().getVariables("iphone_spid").getValue(); //ȡ��sp��ʶ\0015
				String resType="";
				String packId = "";
				String packids = "";//ǰ�油1�����۰�ID
				String packReleationId = "";
				String packReleationIds="";//ǰ�油��Ķ�Ӧ��ϵID
				String showURL = getSystemService().getVariables("iphone_resource_url").getValue(); //ȡ��Ԥ������
				String showHomeURL =  getSystemService().getVariables("iphone_home_url").getValue(); //ȡ����ҳԤ������
				String iphone_fee_channel = getSystemService().getVariables("iphone_fee_channel").getValue(); // IPHONEƵ�����¼Ʒ�ID
				String iphone_fee_column = getSystemService().getVariables("iphone_fee_column").getValue(); // IPHONE��Ŀ���¼Ʒ�ID
				//String iphone_month_packs = getSystemService().getVariables("iphone_month_packs").getValue(); //iphone�շ����۰�ID�б������;�Ÿ���
				String iphone_column_url = getSystemService().getVariables("iphone_column_url").getValue(); //IPHONE��Ŀ�걨��ַ
				String iphone_column_pack_rel = getSystemService().getVariables("iphone_column_pack_rel").getValue(); //IPHONE��Ŀ�����۰���Ӧ��ϵ
				/*
				 * yuzs
				 * 200-11-17
				 * */
				String iphone_fee_channel_magazine = getSystemService().getVariables("iphone_fee_channel_magazine").getValue(); // IPHONE��־Ƶ�����¼Ʒ�ID
				String iphone_fee_column_magazine = getSystemService().getVariables("iphone_fee_column_magazine").getValue(); // IPHONE��־��Ŀ���¼Ʒ�ID
				
				String iphone_fee_channel_comics = getSystemService().getVariables("iphone_fee_channel_comics").getValue(); // IPHONE����Ƶ�����¼Ʒ�ID
				String iphone_fee_column_comics = getSystemService().getVariables("iphone_fee_column_comics").getValue(); // IPHONE��������Ŀ���¼Ʒ�ID
				
				String iphone_fee_channel_newspapers = getSystemService().getVariables("iphone_fee_channel_newspapers").getValue(); // IPHONE��ֽƵ�����¼Ʒ�ID
				String iphone_fee_column_newspapers = getSystemService().getVariables("iphone_fee_column_newspapers").getValue(); // IPHONE��ֽ��Ŀ���¼Ʒ�ID
				
				//----------------
				//http://book.moluck.com/pps/s.do?pg=r&pd=250000002&gd=4069&ad=001&cd=2868&fd={packid}&nd={releationid}&rd={resourceid}&fc=25000000&pn=1
				int rowCount=0;
				//---------------������Ʒ��Ϣ ��ͷ--------------------------
				String[] feeHeadData = {"��Ʒ���-�ַ�����100���㼶��ϵͨ���������,�����λΪһ��","���-�Է�Ϊ��λ","��Ӧ��-��SPNO","��Ʒ����-����50","��Ʒ��Դ·��-��Ʒ�鿴��ַ"};
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
						//packReleationIds = changeReleId(packReleationId,"");//�޸ĺ�Ķ�Ӧ��ϵID
						packids = changPackId(packId);//�޲�������۰�ID
						String resourceId = packReleation.getResourceId();			
						ResourceAll ra = getResourceService().getResource(resourceId);
						//1ͼ�飬2��ֽ��3��־��4������5������6��Ƶ
						String resourceName="";
						if(resourceId.startsWith("1")){
							resType="0001";
							packReleationIds = changeReleId(packReleationId,"1");//�޸ĺ�Ķ�Ӧ��ϵID
							resourceName += "ͼ��-"+ra.getName();
						}if(resourceId.startsWith("2")){
							resType="0002";
							packReleationIds = changeReleId(packReleationId,"2");//�޸ĺ�Ķ�Ӧ��ϵID
							resourceName += "��ֽ-"+ra.getName();
						}if(resourceId.startsWith("3")){
							resType="0003";
							packReleationIds = changeReleId(packReleationId,"3");//�޸ĺ�Ķ�Ӧ��ϵID
							resourceName += "��־-"+ra.getName();
						}if(resourceId.startsWith("4")){
							resType="0004";
							packReleationIds = changeReleId(packReleationId,"4");//�޸ĺ�Ķ�Ӧ��ϵID
							resourceName += "����-"+ra.getName();
						}if(resourceId.startsWith("5")){
							resType="0005";
							packReleationIds = changeReleId(packReleationId,"5");//�޸ĺ�Ķ�Ӧ��ϵID
							resourceName += "����-"+ra.getName();
						}if(resourceId.startsWith("6")){
							resType="0006";
							packReleationIds = changeReleId(packReleationId,"6");//�޸ĺ�Ķ�Ӧ��ϵID
							resourceName += "��Ƶ-"+ra.getName();
						}
						
						//---------------���޸�--------------------
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
				 
				//-----��ȡ�ַ���-����ֵ������ ���뵽 map��----------

				for(FeeXLSData  o : dataList){
					
					String dataId_24 = o.getId();
					String dataId_20 = dataId_24.substring(0, 20);
					String dataId_16 = dataId_24.substring(0, 16);
					String dataId_12 = dataId_24.substring(0, 12);
					String dataId_8 = dataId_24.substring(0, 8);
					String name = o.getProName();
					name = name.substring(0,2);
					if(dataMap.get(dataId_8)==null){ //8λ��û�зŽ�ȥ������ҳ�ģ�����ط��Ժ�Ҫ�Ľ�
						//System.out.println("-------����8---------");
						FeeXLSData data_8 = new FeeXLSData();
						data_8.setId(dataId_8);
						//Fee fee_channel = getFeeService().getFee(iphone_fee_channel); //�õ�Ƶ�����¼Ʒ�
						/*
						 * ���ݲ�ͬ����Դ�������ֲ�ͬ�ļƷ���Ϣ
						 * yuzs
						 * 2009-11-17
						 */ 
						Integer resourceType =  Integer.parseInt(dataId_8.substring(dataId_8.length()-1));
						//System.out.println("---------"+resourceType);
						Fee fee_channel= null;
						String showName="�����ѡ����ǰ��£��������Բ鿴������κ�һ��С˵";
						if(resourceType==1){ //ͼ��							
							fee_channel = getFeeService().getFee(iphone_fee_channel); //�õ�Ƶ�����¼Ʒ�	
							showName="�����ѡ�����ͼ����£��������Բ鿴������κ�һ��С˵";
						}else if(resourceType==2){//��ֽ
							fee_channel = getFeeService().getFee(iphone_fee_channel_newspapers); //�õ���ֽƵ�����¼Ʒ�	
							showName="�����ѡ����Ǳ�ֽ���£��������Բ鿴������κα�ֽ";
						}else if(resourceType==3){//��־
							fee_channel = getFeeService().getFee(iphone_fee_channel_magazine); //�õ���־Ƶ�����¼Ʒ�	
							showName="�����ѡ�������־���£��������Բ鿴������κ�һ����־";
						}else if(resourceType==4){//����
							fee_channel = getFeeService().getFee(iphone_fee_channel_comics); //�õ�����Ƶ�����¼Ʒ�	
							showName="�����ѡ������������£��������Բ鿴������κ�һ������";
						}else{//Ĭ��ͼ��
							fee_channel = getFeeService().getFee(iphone_fee_channel); //�õ�Ƶ�����¼Ʒ�	
							showName="�����ѡ����ǰ��£��������Բ鿴������κ�һ��С˵";
						}
						int feeCode = changeCode(fee_channel.getCode());
						data_8.setFeeCode(feeCode);
						data_8.setProName(name+"Ƶ��");
						data_8.setProUrl(showHomeURL);
						data_8.setSpid(pro_no);
						dataMap.put(dataId_8, data_8); //��8λ�ķŽ�ȥ
						String[] feeData ={name+"Ƶ������",dataId_8,pro_no,feeCode+"","1","2","1",showName};
						feeDataList.add(feeData);
						
					}
					if(dataMap.get(dataId_12)==null){ //12λ��û�зŽ�ȥ(���ò��� 001500011001
						//System.out.println("-------����12---------");
						FeeXLSData data_12 = new FeeXLSData();
						data_12.setId(dataId_12);
						data_12.setFeeCode(0);
						data_12.setProName(name+"Ƶ��"+dataId_12.substring(8, 12));
						data_12.setProUrl(showHomeURL);
						data_12.setSpid(pro_no);
						dataMap.put(dataId_12, data_12); //��8λ�ķŽ�ȥ
						
					}
					if(dataMap.get(dataId_16)==null){ //16λ��û�зŽ�ȥ�����۰��ģ�
						//System.out.println("-------����16---------");
						FeeXLSData data_16 = new FeeXLSData();
						data_16.setId(dataId_16);
						//�õ����۰�ID��10006250 
						String pack_Id = dataId_16.substring(9,16);//�õ����ǣ�0006250 
						Integer pack_num = Integer.parseInt(pack_Id);
						
						ResourcePack currentPack = (ResourcePack) getResourcePackService().getEpack(pack_num);
						data_16.setProName(currentPack.getName());
						if(iphone_column_pack_rel.indexOf(pack_num.toString())>-1){
							String[] column_pack = iphone_column_pack_rel.split(";");
							for(String ss : column_pack){
								//System.out.println("--sslength-->"+ss);
								//System.out.println("--sslength-->"+ss.indexOf(pack_num.toString()));
								if(ss.indexOf(pack_num.toString())>-1){ //6250=2497;6100=2515 �����ж�Ӧ��ϵ
									String[] columns = ss.split("=");
									
									//Fee fee_column = getFeeService().getFee(iphone_fee_column); //�õ���Ŀ���¼Ʒ�
									Integer resourceType =  Integer.parseInt(dataId_8.substring(dataId_8.length()-1));
									//System.out.println("---111------"+resourceType);
									Fee fee_column= null;
									String showName="";
									if(resourceType==1){ //ͼ��							
										fee_column = getFeeService().getFee(iphone_fee_column); //�õ���Ŀ���¼Ʒ�
										showName="�����ѡ��"+currentPack.getName()+"��Ŀ���£��������Բ鿴����Ŀ�µ��κ�һ��С˵";
									}else if(resourceType==2){//��ֽ
										fee_column = getFeeService().getFee(iphone_fee_column_newspapers); //�õ���ֽ��Ŀ���¼Ʒ�
										showName="�����ѡ��"+currentPack.getName()+"��Ŀ���£��������Բ鿴����Ŀ�µ��κα�ֽ";
									}else if(resourceType==3){//��־
										fee_column = getFeeService().getFee(iphone_fee_column_magazine); //�õ���־��Ŀ���¼Ʒ�	
										showName="�����ѡ��"+currentPack.getName()+"��Ŀ���£��������Բ鿴����Ŀ�µ��κ�һ����־";
									}else if(resourceType==4){//����
										fee_column = getFeeService().getFee(iphone_fee_column_comics); //�õ�������Ŀ���¼Ʒ�	
										showName="�����ѡ��"+currentPack.getName()+"��Ŀ���£��������Բ鿴����Ŀ�µ��κ�һ������";
									}else{//Ĭ��ͼ��
										fee_column = getFeeService().getFee(iphone_fee_column); //�õ���Ŀ���¼Ʒ�	
										showName="�����ѡ��"+currentPack.getName()+"��Ŀ���£��������Բ鿴����Ŀ�µ��κ�һ��С˵";
									}
									int feeCode = changeCode(fee_column.getCode());
									data_16.setFeeCode(feeCode);
									
									String[] feeData ={currentPack.getName()+"��Ŀ����",dataId_16,pro_no,feeCode+"","1","2","1",showName};
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
						dataMap.put(dataId_16, data_16); //��8λ�ķŽ�ȥ
						
					}
					if(dataMap.get(dataId_20)==null){ //20λ��û�зŽ�ȥ(���ò���
						//System.out.println("-------����20---------");
						FeeXLSData data_20 = new FeeXLSData();
						data_20.setId(dataId_20);
						data_20.setFeeCode(0);
						data_20.setProName(name+dataId_20.substring(16, 20));
						data_20.setProUrl(showHomeURL);
						data_20.setSpid(pro_no);
						dataMap.put(dataId_20, data_20); //��20λ�ķŽ�ȥ
						
					}
					dataMap.put(dataId_24,o);
				}
			
				Set keySet = dataMap.keySet();
				List keyList = new ArrayList();
				keyList.addAll(keySet);
				Collections.sort(keyList);//����
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
				
				//-------------�����ƷѲ���------------------------------
				
				s = wb.createSheet();
				wb.setSheetName(1, "���²���");
				int secRowCount = 0;
				 //-------------�������²��� ��ͷ---------------------=---
				String[] headData = {"�������ƣ��ַ�����50����","������Ʒ-��Ʒ���","����SP","���ڼ۸�-�Է�Ϊ��λ","�ڳ�-����","�ڳ���λ-��:5,��:2,��:1;��:4;ʱ:11","�����Ƿ��Զ�����-1����,0����","���ڷ�����ܰ��ʾ-�ַ�500����(��ѡ)"};
			  
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
	
	public int  changeCode(String code){   //���ַ��ļƷ�---int����
		
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
			err = "�����ٵ�ѡ��һ��";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			
		} else {
			try{
			 ResourcePack pack  =  getPack();
			 if(pack.getType()!=1 && pack.getType()!=2) //�ǼƴΡ� ����vip
				 throw new Exception("��ǰ������Դû�мƷ���Ϣ");
				// ����CSV
				UserImpl user = (UserImpl) getUser();
				String fileDir = this.creatCSV(user.getId(),
						getSelectedPackReleations());	
				//String fileDir = getResourcePackService().createXLS(user.getId(), getSelectedPacks());
				setSelectedPackReleations(new HashSet());
				// path��ָ�����ص��ļ���·����
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
	 * ѡ�й�����ϵ Ȼ���Ƹ���������۰�,û�мƷ���Ϣ�� ֻ�ܸ��Ƹ�û�мƷ���Ϣ�����۰��������/���³���/�������ݿ���)
	 * �ƴ�/vip ���Ƹ� ��
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
	 * ���۶�Ӧ��ϵ �ö������ƣ����ƣ��õ�
	 * ���۶�Ӧ��ϵ ѡ�к� ������뵽ĳһλ��
	 */

	public void top(IRequestCycle cycle, Object obj) {
		try{
			
			ResourcePackReleation releation = (ResourcePackReleation)obj;
			System.out.println("��ǰ�����::::"+releation.getOrder());
			Integer minOrder = getResourcePackService().findMinOrderInPack(getPack());
		/*	System.out.println("��С�����::::"+minOrder);
			Integer maxOrder = getResourcePackService().findMaxOrderInPack(getPack());
			System.out.println("��������::::"+maxOrder);
			ResourcePackReleation downReleation  =   //��ȡ��ǰ��һ����Դ
				getResourcePackService().findBesideDownReleation(getPack(), releation);
			System.out.println("x��һ�������::::"+downReleation.getOrder());
			ResourcePackReleation upReleation  =   //��ȡ��ǰ��һ����Դ
				getResourcePackService().findBesideUpReleation(getPack(), releation);
			System.out.println("��һ�������::::"+upReleation.getOrder());*/
			if(releation.getOrder()<=minOrder)
				throw new Exception("�Ѿ��ö��ˣ�");
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
			ResourcePackReleation upReleation  =   //��ȡ��ǰ��һ����Դ
				getResourcePackService().findBesideUpReleation(getPack(), releation);
			Integer currentOrder = releation.getOrder();
			Integer upOrder = upReleation.getOrder();
			System.out.println("----��ǰҳ��--"+currentOrder);
			System.out.println("----��һ��ҳ��--"+upOrder);
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
			ResourcePackReleation downReleation  =   //��ȡ��ǰ��һ����Դ
				getResourcePackService().findBesideDownReleation(getPack(), releation);
			Integer currentOrder = releation.getOrder();
			Integer downOrder = downReleation.getOrder();
			System.out.println("----��ǰҳ��--"+currentOrder);
			System.out.println("----��һ��ҳ��--"+downOrder);
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
			System.out.println("��ǰ�����::::"+releation.getOrder());
			Integer maxOrder = getResourcePackService().findMaxOrderInPack(getPack());		
			if(releation.getOrder()>=maxOrder)
				throw new Exception("�Ѿ��õ��ˣ�");
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
			err = "������Ҫѡ��һ��ѡ�";
		}
		if (getSelectedPackReleations() == null)
			err = "��ѡ��Ҫ���ĵ�Ŀ¼��";
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
				else if(getPlace()==1){ //�ö�
					Integer minOrder =  //��Сֵ
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
				}else if(getPlace()==2){//ĳһ��Դ��
					System.out.println("---1-----");
					if(getReleationId()==null)
						throw new Exception("ѡ�����λ��Ϊ:ĳһ��Դ��ʱ������д��Դ��Ӧ�ı�ţ�");
					ResourcePackReleation rel = 
						(ResourcePackReleation) getResourcePackService().getResourcePackReleation(getReleationId());
					Integer firstNum = rel.getFirstOrder();	
					System.out.println("---2-----");
					ResourcePackReleation maxRel =  //�ҵ������Ǹ�
						 getResourcePackService().getMaxSubOrder(getPack(), firstNum);
					System.out.println("---3-----"+maxRel);
					Integer count = 999 - maxRel.getSubOrder();
					this.changeOrder(rel, maxRel, sets, getPack(), count);
				}else if(getPlace()==3){//�õ�
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
	 * @param currentOrder ��ǰid
	 * @param firstNum ǰ��λ
	 * @param secondNum  ǰ��λ
	 * @param sets ��Ҫ�������Դ���ϣ�
	 * 
	 */
	/*public void changeOrder(ResourcePackReleation currentRel,ResourcePackReleation lastRel,Set<ResourcePackReleation> sets,ResourcePack pack,Integer count){
		//�ҵ�����λ��
		//�����ǰʣ��� ��������� ���� ����ĸ�����ֱ�Ӳ���
		try {
			System.out.println("count---"+count);
			System.out.println("sets.size---"+sets.size());
			if(count>=sets.size()){
				System.out.println("---2-----");
				
				if(currentRel.getOrder() == lastRel.getOrder() )//��0�����
					return ;
				System.out.println("--�ѵ��������ˣ�-----");
				System.out.println("--��ǰ��--1---"+currentRel.getOrder());
				System.out.println("--���쵽�����Ǹ�--2---"+lastRel.getOrder());
				List<ResourcePackReleation> changeReleations = 
					getResourcePackService().getSubOrderList(pack, currentRel.getOrder(), lastRel.getOrder());
				Map<Integer,ResourcePackReleation> changeReleations = 
					getResourcePackService().getSubOrderList(pack, currentRel.getOrder(), lastRel.getOrder());
				System.out.println("----chandu----"+changeReleations.size());
				System.out.println("----chandu-ssssssss-111--"+changeReleations);
				
				Set<Integer> ketSet = changeReleations.keySet();
				Iterator it = ketSet.iterator();
				System.out.println("set���ϣ���������"+ketSet);
				System.out.println("map���ϣ���������"+changeReleations);
				int i=1;
				for(ResourcePackReleation rel : sets){	//�����µ�	
					if(changeReleations.containsKey(rel.getId())){//���˳�ӵ��б��� ������Ҫ ���µ���Щ��Դ����ô��˳���б��� ɾ����
						System.out.println("���뵽��������--"+rel.getId());
						changeReleations.remove(rel.getId());
					}
					System.out.println("��ǰ--"+rel.getId()+"-----"+rel);
					rel.setOrder(currentRel.getOrder()+i);
					System.out.println(rel.getId()+"--i---"+i+"----���µ�-ѡ�е�-"+(currentRel.getOrder()+i));
					getResourcePackService().updateResourcePackReleationNotCreateUEB(rel);
					i++;
				}	//�޸ľɵ�
				
				if(changeReleations==null || changeReleations.size()<1)
					return;
				//changeReleations.contains(o)
				//for(ResourcePackReleation rel : changeReleations){	//���¾ɵ�	
				System.out.println("set���ϣ���������"+it);
				for(Integer id : ketSet){
					System.out.println("ids-----"+id);
					ResourcePackReleation rel = changeReleations.get(id);
					System.out.println("----chandu---"+rel.getOrder()+"----id------"+rel);
					rel.setOrder(currentRel.getOrder()+i);
					getResourcePackService().updateResourcePackReleationNotCreateUEB(rel);
					i++;
				}	
				return ;
			}else { //��������λ��
				System.out.println("---����������---");
				ResourcePackReleation downReleation  =   //��ȡ��ǰ��һ����Դ
					getResourcePackService().findBesideDownReleation(pack, lastRel);
				//�鿴��һ����Դ�� ��ȱλ�ã�
				System.out.println("---��һ��-Ӧ���� ---500018000-"+downReleation.getOrder());
				String strOrder = downReleation.getOrder()+"";
				Integer firstOrder = Integer.parseInt(strOrder.substring(0,6));
				Long subCount = getResourcePackService().getSumSubOrder(pack, firstOrder);//��һ����order �µ���Դ����
				System.out.println("---333-");
				count += (1000-subCount);//ʣ�����
				ResourcePackReleation lastReleation =
					getResourcePackService().getMaxSubOrder(pack, firstOrder);
				System.out.println("--44----����----"+count);
				changeOrder(currentRel,lastReleation,sets,pack,count);//����������ȥ
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	public IPropertySelectionModel getPlaceList() {
		Map<String, Integer> authors = new OrderedMap<String, Integer>();
		authors.put("�ö�λ��",1);
		authors.put("ĳһ��Դ��",2);
		authors.put("�õ�λ��",3);
		return new MapPropertySelectModel(authors, false, "");
	} 
	
	public abstract Integer getReleationId();
	public abstract void setReleationId(Integer id);
	

	public abstract Integer getPlace();
	public abstract void setPlace(Integer id);
	
	
	/***************����*************************************
	 */
}
