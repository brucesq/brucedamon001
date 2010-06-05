/**
 * 
 */
package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IPage;
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
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
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
import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceResType;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.SystemService;
import com.hunthawk.reader.service.system.UserService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "packreleationchange" }, mode = Restrict.Mode.ROLE)
public abstract class BatchAddEpackReleationPage extends SearchPage implements
		PageBeginRenderListener {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();

	public abstract ResourcePack getPack();

	public abstract void setPack(ResourcePack pack);

	@InjectObject("spring:userService")
	public abstract UserService getUserService();

	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();

	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getShowEpackReleationPage();

	@Override
	protected void delete(Object object) {

	}

	@Persist("session")
	@InitialValue("new java.util.ArrayList()")
	public abstract List getSelectedEbook();

	public abstract void setSelectedEbook(List set);

	public abstract Object getCurrentObject();

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		ResourceAll pro = (ResourceAll) getCurrentProduct();
		List selectedPros = getSelectedEbook();
		// ����ظ��ļ�¼
		if (bSelected) {
			if (!selectedPros.contains(pro)) {
				selectedPros.add(pro);
			}
		} else {
			selectedPros.remove(pro);
		}
		// persist value
		setSelectedEbook(selectedPros);

	}

	public boolean getCheckboxSelected() {
		return getSelectedEbook().contains(getCurrentProduct());
	}

	public void clearSelectedEbook() {
		setSelectedEbook(new ArrayList());
	}

	// ��Ʒ����
	public abstract String getName();

	public abstract void setName(String name);

	public abstract void setResourceType(Integer type);

	public abstract Integer getResourceType();

	public abstract UserImpl getCreator();

	public abstract void setCreator(UserImpl creator);

	public abstract String getResourceIds();
	
	public abstract void setResourceIds(String ids);

	public IPropertySelectionModel getUserList() {
		ObjectPropertySelectionModel parentProper = new ObjectPropertySelectionModel(
				getUserService().getAll(UserImpl.class), UserImpl.class,
				"getChName", "getId", true, "ȫ��");
		return parentProper;
	}
	public abstract Integer getTof();
	public abstract void setTof(Integer tof);
	
	public IPropertySelectionModel getToflist(){
		Map<String, Integer> map = new OrderedMap<String, Integer>();
		map.put("��", 0);
		map.put("��", 1);
		return new MapPropertySelectModel(map);		
	}
	public Collection<HibernateExpression> getSearchExpressions() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();

		if (getCurrentSp() == null && isNeedFee()) {
			HibernateExpression spE = new NullExpression("cpId");
			hibernateExpressions.add(spE);
		} else if (getCurrentSp() != null) {
			HibernateExpression spE = new CompareExpression("cpId",
					getCurrentSp().getId(), CompareType.Equal);
			hibernateExpressions.add(spE);
		}

		if (!ParameterCheck.isNullOrEmpty(getName())) {
			HibernateExpression nameE = new CompareExpression("name", "%"
					+ getName() + "%", CompareType.Like);
			hibernateExpressions.add(nameE);
		}

		if (!getAuthorId().equals(0)) {
			HibernateExpression author = new CompareExpression("authorId", "%|"
					+ String.valueOf(getAuthorId()) + "|%", CompareType.Like);
			hibernateExpressions.add(author);
		}

		if (getCreator() != null && getCreator().getId() != 0) {
			HibernateExpression creatorE = new CompareExpression("creatorId",
					getCreator().getId(), CompareType.Equal);
			hibernateExpressions.add(creatorE);
		}

		// ѡȡ����״̬�Ĳ�Ʒ
		HibernateExpression status = new CompareExpression("status", 0,
				CompareType.Equal);
		hibernateExpressions.add(status);

		HibernateExpression nameE = new CompareExpression("id", String
				.valueOf(getResourceType())
				+ "%", CompareType.Like);
		hibernateExpressions.add(nameE);

		return hibernateExpressions;
	}

	public void search() {
		//isSearch = true;
		setShowColumn(true);
		//setItemCount(0);
		//setItemList(null);
	}
	//private boolean isSearch = false;
	@InitialValue("false")
	public abstract boolean getShowColumn();
	public abstract void setShowColumn(boolean isShow);

	
	@Override
	public List<SearchCondition> getSearchConditions() {

		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		SearchCondition nameC = new SearchCondition();
		nameC.setName("name");
		nameC.setValue(getName());
		searchConditions.add(nameC);

		nameC = new SearchCondition();
		nameC.setName("resourceType");
		nameC.setValue(getResourceType());
		searchConditions.add(nameC);

		return searchConditions;
	}
	/*
	 *2009-12-24 ��ѯ�����Ż�
	 */
	/*//@InitialValue("0")
	public abstract Integer getItemCount();
	public abstract void setItemCount(Integer count);
	
	//@InitialValue("new java.util.ArrayList()")
	public abstract List getItemList();
	public abstract void setItemList(List list);
	
	private Map<String,Integer> countMap = new HashMap<String,Integer>();
	private Map<String,List> listMap = new HashMap<String,List>();*/
	
	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {	
			
			Set<String> resourceIdSet = new HashSet<String>();
			public int getRowCount() {
				
				if(getShowColumn()){
					//if(getItemCount()!=null && getItemCount()>0 ){
					//	System.out.println("-----�������----"+getItemCount());
					//	return getItemCount();
					//}else{
						System.out.println("-----�ٴν������----");
						UserImpl user = (UserImpl) getUser();
						Integer cpid = null;
						if (user.isRoleProvider()) {
							cpid = user.getProvider().getId();
						} else if (getCurrentSp() != null) {
							cpid = getCurrentSp().getId();
						}
							if(!ParameterCheck.isNullOrEmpty(getResourceIds())){
								String[] rids = getResourceIds().split(",");
								if(rids!=null && rids.length>0){
									for(int i=0;i<rids.length;i++){
										if(rids[i].length()==8)
											resourceIdSet.add(rids[i]);
									}
								}
							}
							boolean isTof = false;
							if(getTof()==null)
								isTof = false;
							else if(getTof()==0)
								isTof = false;
							else if(getTof()==1)
								isTof = true;
							else
								isTof = false;
							//-------2009-12-24----yuzs
							/*String key = getResourceType()+getName()+getAuthorId().toString()+getSortId()+isTof+getPack()+getResourceIds();
							Integer countvalue = countMap.get(key);
							int count = 0;
							if(countvalue != null && countvalue >0){ //�����KEYֵ
								count = countvalue.intValue();							
							}else{	
								count = getResourceService().getResourceResultNotInPack(cpid,
									getResourceType(), getName(), getAuthorId().toString(),
									getSortId(),isTof, null, getPack(),resourceIdSet).intValue();
								countMap.put(key, count);
							}*/
							//setItemCount(count);
							
						return  getResourceService().getResourceResultNotInPack(cpid,
								getResourceType(), getName(), getAuthorId().toString(),
								getSortId(),isTof, null, getPack(),resourceIdSet).intValue();
					//}
				}else{
					return 0;
				}
				// }
				// return getResourceService().getResourceResultCount(
				// getSearchExpressions()).intValue();
			}

			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;
				// if(getSortId() != null && !getSortId().equals(0)){
				if(getShowColumn()){
					//if(getItemList() !=null && getItemList().size()>0){
					//	System.out.println("-----�����б�----");
					//	return getItemList().iterator();
					//}else{
						System.out.println("-----�ٴν�������б�----");
						UserImpl user = (UserImpl) getUser();
						Integer cpid = null;
						if (user.isRoleProvider()) {
							cpid = user.getProvider().getId();
						} else if (getCurrentSp() != null) {
							cpid = getCurrentSp().getId();
						}
						boolean isTof = false;
						if(getTof()==null)
							isTof = false;
						else if(getTof()==0)
							isTof = false;
						else if(getTof()==1)
							isTof = true;
						else
							isTof = false;
						System.out.println("=======ִ�в�ѯ=============");
						/*String key = cpid+
						getResourceType()+ getName()+getAuthorId().toString()+
						getSortId()+isTof+ getPack()+getResourceIds()+ pageNo+ nPageSize;
						System.out.println();
						List lists = listMap.get(key);
						List<ResourceAll> list = null;
						if(lists!=null && lists.size()>0){//��ֵ
							list = lists;
						}else{
						    list = getResourceService().findResourceNotInPack(cpid,
								getResourceType(), getName(), getAuthorId().toString(),
								getSortId(),isTof,  null, getPack(),resourceIdSet, pageNo, nPageSize);
						    listMap.put(key, list);
						}*/
						List list =  getResourceService().findResourceNotInPack(cpid,
								getResourceType(), getName(), getAuthorId().toString(),
								getSortId(),isTof,  null, getPack(),resourceIdSet, pageNo, nPageSize);
						
						
						return list.iterator();
					//}
				}else{
					System.out.println("û��ִ�в�ѯ����");
					return null;
				}
				// }
				// return getResourceService().findResourceBy(pageNo, nPageSize,
				// "id", false, getSearchExpressions()).iterator();
			}
		};
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
						ResourceAll o1 = (ResourceAll) objRow;

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

	public IPropertySelectionModel getResourceTypeList() {
		Map<String, Integer> types = new TreeMap<String, Integer>();
		types.put("ͼ��", ResourceType.TYPE_BOOK);
		types.put("����", ResourceType.TYPE_COMICS);
		types.put("��ֽ", ResourceType.TYPE_NEWSPAPERS);
		types.put("��־", ResourceType.TYPE_MAGAZINE);
		types.put("��Ƶ", ResourceType.TYPE_VIDEO);
		types.put("��Ѷ", ResourceType.TYPE_INFO);
		types.put("���", ResourceType.TYPE_APPLICATION);
		return new MapPropertySelectModel(types, false, "");
	}

	public IPropertySelectionModel getSortList() {
		Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
		HibernateExpression typeF = new CompareExpression("showType",
				getResourceType(), CompareType.Equal);
		hibernateExpressions.add(typeF);
		List<ResourceType> sortTypes = getResourceService().findResourceTypeBymemcached(getResourceType()+"",
				1, Integer.MAX_VALUE, "id", false, hibernateExpressions);
		Map<String, Integer> sorts = new OrderedMap<String, Integer>();
		sorts.put("ȫ��", 0);
		for (ResourceType sort : sortTypes) {
			sorts.put(sort.getName(), sort.getId());
		}
		return new MapPropertySelectModel(sorts, false, "ȫ��");
	}

	public IPropertySelectionModel getSpList() {
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
	
	public abstract Provider getCurrentSp();

	public abstract void setCurrentSp(Provider provider);

	public abstract Fee getCurrentFee();

	public abstract void setCurrentFee(Fee fee);

	public abstract void setFeeStrat(Integer strat);

	public abstract Integer getFeeStrat();

	public abstract void setOrder(Integer order);

	public abstract void setSortId(Integer sort);// С���

	public abstract Integer getSortId();
	
	public abstract Integer getOrder();

	public void pageBeginRender(PageEvent event) {
		if (getResourceType() == null) {
			setResourceType(ResourceType.TYPE_VIDEO);
		}
		setFeeStrat(3);
		setOrder(1);
		if (getAuthorId() == null)
			setAuthorId(0);
	}

	public IPage returnEpackReleation(ResourcePack pack) {
		setSelectedEbook(new ArrayList());
		getShowEpackReleationPage().setPack(pack);
		return getShowEpackReleationPage();
	}

	public IPage submitResource() {
		String err = "";
		if (getSelectedEbook().size() == 0) {
			err = "������Ҫѡ��һ����Դ";
		}
		if (isNeedFee() && getCurrentFee() == null) {
			err = "����Ҫѡ��һ�ּƷ�.";
		}
		Fee fee = getCurrentFee();
		if (StringUtils.isNotEmpty(err)) {
			getDelegate().setFormComponent(null);
			getDelegate().record(err, null);
			return this;
		}
		//int i = 0;
		
		Integer minOrder =  //��С����
			getResourcePackService().findMinOrderInPack(getPack());
		Integer maxOrder = //�������
			getResourcePackService().findMaxOrderInPack(getPack());
		int i = 1;
		System.out.println("----"+i+"---��Сֵ--"+minOrder);
		System.out.println("----"+i+"---���ֵ--"+maxOrder);
		
		String strOrder = "";
		String firstOrder = "";
		boolean isFirst = false;
		if(getPlace()==1){//�ö�		
			if(minOrder !=null && minOrder>0 ){
				strOrder = minOrder+"";
				firstOrder = strOrder.substring(0,6);
			}else{//�²������Դ��
				firstOrder= "500000";
				isFirst=true;
			}
		}
		else if(getPlace()==2){//�õ�
			if(maxOrder !=null && maxOrder>0 ){
				strOrder = maxOrder+"";
				firstOrder = strOrder.substring(0,6);
			}else{
				firstOrder= "500000";
				isFirst=true;
			}
		}else{//�õ�
			if(maxOrder !=null && maxOrder>0 ){
				strOrder = maxOrder+"";
				firstOrder = strOrder.substring(0,6);
			}else{
				firstOrder= "500000";
				isFirst=true;
			}
		}
		for (Object obj : getSelectedEbook()) {
			ResourceAll resource = (ResourceAll) obj;
			ResourcePackReleation rel = new ResourcePackReleation();
			rel.setChoice(getFeeStrat());
			if (isNeedFee()) {
				rel.setCpid(String.valueOf(getCurrentSp().getId()));
				rel.setFeeId(fee.getId());

			}
			//rel.setOrder(getOrder() + (i++));
			/*
			 * ��������order : 999999 - 999  ǰ��λΪ �������򣬺���λΪ�����������
			 * �������
			 * 1.�ö����룬2.�õײ���
			 * yuzs 2009-11-13
			 */
			Integer firstNum=500000;
			if(getPlace()==1){//�ö�
				if(isFirst){//�״�
					firstNum  = Integer.parseInt(firstOrder)+i;
					rel.setOrder(firstNum*1000);
				}else{
					firstNum  = Integer.parseInt(firstOrder)-i;
					rel.setOrder(firstNum*1000);
				}
			}else if(getPlace()==2){//�õ�
				firstNum  = Integer.parseInt(firstOrder)+i;
				rel.setOrder(firstNum*1000);
			}else{//Ĭ���õ�
				firstNum = Integer.parseInt(firstOrder)+i;
				rel.setOrder(firstNum*1000);
			}
			i++;
			System.out.println("----"+i+"-----"+rel.getOrder());
			//--------����----------------
			rel.setPack(getPack());
			rel.setResourceId(resource.getId());
			rel.setStatus(0);
			if (rel.getChoice() == null) {
				rel.setChoice(0);
			}
			try {
				getResourcePackService().addResourcePackReleation(rel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setSelectedEbook(new ArrayList());
		getShowEpackReleationPage().setPack(getPack());
		return getShowEpackReleationPage();
	}
	
	public abstract Integer getPlace();
	public abstract void setPlace(Integer id);
	
	public IPropertySelectionModel getPlaceList() {
		Map<String, Integer> authors = new OrderedMap<String, Integer>();
		authors.put("�ö�λ��",1);
		authors.put("�õ�λ��",2);
		return new MapPropertySelectModel(authors, false, "");
	} 

	public boolean isNeedFee() {
		if (getPack().getType().equals(Constants.FEE_TYPE_VIEW)
				|| getPack().getType().equals(Constants.FEE_TYPE_VIP)) {
			return true;
		}
		return false;
	}

	public IPropertySelectionModel getAuthorList() {
		List<ResourceAuthor> resourceauthor = getResourceService()
				.findResourceAuthorBymemcached("resourceAll",1, Integer.MAX_VALUE, "initialLetter",
						true, new ArrayList<HibernateExpression>());
		Map<String, Integer> authors = new OrderedMap<String, Integer>();
		authors.put("ȫ��", 0);
		for (ResourceAuthor author : resourceauthor) {
			authors.put(author.getName(), author.getId());
		}
		return new MapPropertySelectModel(authors, false, "ȫ��");
	}

	public abstract void setAuthorId(Integer authorId);

	public abstract Integer getAuthorId();

	public ITableColumn getSort() {
		return new SimpleTableColumn("sort", "���", new ITableColumnEvaluator() {

			private static final long serialVersionUID = 625300745851970L;

			public Object getColumnValue(ITableColumn objColumn, Object objRow) {
				ResourceAll rp = (ResourceAll) objRow;
				
				StringBuffer type = new StringBuffer();
				// ������ԴID��ѯ��Դ������
				Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
				HibernateExpression ex = new CompareExpression("rid", rp
						.getId(), CompareType.Equal);
				expressions.add(ex);
				List<ResourceResType> list = getResourceService()
						.findResourceResTypeBy(1, Integer.MAX_VALUE, "rid",
								false, expressions);
				for (Iterator it = list.iterator(); it.hasNext();) {
					ResourceResType rrt = (ResourceResType) it.next();
					ResourceType rt = getResourceService().getResourceType(
							rrt.getResTypeId());
					if(rt == null)
						continue;
					type.append(rt.getName());
					type.append(";");
				}
				// ȥ�����һ���ֺ�
				if (list.size() > 0 && type.length() > 1) {
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
						ResourceAll rp = (ResourceAll) objRow;
						String author = ";";
						int i = 0;
						Integer[] authorIds = rp.getAuthorIds();
						for (Integer authorId : authorIds) {
							ResourceAuthor rAuthor = getResourceService()
									.getResourceAuthorById(authorId);
							if (rAuthor != null) {
								author += rAuthor.getName();
								if (++i < authorIds.length) {
									author += ";";
								}
							}
						}
						return author.substring(1);

					}

				}, false);

	}

}
