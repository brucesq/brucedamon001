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
	 * 获取用户号段信息
	 * @param mobile
	 * @return
	 */
	public MobileInfo getMobileInfo(String mobile);
	/**
	 * 获取用户UA信息
	 * @param ua
	 * @return
	 */
	public UAInfo getUAInfo(String ua);
	
	/**
	 * 获取用户白名单信息
	 * @param mobile
	 * @return
	 */
	public List<PersonInfo> getPersonInfo(String mobile);
	
	
	public <T>T get(Class<T> clazz,Serializable id);
	
	public List<Integer> getUaGroupIdsByUa(String ua);
	
	/**
	 * 注册新用户
	 * @return
	 */
	public String registerNewMobile();
}
