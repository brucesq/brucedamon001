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
			//�����ж� ����������û����Դ���ӷ��࣬����еĻ���ʾ��������ɾ��
			//�ж��Ƿ����ӷ���
			
			boolean allowDelete=true;
			//��ѯ�Ƿ����ӷ���  ��ID��ֵΪparentID ����parentId���в�ѯ   �����ѯ�����Ϊ�� ���ʾ���ӷ���
			Collection<HibernateExpression> expressions2 = new ArrayList<HibernateExpression>();
			HibernateExpression ex2 = new CompareExpression("parent", getResourceService().getResourceType(resourceType.getId())
					, CompareType.Equal);
			expressions2.add(ex2);
			List<ResourceType> list=getResourceService().findResourceTypeBy(1, Integer.MAX_VALUE, "id", false, expressions2);
			if(list!=null && list.size()>0){//˵�����ӷ���
				allowDelete=false;
			}
			if(allowDelete){
				getResourceService().deleteResourceType((ResourceType)object);
			}else{
				getDelegate().setFormComponent(null);
				getDelegate().record("�÷��಻�ܱ�ɾ��,�÷�����ܰ����ӷ���!", null);
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
			name="ͼ��";
		}else if(type==ResourceType.TYPE_COMICS){
			name="����";
		}else if(type==ResourceType.TYPE_MAGAZINE){
			name="��־";
		}else if(type==ResourceType.TYPE_NEWSPAPERS){
			name="��ֽ";
		}else if(type==ResourceType.TYPE_SOUND){
			name="����";
		}else if(type==ResourceType.TYPE_VIDEO){
			name="��Ƶ";
		}else if(type==ResourceType.TYPE_APPLICATION){
			name="���";
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
		// ѡ�����û�
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
	//��Ʒ����
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
		//�ж��Ƿ����ӷ���
		boolean haveChilds=false;
		//��ѯ�Ƿ����ӷ���  ��ID��ֵΪparentID ����parentId���в�ѯ   �����ѯ�����Ϊ�� ���ʾ���ӷ���
		Collection<HibernateExpression> expressions2 = new ArrayList<HibernateExpression>();
		HibernateExpression ex2 = new CompareExpression("parent", getResourceService().getResourceType(resourceType.getId())
				, CompareType.Equal);
		expressions2.add(ex2);
		List<ResourceType> list=getResourceService().findResourceTypeBy(1, Integer.MAX_VALUE, "id", false, expressions2);
		if(list!=null && list.size()>0){//˵�����ӷ���
			haveChilds=true;
		}else{//û���ӷ��� ��Ҫ�ж��Ƿ�����Դ
			//���ݷ��� ID�жϸ÷������Ƿ�����Դ
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("resTypeId", resourceType.getId()
					, CompareType.Equal);
			expressions.add(ex);
			List<ResourceResType> rrts=getResourceService().findResourceResTypeBy(1, Integer.MAX_VALUE, "rid", false, expressions);
			if(rrts==null || rrts.size()<1){//˵���ӷ�������û����Դ
				haveChilds=true;
			}
		}
		if(haveChilds){
			//��������Դ����
				this.setParent(resourceType);
				this.setShowType(resourceType.getShowType());
				return this;
		}else{
			//������Դ�б�
			ShowEbookPage page =getShowEbookPage();
			page.setResourceType(resourceType.getShowType());//����
			page.setSortId(resourceType.getId());
			page.setAuthorId(0);
			page.setOtherPageToHere(true);
			return page;
		}
	}
	/**
	 * ��õ�ǰ��Ʒ
	 * 
	 * @return
	 */
	public abstract Object getCurrentProduct();
	

	
	public ITableColumn getDisplayCreator() {
		return new SimpleTableColumn("creator", "������",
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
	
	public Long getResourceCount(ResourceType type,Long number){ //�ݹ���㷽��
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
		return new SimpleTableColumn("resourceCount", "��Դ����",
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
