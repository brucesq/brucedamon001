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


public class Message implements java.lang.Cloneable, java.io.Serializable
{
    public int msgId;

    public String type;

    public String origion;

    public String dest;

    public String title;

    public String content;

    public String date;

    public String status;

    public Message()
    {
    }

    public Message(int msgId, String type, String origion, String dest, String title, String content, String date, String status)
    {
        this.msgId = msgId;
        this.type = type;
        this.origion = origion;
        this.dest = dest;
        this.title = title;
        this.content = content;
        this.date = date;
        this.status = status;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        Message _r = null;
        try
        {
            _r = (Message)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(msgId != _r.msgId)
            {
                return false;
            }
            if(type != _r.type && type != null && !type.equals(_r.type))
            {
                return false;
            }
            if(origion != _r.origion && origion != null && !origion.equals(_r.origion))
            {
                return false;
            }
            if(dest != _r.dest && dest != null && !dest.equals(_r.dest))
            {
                return false;
            }
            if(title != _r.title && title != null && !title.equals(_r.title))
            {
                return false;
            }
            if(content != _r.content && content != null && !content.equals(_r.content))
            {
                return false;
            }
            if(date != _r.date && date != null && !date.equals(_r.date))
            {
                return false;
            }
            if(status != _r.status && status != null && !status.equals(_r.status))
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
        __h = 5 * __h + msgId;
        if(type != null)
        {
            __h = 5 * __h + type.hashCode();
        }
        if(origion != null)
        {
            __h = 5 * __h + origion.hashCode();
        }
        if(dest != null)
        {
            __h = 5 * __h + dest.hashCode();
        }
        if(title != null)
        {
            __h = 5 * __h + title.hashCode();
        }
        if(content != null)
        {
            __h = 5 * __h + content.hashCode();
        }
        if(date != null)
        {
            __h = 5 * __h + date.hashCode();
        }
        if(status != null)
        {
            __h = 5 * __h + status.hashCode();
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
        __os.writeInt(msgId);
        __os.writeString(type);
        __os.writeString(origion);
        __os.writeString(dest);
        __os.writeString(title);
        __os.writeString(content);
        __os.writeString(date);
        __os.writeString(status);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        msgId = __is.readInt();
        type = __is.readString();
        origion = __is.readString();
        dest = __is.readString();
        title = __is.readString();
        content = __is.readString();
        date = __is.readString();
        status = __is.readString();
    }

    public int getMsgId() {
      return msgId;
    }

    public void setMsgId(int msgId) {
      this.msgId = msgId;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getOrigion() {
      return origion;
    }

    public void setOrigion(String origion) {
      this.origion = origion;
    }

    public String getDest() {
      return dest;
    }

    public void setDest(String dest) {
      this.dest = dest;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }
}
