// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.0

package cn.joy.ggg.api.model;

// <auto-generated>
//
// Generated from file `comment.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public final class CommentListHelper
{
    public static void
    write(IceInternal.BasicStream __os, java.util.List<Comment> __v)
    {
        if(__v == null)
        {
            __os.writeSize(0);
        }
        else
        {
            __os.writeSize(__v.size());
            for(Comment __elem : __v)
            {
                __elem.__write(__os);
            }
        }
    }

    public static java.util.List<Comment>
    read(IceInternal.BasicStream __is)
    {
        java.util.List<Comment> __v;
        __v = new java.util.ArrayList<Comment>();
        final int __len0 = __is.readAndCheckSeqSize(12);
        for(int __i0 = 0; __i0 < __len0; __i0++)
        {
            Comment __elem;
            __elem = new Comment();
            __elem.__read(__is);
            __v.add(__elem);
        }
        return __v;
    }
}