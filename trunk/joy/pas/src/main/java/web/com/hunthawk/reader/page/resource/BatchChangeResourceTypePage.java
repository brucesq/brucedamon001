package com.hunthawk.reader.page.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.simple.ITableColumnEvaluator;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.resource.Comics;
import com.hunthawk.reader.domain.resource.Ebook;
import com.hunthawk.reader.domain.resource.Infomation;
import com.hunthawk.reader.domain.resource.Magazine;
import com.hunthawk.reader.domain.resource.NewsPapers;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.resource.Video;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.enhance.util.ResourceUtil;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.resource.UploadService;

@Restrict(roles = { "resourcechange" }, mode = Restrict.Mode.ROLE)
public abstract class BatchChangeResourceTypePage extends EditPage implements PageBeginRenderListener{
	
	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();
	
	@InjectObject("spring:uploadService")
	public abstract UploadService getUploadService();
	
	/**
	 * 资源的set集合
	 */
	public abstract IUploadFile getUploadFileImage();
	
	public abstract Date getPublishTime();
	public abstract void setPublishTime(Date date);
	
	public abstract Set getResources();
	public abstract void setResources(Set resourceSet);
	
	public abstract Integer getResourceType();
	public abstract void setResourceType(Integer resourceType);
	
	public abstract String getComment();
	public abstract void setComment(String comment);
		
	public abstract String getIntroLon();
	public abstract void setIntroLon(String introLon);
	
	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass(){
		return ResourceAll.class;
	}
	
	public abstract String getPricepackName();
	public abstract void setPricepackName(String pricepackName);
	
	@Override
	protected boolean persist(Object object) {
		String error="";
		try{
			//getSelectedResourceTypes(); //确认选定
			//getDelSelectedResourceTypes();//确认删除
			//getResources();//资源列表
			//List delType = getDelSelectedResourceTypes();
			//List addType = getSelectedResourceTypes();
			
			Set resourceList = getResources();
			if(resourceList==null)
				return false;
			Iterator it = resourceList.iterator();
			while(it.hasNext()){
				ResourceAll resourceAll = (ResourceAll)it.next();
				if(delSelectedResourceType!=null && delSelectedResourceType.size()>0){ //删除分类
					for(int i=0;i<delSelectedResourceType.size();i++){
						ResourceType type = (ResourceType) delSelectedResourceType.get(i);
						getResourceService().deleteResourceResTypeByRT(resourceAll.getId(),type.getId());
					}
				}
				if(selectedResourceType!=null && selectedResourceType.size()>0){ //增加分类
					for(int i=0;i<selectedResourceType.size();i++){
						ResourceType type = (ResourceType) selectedResourceType.get(i);
						ResourceResType rel = new ResourceResType();
						rel.setRid(resourceAll.getId());
						rel.setResTypeId(type.getId());
						getResourceService().addResourceResTypeByUnique(rel);
					}					
				}
				
				if(getPublishTime()!=null){
					resourceAll.setPublishTime(getPublishTime());
				}
				if(getUploadFileImage()!=null){  //处理封面图片
					// 上传了封面图片
					String errorMessage="";
					File dir = new File(getResourceService().getChapterAddress(
							resourceAll.getId()));
					if (!dir.exists())
						dir.mkdirs();
					String uploadfileName = getUploadFileImage().getFileName()
							.substring(
									getUploadFileImage().getFileName()
											.lastIndexOf("\\") + 1);

					boolean resIsRightFormat = isRightFormat(uploadfileName);
					if (!resIsRightFormat) {
						// 文件格式判断
						throw new Exception("图片格式不正确[gif/jpg/png]，请重新选择!");
					}
					if (dir.exists()) { // 删除文件
						for (File file : dir.listFiles()) {
							if (file.getName().startsWith("cover")) {
								getResourceService()
										.deleteFile(file.toString());
								//同步删除图片：
								getResourceService().rsyncUploadFile(ResourceUtil.RSYNC_TYPE_DEL, new String[]{file.getAbsolutePath()});
							}
						}
					}
					errorMessage = getResourceService().upload(
							getUploadFileImage(), "", dir);
					if (!"".equals(errorMessage))
						throw new Exception(errorMessage);
					
					File file = new File(dir+File.separator+uploadfileName);//上传到服务器的文件名称
					String fileName = "cover."+uploadfileName.split("\\.")[1];
				
					File newFile = new File(dir+File.separator+fileName);
					file.renameTo(newFile);
					
					getUploadService().resizeCoverFile(dir.toString(),
							fileName);					
					//同步图片：
					getUploadService().rsyncDirectry(dir);
					
					if(resourceAll instanceof Ebook){
						Ebook book = (Ebook) resourceAll;
						book.setBookPic(fileName);
					}else if(resourceAll instanceof Magazine){
						Magazine magazine = (Magazine)resourceAll;
						magazine.setImage(fileName);
					}else if(resourceAll instanceof NewsPapers){
						NewsPapers newsPapers = (NewsPapers)resourceAll;
						newsPapers.setImage(fileName);
					}else if(resourceAll instanceof Comics){
						Comics comics = (Comics)resourceAll;
						comics.setImage(fileName);
					}	else if(resourceAll instanceof Video){
						Video comics = (Video)resourceAll;
						comics.setImage(fileName);
					}	else if(resourceAll instanceof Infomation){
						Infomation comics = (Infomation)resourceAll;
						comics.setImage(fileName);
					}													
				}
				UserImpl user = (UserImpl) getUser();
				resourceAll.setModifierId(user.getId());
				if(getComment()!=null &&!"".equals(getComment())){
					resourceAll.setCComment(getComment());
				}
				if(getIntroLon()!=null &&!"".equals(getIntroLon())){
					resourceAll.setIntroLon(getIntroLon());
				}
				getResourceService().updateResource(resourceAll, 0);
			}
			
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	
	public void pageBeginRender(PageEvent event){
		if(getModel() == null){
			setModel(new ResourceAll());
		}
	}
	
	public   IBasicTableModel getTableModel()
		{
			return new IBasicTableModel()
			{
				public int getRowCount()
				{
					return getResources().size();
				}
				public Iterator getCurrentPageRows(int nFirst, int nPageSize,
						ITableColumn objSortColumn, boolean bSortOrder)
				{
					//List<ResourceAll> list  = new ArrayList<ResourceAll>();
					//list.addAll(getResources());
					return  getResources().iterator();
				}
			};
		}
	
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		Integer resourceType =1;
		if(getResourceType()!=null)
			resourceType = getResourceType();
		HibernateExpression nameE = new CompareExpression("showType", resourceType,
				CompareType.Equal);
		hibernateExpressions.add(nameE);

		return hibernateExpressions;
	}

	public IPropertySelectionModel getPrivileges() {
		List<ResourceType> resourcetype = getResourceService()
				.findResourceTypeBy(1, Integer.MAX_VALUE, "id", false,
						getSearchExpressions());
		//过滤掉父类对象，只放子类的对象------------------------------
		Set<ResourceType> parentType = new HashSet<ResourceType>();
		for(ResourceType type:resourcetype){
			if(type.getParent()!=null){
				parentType.add(type.getParent());
			}				
		}	
		if(parentType!=null){
		for(ResourceType parent: parentType){
			resourcetype.remove(parent);
		}
		}
		//------------------------------------------------------
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				resourcetype, ResourceType.class, "getName", "getId", false,
				null);
		return model;
	}
	
