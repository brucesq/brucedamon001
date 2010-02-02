package com.hunthawk.reader.pps.service.impl;

import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.IphoneFeeParamService;

public class IphoneFeeParamServiceImpl implements IphoneFeeParamService {
	private BussinessService bussinessService;
	public void setBussinessService(BussinessService bussinessService) {
		this.bussinessService = bussinessService;
	}
	/**
	 * 获取IPHONE栏目包月批价包ID集合 (区分资源类型)
	 * @param resourceId
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String[] getIphoneMonthPackIds(String resourceId){
		String[] packIds=new String[]{};
		if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//图书
			packIds=bussinessService.getVariables("iphone_month_packs").getValue().split(";");
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//杂志
			packIds=bussinessService.getVariables("iphone_month_packs_magazine").getValue().split(";");
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){//漫画
			
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){//报纸
			
		}
		return packIds;
	}
	/**
	 * 获取IPHONE频道收费批价包ID (区分资源类型)
	 * @param resourceId
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String getIphoneChannelPackId(String resourceId){
		String packId="0";
		if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//图书
			packId=bussinessService.getVariables("iphone_channel_packs").getValue();
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//杂志
			packId=bussinessService.getVariables("iphone_channel_packs_magazine").getValue();
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){//漫画
			
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){//报纸
			
		}
		return packId;
	}
	
	/**
	 * 得到IPHONE包月计费id集合 (区分资源类型)
	 * @param resourceId
	 * @param flag 1.频道 2.栏目
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String[] getIphoneFeeIds(String resourceId,int flag){
		String[] feeids=null;
		if(flag==1){
			if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//图书
				String iphone_fee_channel=bussinessService.getVariables("iphone_fee_channel").getValue();
				String iphone_fee_channel_dis=bussinessService.getVariables("iphone_fee_channel_dis").getValue();
				feeids=new String[]{iphone_fee_channel,iphone_fee_channel_dis};
			}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//杂志
				String iphone_fee_channel_magazine=bussinessService.getVariables("iphone_fee_channel_magazine").getValue();
				feeids=new String[]{iphone_fee_channel_magazine};
			}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){//漫画
				String iphone_fee_channel_comics=bussinessService.getVariables("iphone_fee_channel_comics").getValue();
				feeids=new String[]{iphone_fee_channel_comics};
			}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){//报纸
				String iphone_fee_channel_newspapers=bussinessService.getVariables("iphone_fee_channel_newspapers").getValue();
				feeids=new String[]{iphone_fee_channel_newspapers};
			}
		}else if(flag==2){
			if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){//图书
				String iphone_fee_column=bussinessService.getVariables("iphone_fee_column").getValue();
				String iphone_fee_column_dis=bussinessService.getVariables("iphone_fee_column_dis").getValue();
				feeids=new String[]{iphone_fee_column,iphone_fee_column_dis};
			}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){//杂志
				String iphone_fee_column_magazine=bussinessService.getVariables("iphone_fee_column_magazine").getValue();
				feeids=new String[]{iphone_fee_column_magazine};
			}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){//漫画
				String iphone_fee_column_comics=bussinessService.getVariables("iphone_fee_column_comics").getValue();
				feeids=new String[]{iphone_fee_column_comics};
			}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){//报纸
				String iphone_fee_column_newspapers=bussinessService.getVariables("iphone_fee_column_newspapers").getValue();
				feeids=new String[]{iphone_fee_column_newspapers};
			}
		}
		return feeids;
	}
}
