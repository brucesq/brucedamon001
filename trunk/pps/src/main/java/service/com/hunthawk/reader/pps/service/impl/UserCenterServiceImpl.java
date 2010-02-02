/**
 * 
 */
package com.hunthawk.reader.pps.service.impl;

import com.hunthawk.UserCenter.OperatorError;
import com.hunthawk.UserCenter.domain.InformationPO;
import com.hunthawk.UserCenter.domain.LoginPO;
import com.hunthawk.UserCenter.editinfo.UserEditInfoPrx;
import com.hunthawk.UserCenter.editinfo.UserEditInfoPrxHelper;
import com.hunthawk.UserCenter.login.UserLoginPrx;
import com.hunthawk.UserCenter.login.UserLoginPrxHelper;
import com.hunthawk.UserCenter.register.UserRegistPrx;
import com.hunthawk.UserCenter.register.UserRegistPrxHelper;
import com.hunthawk.reader.pps.service.UserCenterService;
import com.hunthawk.reader.pps.usercener.PropertiesUtil;

/**
 * @author liuxh
 * 
 */
public class UserCenterServiceImpl implements UserCenterService {
	public boolean name_exists(String name) {
		boolean exists = true;
		int status = 0;
		Ice.Communicator ic = null;
		try {
			// 初始化
			ic = Ice.Util.initialize();
			// Ice.ObjectPrx base = ic.stringToProxy("UserLogin:tcp -h
			// 219.234.95.190 -p 10000");
			Ice.ObjectPrx base = ic.stringToProxy(PropertiesUtil
					.getParam_login());
			UserLoginPrx userLoginPrx = UserLoginPrxHelper.checkedCast(base);
			if (userLoginPrx == null) {
				throw new Error("Invalid proxy");
			}
			// 远程调用
			exists = userLoginPrx.checkUser(name);
		} catch (OperatorError err) {
			err.printStackTrace();
			status = 1;
		} catch (Ice.LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		// 销毁
		if (ic != null) {
			// Clean up
			//   
			try {
				ic.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		// System.exit(status);
		return exists;
	}

	public boolean registUser(String loginname, String mobile, String passwd) {
		boolean flag = false;
		int status = 0;
		Ice.Communicator ic = null;
		try {
			// 初始化
			ic = Ice.Util.initialize();
			// Ice.ObjectPrx base = ic.stringToProxy("UserRegist:tcp -h
			// 219.234.95.190 -p 10000");
			Ice.ObjectPrx base = ic.stringToProxy(PropertiesUtil
					.getParam_regist());

			UserRegistPrx userRegistPrx = UserRegistPrxHelper.checkedCast(base);
			if (userRegistPrx == null)
				throw new Error("Invalid proxy");

			flag = userRegistPrx.registerUser(loginname, mobile, passwd);

		} catch (OperatorError err) {
			System.out.println("注册失败:" + err.msg);
			err.printStackTrace();
			status = 1;
		} catch (Ice.LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.out.println("cause3:" + e.getMessage());
			System.err.println(e.getMessage());
			status = 1;
		}
		// 销毁
		if (ic != null) {
			try {
				ic.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		// System.exit(status);
		return flag;
	}

	public LoginPO login(String loginname, String passwd, int userstatus) {
		int status = 0;
		Ice.Communicator ic = null;
		LoginPO login = null;
		try {
			// 初始化
			ic = Ice.Util.initialize();
			// Ice.ObjectPrx base = ic.stringToProxy("UserLogin:tcp -h
			// 219.234.95.190 -p 10000");
			Ice.ObjectPrx base = ic.stringToProxy(PropertiesUtil
					.getParam_login());
			UserLoginPrx userLoginPrx = UserLoginPrxHelper.checkedCast(base);
			if (userLoginPrx == null) {
				throw new Error("Invalid proxy");
			}
			// 远程调用
			login = userLoginPrx.login(loginname, passwd, userstatus);

		} catch (OperatorError err) {
			err.printStackTrace();
			status = 1;
		} catch (Ice.LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		// 销毁
		if (ic != null) {
			// Clean up
			//   
			try {
				ic.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		// System.exit(status);
		return login;
	}

	public InformationPO getUserInfo(String mobile) {
		int status = 0;
		Ice.Communicator ic = null;
		InformationPO info = null;
		try {
			// 初始化
			ic = Ice.Util.initialize();
			// Ice.ObjectPrx base = ic.stringToProxy("UserEditInfo:tcp -h
			// 219.234.95.190 -p 10000");
			Ice.ObjectPrx base = ic.stringToProxy(PropertiesUtil
					.getParam_editinfo());
			UserEditInfoPrx ueiPrx = UserEditInfoPrxHelper.checkedCast(base);
			if (ueiPrx == null)
				throw new Error("Invalid proxy");
			// 远程调用
			info = ueiPrx.info(mobile);
		} catch (OperatorError err) {
			err.printStackTrace();
			status = 1;
		} catch (Ice.LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		// 销毁
		if (ic != null) {
			// Clean up
			//   
			try {
				ic.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		// System.exit(status);
		return info;
	}

	public boolean updUserInfo(InformationPO info) {
		boolean flag = false;
		int status = 0;
		Ice.Communicator ic = null;
		try {
			// 初始化
			ic = Ice.Util.initialize();

			// Ice.ObjectPrx base = ic.stringToProxy("UserEditInfo:tcp -h
			// 219.234.95.190 -p 10000");
			Ice.ObjectPrx base = ic.stringToProxy(PropertiesUtil
					.getParam_editinfo());

			UserEditInfoPrx userEIPrx = UserEditInfoPrxHelper.checkedCast(base);
			if (userEIPrx == null)
				throw new Error("Invalid proxy");

			flag = userEIPrx.editInfo(info);
			System.out.println("personal info update result--->" + flag);
		} catch (OperatorError err) {
			System.out.println("个人资料修改失败:" + err.msg);
			err.printStackTrace();
			status = 1;
		} catch (Ice.LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.out.println("cause3:" + e.getMessage());
			System.err.println(e.getMessage());
			status = 1;
		}
		// 销毁
		if (ic != null) {
			// Clean up
			//   
			try {
				ic.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		// System.exit(status);
		return flag;
	}

	public static void main(String[] args) {
		UserCenterServiceImpl impl = new UserCenterServiceImpl();
		InformationPO po = new InformationPO();
		po.address = "2";
		po.answer = "2";
		impl.updUserInfo(po);
	}
}
