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


public class Collect implements java.lang.Cloneable, java.io.Serializable
{
    public String collectid;

    public Topic topics;

    public String ctime;

    public Collect()
    {
    }

    public Collect(String collectid, Topic topics, String ctime)
    {
        this.collectid = collectid;
        this.topics = topics;
        this.ctime = ctime;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        Collect _r = null;
        try
        {
            _r = (Collect)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(collectid != _r.collectid && collectid != null && !collectid.equals(_r.collectid))
            {
                return false;
            }
            if(topics != _r.topics && topics != null && !topics.equals(_r.topics))
            {
                return false;
            }
            if(ctime != _r.ctime && ctime != null && !ctime.equals(_r.ctime))
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
        if(collectid != null)
        {
            __h = 5 * __h + collectid.hashCode();
        }
        if(topics != null)
        {
            __h = 5 * __h + topics.hashCode();
        }
        if(ctime != null)
        {
            __h = 5 * __h + ctime.hashCode();
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
        __os.writeString(collectid);
        topics.__write(__os);
        __os.writeString(ctime);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        collectid = __is.readString();
        topics = new Topic();
        topics.__read(__is);
        ctime = __is.readString();
    }

    public String getCollectid() {
      return collectid;
    }

    public void setCollectid(String collectid) {
      this.collectid = collectid;
    }

    public Topic getTopics() {
      return topics;
    }

    public void setTopics(Topic topics) {
      this.topics = topics;
    }

    public String getCtime() {
      return ctime;
    }

    public void setCtime(String ctime) {
      this.ctime = ctime;
    }
}