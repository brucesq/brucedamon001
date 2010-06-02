package cn.joy.ggg.api.service.impl;

import Ice.Current;
import cn.joy.ggg.api.model.UACListResponse;
import cn.joy.ggg.api.model.UACResponse;
import cn.joy.ggg.api.service._UACServiceDisp;
import cn.joy.ggg.commons.IceServlet;

public class UACServiceImpl extends _UACServiceDisp {

  private static final long serialVersionUID = 6076092575441814595L;

  public UACResponse changePassword(String userId, String oldpasswd, String newpasswd, Current __current) {
    if(IceServlet.SERVICE_PRX_UAC != null) {
      return IceServlet.SERVICE_PRX_UAC.changePassword(userId, oldpasswd, newpasswd);
    }
    return null;
  }

  public UACResponse checkLogin(String userId, String token, String userIp, Current __current) {
    if(IceServlet.SERVICE_PRX_UAC != null) {
      return IceServlet.SERVICE_PRX_UAC.checkLogin(userId, token, userIp);
    }
    return null;
  }

  public UACListResponse getUserInfoById(String userId, Current __current) {
    if(IceServlet.SERVICE_PRX_UAC != null) {
      return IceServlet.SERVICE_PRX_UAC.getUserInfoById(userId);
    }
    return null;
  }

  public UACListResponse getUserInfoByName(String userName, Current __current) {
    if(IceServlet.SERVICE_PRX_UAC != null) {
      return IceServlet.SERVICE_PRX_UAC.getUserInfoByName(userName);
    }
    return null;
  }

  public UACResponse login(String userName, String password, Current __current) {
    if(IceServlet.SERVICE_PRX_UAC != null) {
      return IceServlet.SERVICE_PRX_UAC.login(userName, password);
    }
    return null;
  }

}
