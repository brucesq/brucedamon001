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
// Generated from file `comment.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public interface _CommentServiceDel extends Ice._ObjectDel
{
    cn.joy.ggg.api.model.CommentResponse postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    cn.joy.ggg.api.model.CommentResponse getCommentList(String appId, String cid, int start, int limit, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    cn.joy.ggg.api.model.CommentResponse removeComment(String appId, String cid, String commentIds, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
