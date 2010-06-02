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


public interface _CommentServiceOperationsNC
{
    cn.joy.ggg.api.model.CommentResponse postComment(String appId, String userId, String cid, String connect, boolean isAnonymity);

    cn.joy.ggg.api.model.CommentResponse getCommentList(String appId, String cid, int start, int limit);

    cn.joy.ggg.api.model.CommentResponse removeComment(String appId, String cid, String commentIds);
}