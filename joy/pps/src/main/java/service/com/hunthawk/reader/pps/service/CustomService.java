/**
 * 
 */
package com.hunthawk.reader.pps.service;

import java.util.List;
import java.util.Set;

import com.hunthawk.reader.domain.custom.BookBag;
import com.hunthawk.reader.domain.custom.Bookmark;
import com.hunthawk.reader.domain.custom.Favorites;
import com.hunthawk.reader.domain.custom.UserBuy;
import com.hunthawk.reader.domain.custom.UserBuyMonth;
import com.hunthawk.reader.domain.custom.UserBuyMonthChoice;
import com.hunthawk.reader.domain.custom.UserFootprint;
import com.hunthawk.reader.domain.custom.UserMonthUnicomBackMsg;
import com.hunthawk.reader.domain.custom.UserOrderList;
import com.hunthawk.reader.domain.custom.UserUnsubscribeList;
import com.hunthawk.reader.domain.inter.Reservation;
import com.hunthawk.reader.domain.partner.Fee;

/**
 * @author BruceSun
 * 
 */
public interface CustomService {

	public List<BookBag> getUserBookbag(String mobile);

	/**
	 * 添加书包,同时需要将该信息添加到用户购买表中
	 * 
	 * @param bag
	 * @throws Exception
	 */
	public void addBookbag(BookBag bag) throws Exception;

	/**
	 * 针对N选X的情况,记录用户选择的情况，同时将数据添加到书包及用户购买表中
	 * 
	 * @param pid
	 *            平台的产品ID
	 * @param resourceId
	 *            资源ID
	 * @param feeId
	 *            计费ID
	 * @param mobile
	 *            用户手机号码
	 * @param packId
	 *            批价包ID
	 * @param channelId
	 *            推广渠道ID，可以为空
	 * @throws Exception
	 */
	public void addChoiceBook(String pid, String resourceId, String feeId,
			String mobile, int packId, String channelId) throws Exception;

	/**
	 * 获取用户当月通过N选X方式选定的图书
	 * 
	 * @param mobile
	 * @param feeId
	 * @return
	 */
	public List<UserBuyMonthChoice> getUserChoiceBooks(String mobile,
			String feeId);

	/**
	 * 添加用户流水
	 * 
	 * @param order
	 */
	public void addUserOrderList(UserOrderList order);
	/**
	 * 添加iphone退订流水表
	 * @param order
	 */
	public void addIphoneUnsubList(UserUnsubscribeList order);
	/**
	 * 记录联通返回sequence_id
	 * @param umub
	 */
	public void addIphoneUnicomBackMsg(UserMonthUnicomBackMsg umub);
	/**
	 * 
	 * @param umub
	 * @return
	 */
	public UserMonthUnicomBackMsg getUserMonthUnicomBackMsg(String mobile,int packId,int orderType,String pid,int columnId,String feeid);
	/**
	 * 用户包月购买
	 * 
	 * @param month
	 */
	public void addUserBuyMonth(UserBuyMonth month);
//	/**
//	 * 用户包月删除方法  
//	 * @param mobile
//	 * @param isIphone  是IPHONE产品则增加删除条件 
//	 */
//	public void delUserBuyMonth(String mobile,String feeid,boolean isIphone,String ids);

	/**
	 * 获取用户购买的所有信息
	 * 
	 * @param mobile
	 * @return
	 */
	public List<UserBuy> getUserBuyList(String mobile);

	/**
	 * 判断用户是否购买了本书
	 * 
	 * @param mobile
	 * @param cid
	 * @return
	 */
	public boolean isUserBuyBook(String mobile, String cid);

	public void deleteUserBookbag(String mobile, String cid);

	/**
	 * 获得用户的收藏列表
	 * 
	 * @param mobile
	 * @return
	 * modify by liuxh  09-11-12 增加产品ID参数 过滤产品
	 */
	public List<Favorites> getUserFavorites(String mobile,String productId);
	/**
	 * 获得资源被收藏总数
	 * @param resourceId
	 * @return
	 */
	public int getResourceFavoritesCount(String resourceId);

	public List<Favorites> getUserFavoritesByPage(String mobile, String productId,int pageSize,
			int pageNo);

	public List<BookBag> getUserBookbagsByPage(String mobile, String productId,int pageSize,
			int pageNo);

