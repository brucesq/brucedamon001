package cn.joy.ggg.api.service.impl;

import java.util.List;

import Ice.Current;
import cn.joy.ggg.api.model.FriendDevelopment;
import cn.joy.ggg.api.model.PageSupport;
import cn.joy.ggg.api.model.Topic;
import cn.joy.ggg.api.model.UserProfile;
import cn.joy.ggg.api.service._CommunityServiceDisp;
import cn.joy.ggg.commons.IceServlet;

public class CommunityServiceImpl extends _CommunityServiceDisp {

  private static final long serialVersionUID = -1001095699767698749L;

  @Override
  public int addCollect(String userid, String title, String link, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.addCollect(userid, title, link);
    }
    return 0;
  }

  @Override
  public int addTopic(String userid, Topic tipoc, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.addTopic(userid, tipoc);
    }
    return 0;
  }

  @Override
  public void encodeNotify(Topic topic, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      IceServlet.SERVICE_PRX_COMMUNITY.encodeNotify(topic);
    }
  }

  @Override
  public PageSupport getFriendDevelopment(String userId, int pageNo, int pageSize, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.getFriendDevelopment(userId, pageNo, pageSize);
    }
    return null;
  }

  @Override
  public PageSupport getList(String userid, int pageNo, int pageSize, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.getList(userid, pageNo, pageSize);
    }
    return null;
  }

  @Override
  public PageSupport getScoringHistory(String userid, int pageNo, int pageSize, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.getScoringHistory(userid, pageNo, pageSize);
    }
    return null;
  }

  @Override
  public PageSupport getSelfDevelopment(String selfId, int pageNo, int pageSize, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.getSelfDevelopment(selfId, pageNo, pageSize);
    }
    return null;
  }

  @Override
  public PageSupport getTopicList(String userid, int pageNo, int pageSize, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.getTopicList(userid, pageNo, pageSize);
    }
    return null;
  }

  @Override
  public UserProfile getUser(String userid, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.getUser(userid);
    }
    return null;
  }

  @Override
  public int removeCollect(String userid, String collectid, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.removeCollect(userid, collectid);
    }
    return 0;
  }

  @Override
  public int removeTopic(String userid, String tid, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.removeTopic(userid, tid);
    }
    return 0;
  }

  @Override
  public List<UserProfile> searchUser(UserProfile user, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.searchUser(user);
    }
    return null;
  }

  @Override
  public int setTopic(String userid, Topic topic, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.setTopic(userid, topic);
    }
    return 0;
  }

  @Override
  public int setUser(UserProfile user, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.setUser(user);
    }
    return 0;
  }

  @Override
  public int addDevelopment(FriendDevelopment devel, Current __current) {
    if(IceServlet.SERVICE_PRX_COMMUNITY != null) {
      return IceServlet.SERVICE_PRX_COMMUNITY.addDevelopment(devel);
    }
    return 0;
  }

}
