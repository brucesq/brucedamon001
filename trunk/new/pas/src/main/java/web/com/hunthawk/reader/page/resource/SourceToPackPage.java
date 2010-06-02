package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.framework.util.ParameterCheck;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "packreleationchange" }, mode = Restrict.Mode.ROLE)
public abstract class SourceToPackPage extends EditPage implements PageBeginRenderListener{
	
	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();
	
	public abstract ResourcePack getPack();
	public abstract void setPack(ResourcePack pack);
	
	public abstract String getPricepackId();
	public abstract void setPricepackId(String packId);
	
	public abstract String getPackFeeType();
	public abstract void setPackFeeType(String packFeeType);
	/**
	 * ��Դ��set����
	 */
	public abstract Set getResources();
	public abstract void setResources(Set resourceSet);
	
	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass(){
		return ResourcePackReleation.class;
	}
	
	public abstract String getPricepackName();
	public abstract void setPricepackName(String pricepackName);
	
	@InjectPage("resource/ShowEbookPage")
	public abstract ShowEbookPage getShowEbookPage();
	
	public void savePage(IRequestCycle cycle){
		if(save()){
			//getCallbackStack().popPreviousCallback();
			//ICallback callback = (ICallback)getCallbackStack().getStack().pop();
			//callback.performCallback(cycle);
			try{
				ICallback callback = (ICallback) getCallbackStack().popPreviousCallback();
				callback.performCallback(cycle);
			}catch(Exception e){
				cycle.activate("resource/ShowEbookPage");
			}
			
		}else
			return;
	}
	@Override
	protected boolean persist(Object object) {
		String error="";
		try{
			List<ResourcePack> packs = new ArrayList<ResourcePack>();
			
			if(isCheck()){ //ֱ�Ӵ���Դ���
				String[] packIds = getPricepackId().split(",");
				for(String packId : packIds){
					ResourcePack pack = (ResourcePack)getResourcePackService().getEpack(Integer.parseInt(packId));
					if(pack != null){
						packs.add(pack);
						this.setPack(pack);
					}
				}
			}else{ //�����۰���ȥ�����
				packs.add( getPack());
			}
			
			
			ResourcePackReleation releation = (ResourcePackReleation)object;
			for(ResourcePack pack : packs){
				
				ResourcePackReleation packReleation = new ResourcePackReleation();
				packReleation.setChoice(releation.getChoice());
				packReleation.setCpid(releation.getCpid());
				packReleation.setFeeId(releation.getFeeId());
				/*
				 * 
				 */Integer minOrder =  //��С����
						getResourcePackService().findMinOrderInPack(getPack());
					Integer maxOrder = //�������
						getResourcePackService().findMaxOrderInPack(getPack());
					System.out.println("----��Сֵ--"+minOrder);
					System.out.println("----���ֵ--"+maxOrder);
					
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
				//------------end------------------------
				packReleation.setStatus(0);
				packReleation.setPack(pack);
				
				packReleation.setId(null);
				Integer feeid=0;
				if(packReleation.getFeeId()!=null && !"".equals(packReleation.getFeeId())){
					Fee fee = getFeeService().getFee(packReleation.getFeeId());
					feeid =  fee.getProvider().getId();
				}
				
				Iterator it =  getResources().iterator();
				Integer order = packReleation.getOrder();
				int i = 1;
				while(it.hasNext()){
					ResourceAll resourceAll = (ResourceAll)it.next();
					//���Ʒ�Ϊ���ε�ʱ�򣬻�ȥ�Ƚ�һ�¹�ϵ��������Դ����Ӧ��CPID ��ƷѶ�Ӧ��CPID��������ǰ��εģ���ô���ñȽ�
					
					Integer firstNum=500000;
					if(getPlace()==1){//�ö�
						if(isFirst){//�״�
							firstNum  = Integer.parseInt(firstOrder)+i;
							packReleation.setOrder(firstNum*1000);
						}else{
							firstNum  = Integer.parseInt(firstOrder)-i;
							packReleation.setOrder(firstNum*1000);
						}
					}else if(getPlace()==2){//�õ�
						firstNum  = Integer.parseInt(firstOrder)+i;
						packReleation.setOrder(firstNum*1000);
					}else{//Ĭ���õ�
						firstNum = Integer.parseInt(firstOrder)+i;
						packReleation.setOrder(firstNum*1000);
					}
					i++;
					System.out.println("----"+i+"-----"+"--packid---"+pack.getId()+"----"+packReleation.getOrder());
					
					if(pack.getType().equals(1) || pack.getType().equals(2)){
						if(resourceAll.getCpId().equals(feeid)){			
							packReleation.setResourceId(resourceAll.getId());
							packReleation.setCpid(resourceAll.getCpId().toString());
							//packReleation.setOrder(order+count);
							error += getResourcePackService().addResourcePackReleationMessgae(packReleation,resourceAll);
							//++count;
						}else{
							error +=  resourceAll.getName()+"/";
						}
					}else{	
						packReleation.setResourceId(resourceAll.getId());
						packReleation.setCpid(resourceAll.getCpId().toString());
						//packReleation.setOrder(order+count);
						error += getResourcePackService().addResourcePackReleationMessgae(packReleation,resourceAll);	
						//++count;
					}
				}
			}
			
			
			if(!ParameterCheck.isNullOrEmpty(error))
				throw new Exception(error);
		}catch(Exception e)
		{
			//e.printStackTrace();
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage()+"����Դ�Ѿ����뵽�����۰�������Դ��ƷѲ�ƥ��", null);
			return false;
		}
		return true;
	}

	public abstract Integer getPlace();
	public abstract void setPlace(Integer id);
	
	public IPropertySelectionModel getPlaceList() {
		Map<String, Integer> authors = new OrderedMap<String, Integer>();
		authors.put("�ö�λ��",1);
		authors.put("�õ�λ��",2);
		return new MapPropertySelectModel(authors, false, "");
	} 

	public void pageBeginRender(PageEvent event){
		if(getModel() == null){
			setModel(new ResourcePackReleation());
		}
	}
	@InjectObject("engine-service:external")
	 public abstract IEngineService getExternalService();
	
	 public String getPackURL(){
			
			IEngineService service = getExternalService();
			
			Object[] params = new Object[]
			{ "pricepackId" };
			String templateURL = PageHelper.getExternalFunction(service,
					"resource/BatchPricePackChoicePage", params);

			return templateURL;
			
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

	public String getFeeURL(){
		
		IEngineService service = getExternalService();
		
		Object[] params = new Object[]
		{ "feeId" ,"packReleation"};
		String templateURL = PageHelper.getExternalFunction(service,
				"partner/FeeChoicePage", params);

		return templateURL;	
	}
	@InjectPage("resource/ShowEpackReleationPage")
	public abstract ShowEpackReleationPage getEpackReleationPage();
	
	public  IPage showPackReleations(ResourcePack pack){
		ShowEpackReleationPage page = getEpackReleationPage();
		if(pack == null)
			return this;
		else{
			page.setPack(pack);
			return page;
		}
	}
	
	public boolean isCheckPack(){
		if(getPack()==null)
			return false;
		else 
			return true;	
	}
	
	
	public boolean isCheck(){
		if(getPack()==null){
			return true;
		}else {
			return false;
		}
		
	}
}
