package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
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

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NullExpression;
import com.hunthawk.framework.tapestry.SearchCondition;
import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author xianlaichen
 *
 */
@Restrict(roles = { "resourcetype" }, mode = Restrict.Mode.ROLE)
public abstract class ShowResourceTypePage extends SearchPage{

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectPage("resource/EditResourceTypePage")
	public abstract EditResourceTypePage getEditResourceTypePage();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@Override
	protected  void delete(Object object)
	{
		try{
			ResourceType resourceType=(ResourceType)object;
			//增加判断 分类下面有没有资源和子分类，如果有的话提示错误不允许删除
			//判断是否有子分类
			
			boolean allowDelete=true;
			//查询是否有子分类  将ID赋值为parentID 根据parentId进行查询   如果查询结果不为空 则表示有子分类
			Collection<HibernateExpression> expressions2 = new ArrayList<HibernateExpression>();
			HibernateExpression ex2 = new CompareExpression("parent", getResourceService().getResourceType(resourceType.getId())
					, CompareType.Equal);
			expressions2.add(ex2);
			List<ResourceType> list=getResourceService().findResourceTypeBy(1, Integer.MAX_VALUE, "id", false, expressions2);
			if(list!=null && list.size()>0){//说明有子分类
				allowDelete=false;
			}
			if(allowDelete){
				getResourceService().deleteResourceType((ResourceType)object);
			}else{
				getDelegate().setFormComponent(null);
				getDelegate().record("该分类不能被删除,该分类可能包含子分类!", null);
			}
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		}
	}
	public IPage onEdit(ResourceType resourcetype)
	{
		int type=resourcetype.getShowType();
		String name="";
		if(type==ResourceType.TYPE_BOOK){
			name="图书";
		}else if(type==ResourceType.TYPE_COMICS){
			name="漫画";
		}else if(type==ResourceType.TYPE_MAGAZINE){
			name="杂志";
		}else if(type==ResourceType.TYPE_NEWSPAPERS){
			name="报纸";
		}else if(type==ResourceType.TYPE_SOUND){
			name="铃声";
		}else if(type==ResourceType.TYPE_VIDEO){
			name="视频";
		}else if(type==ResourceType.TYPE_APPLICATION){
			name="软件";
		}else{
			name=getResourceService().getResourceType(resourcetype.getId()).getName();
		}
		getEditResourceTypePage().setModel(resourcetype);
		getEditResourceTypePage().setShowTypeName(name);
		getEditResourceTypePage().setShowType(resourcetype.getShowType());
		getEditResourceTypePage().setParent(resourcetype.getParent());
		return getEditResourceTypePage();
	}
	
	public IPage onAddResourceType() {
		EditResourceTypePage page = getEditResourceTypePage();
		return page;
	}
	
	public IPage onAddResourceType(ResourceType parent) {
		System.out.println(parent.getShowType()+":"+parent.getId());
		EditResourceTypePage page = getEditResourceTypePage();
	    page.setParent(parent);
	    page.setShowType(parent.getShowType());
		return page;
	}
	public IPage searchByType(String showType){
		if(showType==null){
			showType=ResourceType.TYPE_BOOK.toString();
		}
		this.setShowType(Integer.parseInt(showType));
		this.setParent(getParent());
		return this;
	}
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedResourceType();

	public abstract void setSelectedResourceType(Set set);
	
