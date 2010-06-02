// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.0

package cn.joy.ggg.api.service;

// <auto-generated>
//
// Generated from file `community.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public interface _CommunityServiceOperationsNC
{
    java.util.List<cn.joy.ggg.api.model.UserProfile> searchUser(cn.joy.ggg.api.model.UserProfile user);

    int setUser(cn.joy.ggg.api.model.UserProfile user);

    cn.joy.ggg.api.model.UserProfile getUser(String userid);

    int addCollect(String userid, String title, String link);

    int removeCollect(String userid, String collectid);

    cn.joy.ggg.api.model.PageSupport getList(String userid, int pageNo, int pageSize);

    cn.joy.ggg.api.model.PageSupport getScoringHistory(String userid, int pageNo, int pageSize);

    cn.joy.ggg.api.model.PageSupport getTopicList(String userid, int pageNo, int pageSize);

    int addTopic(String userid, cn.joy.ggg.api.model.Topic tipoc);

    int removeTopic(String userid, String tid);

    int setTopic(String userid, cn.joy.ggg.api.model.Topic topic);

    void encodeNotify(cn.joy.ggg.api.model.Topic topic);

    cn.joy.ggg.api.model.PageSupport getFriendDevelopment(String userId, int pageNo, int pageSize);

    cn.joy.ggg.api.model.PageSupport getSelfDevelopment(String selfId, int pageNo, int pageSize);

    int addDevelopment(cn.joy.ggg.api.model.FriendDevelopment devel);
}