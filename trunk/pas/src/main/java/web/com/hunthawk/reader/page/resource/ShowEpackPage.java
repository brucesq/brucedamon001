package com.hunthawk.reader.page.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tapestry.IExternalPage;
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
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.FeeXLSData;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.page.util.PageUtil;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;
@Restrict(roles = { "pack" }, mode = Restrict.Mode.ROLE)
public abstract class ShowEpackPage extends SearchPage implements
		IExternalPage, PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();

	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@InjectPage("resource/EditEpackPage")
	public abstract EditEpackPage getEditEpackPage();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	public abstract String getPackname();

	public abstract void setPackname(String name);

	public abstract Integer getPackid();

	public abstract void setPackid(Integer userid);

	public abstract void setResourcePack(Integer type);

	@Persist("session")
	public abstract Integer getResourcePack();

	public abstract String getName();

	public abstract void setName(String name);

	protected void delete(Object object) {
		try {
			getResourcePackService().deleteResourcePack((ResourcePack) object);
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedPacks();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedPacks(Set set);

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		ResourcePack rp = (ResourcePack) getCurrentPack();
		Set selectedPack = getSelectedPacks();
		// 选择了页面组
		if (bSelected) {
			selectedPack.add(rp);
		} else {
			selectedPack.remove(rp);
		}
		// persist value
		setSelectedPacks(selectedPack);

	}

	public boolean getCheckboxSelected() {
		return getSelectedPacks().contains(getCurrentPack());
	}

	public abstract Object getCurrentPack();

	public void onBatchDelete(IRequestCycle cycle) {
		for (Object obj : getSelectedPacks()) {
			delete(obj);
		}
		setSelectedPacks(new HashSet());
		ICallback callback = (ICallback) getCallbackStack()
				.getCurrentCallback();
		callback.performCallback(cycle);

	}

	public void copyPack(IRequestCycle cycle){
		if(getSelectedPacks().size()==0){
			getDelegate().setFormComponent(null);
			getDelegate().record("您至少要选择一个选项!", null);
			setSelectedPacks(new HashSet());
			return;
		}
		List<ResourcePack> list=new ArrayList(getSelectedPacks());
		//执行两项操作，一是：复制批价包，另一个是复制批价包下的资源关联关系
		UserImpl user = (UserImpl) getUser();
		System.out.println("批价的个数--》》"+list.size());
		for(Iterator it=list.iterator();it.hasNext();){
			
			try {
				ResourcePack oldPack = (ResourcePack)it.next();
				ResourcePack newPack = new ResourcePack();
				
				newPack.setName("复制 "+oldPack.getName());
				newPack.setChoice(oldPack.getChoice());
				newPack.setCode(oldPack.getCode());
				newPack.setFeeId(oldPack.getFeeId());
				newPack.setSpid(oldPack.getSpid());
				newPack.setType(oldPack.getType());
				newPack.setCreateTime(new Date());
				newPack.setCreator(user.getId());			
				getResourcePackService().addPack(newPack);
				//根据pack得到 其下的资源关系对应表
				List<ResourcePackReleation>  packReleationList = 
					getResourcePackService().findResourcePackReleationByPack(oldPack);
				System.out.println("批价的资源的个数--》》"+packReleationList.size());
				for(ResourcePackReleation releation : packReleationList ){
					releation.setChoice(releation.getChoice());
					releation.setCpid(releation.getCpid());
					releation.setFeeId(releation.getFeeId());
					releation.setOrder(releation.getOrder());
					releation.setPack(newPack);
					releation.setResourceId(releation.getResourceId());
					releation.setStatus(releation.getStatus());
					getResourcePackService().addResourcePackReleation(releation);
				}			
				
			} catch (Exception e) {
				getDelegate().setFormComponent(null);
				getDelegate().record("批价包复制失败,请联系管理员！", null);
				setSelectedPacks(new HashSet());
			}
		}
		setSelectedPacks(new HashSet());
	}
	public void search() {
	}

	public IPropertySelectionModel getFeeTypeList() {
		Map<String,Integer> map = new OrderedMap<String,Integer>();
		map.put("全部", 0);
		map.put("免费", 5);
		map.put("包月(VIP)", 2);
		map.put("包月(内容控制)", 3);
		map.put("包月(常规)", 4);
		map.put("按次", 1);
		return new MapPropertySelectModel(map);
	}

	public abstract Integer getCreator();
	public abstract void setCreator(Integer id);
	
	public IPropertySelectionModel getCreatorList() {
		List<UserImpl> list = getUserService().getAll(UserImpl.class);
		
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("全部", -1);
		for (UserImpl user : list) {
			map.put(user.getChName(), user.getId());
		}
		MapPropertySelectModel mapPro = new MapPropertySelectModel(map);
		return mapPro;
	}
	
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if (!ParameterCheck.isNullOrEmpty(getPackname())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getPackname() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		 if(getPackid()!=null && getPackid()>0){
			 HibernateExpression useridE = new
			 CompareExpression("id",getPackid(),CompareType.Equal);
			 hibernateExpressions.add(useridE);
		 }
		if(getResourcePack()!=null && getResourcePack()>0){
		HibernateExpression nameC = new CompareExpression("type",
				getResourcePack(), CompareType.Equal);
		hibernateExpressions.add(nameC);
		}
		if(getCreator()!=null && getCreator()>0){
			HibernateExpression nameC = new CompareExpression("creator",
					getCreator(), CompareType.Equal);
			hibernateExpressions.add(nameC);	
		}
		return hibernateExpressions;
	}

	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("resourcePack");
		nameC.setValue(getResourcePack());
		searchConditions.add(nameC);

		return searchConditions;
	}

	public IBasicTableModel getTableModel() {
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

	public IPage onEdit(ResourcePack pack) {
		getEditEpackPage().setModel(pack);
		return getEditEpackPage();
	}

	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getEpackReleationPage();

	public IPage showSource(ResourcePack pack) {
		ShowEpackReleationPage page = getEpackReleationPage();
		page.setPack(pack);
		return page;
	}

	public String getTableColumns() {
		if (getResourcePack() == null || getResourcePack() < 0) {
			setResourcePack(5);
		}
		return getSystemService().getVariables(
				"table:resource_pack_" + getResourcePack()).getValue();
	}

	public void pageBeginRender(PageEvent event) {
		if (getResourcePack() == null || getResourcePack() < 0) {
			setResourcePack(5);
		}

	}

	public ITableColumn getFeename() {
		return new SimpleTableColumn("feename", "计费信息",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePack rp = (ResourcePack) objRow;
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

	public ITableColumn getUsername() {
		return new SimpleTableColumn("username", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePack rp = (ResourcePack) objRow;
						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, rp.getCreator());
						if(user==null)
							return "";
						else{
							if(user.getName() ==null)
								return "";
							else
								return user.getName();
						}
					}

				}, false);

	}

	
	public ITableColumn getResourceCount() {
		return new SimpleTableColumn("resourceCount", "资源数量",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 625300745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourcePack rp = (ResourcePack) objRow;
						
						Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
						HibernateExpression nameE = new CompareExpression("pack", rp,
								CompareType.Equal);
						hibernateExpressions.add(nameE);
						
						Long number = getResourcePackService().getResourcePackReleationCount(hibernateExpressions);	
						return number;
					}

				}, false);

	}
	
	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public void activateExternalPage(java.lang.Object[] parameters,
			IRequestCycle cycle) {

		Integer packType = (Integer) parameters[0];
		this.setResourcePack(packType);
	}

	public String getPackTypeUrl(Integer type) {
		Object[] params = new Object[] { type };
		return PageHelper.getExternalURL(getExternalService(),
				"resource/ShowEpackPage", params);
	}

	protected String creatCSV(Integer userId,
			Set<ResourcePack> set) {
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
			
		    Map<String, FeeXLSData> dataMap = 
		    	new HashMap<String, FeeXLSData>();
		    List<FeeXLSData> dataList = 
		    	new ArrayList<FeeXLSData>();
		    List<String[]> feeDataList = 
		    	new ArrayList<String[]>();
			out = new FileOutputStream(file);
		   // out = new FileOutputStream("d:\\product_info.xls");
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

				 for(ResourcePack pack : set){
					if(pack==null)
						continue;
					if(pack.getType()!=1 && pack.getType()!=2) //非计次、 包月vip
						continue;
					packId = pack.getId().toString();
								
					List<ResourcePackReleation> releationList = 
						getResourcePackService().findResourcePackReleationByPack(pack);
					if(releationList==null || releationList.size()==0)
						continue;
					for(ResourcePackReleation packReleation : releationList){
						if(packReleation==null)
							continue;
						
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
						
						FeeXLSData data = new FeeXLSData();
						ids = 	pro_no+resType+packids+packReleationIds;
						data.setId(ids);
						data.setFeeCode(feeIntNum);
						data.setProName(resourceName);
						String show_url = showURL.replace("{packid}", packId).replace("{releationid}", packReleationId).replace("{resourceid}", resourceId);
						data.setProUrl(show_url);
						data.setSpid(pro_no);
						dataList.add(data);
						
						//--------------以下为生成表格信息代码--------------------
					}
				}
				
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
						//System.out.println("---feeCode------"+feeCode);
						//-----end--------
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
									//int feeCode = changeCode(fee_column.getCode());
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
									//System.out.println("---feeCode--11---"+feeCode);
									
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
					//System.out.println("----------------"+currentData.getId());
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
		if (getSelectedPacks().size() == 0)
			err = "您至少得选择一个";
		if (!ParameterCheck.isNullOrEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			
		} else {
			
				// 生成CSV
				UserImpl user = (UserImpl) getUser();
				String fileDir = this.creatCSV(user.getId(),
						getSelectedPacks());	
				//String fileDir = getResourcePackService().createXLS(user.getId(), getSelectedPacks());
				setSelectedPacks(new HashSet());
				// path是指欲下载的文件的路径。
				  String contextPath = getServletRequest().getContextPath();
				  throw new RedirectException("http://" + PageUtil.getDomainName(getServletRequest().getRequestURL().toString())
		                   + contextPath + "/DownLoadPage.jsp?file="+fileDir);

		}
	}
}