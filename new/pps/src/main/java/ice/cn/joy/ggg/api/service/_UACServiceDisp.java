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
// Generated from file `uac.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public abstract class _UACServiceDisp extends Ice.ObjectImpl implements UACService
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
        "::service::UACService"
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

    public final cn.joy.ggg.api.model.UACResponse
    changePassword(String userId, String oldpasswd, String newpasswd)
    {
        return changePassword(userId, oldpasswd, newpasswd, null);
    }

    public final cn.joy.ggg.api.model.UACResponse
    checkLogin(String userId, String token, String userIp)
    {
        return checkLogin(userId, token, userIp, null);
    }

    public final cn.joy.ggg.api.model.UACListResponse
    getUserInfoById(String userId)
    {
        return getUserInfoById(userId, null);
    }

    public final cn.joy.ggg.api.model.UACListResponse
    getUserInfoByName(String userName)
    {
        return getUserInfoByName(userName, null);
    }

    public final cn.joy.ggg.api.model.UACResponse
    login(String userName, String password)
    {
        return login(userName, password, null);
    }

    public static Ice.DispatchStatus
    ___login(UACService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userName;
        userName = __is.readString();
        String password;
        password = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.UACResponse __ret = __obj.login(userName, password, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___checkLogin(UACService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userId;
        userId = __is.readString();
        String token;
        token = __is.readString();
        String userIp;
        userIp = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.UACResponse __ret = __obj.checkLogin(userId, token, userIp, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___changePassword(UACService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userId;
        userId = __is.readString();
        String oldpasswd;
        oldpasswd = __is.readString();
        String newpasswd;
        newpasswd = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.UACResponse __ret = __obj.changePassword(userId, oldpasswd, newpasswd, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getUserInfoByName(UACService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userName;
        userName = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.UACListResponse __ret = __obj.getUserInfoByName(userName, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getUserInfoById(UACService __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String userId;
        userId = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        cn.joy.ggg.api.model.UACListResponse __ret = __obj.getUserInfoById(userId, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "changePassword",
        "checkLogin",
        "getUserInfoById",
        "getUserInfoByName",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "login"
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
                return ___changePassword(this, in, __current);
            }
            case 1:
            {
                return ___checkLogin(this, in, __current);
            }
            case 2:
            {
                return ___getUserInfoById(this, in, __current);
            }
            case 3:
            {
                return ___getUserInfoByName(this, in, __current);
            }
            case 4:
            {
                return ___ice_id(this, in, __current);
            }
            case 5:
            {
                return ___ice_ids(this, in, __current);
            }
            case 6:
            {
                return ___ice_isA(this, in, __current);
            }
            case 7:
            {
                return ___ice_ping(this, in, __current);
            }
            case 8:
            {
                return ___login(this, in, __current);
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
        ex.reason = "type service::UACService was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type service::UACService was not generated with stream support";
        throw ex;
    }
}
