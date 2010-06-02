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
// Generated from file `message.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public final class MessageListHelper
{
    public static void
    write(IceInternal.BasicStream __os, java.util.List<Message> __v)
    {
        if(__v == null)
        {
            __os.writeSize(0);
        }
        else
        {
            __os.writeSize(__v.size());
            for(Message __elem : __v)
            {
                __elem.__write(__os);
            }
        }
    }

    public static java.util.List<Message>
    read(IceInternal.BasicStream __is)
    {
        java.util.List<Message> __v;
        __v = new java.util.ArrayList<Message>();
        final int __len0 = __is.readAndCheckSeqSize(11);
        for(int __i0 = 0; __i0 < __len0; __i0++)
        {
            Message __elem;
            __elem = new Message();
            __elem.__read(__is);
            __v.add(__elem);
        }
        return __v;
    }
}