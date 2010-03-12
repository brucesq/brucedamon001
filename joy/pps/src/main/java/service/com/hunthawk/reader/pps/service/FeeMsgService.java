package com.hunthawk.reader.pps.service;

import java.util.Date;

import com.hunthawk.reader.domain.custom.FeeBackMessage;
import com.hunthawk.reader.domain.custom.SearchBalanceBackMessage;
import com.hunthawk.reader.domain.custom.SearchOrderBackMessage;
import com.hunthawk.reader.domain.custom.UserOrderBackMessage;


public interface FeeMsgService {
	/**
	 *	�ͻ�����֧����ACIP02001��
	 *  ����http���󲢴�����
	 * @param service_code ����������
	 * @param svcnum  �û����룻���ڹ�������������û���ֻ������Ĭ����д000000���û���ѯ��������벻Ϊ��  
	 * @param svcid
	 * @param clid SP��ʶ(�����˾ 0001����Ƶ��˾ 0002����Ƶ��˾ 0003)
	 * @param recordsn ������ˮ��
	 * @param resource  ������Դ����
 	 * @param pay_type ֧�����ͣ�1������ 2�����£�
	 * @param pay_money ֧�����
	 * @param cp_name  cp ����
	 * @param pro_channel ��Ʒ����Ƶ��
	 * @param pro_no ��Ʒ����
	 * @param pro_name ��Ʒ����
	 * @param view_url ��Ʒ�Ĳ鿴���ӵ�ַ�����û�ֱ���ڶ����б������鿴��ʱ��������ַ��
	 * @param cancel_url ��Ʒ��Ŀ¼���˶����ӵ�ַ�����û�ֱ���ڶ����б������˶��������û�����ǹ����Ʒ����ѡ���ʡ��
	 * @param invalidate_date ���·����ʧЧʱ�䣬�ǰ���ҵ���ʡ�Ը�ѡ���ʽΪyyyy-MM-dd hh24:mi:ss  
	 * @return  ���ط�װ�ķ�����Ϣ
	 */
	public FeeBackMessage sendFeeMessage(String service_code,String svcnum,String svcid,String clid,String recordsn, String resource,Integer pay_type,Integer pay_money,String cp_name,
			String pro_channel,String pro_no,String pro_name,String view_url,String cancel_url,Date invalidate_date);
	
	/***
	 * �ͻ�������ѯ��ACIP01002��
	 *@param service_code ����������
	 * @param svcnum  �û����룻���ڹ�������������û���ֻ������Ĭ����д000000���û���ѯ��������벻Ϊ��  
	 * @param svcid
	 * @param clid SP��ʶ(�����˾ 0001����Ƶ��˾ 0002����Ƶ��˾ 0003)
	 * @param recordsn ������ˮ��
	 * @param pro_no SP��Ʒ����
	 * @param begin_date  ����ʽΪyyyy-MM-dd hh24:mi:ss  
	 * @param end_date	����ʽΪyyyy-MM-dd hh24:mi:ss  
	 * @return
	 */
	public SearchOrderBackMessage searchOrderMessage(String service_code,String svcnum,String svcid,String clid,String recordsn,String pro_no,String begin_date,String end_date);
	
	
	/***
	 * �ͻ��ʺ�����ѯ��ACIP01001��
	 *@param service_code ����������
	 * @param svcnum  �û����룻���ڹ�������������û���ֻ������Ĭ����д000000���û���ѯ��������벻Ϊ��  
	 * @param svcid
	 * @param clid SP��ʶ(�����˾ 0001����Ƶ��˾ 0002����Ƶ��˾ 0003)
	 * @param recordsn ������ˮ��
	 * @return
	 */
	public SearchBalanceBackMessage searchBalanceMessage(String service_code,String svcnum,String svcid,String clid,String recordsn);
	
	/***
	 * �ͻ������˶��ɹ�֪ͨ��ACIP02002��
	 * @param service_code ����������
	 * @param svcnum  �û����룻���ڹ�������������û���ֻ������Ĭ����д000000���û���ѯ��������벻Ϊ��  
	 * @param svcid
	 * @param clid SP��ʶ(�����˾ 0001����Ƶ��˾ 0002����Ƶ��˾ 0003)
	 * @param recordsn ������ˮ��
	 * @param sequence_id �Ʒ�ƽ̨������ˮ��
	 * @return
	 */
	public boolean unsubscribeMessage(String service_code,String svcnum,String svcid,String clid,String recordsn,String sequence_id);
	
	
	
	/***
	 * �ͻ�������ѯ��ACIP01002��
	 * @param service_code ����������
	 * @param svcnum  �û����룻���ڹ�������������û���ֻ������Ĭ����д000000���û���ѯ��������벻Ϊ��  
	 * @param svcid
	 * @param clid SP��ʶ(�����˾ 0001����Ƶ��˾ 0002����Ƶ��˾ 0003)
	 * @param recordsn ������ˮ��
	 * @param pro_no SP��Ʒ����
	 * @param state 0 ���ж�����1 ��ǰ��Ч����
	 * @param begin_date ����������������β�ѯ(���ڵ���)��ʼʱ�䣬��ʽ yyyy-MM-dd HH:mm:ss
	 * @param end_date ����������������β�ѯ(С�ڵ���)����ʱ�䣬��ʽ yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public UserOrderBackMessage userOrderMessage(String service_code,String svcnum,String clid,String recordsn,String pro_no,String state,String begin_date,String end_date);
	
	
}
