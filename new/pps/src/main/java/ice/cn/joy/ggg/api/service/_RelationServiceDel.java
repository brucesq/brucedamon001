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
// Generated from file `relation.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public interface _RelationServiceDel extends Ice._ObjectDel
{
    cn.joy.ggg.api.model.RelationResponse addFriend(String self, String buddy, String description, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    cn.joy.ggg.api.model.RelationResponse removeFriend(String self, String buddy, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    cn.joy.ggg.api.model.RelationResponse getFriendList(String self, int start, int count, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}