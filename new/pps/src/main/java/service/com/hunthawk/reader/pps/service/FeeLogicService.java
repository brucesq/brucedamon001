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
	 * @param month_fee_bag_id  IPHONE��Ʒ���IPHONE��Ʒ�ǰ��²�Ʒ �˲���Ϊ-1
	 * @return
	 */
	public Map isFee(String productId,String resourceId,String mobile,ResourcePackReleation resourcePackReleation,int packId,int month_fee_bag_id);
}
