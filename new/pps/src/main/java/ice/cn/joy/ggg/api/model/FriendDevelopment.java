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
// Generated from file `community.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public class FriendDevelopment implements java.lang.Cloneable, java.io.Serializable
{
    public String friendid;

    public String content;

    public String time;

    public FriendDevelopment()
    {
    }

    public FriendDevelopment(String friendid, String content, String time)
    {
        this.friendid = friendid;
        this.content = content;
        this.time = time;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        FriendDevelopment _r = null;
        try
        {
            _r = (FriendDevelopment)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(friendid != _r.friendid && friendid != null && !friendid.equals(_r.friendid))
            {
                return false;
            }
            if(content != _r.content && content != null && !content.equals(_r.content))
            {
                return false;
            }
            if(time != _r.time && time != null && !time.equals(_r.time))
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 0;
        if(friendid != null)
        {
            __h = 5 * __h + friendid.hashCode();
        }
        if(content != null)
        {
            __h = 5 * __h + content.hashCode();
        }
        if(time != null)
        {
            __h = 5 * __h + time.hashCode();
        }
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString(friendid);
        __os.writeString(content);
        __os.writeString(time);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        friendid = __is.readString();
        content = __is.readString();
        time = __is.readString();
    }

    public String getFriendid() {
      return friendid;
    }

    public void setFriendid(String friendid) {
      this.friendid = friendid;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }
}