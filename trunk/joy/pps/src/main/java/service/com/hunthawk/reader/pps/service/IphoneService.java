/**
 * 
 */
package com.hunthawk.reader.pps.service;

import com.hunthawk.reader.domain.custom.UserOrderBackMessage;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.domain.resource.ResourcePackReleation;

/**
 * IPHONE�û���������
 * @author BruceSun
 *
 */
public interface IphoneService {
	/**
	 * IPHONE�û������ж�
	 * @param mobile
	 * @param productId
	 * @param packId
	 * @param resourceId
	 * @return
	 */
	public boolean isUserBuyBook(String mobile,String productId,Integer packId,String resourceId);
	public boolean isUserBuy(String mobile,String productId,Integer packId,String resourceId,String relId);
	/**
	 * �жϲ�Ʒ�Ƿ���IPHONE��Ʒ
	 * @param productId
	 * @return
	 */
	public boolean isIphoneProduct(String productId);
	
	/**
	 * �ж����۰��Ƿ���԰���
	 * @param packId
	 * @return
	 */
	public boolean isPackFeeMonth(Integer packId);
	
	/**
	 * �ж��û��Ƿ���VIP��Ա
	 * @param mobile
	 * @return
	 */
	public boolean isVipUser(String mobile);
	
	
	/**
	 * Ƶ������
	 * @param mobile �ֻ�����
	 * @param pid	 ��ƷID
	 * @param packId ���۰�ID ����ͳ��
	 * @param viewUrl ��Ʒ�����ַ
	 * @return
	 */
	public String orderChannel(String mobile,String pid,Integer packId,Fee fee,String viewUrl);
	/**
	 * VIP����
	 * @param mobile
	 * @param pid
	 * @param packId 
	 * @param viewUrl ��Ʒ�����ַ
	 * @return
	 */
	public String orderVip(String mobile,String pid,Integer packId,Fee fee,String viewUrl);
	
	/**
	 * ��Ŀ���¶���
	 * @param mobile
	 * @param pid
	 * @param packId
	 * @param columnId
	 * @param viewUrl ��Ŀ�����ַ
	 * @return
	 */
	public String orderColumn(String mobile,String pid,Integer packId,Fee fee,Integer columnId,String viewUrl);
	

	/**
	 * ���ζ�����Դ
	 * @param mobile
	 * @param pid
	 * @param packId
	 * @param fee
	 * @param resourceId
	 * @param relId ���۰�����ID
	 * @param viewUrl ��Դҳ��ַ
	 * @return
	 */
	public String orderResource(String mobile,String pid,Integer packId,Fee fee,String resourceId,Integer relId,String viewUrl);
	/**
	 * ��ȡ�û�������Դ�ƷѶ��������VIPʹ��VIP�����ۿۼƷѴ��룬����ֱ�ӷ�����ͨ�Ʒ�
	 * @param mobile
	 * @param fee
	 * @return
	 */
	public Fee getResourceFee(String mobile,Fee fee);
	/**
	 * ��ȡ�û���Ʒ���¼ƷѶ��������VIP����VIP�ۿۼƷѴ���,���򷵻���ͨ�ƷѴ���
	 * @param mobile
	 * @return
	 */
	public Fee getChannel(String mobile);
	/**
	 * ������Ŀ�ƷѶ���,�����Ŀ�����ڰ��·���null,���������VIP����VIP�ۿۼƷѴ���,���򷵻���ͨ�ƷѴ���
	 * @param mobile
	 * @param packId
	 * @return
	 */
	public Fee getColumnFee(String mobile,Integer packId);
	/**
	 * ��ȡ�µ�Iphone��Ʒ���
	 * @param rid
	 * @param packId
	 * @param relId
	 * @return
	 */
	public String getIphoneProductNo(String rid,String packId,String relId);
	/**
	 * ��¼iphone���ζ�����Ϣ
	 * @param rpr
	 * @param mobile
	 * @param resourceId
	 * @return
	 */
	public String orderOne(ResourcePackReleation rpr,String mobile,String resourceId,String pid);
	/**
	 * ��¼iphone���¶�����Ϣ
	 * @param rpr
	 * @param mobile
	 * @return
	 */
	public String orderMonth(String feeId,Integer packId,String mobile,String pid);
	/**
	 * �û�������ѯ
	 * @param mobile
	 * @param pro_no
	 * @param state
	 * @param begin_date
	 * @param end_date
	 * @return
	 */
	public UserOrderBackMessage searchOrder(String mobile,String pro_no,String state,String begin_date,String end_date);
}
