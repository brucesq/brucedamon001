package com.hunthawk.reader.pps.service;
/**
 * ���ڻ�ȡIPHONE�ļƷѲ����ӿ�
 * @author liuxh
 *
 */
public interface IphoneFeeParamService {
	/**
	 * ��ȡIPHONE��Ŀ�������۰�ID���� (������Դ����)
	 * @param resourceId
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String[] getIphoneMonthPackIds(String resourceId);
	/**
	 * ��ȡIPHONEƵ���շ����۰�ID (������Դ����)
	 * @param resourceId
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String getIphoneChannelPackId(String resourceId);
	/**
	 * �õ�IPHONE���¼Ʒ�id���� (������Դ����)
	 * @param resourceId
	 * @param flag 1.Ƶ�� 2.��Ŀ
	 * @return
	 * add by liuxh 09-11-16
	 */
	public String[] getIphoneFeeIds(String resourceId,int flag);
}
