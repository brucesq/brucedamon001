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


public abstract class Callback_UACService_getUserInfoByName extends Ice.TwowayCallback
{
    public abstract void response(cn.joy.ggg.api.model.UACListResponse __ret);

    public final void __completed(Ice.AsyncResult __result)
    {
        UACServicePrx __proxy = (UACServicePrx)__result.getProxy();
        cn.joy.ggg.api.model.UACListResponse __ret = null;
        try
        {
            __ret = __proxy.end_getUserInfoByName(__result);
        }
        catch(Ice.LocalException __ex)
        {
            exception(__ex);
            return;
        }
        response(__ret);
    }
}
