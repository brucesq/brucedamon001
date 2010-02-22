/**
 * 
 */
package com.hunthawk.reader.pps.service;

import java.io.Serializable;
import java.util.List;

import com.hunthawk.reader.domain.device.MobileInfo;
import com.hunthawk.reader.domain.device.PersonInfo;
import com.hunthawk.reader.domain.device.UAInfo;

/**
 * @author BruceSun
 *
 */
public interface GuestService {

	/**
	 * ��ȡ�û��Ŷ���Ϣ
	 * @param mobile
	 * @return
	 */
	public MobileInfo getMobileInfo(String mobile);
	/**
	 * ��ȡ�û�UA��Ϣ
	 * @param ua
	 * @return
	 */
	public UAInfo getUAInfo(String ua);
	
	/**
	 * ��ȡ�û���������Ϣ
	 * @param mobile
	 * @return
	 */
	public List<PersonInfo> getPersonInfo(String mobile);
	
	
	public <T>T get(Class<T> clazz,Serializable id);
	
	public List<Integer> getUaGroupIdsByUa(String ua);
	
	/**
	 * ע�����û�
	 * @return
	 */
	public String registerNewMobile();
}