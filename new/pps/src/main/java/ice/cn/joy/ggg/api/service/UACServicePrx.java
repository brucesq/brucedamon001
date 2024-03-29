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


public interface UACServicePrx extends Ice.ObjectPrx
{
    public cn.joy.ggg.api.model.UACResponse login(String userName, String password);

    public cn.joy.ggg.api.model.UACResponse login(String userName, String password, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_login(String userName, String password);

    public Ice.AsyncResult begin_login(String userName, String password, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_login(String userName, String password, Ice.Callback __cb);

    public Ice.AsyncResult begin_login(String userName, String password, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_login(String userName, String password, Callback_UACService_login __cb);

    public Ice.AsyncResult begin_login(String userName, String password, java.util.Map<String, String> __ctx, Callback_UACService_login __cb);

    public cn.joy.ggg.api.model.UACResponse end_login(Ice.AsyncResult __result);

    public cn.joy.ggg.api.model.UACResponse checkLogin(String userId, String token, String userIp);

    public cn.joy.ggg.api.model.UACResponse checkLogin(String userId, String token, String userIp, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_checkLogin(String userId, String token, String userIp);

    public Ice.AsyncResult begin_checkLogin(String userId, String token, String userIp, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_checkLogin(String userId, String token, String userIp, Ice.Callback __cb);

    public Ice.AsyncResult begin_checkLogin(String userId, String token, String userIp, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_checkLogin(String userId, String token, String userIp, Callback_UACService_checkLogin __cb);

    public Ice.AsyncResult begin_checkLogin(String userId, String token, String userIp, java.util.Map<String, String> __ctx, Callback_UACService_checkLogin __cb);

    public cn.joy.ggg.api.model.UACResponse end_checkLogin(Ice.AsyncResult __result);

    public cn.joy.ggg.api.model.UACResponse changePassword(String userId, String oldpasswd, String newpasswd);

    public cn.joy.ggg.api.model.UACResponse changePassword(String userId, String oldpasswd, String newpasswd, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_changePassword(String userId, String oldpasswd, String newpasswd);

    public Ice.AsyncResult begin_changePassword(String userId, String oldpasswd, String newpasswd, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_changePassword(String userId, String oldpasswd, String newpasswd, Ice.Callback __cb);

    public Ice.AsyncResult begin_changePassword(String userId, String oldpasswd, String newpasswd, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_changePassword(String userId, String oldpasswd, String newpasswd, Callback_UACService_changePassword __cb);

    public Ice.AsyncResult begin_changePassword(String userId, String oldpasswd, String newpasswd, java.util.Map<String, String> __ctx, Callback_UACService_changePassword __cb);

    public cn.joy.ggg.api.model.UACResponse end_changePassword(Ice.AsyncResult __result);

    public cn.joy.ggg.api.model.UACListResponse getUserInfoByName(String userName);

    public cn.joy.ggg.api.model.UACListResponse getUserInfoByName(String userName, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_getUserInfoByName(String userName);

    public Ice.AsyncResult begin_getUserInfoByName(String userName, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_getUserInfoByName(String userName, Ice.Callback __cb);

    public Ice.AsyncResult begin_getUserInfoByName(String userName, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_getUserInfoByName(String userName, Callback_UACService_getUserInfoByName __cb);

    public Ice.AsyncResult begin_getUserInfoByName(String userName, java.util.Map<String, String> __ctx, Callback_UACService_getUserInfoByName __cb);

    public cn.joy.ggg.api.model.UACListResponse end_getUserInfoByName(Ice.AsyncResult __result);

    public cn.joy.ggg.api.model.UACListResponse getUserInfoById(String userId);

    public cn.joy.ggg.api.model.UACListResponse getUserInfoById(String userId, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_getUserInfoById(String userId);

    public Ice.AsyncResult begin_getUserInfoById(String userId, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_getUserInfoById(String userId, Ice.Callback __cb);

    public Ice.AsyncResult begin_getUserInfoById(String userId, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_getUserInfoById(String userId, Callback_UACService_getUserInfoById __cb);

    public Ice.AsyncResult begin_getUserInfoById(String userId, java.util.Map<String, String> __ctx, Callback_UACService_getUserInfoById __cb);

    public cn.joy.ggg.api.model.UACListResponse end_getUserInfoById(Ice.AsyncResult __result);
}