	public List<Bookmark> getUserBookmarksByPage(String mobile,String productId,int pageSize,
			int pageNo);

	public void addFavorites(Favorites fav) throws Exception;

	/**增加产品ID参数*/
	public void deleteFavorites(String mobile, String cid,String productid);

	public List<Bookmark> getUserBookmark(String mobile,String productId);

	public void addBookmark(Bookmark mark) throws Exception;

	public void deleteBookmark(Integer id);

	/**
	 * 
	 * @param mobile
	 * @param productId
	 * @return
	 * modify by liuxh 09-11-12 增加参数productId 过滤产品
	 */
	public Long getUserFavoritesResultCount(String mobile,String productId);

	public Long getUserBookbagResultCount(String mobile);

	public int getUserBookmarkResultCount(String mobile,String productId);

	/**
	 * 通过计费ID获取计费对象
	 * 
	 * @param feeId
	 * @return
	 */
	public Fee getFee(String feeId);

	/**
	 * 通过计费URL获取计费对象
	 * 
	 * @param url
	 * @return
	 */
	public Fee getFeeByUrl(String url);

	/**
	 * 获取用户包月的信息
	 * 
	 * @param mobile
	 * @return
	 */
	public List<UserBuyMonth> getUserBuyMonths(String mobile);

	/**
	 * 判断用户是否订购了该包月业务
	 * 
	 * @param mobile
	 * @param feeid
	 * @return
	 */
	public boolean isOrderMonth(String mobile, String feeid);
	
	/**
	 * 判断用户是否订购了该批价包
	 * @param mobile
	 * @param feeid
	 * @param packId
	 * @return
	 */
	public boolean isOrderMonth(String mobile, String feeid,Integer packId);

	/**
	 * 添加连载预订
	 * 
	 * @param mobile
	 *            手机号码
	 * @param contentId
	 *            内容ID
	 * @param packRelationId
	 *            批价包关联ID
	 */
	public void addReservation(String mobile, String contentId,
			Integer packRelationId);

	/**
	 * 获取用户连载预订列表
	 * 
	 * @param mobile
	 * @return
	 */
	public List<Reservation> getReservation(String mobile);

	/**
	 * 删除用户某项连载预订
	 * 
	 * @param mobile
	 * @param contentId
	 */
	public void deleteReservation(String mobile, String contentId);

	/**
	 * 更新用户访问记录地址
	 * 
	 * @param mobile
	 *            手机号码
	 * @param contentId
	 *            内容ID
	 * @param url
	 *            链接地址
	 */
	public void updateUserFootprint(String mobile, String contentId, String url,String productId);

	/**
	 * 获取用户最近访问内容的地址,如果没有返回null
	 * 
	 * @param mobile
	 * @param contentId
	 * @return
	 */
	public String getLastUserFootprint(String mobile, String contentId);

	/**
	 * 返回用户最近看过的历史记录
	 * @param mobile
	 * @return
	 */
	public List<UserFootprint> getUsetFootprints(String mobile,int pageNo,int pageSize,String productId);
	/**
	 * 获取所有用户访问的地址主键
	 * 
	 * @return
	 */
	public Set<String> getAllUserFootprintKey();
	/**
	 * 记录阅读此书的最近的10个读者，然后关联这10个读者阅读过的书目，在这些书目中选择最新的20本出来显示为关联书目，其中显示数可以开放由编辑设置，不超过20本
	 * @param resourceId
	 * @return 返回批价包关联ID
	 * modify by liuxh 09-11-09  增加参数packId 用于标识计费信息
	 */
	public List<Integer> getResourceUserRelation(String resourceId,int packId,int pageSize);
	
	/**
	 * 更改UserMonthUnicomBackMsg 表信息状态
	 * @param id
	 */
	public void updateUserMonthUnicomBackMsgStatus(int id);
	
	/**
	 * 获取用户单条预定记录
	 * @param mobile
	 * @param contentid
	 * @return
	 */
	public Reservation getReservationByContetnId(String mobile,String contentid);
	/**
	 * 判断当前用户 针对某条资源所属的批价包集合中是否有订购包月业务 
	 * @param resourceId
	 * @param mobile
	 * @return
	 */
	public boolean isOtherOrderMonth(String resourceId,String mobile,String productId);
}
