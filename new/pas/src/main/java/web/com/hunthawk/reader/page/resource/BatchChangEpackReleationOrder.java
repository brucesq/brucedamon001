package com.hunthawk.reader.page.resource;

import java.util.ArrayList;
import java.util.HashSet;
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
public abstract class BatchChangEpackReleationOrder extends EditPage implements PageBeginRenderListener{
	
	@InjectObject("spring:resourcePackService")
	public abstract ResourcePackService getResourcePackService();
	
	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();
	
	@InjectObject("spring:feeService")
	public abstract FeeService getFeeService();
	
	public abstract ResourcePack getPack();
	public abstract void setPack(ResourcePack pack);
	
	public abstract Integer getReleationId();
	public abstract void setReleationId(Integer id);
	
	
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
			String err = "";
					Set<ResourcePackReleation> sets = getPackReleations();
					if(getPlace()==null)
						return false;
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
					}else if(getPlace()==2){//ָ��λ��
						System.out.println("---1-----");
						if(getReleationId()==null)
							throw new Exception("����λ�ò���Ϊ��");
						/*ResourcePackReleation rel = 
							(ResourcePackReleation) getResourcePackService().getResourcePackReleation(getReleationId());*/
						ResourcePackReleation rel = 
							getResourcePackService().getIndexReleation(getPack(), getReleationId());
						Integer maxOrder = getResourcePackService().findMaxOrderInPack(getPack());
						if(rel.getOrder()>=maxOrder){ //����λ��Ϊ���λ�� �õ�
							System.out.println("-----");
							String strOrder = maxOrder+"";
							maxOrder = Integer.parseInt(strOrder.substring(0,6));						
							int i=1;
							for(ResourcePackReleation subRel : sets){
								//rel.setOrder(max+1);
								subRel.setOrder((maxOrder+i)*1000);	
								i++;
								getResourcePackService().updateResourcePackReleationNotCreateUEB(subRel);
							}
						}else{//ĳһλ��
							System.out.println("dddd====3====="+rel.getId());
							Integer firstNum = rel.getFirstOrder();	
							System.out.println("---2-----");
							ResourcePackReleation maxRel =  //�ҵ������Ǹ�
								 getResourcePackService().getMaxSubOrder(getPack(), firstNum);
							System.out.println("---3-----"+maxRel);
							Integer count = 999 - maxRel.getSubOrder();
							this.changeOrder(rel, maxRel, sets, getPack(), count);
						}
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
		
		}catch(Exception e)
		{
			e.printStackTrace();
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage()+"����Դ�Ѿ����뵽�����۰�������Դ��ƷѲ�ƥ��", null);
			return false;
		}
		return true;
	}
	public void changeOrder(ResourcePackReleation currentRel,ResourcePackReleation lastRel,Set<ResourcePackReleation> sets,ResourcePack pack,Integer count){
		//�ҵ�����λ��
		//�����ǰʣ��� ��������� ���� ����ĸ�����ֱ�Ӳ���
		try {
			System.out.println("count---"+count);
			System.out.println("sets.size---"+sets.size());
			if(count>=sets.size()){
				System.out.println("---2-----");
				
				if(currentRel.getOrder() == lastRel.getOrder() )//��0�����
					return ;
				System.out.println("--��ǰ��--1---"+currentRel.getOrder());
				System.out.println("--���쵽�����Ǹ�--2---"+lastRel.getOrder());
				/*List<ResourcePackReleation> changeReleations = 
					getResourcePackService().getSubOrderList(pack, currentRel.getOrder(), lastRel.getOrder());*/
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
				Integer subCounts = Integer.parseInt(subCount+"");
				count += (1000-subCounts);//ʣ�����
				ResourcePackReleation lastReleation =
					getResourcePackService().getMaxSubOrder(pack, firstOrder);
				System.out.println("--44----����----"+count);
				changeOrder(currentRel,lastReleation,sets,pack,count);//����������ȥ
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pageBeginRender(PageEvent event){
		if(getModel() == null){
			setModel(new ResourcePackReleation());
		}
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
			authors.put("ָ��λ��",2);
			authors.put("�õ�λ��",3);
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
