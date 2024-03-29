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
// Generated from file `uac.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public class UAC implements java.lang.Cloneable, java.io.Serializable
{
    public String userId;

    public String userName;

    public String token;

    public String nick;

    public String email;

    public String icon;

    public UAC()
    {
    }

    public UAC(String userId, String userName, String token, String nick, String email, String icon)
    {
        this.userId = userId;
        this.userName = userName;
        this.token = token;
        this.nick = nick;
        this.email = email;
        this.icon = icon;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        UAC _r = null;
        try
        {
            _r = (UAC)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(userId != _r.userId && userId != null && !userId.equals(_r.userId))
            {
                return false;
            }
            if(userName != _r.userName && userName != null && !userName.equals(_r.userName))
            {
                return false;
            }
            if(token != _r.token && token != null && !token.equals(_r.token))
            {
                return false;
            }
            if(nick != _r.nick && nick != null && !nick.equals(_r.nick))
            {
                return false;
            }
            if(email != _r.email && email != null && !email.equals(_r.email))
            {
                return false;
            }
            if(icon != _r.icon && icon != null && !icon.equals(_r.icon))
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
        if(userId != null)
        {
            __h = 5 * __h + userId.hashCode();
        }
        if(userName != null)
        {
            __h = 5 * __h + userName.hashCode();
        }
        if(token != null)
        {
            __h = 5 * __h + token.hashCode();
        }
        if(nick != null)
        {
            __h = 5 * __h + nick.hashCode();
        }
        if(email != null)
        {
            __h = 5 * __h + email.hashCode();
        }
        if(icon != null)
        {
            __h = 5 * __h + icon.hashCode();
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
        __os.writeString(userId);
        __os.writeString(userName);
        __os.writeString(token);
        __os.writeString(nick);
        __os.writeString(email);
        __os.writeString(icon);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        userId = __is.readString();
        userName = __is.readString();
        token = __is.readString();
        nick = __is.readString();
        email = __is.readString();
        icon = __is.readString();
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getUserName() {
      return userName;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public String getNick() {
      return nick;
    }

    public void setNick(String nick) {
      this.nick = nick;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getIcon() {
      return icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }
}