	@InjectComponent("roleList")
	public abstract Block getRoleList();

	@InjectComponent("roleExist")
	public abstract Block getRoleExist();

/*	public abstract void setSelectedResourceTypes(List resourcetypes);

	public abstract List getSelectedResourceTypes(); */
	private List selectedResourceType;

	public void setSelectedResourceTypes(List resourcetypes) {
		selectedResourceType = resourcetypes;
	}

	public List getSelectedResourceTypes() {
			return new ArrayList();
	}
	
	public List delSelectedResourceType;
	
	public void setDelSelectedResourceTypes(List resourcetypes){
		delSelectedResourceType = resourcetypes;
	}

	public List getDelSelectedResourceTypes(){
		return new ArrayList();
	} 
	
	public ITableColumn getSort() {
		return new SimpleTableColumn("sort", "类别", new ITableColumnEvaluator() {

			private static final long serialVersionUID = 625300745851970L;

			public Object getColumnValue(ITableColumn objColumn, Object objRow) {
				ResourceAll rp = (ResourceAll) objRow;
				StringBuffer type = new StringBuffer();
				// 根据资源ID查询资源子类型
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("rid", rp
						.getId(), CompareType.Equal);
				expressions.add(ex);
				List<ResourceResType> list = getResourceService()
						.findResourceResTypeBy(1, Integer.MAX_VALUE, "rid", false,
								expressions);
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


	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage getShowEbookPage();
	
	public IPage savePage(IRequestCycle cycle){
		if(save()){
			return getShowEbookPage();
		}else
			return this;
	}
	
	public IPage cancelPage(IRequestCycle cycle) {	
		//getShowEbookPage().setPageGroup(pg);
		return getShowEbookPage();
	}
	// 图片格式
	public boolean isRightFormat(String fileName) {
		String patt = "\\.(jpg|gif|png|bmp)$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}
}
