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
// Generated from file `community.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public abstract class _CommunityServiceDisp extends Ice.ObjectImpl implements CommunityService
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::service::CommunityService"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[1];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String
    ice_staticId()
    {
        return __ids[1];
    }

    public final int
    addCollect(String userid, String title, String link)
    {
        return addCollect(userid, title, link, null);
    }

    public final int
    addDevelopment(cn.joy.ggg.api.model.FriendDevelopment devel)
    {
        return addDevelopment(devel, null);
    }

    public final int
    addTopic(String userid, cn.joy.ggg.api.model.Topic tipoc)
    {
        return addTopic(userid, tipoc, null);
    }

    public final void
    encodeNotify(cn.joy.ggg.api.model.Topic topic)
    {
        encodeNotify(topic, null);
    }

    public final cn.joy.ggg.api.model.PageSupport
    getFriendDevelopment(String userId, int pageNo, int pageSize)
    {
        return getFriendDevelopment(userId, pageNo, pageSize, null);
    }

    public final cn.joy.ggg.api.model.PageSupport
    getList(String userid, int pageNo, int pageSize)
    {
        return getList(userid, pageNo, pageSize, null);
    }

    public final cn.joy.ggg.api.model.PageSupport
    getScoringHistory(String userid, int pageNo, int pageSize)
    {
        return getScoringHistory(userid, pageNo, pageSize, null);
    }

    public final cn.joy.ggg.api.model.PageSupport
    getSelfDevelopment(String selfId, int pageNo, int pageSize)
    {
        return getSelfDevelopment(selfId, pageNo, pageSize, null);
    }

    public final cn.joy.ggg.api.model.PageSupport
    getTopicList(String userid, int pageNo, int pageSize)
    {
        return getTopicList(userid, pageNo, pageSize, null);
    }

    public final cn.joy.ggg.api.model.UserProfile
    getUser(String userid)
    {
        return getUser(userid, null);
    }

    public final int
    removeCollect(String userid, String collectid)
    {
        return removeCollect(userid, collectid, null);
    }

    public final int
    removeTopic(String userid, String tid)
    {
        return removeTopic(userid, tid, null);
    }

    public final java.util.List<cn.joy.ggg.api.model.UserProfile>
    searchUser(cn.joy.ggg.api.model.UserProfile user)
    {
        return searchUser(user, null);
    }

    public final int
    setTopic(String userid, cn.joy.ggg.api.model.Topic topic)
    {
        return setTopic(userid, topic, null);
    }

    public final int
    setUser(cn.joy.ggg.api.model.UserProfile user)
    {
        return setUser(user, null);
    }

    public static Ice.DispatchStatus
    ___searchUser(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        cn.joy.ggg.api.model.UserProfile user;
        user = new cn.joy.ggg.api.model.UserProfile();
        user.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        java.util.List<cn.joy.ggg.api.model.UserProfile> __ret = __obj.searchUser(user, __current);
        cn.joy.ggg.api.model.ProfileListHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setUser(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        cn.joy.ggg.api.model.UserProfile user;
        user = new cn.joy.ggg.api.model.UserProfile();
        user.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.setUser(user, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getUser(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.UserProfile __ret = __obj.getUser(userid, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___addCollect(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        String title;
        title = __is.readString();
        String link;
        link = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.addCollect(userid, title, link, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___removeCollect(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        String collectid;
        collectid = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.removeCollect(userid, collectid, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getList(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        int pageNo;
        pageNo = __is.readInt();
        int pageSize;
        pageSize = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.PageSupport __ret = __obj.getList(userid, pageNo, pageSize, __current);
        __ret.__write(__os);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScoringHistory(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        int pageNo;
        pageNo = __is.readInt();
        int pageSize;
        pageSize = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.PageSupport __ret = __obj.getScoringHistory(userid, pageNo, pageSize, __current);
        __ret.__write(__os);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTopicList(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        int pageNo;
        pageNo = __is.readInt();
        int pageSize;
        pageSize = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.PageSupport __ret = __obj.getTopicList(userid, pageNo, pageSize, __current);
        __ret.__write(__os);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___addTopic(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        cn.joy.ggg.api.model.Topic tipoc;
        tipoc = new cn.joy.ggg.api.model.Topic();
        tipoc.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.addTopic(userid, tipoc, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___removeTopic(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        String tid;
        tid = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.removeTopic(userid, tid, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTopic(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userid;
        userid = __is.readString();
        cn.joy.ggg.api.model.Topic topic;
        topic = new cn.joy.ggg.api.model.Topic();
        topic.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.setTopic(userid, topic, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___encodeNotify(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        cn.joy.ggg.api.model.Topic topic;
        topic = new cn.joy.ggg.api.model.Topic();
        topic.__read(__is);
        __is.endReadEncaps();
        __obj.encodeNotify(topic, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFriendDevelopment(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userId;
        userId = __is.readString();
        int pageNo;
        pageNo = __is.readInt();
        int pageSize;
        pageSize = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.PageSupport __ret = __obj.getFriendDevelopment(userId, pageNo, pageSize, __current);
        __ret.__write(__os);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getSelfDevelopment(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String selfId;
        selfId = __is.readString();
        int pageNo;
        pageNo = __is.readInt();
        int pageSize;
        pageSize = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.PageSupport __ret = __obj.getSelfDevelopment(selfId, pageNo, pageSize, __current);
        __ret.__write(__os);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___addDevelopment(CommunityService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        cn.joy.ggg.api.model.FriendDevelopment devel;
        devel = new cn.joy.ggg.api.model.FriendDevelopment();
        devel.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.addDevelopment(devel, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "addCollect",
        "addDevelopment",
        "addTopic",
        "encodeNotify",
        "getFriendDevelopment",
        "getList",
        "getScoringHistory",
        "getSelfDevelopment",
        "getTopicList",
        "getUser",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "removeCollect",
        "removeTopic",
        "searchUser",
        "setTopic",
        "setUser"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___addCollect(this, in, __current);
            }
            case 1:
            {
                return ___addDevelopment(this, in, __current);
            }
            case 2:
            {
                return ___addTopic(this, in, __current);
            }
            case 3:
            {
                return ___encodeNotify(this, in, __current);
            }
            case 4:
            {
                return ___getFriendDevelopment(this, in, __current);
            }
            case 5:
            {
                return ___getList(this, in, __current);
            }
            case 6:
            {
                return ___getScoringHistory(this, in, __current);
            }
            case 7:
            {
                return ___getSelfDevelopment(this, in, __current);
            }
            case 8:
            {
                return ___getTopicList(this, in, __current);
            }
            case 9:
            {
                return ___getUser(this, in, __current);
            }
            case 10:
            {
                return ___ice_id(this, in, __current);
            }
            case 11:
            {
                return ___ice_ids(this, in, __current);
            }
            case 12:
            {
                return ___ice_isA(this, in, __current);
            }
            case 13:
            {
                return ___ice_ping(this, in, __current);
            }
            case 14:
            {
                return ___removeCollect(this, in, __current);
            }
            case 15:
            {
                return ___removeTopic(this, in, __current);
            }
            case 16:
            {
                return ___searchUser(this, in, __current);
            }
            case 17:
            {
                return ___setTopic(this, in, __current);
            }
            case 18:
            {
                return ___setUser(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type service::CommunityService was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type service::CommunityService was not generated with stream support";
        throw ex;
    }
}