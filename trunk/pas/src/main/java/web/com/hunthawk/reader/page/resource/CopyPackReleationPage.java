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
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.partner.FeeService;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;

@Restrict(roles = { "packreleationchange" }, mode = Restrict.Mode.ROLE)
public abstract class CopyPackReleationPage extends EditPage implements PageBeginRenderListener{
	
	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();
	
	public abstract ResourcePack getPack();
	public abstract void setPack(ResourcePack pack);
	
	/**
	 * ������Դ��set����
	 */
	public abstract Set getPackReleations();
	public abstract void setPackReleations(Set resourceSet);
	
	@SuppressWarnings("unchecked")
	@Override
	public  Class getModelClass(){
		return ResourcePackReleation.class;
	}
	
	public abstract String getPricepackId();
	public abstract void setPricepackId(String packId);
	
	
	public void savePage(IRequestCycle cycle) {
		try{
			if(save()){
				ICallback callback = (ICallback) getCallbackStack().popPreviousCallback();
				callback.performCallback(cycle);
			}
		}catch(Exception e){
			cycle.activate("resource/ShowEpackPage");
		}
	}
	
	@Override
	protected boolean persist(Object object) {
		String error="";
		try{
			List<ResourcePack> packs = new ArrayList<ResourcePack>();
			
				String[] packIds = getPricepackId().split(",");
				for(String packId : packIds){
					ResourcePack pack = (ResourcePack)getResourcePackService().getEpack(Integer.parseInt(packId));
					if(pack != null){
						packs.add(pack);
						//this.setPack(pack);
					}
				}		
			//ResourcePackReleation releation = (ResourcePackReleation)object;
			Set<ResourcePackReleation> releations = getPackReleations();
			if(packs.size()<1)
				throw new Exception("��ѡ���Ƶ������۰���");
			Integer type = getPack().getType();//��ǰ��ѡ��Ķ�Ӧ��ϵ ���������۰����� �����ڷ�Ϊ���֣�һ���� �Ʒѣ��ƴ�/vip ��һ�֣����/���ݿ���/����
			boolean feeName = false;
			if(type==1 || type==2){ //�Ʒ�
				feeName=true;
			}
			for(ResourcePack pack : packs){
				boolean currentFee = false;
				if(pack.getType()==1 || pack.getType()==2)
					currentFee = true;
				if(feeName != currentFee){
					//throw new Exception("��ѡ���۰���������ѡ���������Դ��Ӧ��ϵ���������۰������ݣ�");
					error += "��ѡ���۰�������������Դ��Ӧ��ϵ���������۰����Ͳ�����!";
					continue;
				}
				/*
				 * ���� 2009-11-16 
				 * yuzs
				 */
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
				//----end------------------------------
				for(ResourcePackReleation releation : releations){
					releation.setChoice(releation.getChoice());
					releation.setCpid(releation.getCpid());
					releation.setFeeId(releation.getFeeId());
					/*
					 *���� 2009-11-16
					 *yuzs 
					 */
					Integer firstNum=500000;
					if(getPlace()==1){//�ö�
						if(isFirst){//�״�
							firstNum  = Integer.parseInt(firstOrder)+i;
							releation.setOrder(firstNum*1000);
						}else{
							firstNum  = Integer.parseInt(firstOrder)-i;
							releation.setOrder(firstNum*1000);
						}
					}else if(getPlace()==2){//�õ�
						firstNum  = Integer.parseInt(firstOrder)+i;
						releation.setOrder(firstNum*1000);
					}else{//Ĭ���õ�
						firstNum = Integer.parseInt(firstOrder)+i;
						releation.setOrder(firstNum*1000);
					}
					i++;
					System.out.println("----"+i+"-----"+releation.getOrder());
					//-------end---------------------
					releation.setPack(pack);
					releation.setResourceId(releation.getResourceId());
					releation.setStatus(releation.getStatus());
					try{
						getResourcePackService().addResourcePackReleation(releation);			
					}catch(Exception e){
						error += e.getMessage();
					}
				}
			}
			
			
			if(!ParameterCheck.isNullOrEmpty(error))
				throw new Exception(error);
		}catch(Exception e)
		{
			e.printStackTrace();
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage()+"����Դ�Ѿ����뵽�����۰�������Դ��ƷѲ�ƥ��", null);
			return false;
		}
		return true;
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
					return getPackReleations().size();
				}
				public Iterator getCurrentPageRows(int nFirst, int nPageSize,
						ITableColumn objSortColumn, boolean bSortOrder)
				{
					//List<ResourceAll> list  = new ArrayList<ResourceAll>();
					//list.addAll(getResources());
					return  getPackReleations().iterator();
				}
			};
		}
		
		public abstract Integer getPlace();
		public abstract void setPlace(Integer id);
		
		public IPropertySelectionModel getPlaceList() {
			Map<String, Integer> authors = new OrderedMap<String, Integer>();
			authors.put("�ö�λ��",1);
			authors.put("�õ�λ��",2);
			return new MapPropertySelectModel(authors, false, "");
		} 
		public void cancelPage(IRequestCycle cycle) {
			try{
				ICallback callback = (ICallback) getCallbackStack().popPreviousCallback();
				callback.performCallback(cycle);
			}catch(Exception e){
				cycle.activate("resource/ShowEpackPage");
			}
		}
}
