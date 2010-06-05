/**
 * 
 */
package com.hunthawk.reader.page.bussiness;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NotNullExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Columns;
import com.hunthawk.reader.domain.bussiness.DefaultTemplateSet;
import com.hunthawk.reader.domain.bussiness.PageGroup;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.resource.Material;
import com.hunthawk.reader.domain.resource.MaterialCatalog;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.BussinessService;
import com.hunthawk.reader.service.bussiness.TemplateService;
import com.hunthawk.reader.service.resource.MaterialService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "columnchange" }, mode = Restrict.Mode.ROLE)
public abstract class EditColumnPage extends EditPage implements
		PageBeginRenderListener {
	@InjectObject("spring:bussinessService")
	public abstract BussinessService getBussinessService();

	@InjectObject("spring:templateService")
	public abstract TemplateService getTemplateService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	/** add by liuxh 09-12-23 */
	@InjectObject("spring:materialService")
	public abstract MaterialService getMaterialService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	public abstract IUploadFile getUploadFile();

	public abstract Integer getImageId();

	public abstract void setImageId(Integer imageId);

	public abstract void setImgUrl(String url);

	public abstract String getImgUrl();

	public abstract String getReturnValue();

	public abstract void setReturnValue(String value);

	public abstract void setNeedReturn(Boolean bReturn);

	public abstract Boolean getNeedReturn();

	/** end */

	public abstract Columns getParent();

	public Columns getRelParent(){
		if(getParentColumnId() != null){
			return this.getBussinessService().getColumn(getParentColumnId());
		}
		return null;
	}
	
	public abstract void setParent(Columns parent);

	public abstract PageGroup getPageGroup();

	public abstract void setPageGroup(PageGroup p);

	public abstract Integer getShowType();

	public abstract void setShowType(Integer showType);

	public abstract Integer getResourceType();

	public abstract void setResourceType(Integer resourceType);

	public abstract Integer getResourceTypeId();

	public abstract void setResourceTypeId(Integer resourceTypeId);

	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return Columns.class;
	}

	/** add by liuxh 09-12-23 */
	public boolean isRightFormat(String fileName) {
		MaterialCatalog catalog = getMaterialService().getMaterialCatalog(
				Integer.parseInt(getSystemService().getVariables(
						"default_pic_material_catalog_id").getValue()));
		String patt = "";
		if (catalog.getType().equals(MaterialCatalog.TYPE_IMAGE)) {
			patt = "\\.(jpg|gif|png|bmp)$";
		} else if (catalog.getType().equals(MaterialCatalog.TYPE_MUSIC)) {
			patt = "\\.(mid|mp3|wma|wav)$";
		} else {
			return false;
		}

		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}

	public void uploadMaterial(IRequestCycle cycle) {
		// -------------上传图片开始------------
		try {
			if (getUploadFile() != null) {
				File entryFile = null;
				String fileName = getUploadFile().getFileName().substring(
						getUploadFile().getFileName().lastIndexOf("\\") + 1);

				boolean isRightFormat = isRightFormat(getUploadFile()
						.getFileName());
				if (!isRightFormat) {

					ValidationDelegate delegate = getDelegate();
					delegate.setFormComponent(null);
					delegate
							.record(
									"文件格式不正确[图片素材只能为gif或jpg或png或bmp,音乐素材只能是mid或mp3或wma或wav]，请重新选择",
									null);
					return;
				}
				Variables var = getSystemService().getVariables("upload_dir");
				File dir = new File(var.getValue());
				if (!dir.exists())
					dir.mkdirs();
				getResourceService().upload(getUploadFile(), "", dir);
				entryFile = new File(dir, fileName);

				Material matr = new Material();
				matr.setCatalogId(Integer.parseInt(getSystemService()
						.getVariables("default_pic_material_catalog_id")
						.getValue()));
				if (StringUtils.isEmpty(matr.getName())) {
					matr.setName("默认");
				}
				matr.setCreateTime(new Date());
				matr.setCreator(getUser().getId());
				matr.setModifyTime(new Date());
				matr.setModifier(getUser().getId());
				getMaterialService().addMaterial(matr);

				Variables var1 = getSystemService().getVariables("media_dir");
				String fileType = fileName.substring(
						fileName.lastIndexOf(".") + 1).toLowerCase();
				matr.setExtName(fileType);

				matr.setFilename("material/"
						+ String.valueOf(matr.getId() / 1000) + "/"
						+ matr.getId());

				File destFile = new File(var1.getValue() + "material"
						+ File.separator + String.valueOf(matr.getId() / 1000)
						+ File.separator + matr.getId() + "."
						+ matr.getExtName());

				FileUtils.copyFile(entryFile, destFile);
				getResourceService().rsyncUploadFile(
						ResourceUtil.RSYNC_TYPE_ADD,
						new String[] { destFile.getAbsolutePath() });

				matr.setSize((int) destFile.length());
				FileUtils.forceDeleteOnExit(entryFile);

				getMaterialService().updateMaterial(matr);
				setImageId(matr.getId());
				String url = getSystemService().getVariables("media_url")
						.getValue();
				Material mater = getMaterialService().getMaterial(getImageId());
				setImgUrl(url + mater.getFilename() + "." + mater.getExtName());
				setReturnValue("");
				setNeedReturn(Boolean.FALSE);
				return;
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}

	}

	/** end */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {
		try {
			Columns o = (Columns) object;
			if (isModelNew()) {
				Date date = new Date();
				o.setCreateTime(date);
				o.setModifyTime(date);
				o.setModifier(getUser().getId());
				o.setCreator(getUser().getId());
				o.setParent(getRelParent());
				o.setPagegroup(getPageGroup());
				o.setStatus(1);
				o.setCountSet(100);
				
				/** modify by liuxh 09-12-23 */
				if (getImageId() != null) {
					// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>icon
					// ->"+getImageId().toString());
					o.setIcon(getImageId().toString());
				}
				/** end */
				// o.setOrder(100);
				o.setTitle(o.getName());
				// o.setColumnType(getColumnType());
				o.setCreateType(0);
				if (getResourceTypeId() != 0 && getResourceTypeId() != null)
					o.setResourceTypeId(getResourceTypeId());
				if (!verifyTemplateNeed(o.getColOneTempId())) {
					// 查询默认的栏目模版 1.x
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 1 // o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.COLUMN_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setColOneTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("栏目默认模板不存在,请先进行默认模版设置！");
					}

				}
				if (!verifyTemplateNeed(o.getResOneTempId())) {
					// 查询默认的资源模版 1.x
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 1// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.RESOURCE_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setResOneTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("资源默认模板不存在,请先进行默认模版设置！");
					}

				}

				if (!verifyTemplate(o.getColSecondTempId())) {
					// wap 2.0 栏目模版
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 2// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.COLUMN_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setColSecondTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("默认栏目WAP2.0模板不存在,请先进行默认模版设置！");
					}

				}

				if (!verifyTemplate(o.getColThirdTempId())) {
					// 3g栏目模版
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 3// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.COLUMN_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setColThirdTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("默认栏目3G模板不存在,请先进行默认模版设置！");
					}

				}

				if (!verifyTemplate(o.getResSecondTempId())) {
					// wap2.0
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 2// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.RESOURCE_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setResSecondTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("默认资源WAP2.0模板不存在,请先进行默认模版设置！");
					}

				}

				if (!verifyTemplate(o.getResThirdTempId())) {
					// 3g
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 3// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.RESOURCE_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setResThirdTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("默认资源3G模板不存在,请先进行默认模版设置！");
					}

				}

				if (!verifyTemplateNeed(o.getDelOneTempId())) {
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 1// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.DETAIL_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setDelOneTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("详情默认模板不存在,请先进行默认模版设置！");
					}
				}

				if (!verifyTemplate(o.getDelSecondTempId())) {
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 2// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.DETAIL_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setDelSecondTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("默认详情WAP2.0模板不存在,请先进行默认模版设置！");
					}

				}
				if (!verifyTemplate(o.getDelThirdTempId())) {
					Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
					HibernateExpression ex = new CompareExpression("wapType", 3// o.getPagegroup().getShowType()
							, CompareType.Equal);
					expressions.add(ex);
					HibernateExpression ex2 = new CompareExpression("pageType",
							TemplateType.DETAIL_PAGE, CompareType.Equal);
					expressions.add(ex2);
					List<DefaultTemplateSet> defaultTemplates = getTemplateService()
							.getDefaultTemplateSetList(1, Integer.MAX_VALUE,
									"templateId", false, expressions);
					if (defaultTemplates != null && defaultTemplates.size() > 0) {
						o.setDelThirdTempId(defaultTemplates.get(0)
								.getTemplateId());
					} else {
						throw new Exception("默认详情3G模板不存在,请先进行默认模版设置！");
					}
				}

				Integer order = getBussinessService().getMaxMinOrder(
						getParent(), getPageGroup(), "max");
				o.setOrder(order + 5);
				getBussinessService().addColumn(o);
			} else {
				o.setTitle(o.getName());
				o.setModifyTime(new Date());
				o.setModifier(getUser().getId());
				if(!isRightParent(getParentColumnId())){
					throw new Exception("您选择的父栏目不正确，请您重新选择");
				}
				o.setParent(getRelParent());
				/** modify by liuxh 09-12-23 */
				if (getImageId() != null) {
					o.setIcon(getImageId().toString());
				} else {
					o.setIcon("");
				}
				/** end */
				if (getResourceTypeId() != 0 && getResourceTypeId() != null)
					o.setResourceTypeId(getResourceTypeId());
				getBussinessService().updateColumn(o);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);

			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Columns());
			if(getParent()!= null){
				setParentColumnId(getParent().getId());
			}
		} else {
			Columns c = (Columns) getModel();
			if(c.getParent() != null){
				setParentColumnId(c.getParent().getId());
			}
			/** modify by liuxh 09-12-23 */
			// System.out.println("init >>>>>>>>>>>>"+getImageId());
			if (c.getIcon() != null && !StringUtils.isEmpty(c.getIcon())) {
				setImageId(getImageId() == null ? Integer.parseInt(c.getIcon())
						: getImageId());
			}
			/** end */
			if (c.getResourceTypeId() != null) {
				setResourceTypeId(c.getResourceTypeId());
				ResourceType type = getResourceService().getResourceType(
						c.getResourceTypeId());
				if (type != null)
					setResourceType(type.getShowType());
			}
		}

	}

	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();

	public String getTemplateURL(int templateType, int wapType, String pageid) {
		IEngineService service = getExternalService();

		TemplateType type = new TemplateType();
		type.setId(templateType);
		Object[] params = new Object[] { pageid, type, wapType,
				getPageGroup().getShowType() };
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/TemplateChoicePage", params);

		return templateURL;
	}

	private boolean verifyTemplate(Integer id) {
		if (id == null || id == 0) {
			return true;
		}
		if (getTemplateService().getTemplate(id) == null)
			return false;
		return true;
	}

	private boolean verifyTemplateNeed(Integer id) {
		if (id == null || id == 0) {
			return false;
		}
		if (getTemplateService().getTemplate(id) == null)
			return false;
		return true;
	}

	public String getPackURL() {

		IEngineService service = getExternalService();

		Object[] params = new Object[] { "pricepackId" };
		String templateURL = PageHelper.getExternalFunction(service,
				"resource/PricePackChoicePage", params);

		return templateURL;

	}

	public IPropertySelectionModel getOrderTypeList() {
		return new MapPropertySelectModel(Constants.getOrderTypeMap());
	}

	public IPropertySelectionModel getColumnTypeList() {

		Map<String, Integer> totals = new OrderedMap<String, Integer>();
		totals.put("普通", 0);
		totals.put("分类", 1);
		totals.put("搜索", 2);
		return new MapPropertySelectModel(totals);
	}

	public String getParentColumnURL(){
		IEngineService service = getExternalService();

		
		Object[] params = new Object[] { "columnParentId",this.getPageGroup(),this.getModel() };
		if(isModelNew()){
			params[2] = null;
		}
		String templateURL = PageHelper.getExternalFunction(service,
				"bussiness/ParentColumnChoicePage", params);

		return templateURL;
	}
	
	public abstract  Integer getParentColumnId();
	public abstract void setParentColumnId(Integer columnId);
	
