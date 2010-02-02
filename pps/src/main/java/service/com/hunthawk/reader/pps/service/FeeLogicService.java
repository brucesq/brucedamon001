package com.hunthawk.reader.pps.service;

import java.util.Map;

import com.hunthawk.reader.domain.resource.ResourcePackReleation;

/**
 * 
 * @author liuxh
 *
 */
public interface FeeLogicService {
	/**
	 * 
	 * @param productId
	 * @param resourceId
	 * @param mobile
	 * @param resourcePackReleation
	 * @param packId
	 * @param month_fee_bag_id  IPHONE产品或非IPHONE产品非包月产品 此参数为-1
	 * @return
	 */
	public Map isFee(String productId,String resourceId,String mobile,ResourcePackReleation resourcePackReleation,int packId,int month_fee_bag_id);
}
