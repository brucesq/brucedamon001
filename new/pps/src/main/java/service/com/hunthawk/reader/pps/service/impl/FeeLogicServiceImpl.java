package com.hunthawk.reader.pps.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.domain.Constants;
import com.hunthawk.reader.domain.bussiness.Product;
import com.hunthawk.reader.domain.bussiness.TemplateType;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;
import com.hunthawk.reader.pps.ArrayUtil;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.CustomService;
import com.hunthawk.reader.pps.service.FeeLogicService;
import com.hunthawk.reader.pps.service.IphoneService;
import com.hunthawk.reader.pps.service.ResourceService;

public class FeeLogicServiceImpl implements FeeLogicService {
	private IphoneService iphoneService;
	private CustomService customService;
	private ResourceService resourceService;
	private BussinessService bussinessService;
	
	public void setBussinessService(BussinessService bussinessService) {
		this.bussinessService = bussinessService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setCustomService(CustomService customService) {
		this.customService = customService;
	}

	public void setIphoneService(IphoneService iphoneService) {
		this.iphoneService = iphoneService;
	}

	public Map isFee(String productId,String resourceId,String mobile,ResourcePackReleation resourcePackReleation,int packId,int month_fee_bag_id){
		Map<String,String> map =new HashMap();
		StringBuilder builder=new StringBuilder();
		
		/**�Ƿ��ǰ������û�*/
		boolean isWhiteList = RequestUtil.isFeeDisabled();//������
		Fee fee=null;/**�ƷѶ��� */
		if(resourcePackReleation!=null){
			String feeId=resourcePackReleation.getFeeId();
			if(StringUtils.isEmpty(feeId) || feeId==null){
				feeId=resourcePackReleation.getPack().getFeeId();
			}
			fee =customService.getFee(feeId);
		}
		String fid="";
		if(isWhiteList || fee==null){//���
			return null;
		}else{
			if(fee.getCode().equals("0")){
				return null;
			}else{
				/**�Ƿ�����Դ�����û�*/
				boolean isBuy=false;
				boolean isIphone=iphoneService.isIphoneProduct(productId);
				if(isIphone){
					 if(packId<0)
						 isBuy=true;
					 else
						 isBuy=iphoneService.isUserBuy(mobile, productId, packId, resourceId,String.valueOf(resourcePackReleation.getId()));
					 if(isBuy){
						 return null;
					 }
					 fid=resourcePackReleation.getFeeId();
					
				}else{
					isBuy =customService.isUserBuyBook(mobile, resourceId);// �ж��Ƿ��ѹ���
					if(isBuy){
						return null;
					}
					
					int feeType=resourcePackReleation.getPack().getType();
					if(feeType==Constants.FEE_TYPE_FREE ){//���
						return null;
					}else if(feeType==Constants.FEE_TYPE_VIEW){//����
						fid=resourcePackReleation.getFeeId();
					}else if(feeType==Constants.FEE_TYPE_VIP){//VIP����
						/**�ж��Ƿ���VIP�û�*/
						boolean isVip= customService.isOrderMonth(mobile, resourcePackReleation.getPack().getFeeId());
						if(!isVip){
							/**��VIP�û�����VIP������ַ*/
							fid="&" + ParameterConstants.FEE_ID + "=" +resourcePackReleation.getPack().getFeeId()+"&"+ParameterConstants.MONTH_FEE_BAG_ID+"="+resourcePackReleation.getPack().getId();
						}else{
							/**VIP ����*/
							fid=resourcePackReleation.getPack().getFeeId();
						}
					}else{//�������
						fid=resourcePackReleation.getPack().getFeeId();
					}
				}
				
//				if(isIphone){ //�ж��Ƿ��ѹ���
//					 if(packId<0)
//						 isBuy=true;
//					 else
//						 
//					 if(isBuy){
//						 return null;
//					 }
//					 fid=resourcePackReleation.getFeeId();
//				}else
				if(!isIphone){
					 boolean isMonth=false;//��iphone����ҵ�� ����
					 if(month_fee_bag_id<0){
						 month_fee_bag_id = resourcePackReleation.getPack().getId();
					 }
					 if (month_fee_bag_id>0){
						 ResourcePack pack = resourceService.getResourcePack(month_fee_bag_id);
						 if (pack != null) {
							isMonth =customService.isOrderMonth(mobile,pack.getFeeId());
						 }
					 }
					 if(isMonth){
						 return null;
					 }
				}
					/**�ж��Ƿ�������������*/
					boolean isOtherOrderMonth=customService.isOtherOrderMonth(resourceId, mobile, productId);
					if(isOtherOrderMonth){
						return null;
					}
					/**�ж��Ƿ񵯳��Ʒ�*/
					 if(fee.getIsout() == 1){//�����Ʒ���ʾ
						 builder.append(ParameterConstants.PORTAL_PATH);
						 builder.append("?");
						 builder.append(ParameterConstants.COMMON_PAGE);
						 builder.append("=");
						 builder.append(ParameterConstants.COMMON_PAGE_FEE);
						 builder.append("&");
						 builder.append(ParameterConstants.TEMPLATE_ID);
						 builder.append("=");
						 /**
						  * �����ʷ�ģ������
						  * add by liuxh  09-11-02
						  */
						 Integer feeTemplateId=null; 
						 Product product=bussinessService.getProduct(productId);
						 int showType=product.getShowType();
						 if(showType==2 || showType==3){//ȡwap2.0ģ��
							 feeTemplateId=fee.getTemplateId_wap();
						 }else if(showType==1){
							 //����UA��������
							 int wapType = RequestUtil.getNeedWapType();
							 if (wapType == 2 && isTemplateExist(fee.getTemplateId_wap())) {
								 feeTemplateId = fee.getTemplateId_wap();
							 }
							  if (wapType == 3 && isTemplateExist(fee.getTemplateId_3g())) {
								 feeTemplateId =fee.getTemplateId_3g();
							 }
						 }
						 if(!isTemplateExist(feeTemplateId) || feeTemplateId==null)//����Ĭ�ϵ�
							 feeTemplateId=fee.getTemplateId();
						 builder.append(feeTemplateId);
						 /**
						  * end
						  */
//						 builder.append(fee.getTemplateId());
						 builder.append("&");
					 }else{//�����Ʒ���ʾ
						 if(isIphone){
							 return null;
						 }else{
							 builder.append("/");
							 builder.append(fee.getUrl());
							 builder.append(ParameterConstants.PORTAL_PATH);
							 builder.append("?");
						 }
					 }
			}
		}
		map.put("builder",builder.toString());
		map.put("feeId", fid);
		return map;
	}
	private boolean isTemplateExist(Integer tid) {
		if (tid != null && tid > 0)
			return true;
		return false;
	}
}