//	public IPropertySelectionModel getParentColumnList(){
//		List<Columns> columns = getBussinessService().findColumns(1, 10000, "id", true, new ArrayList());
//		if(!isModelNew()){
//			List<HibernateExpression> ex = new ArrayList<HibernateExpression>();
//			ex.add(new CompareExpression("parent",getModel(),CompareType.Equal));
//			List<Columns> childs = getBussinessService().findColumns(1, 10000, "id", true, ex);
//			columns.removeAll(childs);
//			columns.remove(getModel());
//		}
//		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
//				columns, Columns.class, "getName", "getId", true,
//				"");
//		return model;
//	}
	
	private boolean isRightParent(Integer cid){
		
		if(!isModelNew()){
	        if(((Columns)getModel()).getId().equals(cid)){
	        	return false;
	        }
			List<HibernateExpression> ex = new ArrayList<HibernateExpression>();
			ex.add(new CompareExpression("parent",getModel(),CompareType.Equal));
			List<Columns> childs = getBussinessService().findColumns(1, 10000, "id", true, ex);
			for(Columns column : childs){
				if(column.getId().equals(cid)){
					return false;
				}
			}
		}
		return true;
		
	}
	
	public IPropertySelectionModel getResourceTypeList() {

		Map<String, Integer> totals = new OrderedMap<String, Integer>();
		totals.put("", 0);
		totals.put("图书", 1);
		totals.put("报纸", 2);
		totals.put("杂志", 3);
		totals.put("漫画", 4);
		totals.put("铃声", 5);
		totals.put("视频", 6);
		totals.put("资讯", 7);
		totals.put("软件", 8);
		return new MapPropertySelectModel(totals);
	}

	public IPropertySelectionModel getResourceTypeIdList() {
		Map<String, Integer> totals = new OrderedMap<String, Integer>();
		totals.put("", 0);
		if (getModel() != null) {
			Columns c = (Columns) getModel();
			if (c.getResourceTypeId() != null) {
				ResourceType type = getResourceService().getResourceType(
						c.getResourceTypeId());
				if(type != null)
						totals.put(type.getName(), type.getId());
			}

		}

		if (getResourceType() != null) {
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("showType",
					getResourceType(), CompareType.Equal);
			expressions.add(ex);

			List<ResourceType> list = getResourceService().findResourceTypeBy(
					1, Integer.MAX_VALUE, "id", false, expressions);
			// -----过滤掉父类别----
			Set<ResourceType> parentType = new HashSet<ResourceType>();
			for (ResourceType type : list) {
				if (type.getParent() != null) {
					parentType.add(type.getParent());
				}
			}
			if (parentType != null) {
				for (ResourceType parent : parentType) {
					list.remove(parent);
				}
			}
			List<Integer> resourceTypeIdList1 = new ArrayList<Integer>();
			for (ResourceType type : list) {
				resourceTypeIdList1.add(type.getId());
			}
			// -----过滤掉已经有栏目的分类-------------------------
			Collection<HibernateExpression> expressions1 = new ArrayList<HibernateExpression>();
			if (getPageGroup() != null) {
				HibernateExpression ex1 = new CompareExpression("pagegroup",
						getPageGroup(), CompareType.Equal);
				expressions1.add(ex1);
			}
			NotNullExpression pE = new NotNullExpression("resourceTypeId");
			expressions1.add(pE);

			List<Columns> columns = getBussinessService().findColumns(1,
					Integer.MAX_VALUE, "id", false, expressions1);
			for (Columns column : columns) {
				if (resourceTypeIdList1.contains(column.getResourceTypeId()))
					resourceTypeIdList1.remove(column.getResourceTypeId());
			}
			List<ResourceType> lastList = new ArrayList<ResourceType>();
			if (resourceTypeIdList1 != null) {// 表明有些还是可以的
				for (int i = 0; i < resourceTypeIdList1.size(); i++) {
					lastList.add(getResourceService().getResourceType(
							resourceTypeIdList1.get(i)));
				}
			}
			if (lastList != null) {
				for (ResourceType type : lastList) {
					totals.put(type.getName(), type.getId());
				}
			}
		}
		return new MapPropertySelectModel(totals);
	}

	/** add by liuxh 09-12-23 */
	public String getChooseImageURL() {
		IEngineService service = getExternalService();
		Object[] params = new Object[3];
		params[0] = "imgid";
		params[1] = "imgurl";
		params[2] = String.valueOf(MaterialCatalog.TYPE_IMAGE);
		String resURL = PageHelper.getExternalFunction(service,
				"resource/MaterialChoosePage", params);

		return resURL;
	}
	/** end */
	// /**
	// *
	// * @param value1
	// * 首页模板/资源模板 标识 1=首页 2=资源
	// * @param value2
	// * 模板版本id (1=wap1.x 2=wap2.0 3=3g)
	// * @param pams
	// * 返回字段参数名称
	// * @return
	// */
	// public String getTemplateSetURLByParams(Integer typeId, Integer value2,
	// String pams) {
	//
	// IEngineService service = getExternalService();
	//
	// TemplateType type = new TemplateType();
	// type.setId(typeId);
	// Object[] params = new Object[] { pams, type, value2,
	// ((Columns) getModel()).getPagegroup().getShowType() };
	// String templateURL = PageHelper.getExternalFunction(service,
	// "bussiness/TemplateChoicePage", params);
	//
	// return templateURL;
	// }
}
