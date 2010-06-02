package cn.joy.ggg.api.service.impl;

import Ice.Current;
import cn.joy.ggg.api.model.CommentResponse;
import cn.joy.ggg.api.service._CommentServiceDisp;
import cn.joy.ggg.commons.IceServlet;

public class CommentServiceImpl extends _CommentServiceDisp {

  private static final long serialVersionUID = 97400067471026442L;

  public CommentResponse getCommentList(String appId, String cid, int start, int limit, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMENT != null) {
      return IceServlet.SERVICE_PRX_COMMENT.getCommentList(appId, cid, start, limit);
    }
    return null;
  }

  public CommentResponse postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMENT != null) {
      return IceServlet.SERVICE_PRX_COMMENT.postComment(appId, userId, cid, connect, isAnonymity);
    }
    return null;
  }

  public CommentResponse removeComment(String appId, String cid, String commentIds, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMENT != null) {
      return IceServlet.SERVICE_PRX_COMMENT.removeComment(appId, cid, commentIds);
    }
    return null;
  }

}
