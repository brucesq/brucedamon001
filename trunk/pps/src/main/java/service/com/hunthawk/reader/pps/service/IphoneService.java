/**
 * 
 */
package com.hunthawk.reader.pps.service;

import com.hunthawk.reader.domain.custom.UserOrderBackMessage;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;

/**
 * IPHONE用户订购服务
 * @author BruceSun
 *
 */
public interface IphoneService {
	/**
	 * IPHONE用户订购判断
	 * @param mobile
	 * @param productId
	 * @param packId
	 * @param resourceId
	 * @return
	 */
	public boolean isUserBuyBook(String mobile,String productId,Integer packId,String resourceId);
	public boolean isUserBuy(String mobile,String productId,Integer packId,String resourceId,String relId);
	/**
	 * 判断产品是否是IPHONE产品
	 * @param productId
	 * @return
	 */
	public boolean isIphoneProduct(String productId);
	
	/**
	 * 判断批价包是否可以包月
	 * @param packId
	 * @return
	 */
	public boolean isPackFeeMonth(Integer packId);
	
	/**
	 * 判断用户是否是VIP会员
	 * @param mobile
	 * @return
	 */
	public boolean isVipUser(String mobile);
	
	
	/**
	 * 频道包月
	 * @param mobile 手机号码
	 * @param pid	 产品ID
	 * @param packId 批价包ID 用于统计
	 * @param viewUrl 产品浏览地址
	 * @return
	 */
	public String orderChannel(String mobile,String pid,Integer packId,Fee fee,String viewUrl);
	/**
	 * VIP订购
	 * @param mobile
	 * @param pid
	 * @param packId 
	 * @param viewUrl 产品浏览地址
	 * @return
	 */
	public String orderVip(String mobile,String pid,Integer packId,Fee fee,String viewUrl);
	
	/**
	 * 栏目包月订购
	 * @param mobile
	 * @param pid
	 * @param packId
	 * @param columnId
	 * @param viewUrl 栏目浏览地址
	 * @return
	 */
	public String orderColumn(String mobile,String pid,Integer packId,Fee fee,Integer columnId,String viewUrl);
	

	/**
	 * 按次订购资源
	 * @param mobile
	 * @param pid
	 * @param packId
	 * @param fee
	 * @param resourceId
	 * @param relId 批价包关联ID
	 * @param viewUrl 资源页地址
	 * @return
	 */
	public String orderResource(String mobile,String pid,Integer packId,Fee fee,String resourceId,Integer relId,String viewUrl);
	/**
	 * 获取用户按次资源计费对象，如果是VIP使用VIP按次折扣计费代码，否则直接返回普通计费
	 * @param mobile
	 * @param fee
	 * @return
	 */
	public Fee getResourceFee(String mobile,Fee fee);
	/**
	 * 获取用户产品包月计费对象，如果是VIP返回VIP折扣计费代码,否则返回普通计费代码
	 * @param mobile
	 * @return
	 */
	public Fee getChannel(String mobile);
	/**
	 * 返回栏目计费对象,如果栏目不存在包月返回null,否则如果是VIP返回VIP折扣计费代码,否则返回普通计费代码
	 * @param mobile
	 * @param packId
	 * @return
	 */
	public Fee getColumnFee(String mobile,Integer packId);
	/**
	 * 获取新的Iphone产品编号
	 * @param rid
	 * @param packId
	 * @param relId
	 * @return
	 */
	public String getIphoneProductNo(String rid,String packId,String relId);
	/**
	 * 记录iphone按次订购信息
	 * @param rpr
	 * @param mobile
	 * @param resourceId
	 * @return
	 */
	public String orderOne(ResourcePackReleation rpr,String mobile,String resourceId,String pid);
	/**
	 * 记录iphone包月订购信息
	 * @param rpr
	 * @param mobile
	 * @return
	 */
	public String orderMonth(String feeId,Integer packId,String mobile,String pid);
	/**
	 * 用户订单查询
	 * @param mobile
	 * @param pro_no
	 * @param state
	 * @param begin_date
	 * @param end_date
	 * @return
	 */
	public UserOrderBackMessage searchOrder(String mobile,String pro_no,String state,String begin_date,String end_date);
}
