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
// Generated from file `uac.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public interface _UACServiceOperationsNC
{
    cn.joy.ggg.api.model.UACResponse login(String userName, String password);

    cn.joy.ggg.api.model.UACResponse checkLogin(String userId, String token, String userIp);

    cn.joy.ggg.api.model.UACResponse changePassword(String userId, String oldpasswd, String newpasswd);

    cn.joy.ggg.api.model.UACListResponse getUserInfoByName(String userName);

    cn.joy.ggg.api.model.UACListResponse getUserInfoById(String userId);
}
