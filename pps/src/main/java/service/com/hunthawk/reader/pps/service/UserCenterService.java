/**
 * 
 */
package com.hunthawk.reader.pps.service;

import com.hunthawk.UserCenter.domain.InformationPO;
import com.hunthawk.UserCenter.domain.LoginPO;

/**
 * @author liuxh
 *
 */
public interface UserCenterService {
	public boolean name_exists(String name);
	public boolean registUser(String loginname,String mobile,String passwd);
	public LoginPO login(String loginname,String passwd,int status);
	public InformationPO getUserInfo(String mobile);
	public boolean updUserInfo(InformationPO info);
}
