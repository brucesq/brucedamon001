package cn.joy.ggg.api.service.impl;

import Ice.Current;
import cn.joy.ggg.api.model.RelationResponse;
import cn.joy.ggg.api.service._RelationServiceDisp;
import cn.joy.ggg.commons.IceServlet;

public class RelationServiceImpl extends _RelationServiceDisp {

  private static final long serialVersionUID = 260198425087742871L;

  public RelationResponse addFriend(String self, String buddy, String description, Current __current) {
    if(IceServlet.SERVICE_PRX_RELATION != null) {
      return IceServlet.SERVICE_PRX_RELATION.addFriend(self, buddy, description);
    }
    return null;
  }

  public RelationResponse getFriendList(String self, int start, int count, Current __current) {
    if(IceServlet.SERVICE_PRX_RELATION != null) {
      return IceServlet.SERVICE_PRX_RELATION.getFriendList(self, start, count);
    }
    return null;
  }

  public RelationResponse removeFriend(String self, String buddy, Current __current) {
    if(IceServlet.SERVICE_PRX_RELATION != null) {
      return IceServlet.SERVICE_PRX_RELATION.removeFriend(self, buddy);
    }
    return null;
  }

}
