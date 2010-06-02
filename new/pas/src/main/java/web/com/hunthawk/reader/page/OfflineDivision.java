package com.hunthawk.reader.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.framework.hibernate.CompareExpression;
import com.hunthawk.framework.hibernate.CompareType;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.hibernate.NotNullExpression;
import com.hunthawk.reader.domain.resource.ResourceAll;
import com.hunthawk.reader.service.resource.ResourcePackService;
import com.hunthawk.reader.service.resource.ResourceService;
/**
 * �޸��鲿��Ϣ
 * @author yuzs
 *
 */
public class OfflineDivision extends Thread{

	private ResourceService resourceService;
	private ResourcePackService resourcePackService;
	public OfflineDivision(
			ResourceService resourceService,
			ResourcePackService resourcePackService){
		this.resourceService = resourceService;
		this.resourcePackService = resourcePackService;
	}
	public OfflineDivision(){}

	/*�޸ĵ� ������Դ����û��ʲô�ã�
	 * public void run(){
		List<ResourcePack> packLists = //�������۰��б�
			resourcePackService.findEpack(1, Integer.MAX_VALUE, "id", false, new ArrayList<HibernateExpression>() );
		
		for(ResourcePack pack : packLists){
		//ResourcePack pack = (ResourcePack) resourcePackService.getEpack(6800);
			Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
			HibernateExpression ex = new CompareExpression("pack",pack, CompareType.Equal);
			expressions.add(ex);
			List<ResourcePackReleation> releationList =  //���۰���Ӧ�� ������Դ�б�
				resourcePackService.getResourceFromEpack(1, Integer.MAX_VALUE, "order", true, expressions);
			Integer currentOrder=0;
			if(releationList==null || releationList.size()<1)
				continue;
			for(ResourcePackReleation releation : releationList){	
				currentOrder = releation.getOrder();
				if(currentOrder>100000000)								 
					continue;
				else{
					System.out.println("--�޸�ǰ��--"+releation.getOrder());
					currentOrder = (500000+releation.getOrder())*1000;
					System.out.println("--�޸ĺ��--"+currentOrder);
					releation.setOrder(currentOrder);
				}					
				try {
					
					resourcePackService.updatePackReleationNotCreateUEBAndLog(releation);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}*/
	/*�޸��鲿�Ĵ���
	 *
	 */public void run(){

		//ִ���޸Ĳ���
		//�õ���������Դ���󣨹������ߵģ�
		System.out.println("---------�߳�ִ��-----------------------");
		Collection<HibernateExpression> expressions = new ArrayList<HibernateExpression>();
		HibernateExpression ex = new CompareExpression("status",2, CompareType.NotEqual);
		expressions.add(ex);
		HibernateExpression ex1 = new CompareExpression("division","��", CompareType.NotLike);
		expressions.add(ex1);
		NotNullExpression null1 = new NotNullExpression("division");
		expressions.add(null1);		
		HibernateExpression ex2 = new CompareExpression("id","1%", CompareType.Like); //ֻѡ��ͼ����Դ
		expressions.add(ex2);
		
		List<ResourceAll> resourceList = 
			resourceService.findResourceBy(1, Integer.MAX_VALUE, "id", false, expressions);// new ArrayList<ResourceAll>();
		
		System.out.println("--------"+resourceList.size());
		
		 /* �������ֵ��޸�
		 * for(ResourceAll resource:resourceList){

			if(!"��".equals(resource.getDivision()) && resource.getDivision()!=null && !"".equals(resource.getDivision())){
				if(StringUtils.isNumeric(resource.getDivision())){//�����鲿�� ����
					Integer number= Integer.parseInt(resource.getDivision()); 
					String roman = StrUtil.toRoman(number);
					String resourceName = "";
					//System.out.println("ԭʼ��--ͼ������-����"+resource.getName());
					if(resource.getName().indexOf(roman)!=-1){
						resourceName =resource.getName().substring(0,resource.getName().indexOf(roman));
					}
					//System.out.println("������--�鲿����-����"+resourceName);
					resource.setDivisionContent(resourceName);
					//resource.setModifierId(user.getId());
					resourceService.updateResourceNOChangeStatus(resource,-1);
				}
			}
		}
		 */
		for(ResourceAll resource : resourceList){
			if(!"��".equals(resource.getDivision()) && resource.getDivision()!=null && !"".equals(resource.getDivision())){
				if( resource.getDivision()!=null && resource.getDivision()>0){//�����鲿�� ����
					String resourceName = "";
					if(resource.getName().indexOf("(")!=-1){
						resourceName = resource.getName().substring(0,resource.getName().indexOf("("));
					}
					resource.setDivisionContent(resourceName);
					resourceService.updateResourceNOChangeStatus(resource,-1);
				}
			}	
		}
		System.out.println("---------�߳̽���-----------------------");
	}
/*

	private static final int arr1[]={1000,500,100,50,12,11,10,9,8,7,6,5,4,3,2,1};
	private static final String arr2[]={"M","D","C","L","��", "��","��","��","��","��","��","��", "��","��","��","��"};
	
	public static String toRoman(int n){
		String roman="";
		int i = 0;
		do{
			if(n>=arr1[i]){
				if(n==12){
					roman += "��";	
					break;
				}else if(n==11){
					roman += "��";
					break;
				}else if(n>12 && n<50){
					i = 6;
					n -= arr1[i];
					roman += arr2[i];
				}else{
					n -= arr1[i];
					roman += arr2[i];
				}
			}else{
				i++;
			}
		}while(n>0 && i<arr1.length);
		
		return roman;
	}
	public static void main(String args[]){
		int a[]={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,51,52,53,61,62,63,64,101,102,103,110,111,501,502,503,511,512,513};
		for(int i=0;i<a.length;i++){
			System.out.println(a[i]+"---::--"+toRoman(a[i]));
		}
		System.out.println(toRoman(9999));
		System.out.println(StringUtils.isNumeric(null));
		System.out.println(StringUtils.isNumeric("2a"));
	}*/
	

}
