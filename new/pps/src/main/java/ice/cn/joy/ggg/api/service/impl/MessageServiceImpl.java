package cn.joy.ggg.api.service.impl;

import Ice.Current;
import cn.joy.ggg.api.model.MessageResponse;
import cn.joy.ggg.api.service._MessageServiceDisp;
import cn.joy.ggg.commons.IceServlet;

public class MessageServiceImpl extends _MessageServiceDisp {

  private static final long serialVersionUID = 285702462438098915L;

  public MessageResponse getMessageList(String source, int start, int count, Current __current) {
    if(IceServlet.SERVICE_PRX_MESSAGE != null) {
      return IceServlet.SERVICE_PRX_MESSAGE.getMessageList(source, start, count);
    }
    return null;
  }

  public MessageResponse removeMessage(String source, int msgId, Current __current) {
    if(IceServlet.SERVICE_PRX_MESSAGE != null) {
      return IceServlet.SERVICE_PRX_MESSAGE.removeMessage(source, msgId);
    }
    return null;
  }

  public MessageResponse sendMessage(String source, String dest, String msg, Current __current) {
    if(IceServlet.SERVICE_PRX_MESSAGE != null) {
      return IceServlet.SERVICE_PRX_MESSAGE.sendMessage(source, dest, msg);
    }
    return null;
  }

}