	public abstract Object getCurrentObject();
	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected)
	{
		ResourceType pro = (ResourceType)getCurrentProduct();
		Set selectedPros = getSelectedResourceType();
		// 选择了用户
		if (bSelected)
		{
			selectedPros.add(pro);
		}
		else
		{
			selectedPros.remove(pro);
		}
		// persist value
		setSelectedResourceType(selectedPros);
		
	}

	public boolean getCheckboxSelected()
	{
		return getSelectedResourceType().contains(getCurrentProduct());
	}


	public void onBatchDelete(IRequestCycle cycle)
	 {
		for(Object obj : getSelectedResourceType())
		{
			delete(obj);
		}
		setSelectedResourceType(new HashSet());
		ICallback callback = (ICallback) getCallbackStack().getCurrentCallback();
	    callback.performCallback(cycle);
	 }
	//产品名称
	public abstract String getName();
	public abstract void setName(String name);
	
	public abstract Integer getShowType();
	public abstract void setShowType(Integer showType);
	public  Collection<HibernateExpression> getSearchExpressions()
	{
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		if(!ParameterCheck.isNullOrEmpty(getName()))
		{
			HibernateExpression nameE = new CompareExpression("name","%"+getName()+"%",CompareType.Like);
			hibernateExpressions.add(nameE);
		}
		HibernateExpression typeE = new CompareExpression("showType", getShowType()==null?ResourceType.TYPE_VIDEO:getShowType(),
					CompareType.Equal);
		hibernateExpressions.add(typeE);
		if(getParent()==null){
			NullExpression  pE=new NullExpression("parent");
			hibernateExpressions.add( pE);
		}else{
			HibernateExpression parentE = new CompareExpression("parent",getParent(),
					CompareType.Equal);
			hibernateExpressions.add(parentE);
		}
		return hibernateExpressions;
	}
	public void search(){
	}
	@Override
	public  List<SearchCondition> getSearchConditions()
	{
		
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);
		
		SearchCondition nameS = new SearchCondition();
		nameS.setName("showType");
		nameS.setValue(getShowType());
		searchConditions.add(nameS);
		
		SearchCondition nameP = new SearchCondition();
		nameP.setName("parent");
		nameP.setValue(getParent());
		searchConditions.add(nameP);
		return searchConditions;
	}

	public IPropertySelectionModel getAuthorStatusList(){
		return new MapPropertySelectModel(Constants.getAuthorStatus());
	}
	
	public   IBasicTableModel getTableModel()
	{
		return new IBasicTableModel()
		{
			public int getRowCount()
			{
				return getResourceService().getResourceTypeResultCount( getSearchExpressions()).intValue();
			}
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder)
			{
				int pageNo = nFirst / nPageSize;
				pageNo++;
				return getResourceService().findResourceTypeBy(pageNo, nPageSize, "id", false,  getSearchExpressions()).iterator();
			}
		};
	}
	
	public abstract ResourceType getParent();

	public abstract void setParent(ResourceType parent);
	
	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage getShowEbookPage();
	public IPage showChildResourceType(ResourceType resourceType){
		//判断是否有子分类
		boolean haveChilds=false;
		//查询是否有子分类  将ID赋值为parentID 根据parentId进行查询   如果查询结果不为空 则表示有子分类
		Collection<HibernateExpression> expressions2 = new ArrayList<HibernateExpression>();
		HibernateExpression ex2 = new CompareExpression("parent", getResourceService().getResourceType(resourceType.getId())
				, CompareType.Equal);
		expressions2.add(ex2);
		List<ResourceType> list=getResourceService().findResourceTypeBy(1, Integer.MAX_VALUE, "id", false, expressions2);
		if(list!=null && list.size()>0){//说明有子分类
			haveChilds=true;
		}else{//没有子分类 则要判断是否有资源
			//根据分类 ID判断该分类下是否有资源
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("resTypeId", resourceType.getId()
					, CompareType.Equal);
			expressions.add(ex);
			List<ResourceResType> rrts=getResourceService().findResourceResTypeBy(1, Integer.MAX_VALUE, "rid", false, expressions);
			if(rrts==null || rrts.size()<1){//说明子分类下面没有资源
				haveChilds=true;
			}
		}
		if(haveChilds){
			//进入子资源分类
				this.setParent(resourceType);
				this.setShowType(resourceType.getShowType());
				return this;
		}else{
			//进入资源列表
			ShowEbookPage page =getShowEbookPage();
			page.setResourceType(resourceType.getShowType());//大类
			page.setSortId(resourceType.getId());
			page.setAuthorId(0);
			page.setOtherPageToHere(true);
			return page;
		}
	}
	/**
	 * 获得当前产品
	 * 
	 * @return
	 */
	public abstract Object getCurrentProduct();
	

	
	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "创建者",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceType o1 = (ResourceType) objRow;

						UserImpl user = (UserImpl) getUserService().getObject(
								UserImpl.class, o1.getCreatorId());
						if (user == null) {
							return "";
						} else {
							return user.getName();
						}

					}

				}, false);

	}
	
	public Long getResourceCount(ResourceType type,Long number){ //递归计算方法
		//int number=0;
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression name = new CompareExpression("parent", type,
				CompareType.Equal);
		expressions.add(name);
		List<ResourceType> listType= getResourceService().findResourceTypeBy(1, 100, "id", false, expressions);
		if(listType!=null && listType.size()>0){
			for(int i=0;i<listType.size();i++){
			ResourceType typeNext = listType.get(i);
			number = getResourceCount(typeNext,number);
			}
		}else{				
			Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
			HibernateExpression nameE = new CompareExpression("resTypeId", type.getId(),
					CompareType.Equal);
			hibernateExpressions.add(nameE);
			number += getResourceService().getResourceTypeResResultCount(hibernateExpressions);
		}
		return number;
	}
	public ITableColumn getResourceCount() {
		return new SimpleTableColumn("resourceCount", "资源数量",
				new ITableColumnEvaluator() {

					private static final long serialVersionUID = 31491600745851970L;

					public Object getColumnValue(ITableColumn objColumn,
							Object objRow) {
						ResourceType o1 = (ResourceType) objRow;
						
						return getResourceCount(o1,0L);
					}

				}, false);

	}
}
