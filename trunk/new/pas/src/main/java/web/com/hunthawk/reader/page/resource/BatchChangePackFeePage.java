package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
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
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.partner.Provider;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.page.guide.Select;
import com.hunthawk.reader.page.guide.Type;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.partner.PartnerService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "packreleationchange" }, mode = Restrict.Mode.ROLE)
public abstract class BatchChangePackFeePage extends EditPage implements PageBeginRenderListener{
	
	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();
	
	/**
	 * ��Դ��set����
	 */
	public abstract Set getPackReleations();
	public abstract void setPackReleations(Set packReleations);
	
	public abstract ResourcePack getPack();
	public abstract void setPack(ResourcePack pack);
	
	public abstract String getFeeId();
	public abstract void setFeeId(String feeId);
	
	public abstract Integer getChoice();
	public abstract void setChoice(Integer choice);
	
	public abstract Integer getOrder();
	public abstract void setOrder(Integer order);
	
	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass(){
		return ResourcePackReleation.class;
	}
	
	@Override
	protected boolean persist(Object object) {
		String error="";
			System.out.println("---->>>---"+getSelectResult().toString());
		try{
			/*��ǰ�ļƷ�ѡ�� ���ж���Դ�µ�CPIDͬ �Ʒ��µ�CPID��һ�� ���޸ĵ�
			 * Set packReleationList = getPackReleations();
			Iterator it = packReleationList.iterator();
			String feeId  = getFeeId();
			String providerId="";
			if(feeId!=null && !"".equals(feeId)){
				Fee fee = getFeeService().getFee(feeId);
				providerId = fee.getProviderId().toString();
			}
			//Integer choice = getChoice();
			Integer choice = 0;
			if(getChoice()!=null && getChoice()!=0)
				choice = getChoice();
			Integer order = 0;
			if(getOrder()!=null && getOrder()!=0)
				order = getOrder();
			int i=0;
			while(it.hasNext()){
				ResourcePackReleation packReleation = 
					(ResourcePackReleation)it.next();
				if(packReleation.getCpid()!=null && providerId!=null 
						&& !"".equals(providerId) && !"".equals(packReleation.getCpid())){
					if (!providerId.equals(packReleation.getCpid()))
							error  += packReleation.getResourceId()+",";								
					else
						packReleation.setFeeId(feeId);
				}
				if(choice!=0)
					packReleation.setChoice(choice);
				if(order!=0){
					packReleation.setOrder(order+i);
					i++;
				}
				//getResourcePackService().updateResourcePackReleation(packReleation);
			}
			if(error!=null && !"".equals(error))
				throw new Exception("��Դ��"+error+"��CPID�����뵱ǰ��ѡ��ļƷѹ�����CPID��һ�£�");
				*/
			//�����޸ļƷѵ�ʱ���ǰ�����Դ���ڵ� CPID�µļƷ� ��ֱ���޸ĵģ���ǣ�����жϵ�ѡ��
			
			String feeIdColumns = "";
			if(getSelectResult()!=null && !"".equals(getSelectResult()))
					feeIdColumns = getSelectResult().toString();
			String[] feeIdArray = feeIdColumns.split("\\,");//��ͬ�ļƷ����飬��һ��Map�����Щ�Ʒ�ͬCPID�Ķ�Ӧ��ϵ
			Map<Integer,String> feeMap = new HashMap<Integer,String>();
			for(int i=0;i<feeIdArray.length;i++){
				//Provider provider = getPartnerService().getProvider(providerId)
				Fee fee = getFeeService().getFee(feeIdArray[i]);//�õ��Ʒ�
				feeMap.put(fee.getProviderId(),feeIdArray[i]);//�� CP�� ��ǰѡ��ļƷ���Ϣ��������
			}
			Set packReleationList = getPackReleations();//���۹�ϵ�б�
			Iterator it = packReleationList.iterator();
			Integer choice = 0;
			if(getChoice()!=null && getChoice()!=0)
				choice = getChoice();
			/*Integer order = 0;
			if(getOrder()!=null && getOrder()!=0)
				order = getOrder();*/
			int i=0;
			while(it.hasNext()){
				ResourcePackReleation packReleation = 
					(ResourcePackReleation)it.next();
				
				String feeId = feeMap.get(Integer.parseInt(packReleation.getCpid()));//�Ӵ�ŵļƷѵ�MAP��ȡ����ǰѡ����Ǹ� �Ʒ�ID
				if(feeId!=null)//����û��ѡ��
					packReleation.setFeeId(feeId);
				if(choice!=0)
					packReleation.setChoice(choice);
				/*if(order!=0){
					packReleation.setOrder(order+i);
					i++;
				}*/
				getResourcePackService().updateResourcePackReleation(packReleation);
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
			setModel(new ResourcePackReleation());
		}

		List<Type> selectList = new ArrayList<Type>(); 
		Map<Integer,Provider> map = new OrderedMap<Integer,Provider>();
		if(getPackReleations()!=null && getPackReleations().size()>0){
			Set<ResourcePackReleation>  resourceList = getPackReleations();
			for(ResourcePackReleation releation : resourceList){
				Provider provider = getPartnerService().getProvider(Integer.parseInt(releation.getCpid()));
				map.put(provider.getId(), provider);
			}	
		}
		if(map!=null && map.size()>0){
			Set keySet = map.keySet();
			Iterator it = keySet.iterator();
			while(it.hasNext()){
				Select select = new Select();
				Provider provider = map.get(it.next());
				select.setTitle(provider.getIntro());
					IPropertySelectionModel selectionModel ;
					Map<String, String> types = new HashMap<String, String>();
					types.put("", "");
					Collection<HibernateExpression> hibernateExpressions = new ArrayList<HibernateExpression>();
					
					HibernateExpression typeF = new CompareExpression("provider",
							provider, CompareType.Equal);
					hibernateExpressions.add(typeF);
					
					HibernateExpression typeE = new CompareExpression("status", //���ߵ�
							1, CompareType.Equal);
					hibernateExpressions.add(typeE);
					
					HibernateExpression typeD = new CompareExpression("type",//���ε�
							3, CompareType.Equal);
					hibernateExpressions.add(typeD);
					
					List<Fee> feeList = getFeeService().findFee(1, Integer.MAX_VALUE, "id", false, hibernateExpressions);
					if(feeList!=null && feeList.size()>0){
						for(Fee fee :feeList){
							types.put("�Ʒ����ƣ�"+fee.getName()+"���۸�"+fee.getCode()+"(Ԫ)", fee.getId());
						}
					}					
					selectionModel = new MapPropertySelectModel(types, false, "");
				
				select.setPropertySelectionModel(selectionModel);
				selectList.add(select);
			}
		}
		setSelectList(selectList);
		
	}
	
	public   IBasicTableModel getTableModel()
		{
			return new IBasicTableModel()
			{
				public int getRowCount()
				{
					return getPackReleations().size();
				}
				public Iterator getCurrentPageRows(int nFirst, int nPageSize,
						ITableColumn objSortColumn, boolean bSortOrder)
				{
				
					return  getPackReleations().iterator();
				}
			};
		}
	
	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getShowEpackReleationPage();
	
	public IPage savePage(IRequestCycle cycle){
		if(save()){
			getShowEpackReleationPage().setPack(getPack());
			return getShowEpackReleationPage();
		}else
			return this;
	}
	
	public IPage cancelPage(IRequestCycle cycle,ResourcePack pack) {	
		getShowEpackReleationPage().setPack(pack);
		return getShowEpackReleationPage();
	}
	
	@InjectObject("engine-service:external")
	public abstract IEngineService getExternalService();
	
	public String getFeeURL() {
		
		IEngineService service = getExternalService();
		Object[] params = new Object[] { "feeId","packReleation"};
		String templateURL = PageHelper.getExternalFunction(service,
				"partner/FeeChoicePage", params);
		return templateURL;

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
							ResourceAll ra = getResourceService().getResource(
									rp.getResourceId());
							if (ra == null)
								return "��Դ��ʧ";
							return ra.getName();
						}
					}

				}, false);
	}
	
	@InjectObject("spring:partnerService")
	public abstract PartnerService getPartnerService();

	@InitialValue("new java.util.ArrayList()")
	public abstract List getSelectList();

	public abstract void setSelectList(List list);
	
	public abstract void setSelectResult(String s);

	@InitialValue("new java.lang.String()")
	public abstract String getSelectResult();

	public abstract void setCurrentSelect(Type select);

	public abstract Type getCurrentSelect();
	
	
	public void updateSelect(IRequestCycle cycle) {
		if (cycle.isRewinding()) {
			Type select = getCurrentSelect();
			if (select.getValue() != null) {
				setSelectResult(getSelectResult() +  select.getValue().toString()+ ",");
			}
		}
	}
}
