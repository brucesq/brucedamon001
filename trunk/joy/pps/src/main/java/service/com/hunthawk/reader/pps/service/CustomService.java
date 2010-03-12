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
	 * ������,ͬʱ��Ҫ������Ϣ��ӵ��û��������
	 * 
	 * @param bag
	 * @throws Exception
	 */
	public void addBookbag(BookBag bag) throws Exception;

	/**
	 * ���NѡX�����,��¼�û�ѡ��������ͬʱ��������ӵ�������û��������
	 * 
	 * @param pid
	 *            ƽ̨�Ĳ�ƷID
	 * @param resourceId
	 *            ��ԴID
	 * @param feeId
	 *            �Ʒ�ID
	 * @param mobile
	 *            �û��ֻ�����
	 * @param packId
	 *            ���۰�ID
	 * @param channelId
	 *            �ƹ�����ID������Ϊ��
	 * @throws Exception
	 */
	public void addChoiceBook(String pid, String resourceId, String feeId,
			String mobile, int packId, String channelId) throws Exception;

	/**
	 * ��ȡ�û�����ͨ��NѡX��ʽѡ����ͼ��
	 * 
	 * @param mobile
	 * @param feeId
	 * @return
	 */
	public List<UserBuyMonthChoice> getUserChoiceBooks(String mobile,
			String feeId);

	/**
	 * ����û���ˮ
	 * 
	 * @param order
	 */
	public void addUserOrderList(UserOrderList order);
	/**
	 * ���iphone�˶���ˮ��
	 * @param order
	 */
	public void addIphoneUnsubList(UserUnsubscribeList order);
	/**
	 * ��¼��ͨ����sequence_id
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
	 * �û����¹���
	 * 
	 * @param month
	 */
	public void addUserBuyMonth(UserBuyMonth month);
//	/**
//	 * �û�����ɾ������  
//	 * @param mobile
//	 * @param isIphone  ��IPHONE��Ʒ������ɾ������ 
//	 */
//	public void delUserBuyMonth(String mobile,String feeid,boolean isIphone,String ids);

	/**
	 * ��ȡ�û������������Ϣ
	 * 
	 * @param mobile
	 * @return
	 */
	public List<UserBuy> getUserBuyList(String mobile);

	/**
	 * �ж��û��Ƿ����˱���
	 * 
	 * @param mobile
	 * @param cid
	 * @return
	 */
	public boolean isUserBuyBook(String mobile, String cid);

	public void deleteUserBookbag(String mobile, String cid);

	/**
	 * ����û����ղ��б�
	 * 
	 * @param mobile
	 * @return
	 * modify by liuxh  09-11-12 ���Ӳ�ƷID���� ���˲�Ʒ
	 */
	public List<Favorites> getUserFavorites(String mobile,String productId);
	/**
	 * �����Դ���ղ�����
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

	/**���Ӳ�ƷID����*/
	public void deleteFavorites(String mobile, String cid,String productid);

	public List<Bookmark> getUserBookmark(String mobile,String productId);

	public void addBookmark(Bookmark mark) throws Exception;

	public void deleteBookmark(Integer id);

	/**
	 * 
	 * @param mobile
	 * @param productId
	 * @return
	 * modify by liuxh 09-11-12 ���Ӳ���productId ���˲�Ʒ
	 */
	public Long getUserFavoritesResultCount(String mobile,String productId);

	public Long getUserBookbagResultCount(String mobile);

	public int getUserBookmarkResultCount(String mobile,String productId);

	/**
	 * ͨ���Ʒ�ID��ȡ�ƷѶ���
	 * 
	 * @param feeId
	 * @return
	 */
	public Fee getFee(String feeId);

	/**
	 * ͨ���Ʒ�URL��ȡ�ƷѶ���
	 * 
	 * @param url
	 * @return
	 */
	public Fee getFeeByUrl(String url);

	/**
	 * ��ȡ�û����µ���Ϣ
	 * 
	 * @param mobile
	 * @return
	 */
	public List<UserBuyMonth> getUserBuyMonths(String mobile);

	/**
	 * �ж��û��Ƿ񶩹��˸ð���ҵ��
	 * 
	 * @param mobile
	 * @param feeid
	 * @return
	 */
	public boolean isOrderMonth(String mobile, String feeid);
	
	/**
	 * �ж��û��Ƿ񶩹��˸����۰�
	 * @param mobile
	 * @param feeid
	 * @param packId
	 * @return
	 */
	public boolean isOrderMonth(String mobile, String feeid,Integer packId);

	/**
	 * �������Ԥ��
	 * 
	 * @param mobile
	 *            �ֻ�����
	 * @param contentId
	 *            ����ID
	 * @param packRelationId
	 *            ���۰�����ID
	 */
	public void addReservation(String mobile, String contentId,
			Integer packRelationId);

	/**
	 * ��ȡ�û�����Ԥ���б�
	 * 
	 * @param mobile
	 * @return
	 */
	public List<Reservation> getReservation(String mobile);

	/**
	 * ɾ���û�ĳ������Ԥ��
	 * 
	 * @param mobile
	 * @param contentId
	 */
	public void deleteReservation(String mobile, String contentId);

	/**
	 * �����û����ʼ�¼��ַ
	 * 
	 * @param mobile
	 *            �ֻ�����
	 * @param contentId
	 *            ����ID
	 * @param url
	 *            ���ӵ�ַ
	 */
	public void updateUserFootprint(String mobile, String contentId, String url,String productId);

	/**
	 * ��ȡ�û�����������ݵĵ�ַ,���û�з���null
	 * 
	 * @param mobile
	 * @param contentId
	 * @return
	 */
	public String getLastUserFootprint(String mobile, String contentId);

	/**
	 * �����û������������ʷ��¼
	 * @param mobile
	 * @return
	 */
	public List<UserFootprint> getUsetFootprints(String mobile,int pageNo,int pageSize,String productId);
	/**
	 * ��ȡ�����û����ʵĵ�ַ����
	 * 
	 * @return
	 */
	public Set<String> getAllUserFootprintKey();
	/**
	 * ��¼�Ķ�����������10�����ߣ�Ȼ�������10�������Ķ�������Ŀ������Щ��Ŀ��ѡ�����µ�20��������ʾΪ������Ŀ��������ʾ�����Կ����ɱ༭���ã�������20��
	 * @param resourceId
	 * @return �������۰�����ID
	 * modify by liuxh 09-11-09  ���Ӳ���packId ���ڱ�ʶ�Ʒ���Ϣ
	 */
	public List<Integer> getResourceUserRelation(String resourceId,int packId,int pageSize);
	
	/**
	 * ����UserMonthUnicomBackMsg ����Ϣ״̬
	 * @param id
	 */
	public void updateUserMonthUnicomBackMsgStatus(int id);
	
	/**
	 * ��ȡ�û�����Ԥ����¼
	 * @param mobile
	 * @param contentid
	 * @return
	 */
	public Reservation getReservationByContetnId(String mobile,String contentid);
	/**
	 * �жϵ�ǰ�û� ���ĳ����Դ���������۰��������Ƿ��ж�������ҵ�� 
	 * @param resourceId
	 * @param mobile
	 * @return
	 */
	public boolean isOtherOrderMonth(String resourceId,String mobile,String productId);
}
