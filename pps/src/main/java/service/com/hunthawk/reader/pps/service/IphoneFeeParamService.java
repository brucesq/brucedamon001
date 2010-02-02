package com.hunthawk.reader.pps.service;
/**
 * 用于获取IPHONE的计费参数接口
 * @author liuxh
 *
 */
public interface IphoneFeeParamService {
	/**
	 * 获取IPHONE栏目包月批价包ID集合 (区分资源类型)
	 * @param resourceId
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String[] getIphoneMonthPackIds(String resourceId);
	/**
	 * 获取IPHONE频道收费批价包ID (区分资源类型)
	 * @param resourceId
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String getIphoneChannelPackId(String resourceId);
	/**
	 * 得到IPHONE包月计费id集合 (区分资源类型)
	 * @param resourceId
	 * @param flag 1.频道 2.栏目
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String[] getIphoneFeeIds(String resourceId,int flag);
}
